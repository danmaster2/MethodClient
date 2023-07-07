package Method.Client.modmaker.Windows.Blocks.Entity;

import Method.Client.modmaker.BlockObjects;
import Method.Client.modmaker.MainBlockType;
import Method.Client.modmaker.Tabs;
import Method.Client.modmaker.Windows.Block;
import Method.Client.modmaker.Windows.DragableBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class RemovePotion extends Block {

    public RemovePotion() {
        super("RemovePotion", MainBlockType.Default, Tabs.Entity, BlockObjects.Name, BlockObjects.Words, BlockObjects.NumericalTextEnter
        ,BlockObjects.Words, BlockObjects.NumericalTextEnter);
        this.description = "Remove Potion Effect";
        this.typesAccepted.add(typesCollapse(MainBlockType.Entity));
        this.typesAccepted.add(typesCollapse(MainBlockType.Potion));
        this.words[0] = "Entity";
        this.words[1] = "Potion";

    }

    @Override
    public void runCode(DragableBlock dragableBlock, Object event) {
        ((EntityLivingBase) dragableBlock.local.codeExecuter.solveObject(dragableBlock, 0, event)).removeActivePotionEffect((Potion) dragableBlock.local.codeExecuter.solveObject(dragableBlock, 1, event));
        super.runCode(dragableBlock, event);
    }

}
