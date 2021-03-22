package Method.Client.module;

import Method.Client.managers.Setting;
import Method.Client.utils.Patcher.Events.*;
import Method.Client.utils.system.Connection;
import net.minecraft.client.Minecraft;
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

public class Module {
    protected static Minecraft mc = Minecraft.getMinecraft();
    protected static Minecraft MC = Minecraft.getMinecraft();


    public ArrayList<Module> StoredModules = new ArrayList<>();

    public ArrayList<Setting> StoredSettings = new ArrayList<>();
    public ArrayList<Integer> Keys;

    private boolean toggled;
    public boolean visible = true;
    private String name;
    private String displayName;
    private final String tooltip;
    private Category category;

    public Module(String name, int key, Category category, String tooltip) {
        this.name = name;
        this.tooltip = tooltip;
        this.Keys = new ArrayList<>();
        this.Keys.add(key);
        this.category = category;
        toggled = false;
        setup();
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public boolean onPacket(Object packet, Connection.Side side) {
        return true;
    }

    public boolean onDisablePacket(Object packet, Connection.Side side) {
        return true;
    }

    public void onToggle() {
    }

    public void toggle() {
        toggled = !toggled;
        onToggle();
        if (toggled) {
            if (!ModuleManager.toggledModules.contains(this))
                ModuleManager.toggledModules.add(this);
            onEnable();
        } else {
            onDisable();
            ModuleManager.toggledModules.remove(this);
        }
    }

    public ArrayList<String> fontoptions() {
        ArrayList<String> Fontoptions = new ArrayList<>();
        Fontoptions.add("Arial");
        Fontoptions.add("Impact");
        Fontoptions.add("Times");
        Fontoptions.add("MC");
        return Fontoptions;
    }

    public ArrayList<String> BlockEspOptions() {
        ArrayList<String> BlockOptions = new ArrayList<>();
        BlockOptions.add("Outline");
        BlockOptions.add("Full");
        BlockOptions.add("Flat");
        BlockOptions.add("FlatOutline");
        BlockOptions.add("Beacon");
        BlockOptions.add("Xspot");
        BlockOptions.add("Tracer");
        BlockOptions.add("Shape");
        BlockOptions.add("None");
        return BlockOptions;
    }

    public boolean isToggled() {
        return toggled;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getKeys() {
        return Keys;
    }

    public void setKey(int Key,boolean Control,boolean Shift,boolean Alt) {
        this.Keys = new ArrayList<>();
        if (Control)
            this.Keys.add(Keyboard.KEY_LCONTROL);
        if (Shift)
            this.Keys.add(Keyboard.KEY_LSHIFT);
        if (Alt)
            this.Keys.add(Keyboard.KEY_LMENU);

        this.Keys.add(Key);
    }


    public void setKeys(String keys) {
        if (keys != null) {
            keys = keys.replaceAll("\\[", "");
            keys = keys.replaceAll("]", "");
            keys = keys.replaceAll(" ", "");
            String[] tryit = keys.split(",");
            ArrayList<Integer> key = new ArrayList<>();
            for (String s : tryit) {
                key.add(Integer.valueOf(s));
            }
            this.Keys = key;
        }
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setToggled(boolean toggled) {
        this.toggled = toggled;
        onToggle();
        if (toggled) {
            if (!ModuleManager.toggledModules.contains(this))
                ModuleManager.toggledModules.add(this);
            onEnable();
        } else {
            onDisable();
            ModuleManager.toggledModules.remove(this);
        }
    }

    public String getDisplayName() {
        return displayName == null ? name : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setup() {
    }

    public ArrayList<Module> getStoredModules() {
        return StoredModules;
    }

    public void setStoredModules(ArrayList<Module> storedModules) {
        StoredModules = storedModules;
    }

    public ArrayList<Setting> getStoredSettings() {
        return StoredSettings;
    }

    public void setStoredSettings(ArrayList<Setting> storedSettings) {
        this.StoredSettings = storedSettings;
    }


    public void onClientTick(TickEvent.ClientTickEvent event) {
    }

    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
    }

    public void onItemPickup(EntityItemPickupEvent event) {
    }

    public void onProjectileImpact(ProjectileImpactEvent event) {
    }

    public void onAttackEntity(AttackEntityEvent event) {
    }

    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
    }

    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
    }

    public void onRenderWorldLast(RenderWorldLastEvent event) {
    }

    public void onRenderGameOverlay(RenderGameOverlayEvent.Text event) {
    }

    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
    }

    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
    }

    public void SetOpaqueCubeEvent(SetOpaqueCubeEvent event) {
    }

    public void onGetAmbientOcclusionLightValue(GetAmbientOcclusionLightValueEvent event) {
    }

    public void onShouldSideBeRendered(ShouldSideBeRenderedEvent event) {
    }

    public void onRenderBlockModel(RenderBlockModelEvent event) {
    }

    public void onRenderTileEntity(RenderTileEntityEvent event) {
    }

    public void EventCanCollide(EventCanCollide event) {
    }

    public void ItemTooltipEvent(ItemTooltipEvent event) {
    }

    public void postBackgroundTooltipRender(RenderTooltipEvent.PostBackground event) {
    }

    public void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
    }

    public void RenderGameOverLayPost(RenderGameOverlayEvent.Post event) {
    }

    public void onPlayerMove(PlayerMoveEvent event) {
    }

    public void onPlayerJump(EntityPlayerJumpEvent event) {
    }

    public void RendergameOverlay(RenderGameOverlayEvent event) {
    }

    public void ChunkeventUNLOAD(ChunkEvent.Unload event) {
    }

    public void ChunkeventLOAD(ChunkEvent.Load event) {
    }

    public void fogColor(EntityViewRenderEvent.FogColors event) {
    }

    public void fogDensity(EntityViewRenderEvent.FogDensity event) {
    }

    public void DamageBlock(PlayerDamageBlockEvent event) {
    }

    public void PlayerSleepInBedEvent(PlayerSleepInBedEvent event) {
    }

    public void GetLiquidCollisionBoxEvent(GetLiquidCollisionBoxEvent event) {
    }

    public void EventBookPage(EventBookPage event) {
    }

    public void ClientChatReceivedEvent(ClientChatReceivedEvent event) {
    }

    public void ClientChatEvent(ClientChatEvent event) {
    }

    public void LivingDeathEvent(LivingDeathEvent event) {
    }

    public void WorldEvent(WorldEvent event) {
    }

    public void GuiScreenEvent(GuiScreenEvent event) {
    }

    public void GetCollisionBoxesEvent(GetCollisionBoxesEvent event) {
    }

    public void RendertooltipPre(RenderTooltipEvent.Pre event) {
    }

    public void RenderPlayerEvent(RenderPlayerEvent event) {
    }

    public void RenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
    }

    public void FOVModifier(EntityViewRenderEvent.FOVModifier event) {
    }

    public void DrawBlockHighlightEvent(DrawBlockHighlightEvent event) {
    }

    public void PostMotionEvent(PostMotionEvent event) {
    }

    public void PreMotionEvent(PreMotionEvent event) {
    }

    public void BackgroundDrawnEvent(GuiScreenEvent.BackgroundDrawnEvent event) {
    }

    public void RenderTickEvent(TickEvent.RenderTickEvent event) {
    }

    public void onRenderPre(RenderGameOverlayEvent.Pre event) {
    }

    public void setsave() {
    }

    public void setdelete() {
    }

    public void onWorldLoad(WorldEvent.Load event) {
    }

    public void onWorldUnload(WorldEvent.Unload event) {
    }

    public void GuiOpen(GuiOpenEvent event) {
    }

    public void PlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
    }
}