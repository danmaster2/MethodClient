package Method.Client.module.combat;


import Method.Client.managers.FriendManager;
import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import Method.Client.utils.system.Connection;
import Method.Client.utils.visual.ChatUtils;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.util.HashMap;

import static Method.Client.Main.setmgr;

public class TotemPop extends Module {


    public static HashMap<String, Integer> popList;

    Setting Friend = setmgr.add(Friend = new Setting("Friend", this, true));


    public TotemPop() {
        super("TotemPopCounter", Keyboard.KEY_NONE, Category.COMBAT, "Notifies when someone popped a totem");
    }


    @Override
    public boolean onPacket(Object packet, Connection.Side side) {
        if (packet instanceof SPacketEntityStatus) {
            final SPacketEntityStatus packet2 = (SPacketEntityStatus) packet;
            if (packet2.getOpCode() == 35) {
                final Entity entity = packet2.getEntity(mc.world);
                pop(entity);
            }

        }
        return true;
    }

    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        for (final EntityPlayer player : mc.world.playerEntities) {
            if (player.getHealth() <= 0.0f && popList.containsKey(player.getName())) {
                ChatUtils.message(ChatFormatting.RED + player.getName() + " died after popping " + ChatFormatting.GREEN + popList.get(player.getName()) + ChatFormatting.RED + " totems!");
                popList.remove(player.getName(), popList.get(player.getName()));
            }
        }


        super.onClientTick(event);
    }

    public void pop(Entity entity) {
        if (mc.player == null || mc.world == null)
            return;

        if (popList == null)
            popList = new HashMap<>();

        if (Friend.getValBoolean() || !FriendManager.isFriend(entity.getName()))
            if (popList.get(entity.getName()) == null) {
                popList.put(entity.getName(), 1);
                ChatUtils.message(ChatFormatting.RED + entity.getName() + " popped " + ChatFormatting.YELLOW + 1 + ChatFormatting.RED + " totem!");
            } else {
                Check(entity);
            }

    }

    private void Check(Entity entity) {
        if (popList.get(entity.getName()) != null) {
            int popCounter = popList.get(entity.getName());
            popList.put(entity.getName(), ++popCounter);
            ChatUtils.message(ChatFormatting.RED + entity.getName() + ChatFormatting.RED + " popped " + ChatFormatting.YELLOW + ++popCounter + ChatFormatting.RED + " totems!");
        }
    }

    public static int getpops(Entity entity) {
        if (popList.get(entity.getName()) != null) {
            return popList.get(entity.getName());
        }
        return 0;
    }

}
