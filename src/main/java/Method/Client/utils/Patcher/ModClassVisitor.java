package Method.Client.utils.Patcher;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;

public abstract class ModClassVisitor extends ClassVisitor {

    private final ArrayList<MethodVisitorRegistryEntry> methodVisitorRegistry = new ArrayList<>();

    public ModClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        for (MethodVisitorRegistryEntry entry : methodVisitorRegistry)
            if (name.equals(entry.name) && desc.equals(entry.desc))
                return entry.factory.createMethodVisitor(mv);

        return mv;
    }

    protected String unmap(String typeName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(typeName);
    }

    protected void registerMethodVisitor(String name, String desc, MethodVisitorFactory factory) {
        methodVisitorRegistry.add(new MethodVisitorRegistryEntry(name, desc, factory));
    }

    public interface MethodVisitorFactory {
        MethodVisitor createMethodVisitor(MethodVisitor mv);
    }

    private static final class MethodVisitorRegistryEntry {
        private final String name;
        private final String desc;
        private final MethodVisitorFactory factory;

        public MethodVisitorRegistryEntry(String name, String desc, MethodVisitorFactory factory) {
            this.name = name;
            this.desc = desc;
            this.factory = factory;
        }
    }
}
