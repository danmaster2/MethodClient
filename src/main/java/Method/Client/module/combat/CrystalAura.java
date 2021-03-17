package Method.Client.module.combat;

import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.TimerUtils;
import Method.Client.utils.Utils;
import Method.Client.utils.system.Connection;
import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.RenderUtils;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Method.Client.Main.setmgr;
import static Method.Client.utils.visual.RenderUtils.RenderBlock;
import static Method.Client.utils.visual.RenderUtils.Standardbb;

public class CrystalAura extends Module {

    public Setting range = setmgr.add(new Setting("Hit Range", this, 6, 0, 8, false));
    public Setting Explode = setmgr.add(new Setting("Explode", this, true));
    public Setting Placer = setmgr.add(new Setting("Place Crystals", this, true));
    public Setting Damage = setmgr.add(new Setting("Max Self Dmg", this, 8, 0, 20, false));
    public Setting OtherDamage = setmgr.add(new Setting("Min Enemy Dmg", this, 8, 0, 20, false));
    public Setting HitDelayBetween = setmgr.add(new Setting("Hit Delay", this, .2, 0, 1, false));
    public Setting PlaceDelayBetween = setmgr.add(new Setting("Place Delay", this, .2, 0, 1, false));
    public Setting FastSwitch = setmgr.add(new Setting("Fast Switch", this, true));
    public Setting Packetreach = setmgr.add(new Setting("Packet reach", this, false));
    public Setting Hand = setmgr.add(new Setting("Hand", this, "Mainhand", "Mainhand", "Offhand", "Both", "None"));
    public Setting SpoofAngle = setmgr.add(new Setting("Spoof Angle", this, true));
    public Setting OverlayColor = setmgr.add(new Setting("OverlayColor", this, 0, 1, 1, .62));
    public Setting Mode = setmgr.add(new Setting("Render", this, "Outline", BlockEspOptions()));
    public Setting LineWidth = setmgr.add(new Setting("LineWidth", this, 1, 0, 3, false));
    public Setting HoleJiggle = setmgr.add(new Setting("Hole Jiggle", this, true));
    public Setting MultiPlace = setmgr.add(new Setting("MultiPlace", this, true));
    public Setting SwordHit = setmgr.add(new Setting("SwordHit", this, false));

    private final TimerUtils attackTimer = new TimerUtils();
    private final TimerUtils placeTimer = new TimerUtils();
    private final List<PlaceLocation> placeLocations = new ArrayList<>();
    private static ExecutorService executor;
    public float[] Rots;

    public CrystalAura() {
        super("CrystalAura", Keyboard.KEY_NONE, Category.COMBAT, "CrystalAura");
    }

    @Override
    public void setup() {
        executor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        ArrayList<EntityEnderCrystal> Crystals = new ArrayList<>();

        for (Entity entity : mc.world.loadedEntityList)
            if (entity instanceof EntityEnderCrystal)
                Crystals.add((EntityEnderCrystal) entity);
        try {
            executor.execute(() -> {
                        for (Entity entity : mc.world.loadedEntityList)
                            if (entity instanceof EntityOtherPlayerMP) {
                                EntityOtherPlayerMP playerMP = (EntityOtherPlayerMP) entity;
                                if (FriendManager.isFriend(playerMP.getName()))
                                    continue;
                                if (Explode.getValBoolean()) {
                                    if (MultiPlace.getValBoolean())
                                        Collections.shuffle(Crystals);
                                    for (EntityEnderCrystal crystal : Crystals)
                                        if (playerMP.getDistance(crystal) < 12 && crystal.getDistance(mc.player) <= range.getValDouble())
                                            if ((Calculate_Damage(crystal.posX, crystal.posY, crystal.posZ, mc.player) < Damage.getValDouble()))
                                                if ((Calculate_Damage(crystal.posX, crystal.posY, crystal.posZ, playerMP) > OtherDamage.getValDouble()))
                                                    processAttack(crystal);

                                }
                                if (Placer.getValBoolean()) {
                                    ArrayList<BlockPos> posable = new ArrayList<>();
                                    if (mc.player.getDistance(playerMP) < 13)
                                        for (int x = (int) playerMP.posX - 8; x <= (int) playerMP.posX + 8; ++x)
                                            for (int z = (int) playerMP.posZ - 8; z <= (int) playerMP.posZ + 8; ++z)
                                                for (int y = (int) playerMP.posY - 4; y <= (int) playerMP.posY + 3; ++y) {
                                                    BlockPos blockPos = new BlockPos(x, y, z);
                                                    if (canPlaceCrystal(blockPos))
                                                        posable.add(blockPos);
                                                }
                                    double[] Best = new double[]{-1.0D, -1.0D};
                                    for (int i = 0; i < posable.size(); i++)
                                        if ((Calculate_Damage(posable.get(i).x + .5, posable.get(i).y + 1, posable.get(i).z + .5, mc.player) < Damage.getValDouble())) {
                                            double other = (Calculate_Damage(posable.get(i).x + .5, posable.get(i).y + 1, posable.get(i).z + .5, playerMP));
                                            if (other > OtherDamage.getValDouble() && other > Best[1]) {
                                                Best[0] = i;
                                                Best[1] = other;
                                            }
                                        }
                                    if (Best[0] != -1)
                                        placeCrystalOnBlock(posable.get((int) Best[0]), Best[1]);
                                }
                            }
                    }
            );
        } catch (ConcurrentModificationException ignored) {
        }
    }

    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (side == Connection.Side.IN)
            if (packet instanceof SPacketSpawnObject) {
                final SPacketSpawnObject packetSpawnObject = (SPacketSpawnObject) packet;
                if (packetSpawnObject.getType() == 51)
                    for (PlaceLocation placeLocation : placeLocations)
                        if (placeLocation.distanceSq(new BlockPos(packetSpawnObject.getX(), packetSpawnObject.getY(), packetSpawnObject.getZ())) < 2) {
                            placeLocation.Timeset = System.currentTimeMillis();
                            placeLocation.PacketConfirmed = true;
                        }
            }
        if (side == Connection.Side.OUT)
            if (SpoofAngle.getValBoolean() && Rots != null)
                if ((packet instanceof CPacketPlayer.Rotation || packet instanceof CPacketPlayer.PositionRotation)) {
                    final CPacketPlayer packet2 = (CPacketPlayer) packet;
                    packet2.yaw = Rots[0];
                    packet2.pitch = Rots[1];
                }
        return true;
    }

    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (PlaceLocation placeLocation : placeLocations)
            if (placeLocation.PacketConfirmed) {
                RenderBlock(Mode.getValString(), Standardbb(new BlockPos(placeLocation.x, placeLocation.y, placeLocation.z)), OverlayColor.getcolor(), LineWidth.getValDouble());
                RenderUtils.SimpleNametag(new Vec3d(placeLocation.x, placeLocation.y, placeLocation.z), new DecimalFormat("0.00").format(placeLocation.damage));
            }
        placeLocations.removeIf(placeLocation -> placeLocation.Timeset + 1000 < System.currentTimeMillis());
    }

    public boolean canPlaceCrystal(final BlockPos blockPos) {
        try {
            if (!(mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK))
                return false;
            if (!(mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.up(2)).getBlock() == Blocks.AIR))
                return false;
            for (final Object entity : mc.world.getEntitiesWithinAABB((Class<? extends Entity>) Entity.class, new AxisAlignedBB(blockPos).grow(0, 3, 0))) {
                if (!((entity instanceof EntityEnderCrystal && !MultiPlace.getValBoolean()) || entity instanceof EntityXPOrb || entity instanceof EntityItem))
                    return false;
            }
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    public void placeCrystalOnBlock(final BlockPos pos, double v) {
        if (placeTimer.isDelay((long) (PlaceDelayBetween.getValDouble() * 1000))) {
            RayTraceResult result = mc.world.rayTraceBlocks(Utils.getEyesPos(), new Vec3d(pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5));
            EnumFacing facing = (result == null || result.sideHit == null) ? EnumFacing.UP : result.sideHit;
            placeTimer.setLastMS();
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && (Hand.getValString().equalsIgnoreCase("Mainhand") && FastSwitch.getValBoolean()))
                if (find_in_hotbar(Items.END_CRYSTAL) != -1) {
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(find_in_hotbar(Items.END_CRYSTAL)));
                    mc.player.inventory.currentItem = find_in_hotbar(Items.END_CRYSTAL);
                }
            TryJiggle(pos);
            EnumHand hand = null;
            if (mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) && (Hand.getValString().equalsIgnoreCase("Offhand")
                    || (Hand.getValString().equalsIgnoreCase("Either"))))
                hand = EnumHand.OFF_HAND;
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && (Hand.getValString().equalsIgnoreCase("Mainhand")
                    || (Hand.getValString().equalsIgnoreCase("Either"))))
                hand = EnumHand.MAIN_HAND;
            if (hand != null) {
                if (SpoofAngle.getValBoolean()) {
                    Rots = Utils.getNeededRotations(new Vec3d(pos.x + .5, pos.y + .5, pos.z + .5), 0, 0);
                    Wrapper.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(Rots[0], Rots[1], Wrapper.mc.player.onGround));
                }
                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, hand, .5f, 0.5f, 0.5f));
                placeLocations.add(new PlaceLocation(pos.x, pos.y, pos.z, v));
            }
        }
    }

    private void TryJiggle(BlockPos pos) {
        if (HoleJiggle.getValBoolean())
            if (mc.player.getDistance(pos.x, pos.y, pos.z) > 5)
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(Math.floor(mc.player.posX) + .5 + (mc.player.posX < pos.x ? .2 : -.2), mc.player.posY, Math.floor(mc.player.posZ) + .5 + (mc.player.posZ < pos.z ? .2 : -.2), mc.player.onGround));
    }

    private int find_in_hotbar(Item item) {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY && stack.getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }

    static class PlaceLocation extends Vec3i {
        double damage;
        boolean PacketConfirmed;
        double Timeset;

        private PlaceLocation(double xIn, double yIn, double zIn, double v) {
            super(xIn, yIn, zIn);
            this.damage = v;
        }
    }

    public void processAttack(EntityEnderCrystal entity) {
        if (Packetreach.getValBoolean()) {
            double posX = entity.posX - 3.5 * Math.cos(Math.toRadians(Utils.getYaw(entity) + 90.0f));
            double posZ = entity.posZ - 3.5 * Math.sin(Math.toRadians(Utils.getYaw(entity) + 90.0f));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.PositionRotation(posX, entity.posY, posZ, Utils.getYaw(entity), Utils.getPitch(entity), mc.player.onGround));
            Wrapper.INSTANCE.sendPacket(new CPacketUseEntity(entity));
            Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.onGround));
        }
        if (attackTimer.isDelay((long) (HitDelayBetween.getValDouble() * 1000))) {
            attackTimer.setLastMS();
            TryJiggle(entity.getPosition());
            if (SpoofAngle.getValBoolean()) {
                Rots = Utils.getNeededRotations(entity.getPositionVector().add(0, .5, 0), 0, 0);
                Wrapper.INSTANCE.sendPacket(new CPacketPlayer.Rotation(Rots[0], Rots[1], mc.player.onGround));
            }
            if (SwordHit.getValBoolean() && find_in_hotbar(Items.DIAMOND_SWORD) != -1 && mc.player.isPotionActive(MobEffects.WEAKNESS)) {
                mc.player.connection.sendPacket(new CPacketHeldItemChange(find_in_hotbar(Items.DIAMOND_SWORD)));
                mc.player.inventory.currentItem = find_in_hotbar(Items.DIAMOND_SWORD);
            }
            if (!SwordHit.getValBoolean() || mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                Wrapper.INSTANCE.attack(entity);
                mc.player.swingArm(EnumHand.MAIN_HAND);
            }
        }
    }

    private static float get_damage_multiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0.0F : (diff == 2 ? 1.0F : (diff == 1 ? 0.5F : 1.5F)));
    }

    public static float Calculate_Damage(double posX, double posY, double posZ, Entity entity) {
        double distancedsize = entity.getDistance(posX, posY, posZ) / 12.0D;
        double blockDensity = entity.world.getBlockDensity(new Vec3d(posX, posY, posZ), entity.getEntityBoundingBox());
        double v = (1.0D - distancedsize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * 12.0D + 1.0D));
        if (entity instanceof EntityLivingBase) {
            return get_blast_reduction((EntityLivingBase) entity, get_damage_multiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }
        return (float) 1.0D;
    }

    public static float get_blast_reduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            damage = CombatRules.getDamageAfterAbsorb(damage, (ep.getTotalArmorValue()), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), DamageSource.causeExplosionDamage(explosion));
            damage *= 1.0F - MathHelper.clamp((float) k, 0.0F, 20.0F) / 25.0F;
            if (entity.isPotionActive(Objects.requireNonNull(MobEffects.RESISTANCE))) {
                damage -= damage / 4.0F;
            }
            return Math.max(damage, 0.0F);
        } else {
            return CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        }
    }
}
