package com.vlkan.v2;

import org.objectweb.asm.*;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicInteger;

public class AppTransforming {

    public static void main(String[] args) throws Exception {
        injectLocation("com.vlkan.v2.AppActual");
        AppActual.main(args);
    }

    private static void injectLocation(String... classNames) throws Exception {
        AtomicInteger locationRegistryCounter = new AtomicInteger(0);
        for (String className : classNames) {
            injectLocation(className, locationRegistryCounter);
        }
        Log4jLocationRegistry.init(locationRegistryCounter.get());
    }

    private static void injectLocation(String className, AtomicInteger locationRegistryCounter) throws Exception {
        ClassReader cr = new ClassReader(className);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {

            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                MethodVisitor parentMethodVisitor = super.visitMethod(acc, name, desc, sig, ex);
                return new LocationInjectingMethodVisitor(locationRegistryCounter, parentMethodVisitor);
            }

        }, 0);

        MethodHandles.lookup().defineClass(cw.toByteArray());

    }

    private static final class LocationInjectingMethodVisitor extends MethodVisitor {

        private final AtomicInteger locationRegistryCounter;

        private String lastStringArg;

        private LocationInjectingMethodVisitor(
                AtomicInteger locationRegistryCounter,
                MethodVisitor parentMethodVisitor) {
            super(Opcodes.ASM9, parentMethodVisitor);
            this.locationRegistryCounter = locationRegistryCounter;
        }

        @Override
        public void visitLdcInsn(Object value) {
            if (value instanceof String) {
                lastStringArg = (String) value;
            }
            super.visitLdcInsn(value);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            boolean loggerCalled = Opcodes.INVOKEINTERFACE == opcode &&
                    "org/apache/logging/log4j/Logger".equals(owner) &&
                    "info".equals(name) &&
                    "(Ljava/lang/String;)V".equals(descriptor) &&
                    isInterface;
            if (!loggerCalled) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                return;
            }
            // Pop the `String` argument from the stack.
            visitInsn(Opcodes.POP);
            // Invoke `Logger#atInfo()`.
            super.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "org/apache/logging/log4j/Logger",
                    "atInfo",
                    "()Lorg/apache/logging/log4j/LogBuilder;",
                    true);
            // Invoke `Log4jLocationRegistry.get(int)`.
            visitLdcInsn(locationRegistryCounter.getAndIncrement());
            super.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "com/vlkan/v2/Log4jLocationRegistry",
                    "get",
                    "(I)Ljava/lang/StackTraceElement;",
                    false);
            // Invoke `withLocation(StackTraceElement)`.
            super.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "org/apache/logging/log4j/LogBuilder",
                    "withLocation",
                    "(Ljava/lang/StackTraceElement;)Lorg/apache/logging/log4j/LogBuilder;",
                    true);
            // Invoke `log(String)`.
            super.visitLdcInsn(lastStringArg);
            super.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    "org/apache/logging/log4j/LogBuilder",
                    "log",
                    "(Ljava/lang/String;)V",
                    true);
        }

    }

}
