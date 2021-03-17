package Method.Client.utils;

import Method.Client.Main;
import Method.Client.module.Module;
import Method.Client.module.ModuleManager;
import Method.Client.module.Onscreen.OnscreenGUI;
import Method.Client.module.combat.AntiBot;
import Method.Client.module.misc.ModSettings;
import Method.Client.module.render.NameTags;
import Method.Client.utils.Screens.NewScreen;
import Method.Client.utils.SeedViewer.WorldLoader;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.WorldDownloader;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static Method.Client.module.ModuleManager.onKeyPressed;

public class EventsHandler {
    public static boolean isInit = false;

    public boolean onPacket(Object packet, Connection.Side side) {
        boolean suc = true;
        try {
            for (Module m : ModuleManager.getEnabledmodules()) {
                if (!isInit)
                    suc &= m.onDisablePacket(packet, side);
                if (Wrapper.INSTANCE.world() == null) {
                    continue;
                }
                suc &= m.onPacket(packet, side);
            }

        } catch (RuntimeException ex) {
            cow("PacketError", ex);
        }
        return suc;
    }

    private void cow(String Evnt, RuntimeException ex) {
        if (!ModSettings.ShowErrors.getValBoolean())
            return;
        ex.printStackTrace();
        ChatUtils.error("RuntimeException: " + Evnt);
        ChatUtils.error(ex.toString());
        Wrapper.INSTANCE.copy(ex.toString());
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.world() == null) {
            AntiBot.bots.clear();
            isInit = false;
            try {
                NewScreen.onClientTick(event);
            } catch (RuntimeException ex) {
                cow("onClientTick", ex);
            }

            return;
        }
        try {
            if (!isInit) {
                new Connection(this);
                isInit = true;
            }
            WorldDownloader.Clienttick();
            ModuleManager.onClientTick(event);
        } catch (RuntimeException ex) {
            cow("onClientTick", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (!isInit) {
            return;
        }
        try {
            int key = Keyboard.getEventKey();
            if (Keyboard.getEventKeyState()) {
                onKeyPressed(key);
            }
        } catch (RuntimeException ex) {
            cow("onKeyInput", ex);
        }
    }


    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Main.MODID)) {
            new Thread(() -> {
                try {
                    while (Wrapper.mc.currentScreen != Main.ClickGui) {
                        Wrapper.mc.displayGuiScreen(Main.ClickGui);
                        Thread.sleep(25);
                        if (Wrapper.mc.currentScreen == Main.ClickGui) {
                            break;
                        }
                    }
                    Wrapper.mc.displayGuiScreen(Main.ClickGui);

                } catch (Exception ignored) {
                }
            }).start();

            Main.config.syncConfig();
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void ClientChatReceivedEvent(ClientChatReceivedEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.ClientChatReceivedEvent(event);
        } catch (RuntimeException ex) {
            cow("ClientChatReceivedEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.PlayerRespawnEvent(event);
        } catch (RuntimeException ex) {
            cow("PlayerRespawnEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void BackgroundDrawnEvent(GuiScreenEvent.BackgroundDrawnEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.BackgroundDrawnEvent(event);
        } catch (RuntimeException ex) {
            cow("BackgroundDrawnEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onRenderPre(event);
        } catch (RuntimeException ex) {
            cow("onRenderPre", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GetCollisionBoxesEvent(GetCollisionBoxesEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.GetCollisionBoxesEvent(event);
        } catch (RuntimeException ex) {
            cow("GetCollisionBoxesEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.DrawBlockHighlightEvent(event);
        } catch (RuntimeException ex) {
            cow("DrawBlockHighlightEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void ClientChatEvent(ClientChatEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.ClientChatEvent(event);
        } catch (RuntimeException ex) {
            cow("ClientChatEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.LivingDeathEvent(event);
        } catch (RuntimeException ex) {
            cow("LivingDeathEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GuiScreenEventInit(GuiScreenEvent.InitGuiEvent.Post event) {

        try {
            NewScreen.GuiScreenEventInit(event);
        } catch (RuntimeException ex) {
            cow("GuiScreenEventPre", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void DrawScreenEvent(GuiScreenEvent.DrawScreenEvent event) {

        try {
            NewScreen.DrawScreenEvent(event);
        } catch (RuntimeException ex) {
            cow("DrawScreenEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void PopulateChunkEvent(PopulateChunkEvent.Populate event) {
        try {
            WorldLoader.event(event);
        } catch (RuntimeException ex) {
            cow("PopulateChunkEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void DecorateBiomeEvent(DecorateBiomeEvent.Decorate event) {
        try {
            WorldLoader.DecorateBiomeEvent(event);
        } catch (RuntimeException ex) {
            cow("DecorateBiomeEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldUnload(WorldEvent.Unload event) {
        try {
            NewScreen.onWorldUnload(event);
            if (!isInit) {
                return;
            }
            ModuleManager.onWorldUnload(event);

        } catch (RuntimeException ex) {
            cow("onWorldUnload", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onWorldLoad(WorldEvent.Load event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onWorldLoad(event);
        } catch (RuntimeException ex) {
            cow("onWorldLoad", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GuiScreenEventPost(GuiScreenEvent.ActionPerformedEvent.Post event) {

        try {
            NewScreen.GuiScreenEventPost(event);
        } catch (RuntimeException ex) {
            cow("GuiScreenEventActionPerformedEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GuiScreenEventPre(GuiScreenEvent.ActionPerformedEvent.Pre event) {
        try {
            NewScreen.GuiScreenEventPre(event);
        } catch (RuntimeException ex) {
            cow("GuiScreenEventPre", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GuiOpen(GuiOpenEvent event) {
        try {
            NewScreen.GuiOpen(event);

            if (!isInit) {
                return;
            }
            ModuleManager.GuiOpen(event);
        } catch (RuntimeException ex) {
            cow("GuiOpen", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onCameraSetup(event);
        } catch (RuntimeException ex) {
            cow("onCameraSetup", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RenderBlockOverlayEvent(event);
        } catch (RuntimeException ex) {
            cow("RenderBlockOverlayEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RenderPlayerEvent(RenderPlayerEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RenderPlayerEvent(event);
        } catch (RuntimeException ex) {
            cow("RenderPlayerEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fogColor(EntityViewRenderEvent.FogColors event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.fogColor(event);
        } catch (RuntimeException ex) {
            cow("fogColor", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void fogDensity(EntityViewRenderEvent.FogDensity event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.fogDensity(event);
        } catch (RuntimeException ex) {
            cow("fogColor", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void renderNamePlate(RenderLivingEvent.Specials.Pre e) {
        try {
            if (NameTags.toggled) {
                if (e.getEntity() instanceof EntityPlayer)
                    e.setCanceled(true);
            }
        } catch (RuntimeException ex) {
            cow("renderNamePlate", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onTooltip(ItemTooltipEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.ItemTooltipEvent(event);
        } catch (RuntimeException ex) {
            cow("onCameraSetup", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RendergameOverlay(RenderGameOverlayEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RendergameOverlay(event);
        } catch (RuntimeException ex) {
            cow("onCameraSetup", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void BedSleep(PlayerSleepInBedEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.PlayerSleepInBedEvent(event);
        } catch (RuntimeException ex) {
            cow("onCameraSetup", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onItemPickup(EntityItemPickupEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onItemPickup(event);
        } catch (RuntimeException ex) {
            cow("onItemPickup", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onProjectileImpact(ProjectileImpactEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onProjectileImpact(event);
        } catch (RuntimeException ex) {
            cow("ProjectileImpact", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onAttackEntity(AttackEntityEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onAttackEntity(event);
        } catch (RuntimeException ex) {
            cow("onAttackEntity", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onPlayerTick(event);
        } catch (RuntimeException ex) {
            cow("onPlayerTick", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void Chunkevent(ChunkEvent.Unload event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.ChunkeventUNLOAD(event);
        } catch (RuntimeException ex) {
            cow("ChunkeventUnload", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void Chunkevent(ChunkEvent.Load event) {
        if (!isInit) {
            return;
        }
        try {
            WorldDownloader.ChunkeventLOAD(event);
            ModuleManager.ChunkeventLOAD(event);
        } catch (RuntimeException ex) {
            cow("ChunkeventLOAD", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onLivingUpdate(event);
        } catch (RuntimeException ex) {
            cow("onLivingUpdate", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        if (Wrapper.INSTANCE.player() == null || Wrapper.INSTANCE.world() == null || Wrapper.INSTANCE.mcSettings().hideGUI) {
            return;
        }
        try {
            ModuleManager.onRenderWorldLast(event);
        } catch (RuntimeException ex) {
            cow("RenderWorldLastEvent", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        if (!isInit) {
            return;
        }
        try {
            OnscreenGUI.onRenderGameOverlay(event);
            ModuleManager.onRenderGameOverlay(event);
        } catch (RuntimeException ex) {
            cow("RenderGameOverlayEvent.Text", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RenderGameOverLayPost(event);
        } catch (RuntimeException ex) {
            cow("RenderGameOverLayPost", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.FOVModifier(event);
        } catch (RuntimeException ex) {
            cow("FOVModifier", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void PlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        try {
            if (event.player != null) {
                ArrayList<IRecipe> recipes = Lists.newArrayList(CraftingManager.REGISTRY);
                recipes.removeIf((recipe) -> recipe.getRecipeOutput().isEmpty());
                recipes.removeIf((recipe) -> recipe.getIngredients().isEmpty());
                event.player.unlockRecipes(recipes);
            }
        } catch (RuntimeException ex) {
            cow("PlayerLoggedInEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onLeftClickBlock(event);
        } catch (RuntimeException ex) {
            cow("onLeftClickBlock", ex);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void WorldEvent(WorldEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.WorldEvent(event);
        } catch (RuntimeException ex) {
            cow("WorldEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.onRightClickBlock(event);
        } catch (RuntimeException ex) {
            cow("OnRightClickBlock", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postBackgroundTooltipRender(RenderTooltipEvent.PostBackground event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.postBackgroundTooltipRender(event);
        } catch (RuntimeException ex) {
            cow("postBackgroundTooltipRender", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void RendertooltipPre(RenderTooltipEvent.Pre event) {

        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RendertooltipPre(event);
        } catch (RuntimeException ex) {
            cow("RendertooltipPre", ex);
        }
    }

    @SubscribeEvent
    public void RenderTickEvent(TickEvent.RenderTickEvent event) {

        if (!isInit) {
            return;
        }
        try {
            ModuleManager.RenderTickEvent(event);
        } catch (RuntimeException ex) {
            cow("RenderTickEvent", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.postDrawScreen(event);
        } catch (RuntimeException ex) {
            cow("postDrawScreen", ex);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void GuiScreenEvent(GuiScreenEvent event) {
        if (!isInit) {
            return;
        }
        try {
            ModuleManager.GuiScreenEvent(event);
        } catch (RuntimeException ex) {
            cow("GuiScreenEvent", ex);
        }
    }

}
