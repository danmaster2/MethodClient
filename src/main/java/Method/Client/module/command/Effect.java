package Method.Client.module.command;


import Method.Client.utils.visual.ChatUtils;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Objects;

public class Effect extends Command
{
	public Effect()
	{
		super("effect");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			if(args[0].equalsIgnoreCase("add")) {
				int id = Integer.parseInt(args[1]);
				int duration = Integer.parseInt(args[2]);
				int amplifier = Integer.parseInt(args[3]);
				if(Potion.getPotionById(id) == null) {
					ChatUtils.error("Potion is null");
					return;
				}
				addEffect(id, duration, amplifier);
			}
			else
			if(args[0].equalsIgnoreCase("remove")) {
				int id = Integer.parseInt(args[1]);
				if(Potion.getPotionById(id) == null) {
					ChatUtils.error("Potion is null");
					return;
				}
				removeEffect(id);
			}
			else
			if(args[0].equalsIgnoreCase("clear")) {
				clearEffects();
			}
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}
	
	void addEffect(int id, int duration, int amplifier) {
		mc.player.addPotionEffect(new PotionEffect(Objects.requireNonNull(Potion.getPotionById(id)), duration, amplifier));
	}
	
	void removeEffect(int id) {
		mc.player.removePotionEffect(Objects.requireNonNull(Potion.getPotionById(id)));
	}
	
	void clearEffects() {
		for(PotionEffect effect : mc.player.getActivePotionEffects()) {
			mc.player.removePotionEffect(effect.getPotion());
		}
	}


	@Override
	public String getDescription()
	{
		return "Effect manager.";
	}

	@Override
	public String getSyntax()
	{
		return "effect <add/remove/clear> <id> <duration> <amplifier>";
	}
}