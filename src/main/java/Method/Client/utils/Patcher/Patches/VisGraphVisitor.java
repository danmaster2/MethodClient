
package Method.Client.utils.Patcher.Patches;


import Method.Client.utils.Patcher.Events.SetOpaqueCubeEvent;
import Method.Client.utils.Patcher.ModClassVisitor;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.*;


public final class VisGraphVisitor extends ModClassVisitor {
    public VisGraphVisitor(ClassVisitor cv, boolean obf) {
        super(cv);

        String blockPos = unmap("net/minecraft/util/math/BlockPos");

        String setOpaqueCube_name = obf ? "a" : "setOpaqueCube";
        String setOpaqueCube_desc = "(L" + blockPos + ";)V";

        registerMethodVisitor(setOpaqueCube_name, setOpaqueCube_desc, SetOpaqueCubeVisitor::new);
    }

    public static class SetOpaqueCubeVisitor extends MethodVisitor {
        public SetOpaqueCubeVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {
            System.out.println("VisGraphVisitor.SetOpaqueCubeVisitor.visitCode()");

            super.visitCode();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "setOpaqueCube", "()Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l1);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }

        public static boolean setOpaqueCube() {
            return !MinecraftForge.EVENT_BUS.post(new SetOpaqueCubeEvent());
        }

    }


}
