package net.carbonmc.graphene.engine.collision.gpu;

import net.carbonmc.graphene.config.CoolConfig;
import net.minecraft.world.phys.AABB;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.PointerBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opencl.CL10.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Server-side OpenCL broad-phase for entity AABB collision pairs.
 *
 * This module discovers an OpenCL device (prefer GPU), compiles the kernel under resources/kernels/aabb_broadphase.cl,
 * uploads AABBs and emits candidate pairs (idA,idB).
 */
public final class OpenCLBroadphase {
    private static final String KERNEL_PATH = "/kernels/aabb_broadphase.cl";

    private boolean initialized;
    private static final AtomicBoolean CL_CREATED = new AtomicBoolean(false);
    private long platform;
    private long device;
    private long context;
    private long queue;
    private long program;
    private long kernel;

    // Device buffers (re-used per dispatch)
    private long bufMin = NULL;
    private long bufMax = NULL;
    private long bufIds = NULL;
    private long bufPairs = NULL;
    private long bufCount = NULL;
    private long bufOverflow = NULL;

    private int capacityAABBs = 0;
    private int capacityPairs = 0;

    public static final class Pair {
        public final int a, b;
        public Pair(int a, int b) { this.a = a; this.b = b; }
    }

    public synchronized void initIfNeeded() {
        if (initialized) return;
        // Load OpenCL symbols once per process
        if (!CL_CREATED.get()) {
            try {
                CL.create();
            } catch (IllegalStateException ignored) {
                // Already created elsewhere
            }
            CL_CREATED.set(true);
        }
        try (MemoryStack stack = stackPush()) {
            // Platforms
            IntBuffer pi = stack.mallocInt(1);
            check(clGetPlatformIDs((PointerBuffer) null, pi), "clGetPlatformIDs(count)");
            int pcount = pi.get(0);
            PointerBuffer plats = stack.mallocPointer(pcount);
            check(clGetPlatformIDs(plats, (IntBuffer) null), "clGetPlatformIDs(list)");

            // Pick first platform with a GPU, else CPU
            long chosenPlatform = NULL;
            long chosenDevice = NULL;
            for (int idx = 0; idx < pcount; idx++) {
                long p = plats.get(idx);
                long d = pickDevice(stack, p, CL_DEVICE_TYPE_GPU);
                if (d != NULL) { chosenPlatform = p; chosenDevice = d; break; }
            }
            if (chosenDevice == NULL) {
                for (int idx = 0; idx < pcount; idx++) {
                    long p = plats.get(idx);
                    long d = pickDevice(stack, p, CL_DEVICE_TYPE_CPU);
                    if (d != NULL) { chosenPlatform = p; chosenDevice = d; break; }
                }
            }
            if (chosenDevice == NULL) {
                throw new IllegalStateException("No OpenCL device available (GPU/CPU)");
            }
            this.platform = chosenPlatform;
            this.device = chosenDevice;

            // Context
            PointerBuffer ctxProps = stack.mallocPointer(3);
            ctxProps.put(CL_CONTEXT_PLATFORM).put(chosenPlatform).put(0).flip();
            PointerBuffer devBuf = stack.pointers(chosenDevice);
            IntBuffer errcode = stack.mallocInt(1);
            context = clCreateContext(ctxProps, devBuf, null, NULL, errcode);
            check(errcode.get(0), "clCreateContext");

            // Queue
            queue = clCreateCommandQueue(context, chosenDevice, 0L, errcode);
            check(errcode.get(0), "clCreateCommandQueue");

            // Program & Kernel
            String source = loadKernel();
            program = clCreateProgramWithSource(context, source, errcode);
            check(errcode.get(0), "clCreateProgramWithSource");

            int build = clBuildProgram(program, stack.pointers(chosenDevice), "", null, NULL);
            if (build != CL_SUCCESS) {
                // Fetch build log
                ByteBuffer log = getProgramBuildLog(program, chosenDevice);
                String msg = memUTF8(log);
                throw new IllegalStateException("OpenCL program build failed: \n" + msg);
            }

            kernel = clCreateKernel(program, "aabb_broadphase", errcode);
            check(errcode.get(0), "clCreateKernel");

            initialized = true;
        }
    }

    private long pickDevice(MemoryStack stack, long plat, long type) {
        IntBuffer di = stack.mallocInt(1);
        int err = clGetDeviceIDs(plat, type, (PointerBuffer) null, di);
        if (err != CL_SUCCESS || di.get(0) <= 0) return NULL;
        PointerBuffer devs = stack.mallocPointer(di.get(0));
        check(clGetDeviceIDs(plat, type, devs, (IntBuffer) null), "clGetDeviceIDs(list)");
        return devs.get(0);
    }

    private static void check(int err, String where) {
        if (err != CL_SUCCESS) throw new IllegalStateException(where + " failed: error=" + err);
    }

    private static String loadKernel() {
        try (InputStream in = OpenCLBroadphase.class.getResourceAsStream(KERNEL_PATH)) {
            if (in == null) throw new IOException("Kernel not found: " + KERNEL_PATH);
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line).append('\n');
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureCapacity(int n, int maxPairs) {
        if (n <= capacityAABBs && maxPairs <= capacityPairs) return;
        // Release old buffers
        if (bufMin != NULL) clReleaseMemObject(bufMin);
        if (bufMax != NULL) clReleaseMemObject(bufMax);
        if (bufIds != NULL) clReleaseMemObject(bufIds);
        if (bufPairs != NULL) clReleaseMemObject(bufPairs);
        if (bufCount != NULL) clReleaseMemObject(bufCount);
        if (bufOverflow != NULL) clReleaseMemObject(bufOverflow);

        long flagsRO = CL_MEM_READ_ONLY;
        long flagsWO = CL_MEM_WRITE_ONLY;
        long flagsRW = CL_MEM_READ_WRITE;

        bufMin = clCreateBuffer(context, flagsRO, (long) n * 4L * Float.BYTES, (IntBuffer) null);
        bufMax = clCreateBuffer(context, flagsRO, (long) n * 4L * Float.BYTES, (IntBuffer) null);
        bufIds = clCreateBuffer(context, flagsRO, (long) n * Integer.BYTES, (IntBuffer) null);
        bufPairs = clCreateBuffer(context, flagsWO, (long) maxPairs * 2L * Integer.BYTES, (IntBuffer) null);
        bufCount = clCreateBuffer(context, flagsRW, (long) Integer.BYTES, (IntBuffer) null);
        bufOverflow = clCreateBuffer(context, flagsRW, (long) Integer.BYTES, (IntBuffer) null);

        capacityAABBs = n;
        capacityPairs = maxPairs;
    }

    public List<Pair> computePairs(List<AABB> boxes, int[] ids) {
        Objects.requireNonNull(boxes, "boxes");
        if (!CoolConfig.enableGpuCollision.get()) return List.of();
        initIfNeeded();
        final int n = boxes.size();
        if (n <= 1) return List.of();
        if (ids == null || ids.length < n) throw new IllegalArgumentException("ids length < n");

        int maxPairs = CoolConfig.gpuCollisionMaxPairs.get();
        ensureCapacity(n, maxPairs);

        try (MemoryStack stack = stackPush()) {
            // Host buffers
            FloatBuffer hMin = stack.mallocFloat(n * 4);
            FloatBuffer hMax = stack.mallocFloat(n * 4);
            IntBuffer hIds = stack.mallocInt(n);

            for (int i = 0; i < n; i++) {
                AABB b = boxes.get(i);
                hMin.put((float) b.minX).put((float) b.minY).put((float) b.minZ).put(0f);
                hMax.put((float) b.maxX).put((float) b.maxY).put((float) b.maxZ).put(0f);
                hIds.put(ids[i]);
            }
            hMin.flip(); hMax.flip(); hIds.flip();

            // Zero counters
            IntBuffer zero = stack.ints(0);
            clEnqueueWriteBuffer(queue, bufCount, true, 0, zero, null, null);
            clEnqueueWriteBuffer(queue, bufOverflow, true, 0, zero, null, null);

            // Upload
            check(clEnqueueWriteBuffer(queue, bufMin, true, 0, hMin, null, null), "clEnqueueWriteBuffer(min)");
            check(clEnqueueWriteBuffer(queue, bufMax, true, 0, hMax, null, null), "clEnqueueWriteBuffer(max)");
            check(clEnqueueWriteBuffer(queue, bufIds, true, 0, hIds, null, null), "clEnqueueWriteBuffer(ids)");

            // Set args
            check(clSetKernelArg(kernel, 0, stack.pointers(bufMin)), "clSetKernelArg(0)");
            check(clSetKernelArg(kernel, 1, stack.pointers(bufMax)), "clSetKernelArg(1)");
            check(clSetKernelArg(kernel, 2, stack.pointers(bufIds)), "clSetKernelArg(2)");
            check(clSetKernelArg1i(kernel, 3, n), "clSetKernelArg(3)");
            check(clSetKernelArg(kernel, 4, stack.pointers(bufPairs)), "clSetKernelArg(4)");
            check(clSetKernelArg(kernel, 5, stack.pointers(bufCount)), "clSetKernelArg(5)");
            check(clSetKernelArg1i(kernel, 6, maxPairs), "clSetKernelArg(6)");
            check(clSetKernelArg(kernel, 7, stack.pointers(bufOverflow)), "clSetKernelArg(7)");

            // Dispatch
            PointerBuffer gws = stack.mallocPointer(1).put(0, n);
            check(clEnqueueNDRangeKernel(queue, kernel, 1, null, gws, null, null, null), "clEnqueueNDRangeKernel");
            clFinish(queue);

            // Read back count and overflow
            IntBuffer outCount = stack.mallocInt(1);
            clEnqueueReadBuffer(queue, bufCount, true, 0, outCount, null, null);
            int count = outCount.get(0);
            count = Math.min(count, maxPairs);

            IntBuffer overflow = stack.mallocInt(1);
            clEnqueueReadBuffer(queue, bufOverflow, true, 0, overflow, null, null);
            boolean overflowed = overflow.get(0) != 0;

            List<Pair> result = new ArrayList<>(count);
            if (count > 0) {
                IntBuffer pairs = memAllocInt(count * 2);
                try {
                    clEnqueueReadBuffer(queue, bufPairs, true, 0, pairs, null, null);
                    for (int i = 0; i < count; i++) {
                        int a = pairs.get(i * 2);
                        int b = pairs.get(i * 2 + 1);
                        result.add(new Pair(a, b));
                    }
                } finally {
                    memFree(pairs);
                }
            }

            if (overflowed) {
                // Optionally log or handle by re-tiling on CPU side.
            }

            return result;
        }
    }

    public synchronized void destroy() {
        if (!initialized) return;
        if (bufMin != NULL) clReleaseMemObject(bufMin);
        if (bufMax != NULL) clReleaseMemObject(bufMax);
        if (bufIds != NULL) clReleaseMemObject(bufIds);
        if (bufPairs != NULL) clReleaseMemObject(bufPairs);
        if (bufCount != NULL) clReleaseMemObject(bufCount);
        if (bufOverflow != NULL) clReleaseMemObject(bufOverflow);
        if (kernel != NULL) clReleaseKernel(kernel);
        if (program != NULL) clReleaseProgram(program);
        if (queue != NULL) clReleaseCommandQueue(queue);
        if (context != NULL) clReleaseContext(context);
        initialized = false;
    }

    private static ByteBuffer getProgramBuildLog(long program, long device) {
        try (MemoryStack stack = stackPush()) {
            PointerBuffer size = stack.mallocPointer(1);
            clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, (ByteBuffer) null, size);
            int sz = (int) size.get(0);
            ByteBuffer buffer = memAlloc(sz);
            clGetProgramBuildInfo(program, device, CL_PROGRAM_BUILD_LOG, buffer, null);
            return buffer;
        }
    }
}
