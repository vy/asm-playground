package com.vlkan;

import org.objectweb.asm.*;

import java.lang.invoke.MethodHandles;

public class AppTransforming {

    public static void main(String[] args) throws Exception {
        injectSourceLocation("com.vlkan.AppActual");
        AppActual.main(args);
    }

    private static void injectSourceLocation(String className) throws Exception {
        ClassReader cr = new ClassReader(className);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        cr.accept(new ClassVisitor(Opcodes.ASM9, cw) {

            private String fileName;

            private String className;

            @Override
            public void visit(int ver, int acc, String name, String sig, String superName, String[] ifs) {
                super.visit(ver, acc, name, sig, superName, ifs);
                className = name.replace('/', '.');
            }

            @Override
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
                if (fileName == null) {
                    fileName = source;
                }
            }

            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                MethodVisitor parentMethodVisitor = super.visitMethod(acc, name, desc, sig, ex);
                return new SourceLocationInjectingMethodVisitor(parentMethodVisitor, fileName, className, name);
            }

        }, 0);

        MethodHandles.lookup().defineClass(cw.toByteArray());

    }

    private static final class SourceLocationInjectingMethodVisitor extends MethodVisitor {

        private final String fileName;

        private final String className;

        private final String methodName;

        private int lineNumber;

        private SourceLocationInjectingMethodVisitor(
                MethodVisitor parentMethodVisitor,
                String fileName,
                String className,
                String methodName) {
            super(Opcodes.ASM9, parentMethodVisitor);
            this.fileName = fileName;
            this.className = className;
            this.methodName = methodName;
        }

        @Override
        public void visitLineNumber(int lineNumber, Label start) {
            super.visitLineNumber(lineNumber, start);
            this.lineNumber = lineNumber;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            boolean loggerCalled = Opcodes.INVOKESTATIC == opcode &&
                    "com/vlkan/Log4j".equals(owner) &&
                    "log".equals(name) &&
                    "()V".equals(descriptor);
            if (loggerCalled) {
                injectSourceLocation();
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        private void injectSourceLocation() {
            visitFieldInsn(Opcodes.GETSTATIC, "com/vlkan/Log4j", "LOCATION_REF", "Ljava/lang/ThreadLocal;");
            super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/lang/ThreadLocal",
                    "get",
                    "()Ljava/lang/Object;",
                    false);
            visitTypeInsn(Opcodes.CHECKCAST, "com/vlkan/Log4j$SourceLocation");
            visitLdcInsn(fileName);
            visitLdcInsn(className);
            visitLdcInsn(methodName);
            visitIntInsn(Opcodes.BIPUSH, lineNumber);
            super.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "com/vlkan/Log4j$SourceLocation",
                    "init",
                    "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V",
                    false);
        }

    }

}
