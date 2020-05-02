package xyz.olivermartin.multichat.local.common.commands;

import xyz.olivermartin.multichat.local.common.MultiChatLocal;

public abstract class ProxyExecuteCommand {

	protected boolean executeProxyExecuteCommand(MultiChatLocalCommandSender sender, String[] args) {

		if (args.length < 1) {
			return false;
		}

		boolean playerFlag = false;
		String player = ".*";

		// Handle flags
		int index = 0;

		while (index < args.length) {

			if (args[index].equalsIgnoreCase("-p")) {
				if (index+1 < args.length) {
					playerFlag = true;
					player = args[index+1];
				}
			} else {
				break;
			}

			index = index+2;

		}

		if (index >= args.length) {
			return false; // Show usage
		}

		String message = "";
		for (String arg : args) {
			if (index > 0) {
				index--;
			} else {
				message = message + arg + " ";
			}
		}

		if (playerFlag) {

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendProxyExecutePlayerMessage(message, player);

		} else {

			MultiChatLocal.getInstance().getProxyCommunicationManager().sendProxyExecuteMessage(message);

		}

		sender.sendGoodMessage("SENT COMMAND TO PROXY SERVER");

		return true;

	}

}
