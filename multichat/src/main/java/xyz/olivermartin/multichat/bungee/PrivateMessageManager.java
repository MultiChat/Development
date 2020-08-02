package xyz.olivermartin.multichat.bungee;

import java.util.UUID;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.ProxyUtils;
import xyz.olivermartin.multichat.proxy.common.config.ConfigFile;
import xyz.olivermartin.multichat.proxy.common.config.ConfigValues;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

public class PrivateMessageManager {

	private static PrivateMessageManager instance;

	public static PrivateMessageManager getInstance() {
		return instance;
	}

	static {
		instance = new PrivateMessageManager();
	}

	/* END STATIC */

	private ChatManipulation chatfix;

	private PrivateMessageManager() { 
		chatfix = new ChatManipulation();
	}

	private String getOutFormat() {
		return ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString(ConfigValues.Config.PM_OUT_FORMAT);
	}

	private String getInFormat() {
		return ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString(ConfigValues.Config.PM_IN_FORMAT);
	}

	private String getSpyFormat() {
		return ConfigManager.getInstance().getHandler(ConfigFile.CONFIG).getConfig().getString(ConfigValues.Config.PM_SPY_FORMAT);
	}

	private void displayMessage(ProxiedPlayer player, String message) {

		if (MultiChat.legacyServers.contains(player.getServer().getInfo().getName())) {
			player.sendMessage(ProxyJsonUtils.parseMultiple(MultiChatUtil.approximateHexCodes(message)));
		} else {
			player.sendMessage(ProxyJsonUtils.parseMultiple(message));
		}

	}

	private void displayConsoleMessage(String message) {
		ProxyServer.getInstance().getConsole().sendMessage(ProxyJsonUtils.parseMultiple(MultiChatUtil.approximateHexCodes(message)));
	}

	private void updateLastMessage(UUID sender, UUID target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ds.getLastMsg().put(sender, target);
		ds.getLastMsg().put(target, sender);

	}

	public void sendMessage(String message, ProxiedPlayer sender, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER)
		String finalmessage = chatfix.replaceMsgVars(getOutFormat(), message, sender, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayMessage(sender, finalmessage);

		// Replace placeholders (TARGET)
		finalmessage = chatfix.replaceMsgVars(getInFormat(), message, sender, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayMessage(target, finalmessage);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgVars(getSpyFormat(), message, sender, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != sender.getUniqueId())
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")
							|| target.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage);

			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(sender.getUniqueId(), target.getUniqueId());

		ConsoleManager.logSocialSpy(sender.getName(), target.getName(), message);

	}

	public void sendMessageConsoleTarget(String message, ProxiedPlayer sender) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER)
		String finalmessage = chatfix.replaceMsgConsoleTargetVars(getOutFormat(), message, (ProxiedPlayer)sender);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayMessage(sender, finalmessage);

		// Replace placeholders (TARGET) (CONSOLE)
		finalmessage = chatfix.replaceMsgConsoleTargetVars(getInFormat(), message, (ProxiedPlayer)sender);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayConsoleMessage(finalmessage);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgConsoleTargetVars(getSpyFormat(), message, (ProxiedPlayer)sender);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != ((ProxiedPlayer)sender).getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage);
			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(sender.getUniqueId(), new UUID(0L, 0L));

	}

	public void sendMessageConsoleSender(String message, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER) (CONSOLE)
		String finalmessage = chatfix.replaceMsgConsoleSenderVars(getOutFormat(), message, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayConsoleMessage(finalmessage);

		// Replace placeholders (TARGET)
		finalmessage = chatfix.replaceMsgConsoleSenderVars(getInFormat(), message, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		displayMessage(target, finalmessage);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgConsoleSenderVars(getSpyFormat(), message, target);
		// Translate formats
		finalmessage = ProxyUtils.translateColourCodes(finalmessage);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(target.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage);
			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(new UUID(0L, 0L), target.getUniqueId());

	}

}
