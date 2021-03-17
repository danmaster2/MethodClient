package Method.Client.module.render;


import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.PlayerIdentity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.LinkedHashMap;

import static Method.Client.Main.setmgr;

public class MobOwner extends Module {

    public MobOwner() {
        super("MobOwner", Keyboard.KEY_NONE, Category.RENDER, "MobOwner");
    }

    Setting Speedh = setmgr.add(new Setting("Speed horse", this, false));
    Setting Jumph = setmgr.add(new Setting("Jump Horse", this, false));
    Setting Hpm = setmgr.add(new Setting("Hp", this, false));


    public static LinkedHashMap<String, PlayerIdentity> identityCacheMap = new LinkedHashMap<>();

    public static PlayerIdentity getPlayerIdentity(String UUID) {
        if (identityCacheMap.containsKey(UUID)) {
            return identityCacheMap.get(UUID);
        }
        return new PlayerIdentity(UUID);
    }


    @Override
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Entity entity : mc.world.getLoadedEntityList()) {
            if (entity instanceof EntityTameable) {
                EntityTameable tameableEntity = (EntityTameable) entity;
                if (tameableEntity.isTamed() && tameableEntity.getOwnerId() != null) {
                    tameableEntity.setAlwaysRenderNameTag(true);
                    String Hp = Hpm.getValBoolean() ? "\n" + ((EntityTameable) entity).getHealth() : "";
                    PlayerIdentity identity = getPlayerIdentity(tameableEntity.getOwnerId().toString());
                    tameableEntity.setCustomNameTag("Owned by " + identity.getDisplayName() + Hp);
                }
            }
            if (entity instanceof AbstractHorse) {
                AbstractHorse tameableEntity = (AbstractHorse) entity;
                if (tameableEntity.isTame() && tameableEntity.getOwnerUniqueId() != null) {
                    String Speed = Speedh.getValBoolean() ? " Speed: " + ((AbstractHorse) entity).getAIMoveSpeed() * 43.17 : "";
                    String Hp = Hpm.getValBoolean() ? " HP: " + ((AbstractHorse) entity).getHealth() : "";
                    String Jump = Jumph.getValBoolean() ? " Jump: " + (-0.1817584952
                            * Math.pow(((AbstractHorse) entity).getHorseJumpStrength(), 3) + 3.689713992
                            * Math.pow(((AbstractHorse) entity).getHorseJumpStrength(), 2) + 2.128599134
                            * ((AbstractHorse) entity).getHorseJumpStrength() - 0.343930367) : "";
                    tameableEntity.setAlwaysRenderNameTag(true);
                    PlayerIdentity identity = getPlayerIdentity(tameableEntity.getOwnerUniqueId().toString());
                    tameableEntity.setCustomNameTag("Owned by " + identity.getDisplayName() + Speed + Jump + Hp);
                }
            }
        }
        super.onRenderWorldLast(event);
    }


}
