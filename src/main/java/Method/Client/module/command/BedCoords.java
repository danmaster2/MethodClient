package Method.Client.module.command;

import Method.Client.utils.visual.ChatUtils;

public class BedCoords extends Command
{
	public BedCoords()
	{
		super("BedCoords");
	}



    @Override
	public void runCommand(String s, String[] args)
	{
		try
		{
				ChatUtils.message(mc.player.getBedLocation().toString());
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription()
	{
		return "BedCoords";
	}

	@Override
	public String getSyntax()
	{
		return "BedCoords ";
	}
}