package Method.Client.modmaker.Windows.Blocks.Math;

import Method.Client.modmaker.BlockObjects;
import Method.Client.modmaker.MainBlockType;
import Method.Client.modmaker.Tabs;
import Method.Client.modmaker.Windows.Block;
import Method.Client.modmaker.Windows.DragableBlock;

public class Addition extends Block {

    public Addition() {
        super( "Addition", MainBlockType.Numbers, Tabs.Math, BlockObjects.NumericalTextEnter, BlockObjects.Words, BlockObjects.NumericalTextEnter);
        this.words[0] = "+";
        this.typesAccepted.add(typesCollapse(MainBlockType.Numbers));
        this.typesAccepted.add(typesCollapse(MainBlockType.Numbers));
        this.description = "Adds two numbers together using +";
    }

    @Override
    public double runCodeSolve(DragableBlock dragableBlock, Object event) {
        return dragableBlock.local.codeExecuter.solveNumerical(dragableBlock, 0, event) + dragableBlock.local.codeExecuter.solveNumerical(dragableBlock, 1, event);
    }


}
