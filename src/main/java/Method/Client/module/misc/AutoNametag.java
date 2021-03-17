package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.Objects;

import static Method.Client.Main.setmgr;

public class AutoNametag extends Module {

    Setting Radius = setmgr.add( new Setting("range", this, 4, 0, 10, true));
    Setting ReplaceOldNames = setmgr.add( new Setting("ReplaceOldNames", this, true));
    Setting AutoSwitch = setmgr.add( new Setting("AutoSwitch", this, true));
    Setting WithersOnly = setmgr.add( new Setting("WithersOnly ", this, true));


    public AutoNametag() {
        super("AutoNametag", Keyboard.KEY_NONE, Category.MISC, "AutoNametag");
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.currentScreen != null)
            return;

        if (!(mc.player.getHeldItemMainhand().getItem() instanceof ItemNameTag)) {
            int i1 = -1;

            if (AutoSwitch.getValBoolean()) {
                for (int i = 0; i < 9; ++i) {
                    ItemStack item = mc.player.inventory.getStackInSlot(i);

                    if (item.isEmpty())
                        continue;

                    if (item.getItem() instanceof ItemNameTag) {
                        if (!item.hasDisplayName())
                            continue;

                        i1 = i;
                        mc.player.inventory.currentItem = i1;
                        mc.playerController.updateController();
                        break;
                    }
                }
            }

            if (i1 == -1)
                return;
        }

        ItemStack name = mc.player.getHeldItemMainhand();

        if (!name.hasDisplayName())
            return;

        EntityLivingBase l_Entity = mc.world.loadedEntityList.stream()
                .filter(p_Entity -> IsValidEntity(p_Entity, name.getDisplayName()))
                .map(p_Entity -> (EntityLivingBase) p_Entity)
                .min(Comparator.comparing(p_Entity -> mc.player.getDistance(p_Entity)))
                .orElse(null);

        if (l_Entity != null) {

            final double[] lPos = calculateLookAt(
                    l_Entity.posX,
                    l_Entity.posY,
                    l_Entity.posZ,
                    mc.player);

            ChatUtils.message(String.format("Gave %s the nametag of %s", l_Entity.getName(), name.getDisplayName()));

            mc.player.rotationYawHead = (float) lPos[0];

            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(l_Entity, EnumHand.MAIN_HAND));

        }
    }

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        // to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]
                {yaw, pitch};
    }


    private boolean IsValidEntity(Entity entity, final String pName) {
        if (!(entity instanceof EntityLivingBase)) return false;
        if (entity.getDistance(mc.player) > Radius.getValDouble()) return false;
        if (entity instanceof EntityPlayer) return false;
        if (!entity.getCustomNameTag().isEmpty() && !ReplaceOldNames.getValBoolean()) return false;

        if (ReplaceOldNames.getValBoolean())
            if (!entity.getCustomNameTag().isEmpty() && entity.getName().equals(pName)) return false;

        return !WithersOnly.getValBoolean() || entity instanceof EntityWither;
    }


}
