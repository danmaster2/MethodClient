package Method.Client.module.render;

import Method.Client.Main;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.Patcher.Events.*;
import Method.Client.utils.Screens.Custom.Xray.XrayGuiSettings;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;

import static Method.Client.Main.setmgr;


public class Xray extends Module {

    private final XrayGuiSettings blocks = new XrayGuiSettings(
            Blocks.COAL_ORE, Blocks.COAL_BLOCK, Blocks.IRON_ORE, Blocks.IRON_BLOCK,
            Blocks.GOLD_ORE, Blocks.GOLD_BLOCK, Blocks.LAPIS_ORE,
            Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE,
            Blocks.REDSTONE_BLOCK, Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK,
            Blocks.EMERALD_ORE, Blocks.EMERALD_BLOCK, Blocks.QUARTZ_ORE,
            Blocks.LAVA, Blocks.MOB_SPAWNER, Blocks.PORTAL, Blocks.END_PORTAL,
            Blocks.END_PORTAL_FRAME);

    public static ArrayList<String> blockNames;
     Setting Gui = setmgr.add(new Setting("Gui", this, Main.Xray));

    public Xray() {
        super("Xray", Keyboard.KEY_NONE, Category.RENDER, "Xray");
    }

    @Override
    public void onEnable() {
        blockNames = new ArrayList<>(XrayGuiSettings.getBlockNames());
        MinecraftForge.EVENT_BUS.register(this);
        mc.renderGlobal.loadRenderers();
    }


    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        mc.gameSettings.gammaSetting = 16;
    }

    @Override
    public void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
        event.setCanceled(true);
    }

    @Override
    public void onGetAmbientOcclusionLightValue(GetAmbientOcclusionLightValueEvent event) {
        event.setLightValue(1);
    }

    @Override
    public void onShouldSideBeRendered(ShouldSideBeRenderedEvent event) {
        event.setRendered(isVisible(event.getState().getBlock()));
    }

    @Override
    public void onRenderBlockModel(RenderBlockModelEvent event) {
        if (!isVisible(event.getState().getBlock()))
            event.setCanceled(true);
    }


    // For chest objects//
    @Override
    public void onRenderTileEntity(RenderTileEntityEvent event) {
        if (!isVisible(event.getTileEntity().getBlockType()))
            event.setCanceled(true);
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        mc.renderGlobal.loadRenderers();

    }

    private boolean isVisible(Block block) {
        String name = getName(block);
        int index = Collections.binarySearch(blockNames, name);
        return index >= 0;
    }

    public static String getName(Block block) {
        return "" + Block.REGISTRY.getNameForObject(block);
    }

}
