package Method.Client.modmaker.Windows.Blocks.GameSettings;

import Method.Client.modmaker.BlockObjects;
import Method.Client.modmaker.MainBlockType;
import Method.Client.modmaker.Tabs;
import Method.Client.modmaker.Windows.Block;
import Method.Client.modmaker.Windows.DragableBlock;
import net.minecraft.client.Minecraft;

public class CurrentItem extends Block {

    public CurrentItem() {
        super("CurrentItem", MainBlockType.Default, Tabs.Game, BlockObjects.Name, BlockObjects.NumericalTextEnter);
        this.typesAccepted.add(typesCollapse(MainBlockType.Numbers));
        this.description = "Set current item to inv slot Number " + "\n" + "mc.player.inventory.currentItem(int) ";
    }

    @Override
    public void runCode(DragableBlock dragableBlock, Object event) {
        Minecraft.getMinecraft().player.inventory.currentItem = (int) dragableBlock.local.codeExecuter.solveNumerical(dragableBlock,0, event);
        super.runCode(dragableBlock, event);
    }

}
