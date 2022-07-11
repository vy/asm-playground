package com.vlkan.v1;

public enum Log4j {;

    public static final class SourceLocation {

        private String fileName;

        private String className;

        private String methodName;

        private int lineNumber;

        public void init(String fileName, String className, String methodName, int lineNumber) {
            this.fileName = fileName;
            this.className = className;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
        }

        @Override
        public String toString() {
            return String.format("%s!%s#%s:%s", fileName, className, methodName, lineNumber);
        }

    }

    public static final ThreadLocal<SourceLocation> LOCATION_REF = ThreadLocal.withInitial(SourceLocation::new);

    public static void log() {
        SourceLocation location = LOCATION_REF.get();
        boolean locationProvided = location.lineNumber > 0;
        if (!locationProvided) {
            StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
            // Skip the first element pointing to this method.
            StackTraceElement stackTraceElement = stackTraceElements[1];
            location.init(
                    stackTraceElement.getFileName(),
                    stackTraceElement.getClassName(),
                    stackTraceElement.getMethodName(),
                    stackTraceElement.getLineNumber());
        }
        System.out.format(
                "[%s] %s%n",
                location,
                locationProvided ? "provided location" : "populated location");
    }

}
