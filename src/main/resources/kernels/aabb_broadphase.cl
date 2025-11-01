// OpenCL kernel: O(N^2) broad-phase AABB overlap test
// Inputs:
//  - aabbMin[n]: float4 (x,y,z,w) min corners
//  - aabbMax[n]: float4 (x,y,z,w) max corners
//  - ids[n]:     int unique IDs per AABB
//  - n:          number of AABBs
// Outputs:
//  - outPairs[maxPairs]: int2 of (idA, idB)
//  - outCount[1]: atomic counter of written pairs
//  - overflowFlag[1]: set to 1 if outPairs capacity exceeded
__kernel void aabb_broadphase(
    __global const float4* aabbMin,
    __global const float4* aabbMax,
    __global const int*    ids,
    const int              n,
    __global int2*         outPairs,
    __global int*          outCount,
    const int              maxPairs,
    __global int*          overflowFlag)
{
    int i = get_global_id(0);
    if (i >= n) return;

    float4 minA = aabbMin[i];
    float4 maxA = aabbMax[i];

    for (int j = i + 1; j < n; ++j) {
        float4 minB = aabbMin[j];
        float4 maxB = aabbMax[j];

        // AABB overlap test on XYZ
        int overlap = (minA.x <= maxB.x && maxA.x >= minB.x) &&
                      (minA.y <= maxB.y && maxA.y >= minB.y) &&
                      (minA.z <= maxB.z && maxA.z >= minB.z);
        if (!overlap) continue;

        int slot = atomic_inc(outCount);
        if (slot < maxPairs) {
            outPairs[slot] = (int2)(ids[i], ids[j]);
        } else {
            // Signal overflow; keep counter growing so host can see actual total
            *overflowFlag = 1;
        }
    }
}
