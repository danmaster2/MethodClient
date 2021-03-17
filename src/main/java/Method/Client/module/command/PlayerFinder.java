package Method.Client.module.command;


import Method.Client.utils.system.Wrapper;
import Method.Client.utils.visual.ChatUtils;
import net.minecraft.client.network.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerFinder extends Command
{
	public PlayerFinder()
	{
		super("pfind");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			ArrayList<String> list = new ArrayList<String>();
			
			if(args[0].equalsIgnoreCase("all")) {
				for(NetworkPlayerInfo npi : Objects.requireNonNull(Wrapper.INSTANCE.mc().getConnection()).getPlayerInfoMap()) {
					list.add("\n" + npi.getGameProfile().getName());
				}
			}
			else
			if(args[0].equalsIgnoreCase("creatives")) {
				for(NetworkPlayerInfo npi : Objects.requireNonNull(Wrapper.INSTANCE.mc().getConnection()).getPlayerInfoMap()) {
					if(npi.getGameType().isCreative()) {
						list.add("\n" + npi.getGameProfile().getName());
					}
				}	
			}
			
			if(list.isEmpty()) {
				ChatUtils.error("List is empty.");
			}
			else
			{
				Wrapper.INSTANCE.copy(list.toString());
				ChatUtils.message("List copied to clipboard.");
			}
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}


	@Override
	public String getDescription()
	{
		return "Get list of players.";
	}

	@Override
	public String getSyntax()
	{
		return "pfind <all/creatives>";
	}
}