
package Method.Client.utils.Patcher.Patches;


import Method.Client.utils.Patcher.Events.RenderBlockModelEvent;
import Method.Client.utils.Patcher.ModClassVisitor;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.MinecraftForge;
import org.objectweb.asm.*;

public final class ForgeBlockModelRendererVisitor extends ModClassVisitor {
    public ForgeBlockModelRendererVisitor(ClassVisitor cv, boolean obf) {
        super(cv);

        String iBlockAccess = unmap("net/minecraft/world/IBlockAccess");
        String iBakedModel = unmap("net/minecraft/client/renderer/block/model/IBakedModel");
        String iBlockState = unmap("net/minecraft/block/state/IBlockState");
        String blockPos = unmap("net/minecraft/util/math/BlockPos");
        String bufferBuilder = unmap("net/minecraft/client/renderer/BufferBuilder");

        String renderModelFlat_name = obf ? "c" : "renderModelFlat";
        String renderModelSmooth_name = obf ? "b" : "renderModelSmooth";
        String renderModel_desc = "(L" + iBlockAccess + ";" + "L" + iBakedModel + ";" + "L" + iBlockState + ";" + "L" + blockPos + ";" + "L" + bufferBuilder + ";ZJ)Z";

        registerMethodVisitor(renderModelFlat_name, renderModel_desc, RenderModelFlatVisitor::new);
        registerMethodVisitor(renderModelSmooth_name, renderModel_desc, RenderModelSmoothVisitor::new);
    }

    public static class RenderModelFlatVisitor extends MethodVisitor {
        public RenderModelFlatVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {
            System.out.println("ForgeBlockModelRendererVisitor.RenderModelFlatVisitor.visitCode()");

            super.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(this.getClass()), "renderBlockModel", "(Lnet/minecraft/block/state/IBlockState;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }

        public static boolean renderBlockModel(IBlockState state) {
            return !MinecraftForge.EVENT_BUS.post(new RenderBlockModelEvent(state));
        }
    }


    public static class RenderModelSmoothVisitor extends MethodVisitor {
        public RenderModelSmoothVisitor(MethodVisitor mv) {
            super(Opcodes.ASM4, mv);
        }

        @Override
        public void visitCode() {
            System.out.println(
                    "ForgeBlockModelRendererVisitor.RenderModelSmoothVisitor.visitCode()");

            super.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 3);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    Type.getInternalName(this.getClass()),
                    "renderBlockModel",
                    "(Lnet/minecraft/block/state/IBlockState;)Z", false);
            Label l1 = new Label();
            mv.visitJumpInsn(Opcodes.IFNE, l1);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitLabel(l1);
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        }

        public static boolean renderBlockModel(IBlockState state) {
            return !MinecraftForge.EVENT_BUS.post(new RenderBlockModelEvent(state));
        }
    }
}
