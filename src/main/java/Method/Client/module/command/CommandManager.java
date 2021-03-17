package Method.Client.module.command;


import Method.Client.utils.visual.ChatUtils;

import java.util.ArrayList;

public class CommandManager {
    public static ArrayList<Command> commands = new ArrayList<Command>();
    private volatile static CommandManager instance;

    public static char cmdPrefix = '.';

    public CommandManager() {
        addCommands();
    }

    public void addCommands() {
        commands.add(new Help());
        commands.add(new VClip());
        commands.add(new OpenFolder());
        commands.add(new Login());
        commands.add(new UsernameHistory());
        commands.add(new Say());
        commands.add(new OpenGui());
        commands.add(new Effect());
        commands.add(new PlayerFinder());
        commands.add(new WorldSeed());
        commands.add(new Friend());
        commands.add(new ClearChat());
        commands.add(new OpenDir());
        commands.add(new Author());
        commands.add(new ResetGui());
        commands.add(new Yaw());
        commands.add(new Pitch());
        commands.add(new BedCoords());
        commands.add(new Drop());
        commands.add(new Peek());
        commands.add(new Vanish());
        commands.add(new StackSize());
        commands.add(new Hclip());
        commands.add(new Reset());
        commands.add(new Give());
        commands.add(new Hat());
        commands.add(new Head());
        commands.add(new Lore());
        commands.add(new Nbt());
        commands.add(new Rename());
        commands.add(new Repair());
        commands.add(new Tp());
        commands.add(new Profile());

    }

    public void runCommands(String s) {
        String readString = s.trim().substring(Character.toString(cmdPrefix).length()).trim();
        boolean commandResolved = false;
        boolean hasArgs = readString.trim().contains(" ");
        String commandName = hasArgs ? readString.split(" ")[0] : readString.trim();
        String[] args = hasArgs ? readString.substring(commandName.length()).trim().split(" ") : new String[0];

        for (Command command : commands) {
            if (command.getCommand().trim().equalsIgnoreCase(commandName.trim())) {
                command.runCommand(readString, args);
                commandResolved = true;
                break;
            }
        }

        if (!commandResolved) {
            ChatUtils.error("Cannot resolve internal command: \u00a7c" + commandName);
        }
    }


    public static CommandManager getInstance() {
        if (instance == null)
            instance = new CommandManager();
        return instance;
    }
}