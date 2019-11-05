package editmobdrops.commands;

import editmobdrops.handlers.ConfigHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.List;

public class ReloadConfigCommand extends CommandBase {
	public String getName() {
		return "editmobdrops";
	}

	public String getUsage(ICommandSender sender) {
		return "commands.editmobdrops.usage";
	}

	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1)
			throw new WrongUsageException(getUsage(sender));

		if (args[0].equals("reloadconfig")) {
			ConfigHandler.reloadConfig();
			getCommandSenderAsPlayer(sender).sendMessage(new TextComponentString("Config reloaded"));
		}
	}

	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		if (args.length == 1)
			return getListOfStringsMatchingLastWord(args, "reloadconfig");

		return super.getTabCompletions(server, sender, args, targetPos);
	}
}
