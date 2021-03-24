package Method.Client.module;

import Method.Client.managers.FileManager;
import Method.Client.module.Onscreen.Display.Hole;
import Method.Client.module.Onscreen.Display.*;
import Method.Client.module.Profiles.Profiletem;
import Method.Client.module.combat.*;
import Method.Client.managers.CommandManager;
import Method.Client.module.misc.*;
import Method.Client.module.movement.*;
import Method.Client.module.player.*;
import Method.Client.module.render.*;
import Method.Client.utils.Patcher.Events.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ModuleManager {

    public static ArrayList<Module> modules = new ArrayList<>();
    public static ArrayList<Module> toggledModules = new ArrayList<>();
    public static ArrayList<Module> FileManagerLoader = new ArrayList<>();


    public ModuleManager() {
        /////////////////
        //// CHAT ///////
        /////////////////
        addModule(new Antispam());
        addModule(new ChatMutator());
        addModule(new TimeStamp());
        /////////////////
        //// Combat /////
        /////////////////
        addModule(new AimBot());
        addModule(new AntiBot());
        addModule(new HoleFill());
        addModule(new AutoArmor());
        addModule(new AntiCrystal());
        addModule(new Anchor());
        addModule(new AutoTotem());
        addModule(new AutoTrap());
        addModule(new Burrow());
        addModule(new Criticals());
        addModule(new CrystalAura());
        addModule(new BowMod());
        addModule(new InteractClick());
        addModule(new KillAura());
        addModule(new MoreKnockback());
        addModule(new Regen());
        addModule(new Refill());
        addModule(new Strafe());
        addModule(new Offhand());
        addModule(new SelfTrap());
        addModule(new Surround());
        addModule(new Trigger());
        addModule(new Webfill());
        addModule(new TotemPop());
        addModule(new Velocity());
        /////////////////
        //// MISC ///////
        /////////////////
        addModule(new AntiCrash());
        addModule(new AntiCheat());
        addModule(new AntiHurt());
        addModule(new Antipacket());
        addModule(new AntiHandShake());
        addModule(new AutoClicker());
        addModule(new AutoNametag());
        addModule(new Pickupmod());
        addModule(new Livestock());
        addModule(new CoordsFinder());
        addModule(new EchestBP());
        addModule(new FastSleep());
        addModule(new HitEffects());
        addModule(new Derp());
        addModule(new GuiModule());
        addModule(new InvMove());
        addModule(new ToolTipPlus());
        addModule(new NbtView());
        addModule(new NoSlowdown());
        addModule(new Ghost());
        addModule(new ModSettings());
        addModule(new VersionSpoofer());
        addModule(new PluginsGetter());
        addModule(new GuiPeek());
        addModule(new Shulkerspy());
        addModule(new ServerCrash());
        addModule(new VanishDetector());
        addModule(new QuickCraft());
        /////////////////
        //// Movement ///
        /////////////////
        addModule(new AntiFall());
        addModule(new AutoSwim());
        addModule(new AutoHold());
        addModule(new Bunnyhop());
        addModule(new Blink());
        addModule(new BoatFly());
        addModule(new ElytraFly());
        addModule(new Entityspeed());
        addModule(new EntityVanish());
        addModule(new FastFall());
        addModule(new Fly());
        addModule(new Glide());
        addModule(new Jump());
        addModule(new Jesus());
        addModule(new Levitate());
        addModule(new LiquidSpeed());
        addModule(new LongJump());
        addModule(new Parkour());
        addModule(new Phase());
        addModule(new SafeWalk());
        addModule(new Sneak());
        addModule(new Speed());
        addModule(new Spider());
        addModule(new Sprint());
        addModule(new Step());
        addModule(new Teleport());
        /////////////////
        //// Onscreen ///
        /////////////////
        addModule(new Armor());
        addModule(new Biome());
        addModule(new Coords());
        addModule(new ChunkSize());
        addModule(new Direction());
        addModule(new Durability());
        addModule(new EnabledMods());
        addModule(new Enemypos());
        addModule(new KeyStroke());
        addModule(new Fps());
        addModule(new CombatItems());
        addModule(new Angles());
        addModule(new Blockview());
        addModule(new Hole());
        addModule(new Hunger());
        addModule(new Inventory());
        addModule(new NetherCords());
        addModule(new Ping());
        addModule(new Player());
        addModule(new PlayerCount());
        addModule(new PlayerSpeed());
        addModule(new Potions());
        addModule(new Server());
        addModule(new ServerResponce());
        addModule(new Time());
        addModule(new Tps());
        ////////////////
        //// Player ////
        ////////////////
        addModule(new AntiAFK());
        addModule(new AutoFish());
        addModule(new AutoRemount());
        addModule(new AutoRespawn());
        addModule(new Autotool());
        addModule(new BuildHeight());
        addModule(new ChestStealer());
        addModule(new Disconnect());
        addModule(new FastBreak());
        addModule(new FastLadder());
        addModule(new FastPlace());
        addModule(new FireballReturn());
        addModule(new FoodMod());
        addModule(new FreeCam());
        addModule(new Reach());
        addModule(new God());
        addModule(new LiquidInteract());
        addModule(new NoServerChange());
        addModule(new Noswing());
        addModule(new Nowall());
        addModule(new Nuker());
        addModule(new PitchLock());
        addModule(new PortalMod());
        addModule(new Scaffold());
        addModule(new SchematicaNCP());
        addModule(new SkinBlink());
        addModule(new SmallShield());
        addModule(new Timer());
        addModule(new Xcarry());
        addModule(new YawLock());
        ////////////////
        //// Render ////
        ////////////////
        addModule(new BlockOverlay());
        addModule(new Breadcrumb());
        addModule(new BossStack());
        addModule(new BreakEsp());
        addModule(new ChestESP());
        addModule(new ChunkBorder());
        addModule(new ESP());
        addModule(new ExtraTab());
        addModule(new ArmorRender());
        addModule(new HoleEsp());
        addModule(new Fullbright());
        addModule(new F3Spoof());
        addModule(new ItemESP());
        addModule(new Visualrange());
        addModule(new MobOwner());
        addModule(new SeedViewer());
        addModule(new MotionBlur());
        addModule(new NewChunks());
        addModule(new NoEffect());
        addModule(new NoRender());
        addModule(new NoBlockLag());
        addModule(new NetherSky());
        addModule(new NameTags());
        addModule(new Search());
        addModule(new SkyColor());
        addModule(new Tracers());
        addModule(new Trail());
        addModule(new Trajectories());
        addModule(new WallHack());
        addModule(new Xray());
        addModule(new Fovmod());

        ////////////////
        //// PROFILES //
        ////////////////
        if (!(FileManager.SaveDir.exists())) {
            addModule(new Profiletem("Example"));
            addModule(new Profiletem("Example2"));
        } else FileManager.loadPROFILES();
    }

    public static void addModule(Module m) {
        modules.add(m);
    }

    public static ArrayList<Module> getModules() {
        return modules;
    }

    public static ArrayList<Module> getEnabledmodules() {
        return toggledModules;
    }


    public static void onKeyPressed(int key) {
        for (Module m : modules) {
            boolean NeedControl = false, NeedShift =false, NeedAlt = false;
            int Keydiff = 0;
            for (Integer mKey : m.getKeys()) {
                if (mKey == Keyboard.KEY_LCONTROL)
                    NeedControl = true;
                else if (mKey == Keyboard.KEY_LSHIFT)
                    NeedShift = true;
                else if (mKey == Keyboard.KEY_LMENU)
                    NeedAlt = true;
                else Keydiff = mKey;
            }
            if (key == Keydiff) {
                if (NeedControl)
                    if (!Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                        return;
                if (NeedShift)
                    if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                        return;
                if (NeedAlt)
                    if (!Keyboard.isKeyDown(Keyboard.KEY_LMENU))
                        return;
                m.toggle();

            }
        }
    }

    public static Module getModuleByName(String name) {
        return modules.stream().filter(module -> module.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }


    public static ArrayList<Module> getModulesInCategory(Category categoryIn) {
        ArrayList<Module> mods = new ArrayList<>();
        for (Module m : getSortedHacksabc(false)) {
            if (m.getCategory() == categoryIn)
                mods.add(m);
        }
        return mods;
    }

    public static void onWorldLoad(WorldEvent.Load event) {
        for (Module module : FileManagerLoader) {
            module.setToggled(true);
        }
        FileManagerLoader.clear();

        for (Module m : toggledModules) {
            m.onWorldLoad(event);
        }
    }

    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        for (Module m : toggledModules) {
            m.onCameraSetup(event);
        }
    }

    public static void onItemPickup(EntityItemPickupEvent event) {
        for (Module m : toggledModules) {
            m.onItemPickup(event);
        }
    }

    public static void onProjectileImpact(ProjectileImpactEvent event) {
        for (Module m : toggledModules) {
            m.onProjectileImpact(event);
        }
    }

    public static void onAttackEntity(AttackEntityEvent event) {
        for (Module m : toggledModules) {
            m.onAttackEntity(event);
        }

    }

    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        for (Module m : toggledModules) {
            m.onPlayerTick(event);

        }
    }

    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        for (Module m : toggledModules) {
            m.onLivingUpdate(event);
        }

    }

    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Module m : toggledModules) {
            m.onRenderWorldLast(event);
        }
    }

    public static void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
        for (Module m : toggledModules) {
            m.onRenderGameOverlay(event);
        }
    }

    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        for (Module m : toggledModules) {
            m.onLeftClickBlock(event);
        }
    }

    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        for (Module m : toggledModules) {
            m.onRightClickBlock(event);
        }
    }


    public static void onClientTick(TickEvent.ClientTickEvent event) {
        for (Module m : toggledModules) {
            m.onClientTick(event);
        }
    }

    public static void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
        for (Module m : toggledModules) {
            m.SetOpaqueCubeEvent(event);
        }
    }

    public static void onGetAmbientOcclusionLightValue(GetAmbientOcclusionLightValueEvent event) {
        for (Module m : toggledModules) {
            m.onGetAmbientOcclusionLightValue(event);
        }
    }

    public static void onShouldSideBeRendered(ShouldSideBeRenderedEvent event) {
        for (Module m : toggledModules) {
            m.onShouldSideBeRendered(event);
        }
    }

    public static void onRenderBlockModel(RenderBlockModelEvent event) {
        for (Module m : toggledModules) {
            m.onRenderBlockModel(event);
        }
    }

    public static void onRenderTileEntity(RenderTileEntityEvent event) {
        for (Module m : toggledModules) {
            m.onRenderTileEntity(event);
        }
    }

    public static void EventCanCollide(EventCanCollide event) {
        for (Module m : toggledModules) {
            m.EventCanCollide(event);
        }
    }

    public static void ItemTooltipEvent(ItemTooltipEvent event) {
        for (Module m : toggledModules) {
            m.ItemTooltipEvent(event);
        }
    }


    public static void postBackgroundTooltipRender(RenderTooltipEvent.PostBackground event) {
        for (Module m : toggledModules) {
            m.postBackgroundTooltipRender(event);
        }
    }

    public static void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
        for (Module m : toggledModules) {
            m.postDrawScreen(event);
        }
    }

    public static void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
        for (Module m : toggledModules) {
            m.RenderGameOverLayPost(event);
        }
    }

    public static void onPlayerMove(PlayerMoveEvent event) {
        for (Module m : toggledModules) {
            m.onPlayerMove(event);
        }
    }

    public static void onPlayerJump(EntityPlayerJumpEvent event) {
        for (Module m : toggledModules) {
            m.onPlayerJump(event);
        }
    }

    public static void RendergameOverlay(RenderGameOverlayEvent event) {
        for (Module m : toggledModules) {
            m.RendergameOverlay(event);
        }
    }

    public static void ChunkeventUNLOAD(ChunkEvent.Unload event) {
        for (Module m : toggledModules) {
            m.ChunkeventUNLOAD(event);
        }
    }

    public static void ChunkeventLOAD(ChunkEvent.Load event) {
        for (Module m : toggledModules) {
            m.ChunkeventLOAD(event);
        }
    }

    public static void fogColor(EntityViewRenderEvent.FogColors event) {
        for (Module m : toggledModules) {
            m.fogColor(event);
        }
    }

    public static void fogDensity(EntityViewRenderEvent.FogDensity event) {

        for (Module m : toggledModules) {
            m.fogDensity(event);
        }
    }

    public static void DamageBlock(PlayerDamageBlockEvent event) {
        for (Module m : toggledModules) {
            m.DamageBlock(event);
        }
    }

    public static void PlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
        for (Module m : toggledModules) {
            m.PlayerSleepInBedEvent(event);
        }
    }

    public static void onGetLiquidCollisionBox(GetLiquidCollisionBoxEvent event) {
        for (Module m : toggledModules) {
            m.GetLiquidCollisionBoxEvent(event);
        }
    }

    public static void EventBookPage(EventBookPage event) {
        for (Module m : toggledModules) {
            m.EventBookPage(event);
        }
    }

    public static void ClientChatReceivedEvent(ClientChatReceivedEvent event) {
        for (Module m : toggledModules) {
            m.ClientChatReceivedEvent(event);
        }
    }

    public static void LivingDeathEvent(LivingDeathEvent event) {
        for (Module m : toggledModules) {
            m.LivingDeathEvent(event);
        }
    }

    public static void WorldEvent(WorldEvent event) {
        for (Module m : toggledModules) {
            m.WorldEvent(event);
        }
    }

    public static void GuiScreenEvent(GuiScreenEvent event) {
        for (Module m : toggledModules) {
            m.GuiScreenEvent(event);
        }
    }

    public static void RendertooltipPre(RenderTooltipEvent.Pre event) {
        for (Module m : toggledModules) {
            m.RendertooltipPre(event);
        }
    }

    public static void RenderPlayerEvent(RenderPlayerEvent event) {
        for (Module m : toggledModules) {
            m.RenderPlayerEvent(event);
        }
    }

    public static void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
        for (Module m : toggledModules) {
            m.RenderBlockOverlayEvent(event);
        }
    }

    public static void GetCollisionBoxesEvent(GetCollisionBoxesEvent event) {
        for (Module m : toggledModules) {
            m.GetCollisionBoxesEvent(event);
        }
    }

    public static void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
        for (Module m : toggledModules) {
            m.FOVModifier(event);
        }
    }

    public static void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
        for (Module m : toggledModules) {
            m.DrawBlockHighlightEvent(event);
        }
    }

    public static void PreMotionEvent(PreMotionEvent event) {
        for (Module m : toggledModules) {
            m.PreMotionEvent(event);
        }
    }

    public static void PostMotionEvent(PostMotionEvent event) {
        for (Module m : toggledModules) {
            m.PostMotionEvent(event);
        }
    }

    public static void BackgroundDrawnEvent(GuiScreenEvent.BackgroundDrawnEvent event) {
        for (Module m : toggledModules) {
            m.BackgroundDrawnEvent(event);
        }
    }

    public static void RenderTickEvent(TickEvent.RenderTickEvent event) {
        for (Module m : toggledModules) {
            m.RenderTickEvent(event);
        }
    }

    public static void onRenderPre(RenderGameOverlayEvent.Pre event) {
        for (Module m : toggledModules) {
            m.onRenderPre(event);
        }
    }


    public static void onWorldUnload(WorldEvent.Unload event) {
        for (Module m : toggledModules) {
            m.onWorldUnload(event);
        }
    }

    public static void GuiOpen(GuiOpenEvent event) {
        for (Module m : toggledModules) {
            m.GuiOpen(event);
        }
    }

    public static void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        for (Module m : toggledModules) {
            m.PlayerRespawnEvent(event);
        }
    }

    public static void ClientChatEvent(ClientChatEvent event) {
        if (event.getMessage().startsWith(String.valueOf(CommandManager.cmdPrefix))) {
            CommandManager.getInstance().runCommands(CommandManager.cmdPrefix + event.getMessage().substring(1));
            event.setCanceled(true);
            event.setMessage(null);
        }
        for (Module m : toggledModules) {
            m.ClientChatEvent(event);
        }
    }

    public static ArrayList<Module> getSortedMods(boolean reverse, boolean enabled, boolean visible) {
        final ArrayList<Module> list = new ArrayList<>();
        final ArrayList<String> listofmods = new ArrayList<>();
        if (!enabled)
            for (final Module mod : getModules()) {
                if (visible && mod.visible)
                    listofmods.add(mod.getName());
                if (!visible)
                    listofmods.add(mod.getName());

            }
        if (enabled)
            for (final Module mod : getEnabledmodules()) {
                if (visible && mod.visible)
                    listofmods.add(mod.getName());
                if (!visible)
                    listofmods.add(mod.getName());
            }
        listofmods.sort(Comparator.comparing(String::length));
        if (reverse)
            Collections.reverse(listofmods);
        for (final String s : listofmods) {
            list.add(ModuleManager.getModuleByName(s));
        }
        return list;

    }

    public static ArrayList<Module> getSortedHacksabc(boolean reverse) {
        final ArrayList<Module> list = new ArrayList<>();
        final ArrayList<String> listofcountries = new ArrayList<>();
        for (final Module module : getModules()) {
            listofcountries.add(module.getName());
        }
        java.util.Collections.sort(listofcountries);
        if (reverse)
            Collections.reverse(listofcountries);
        for (final String s : listofcountries) {
            list.add(ModuleManager.getModuleByName(s));
        }
        return list;

    }


}
