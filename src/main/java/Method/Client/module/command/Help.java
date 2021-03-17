package Method.Client.module.command;


import Method.Client.utils.visual.ChatUtils;

public class Help extends Command
{
	public Help()
	{
		super("help");
	}

	@Override
	public void runCommand(String s, String[] args)
	{
		for(Command cmd: CommandManager.commands)
		{
			if(cmd != this) {
				ChatUtils.message(cmd.getSyntax().replace("<", "<\2479").replace(">", "\2477>") + " - " + cmd.getDescription());
			}
		}
	}

	@Override
	public String getDescription()
	{
		return "Lists all commands.";
	}

	@Override
	public String getSyntax()
	{
		return "help";
	}
}