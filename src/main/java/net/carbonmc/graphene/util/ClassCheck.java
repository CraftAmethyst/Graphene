package net.carbonmc.graphene.util;

public class ClassCheck {
    public static boolean shouldSkipRedirection() {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();

        for (int i = 2; i < Math.min(callStack.length, 32); ++i) {
            String className = callStack[i].getClassName();
            if (className.contains("CullTask") ||
                    className.contains("entityculling")) {
                return true;
            }
        }

        return false;
    }
}
