package Method.Client.module.command;

import Method.Client.managers.FileManager;
import Method.Client.utils.visual.ChatUtils;

import java.awt.*;

public class OpenDir extends Command
{
	public OpenDir()
	{
		super("Opendir");
	}



    @Override
	public void runCommand(String s, String[] args)
	{
		try
		{
			Desktop.getDesktop().open(FileManager.SaveDir);
		}
		catch(Exception e)
		{
			ChatUtils.error("Usage: " + getSyntax());
		}
	}

	@Override
	public String getDescription()
	{
		return "Open Dir";
	}

	@Override
	public String getSyntax()
	{
		return "OpenDir";
	}
}