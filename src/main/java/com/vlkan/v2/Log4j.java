package com.vlkan.v2;

public enum Log4j {;

    public static void log(StackTraceElement location) {
        boolean locationProvided = location != null;
        if (!locationProvided) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            // Skip the first element pointing to this method.
            location = stackTraceElements[1];
        }
        System.out.format(
                "[%s!%s#%s:%s] %s%n",
                location.getFileName(),
                location.getClassName(),
                location.getMethodName(),
                location.getLineNumber(),
                locationProvided ? "provided location" : "populated location");
    }

}
