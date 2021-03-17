package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;

import java.util.UUID;

public class FakePlayer extends Command {
    public FakePlayer() {
        super("FakePlayer");
    }

    @Override
    public void runCommand(String s, String[] args) {
        try {
            EntityOtherPlayerMP fake = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.randomUUID(), args[0]));
            fake.setPosition(mc.player.posX,mc.player.posY,mc.player.posZ);
            mc.world.loadedEntityList.add(fake);

            ChatUtils.message("Added Fake Player ");
        } catch (Exception e) {
            ChatUtils.error("Usage: " + getSyntax());
        }
    }

    @Override
    public String getDescription() {
        return "FakePlayer Spawner";
    }

    @Override
    public String getSyntax() {
        return "FakePlayer <Name>";
    }
}