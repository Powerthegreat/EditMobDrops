package editmobdrops.commands;

import editmobdrops.handlers.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class ReloadConfigCommand extends CommandBase {
	public String getCommandName() {
		return "editmobdrops";
	}

	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "commands.editmobdrops.usage";
	}

	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length < 1)
			throw new WrongUsageException(getCommandUsage(sender));

		if (args[0].equals("reloadconfig")) {
			ConfigHandler.reloadConfig();
			getCommandSenderAsPlayer(sender).addChatMessage(new ChatComponentText("Config reloaded"));
		}
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "reloadconfig");

		return super.addTabCompletionOptions(sender, args);
	}
}
