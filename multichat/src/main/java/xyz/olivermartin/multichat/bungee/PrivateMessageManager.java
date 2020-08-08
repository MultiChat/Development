package xyz.olivermartin.multichat.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;
import xyz.olivermartin.multichat.proxy.common.config.ProxyConfigs;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

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

	private void displayMessage(ProxiedPlayer player, String rawMessage, String replacement) {

		rawMessage = MultiChatUtil.translateColorCodes(rawMessage);
		replacement = MultiChatUtil.translateColorCodes(replacement);

		if (ProxyConfigs.CONFIG.isLegacyServer(player.getServer().getInfo().getName())) {
			rawMessage = MultiChatUtil.approximateRGBColorCodes(rawMessage);
			replacement = MultiChatUtil.approximateRGBColorCodes(replacement);
		}

		player.sendMessage(ProxyJsonUtils.parseMessage(rawMessage, "%MESSAGE%", replacement));

	}

	private void displayConsoleMessage(String rawMessage, String replacement) {

		rawMessage = MultiChatUtil.approximateRGBColorCodes(MultiChatUtil.translateColorCodes(rawMessage));
		replacement = MultiChatUtil.approximateRGBColorCodes(MultiChatUtil.translateColorCodes(replacement));
		ProxyServer.getInstance().getConsole().sendMessage(ProxyJsonUtils.parseMessage(rawMessage, "%MESSAGE%", replacement));

	}

	private void updateLastMessage(UUID sender, UUID target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();
		ds.getLastMsg().put(sender, target);
		ds.getLastMsg().put(target, sender);

	}

	public void sendMessage(String message, ProxiedPlayer sender, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER)
		String finalmessage = chatfix.replaceMsgVars(ProxyConfigs.CONFIG.getPmOutFormat(), message, sender, target);

		displayMessage(sender, finalmessage, message);

		// Replace placeholders (TARGET)
		finalmessage = chatfix.replaceMsgVars(ProxyConfigs.CONFIG.getPmInFormat(), message, sender, target);

		displayMessage(target, finalmessage, message);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgVars(ProxyConfigs.CONFIG.getPmSpyFormat(), message, sender, target);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != sender.getUniqueId())
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")
							|| target.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage, message);

			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(sender.getUniqueId(), target.getUniqueId());

		ConsoleManager.logSocialSpy(sender.getName(), target.getName(), message);

	}

	public void sendMessageConsoleTarget(String message, ProxiedPlayer sender) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER)
		String finalmessage = chatfix.replaceMsgConsoleTargetVars(ProxyConfigs.CONFIG.getPmOutFormat(), message, sender);

		displayMessage(sender, finalmessage, message);

		// Replace placeholders (TARGET) (CONSOLE)
		finalmessage = chatfix.replaceMsgConsoleTargetVars(ProxyConfigs.CONFIG.getPmInFormat(), message, sender);

		displayConsoleMessage(finalmessage, message);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgConsoleTargetVars(ProxyConfigs.CONFIG.getPmSpyFormat(), message, sender);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != sender.getUniqueId())
					&& (!(sender.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage, message);
			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(sender.getUniqueId(), new UUID(0L, 0L));

	}

	public void sendMessageConsoleSender(String message, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		// Replace placeholders (SENDER) (CONSOLE)
		String finalmessage = chatfix.replaceMsgConsoleSenderVars(ProxyConfigs.CONFIG.getPmOutFormat(), message, target);

		displayConsoleMessage(finalmessage, message);

		// Replace placeholders (TARGET)
		finalmessage = chatfix.replaceMsgConsoleSenderVars(ProxyConfigs.CONFIG.getPmInFormat(), message, target);

		displayMessage(target, finalmessage, message);

		// Replace placeholders (SPY)
		finalmessage = chatfix.replaceMsgConsoleSenderVars(ProxyConfigs.CONFIG.getPmSpyFormat(), message, target);

		for (ProxiedPlayer onlineplayer : ProxyServer.getInstance().getPlayers()) {

			if ((onlineplayer.hasPermission("multichat.staff.spy"))
					&& (ds.getSocialSpy().contains(onlineplayer.getUniqueId()))
					&& (onlineplayer.getUniqueId() != target.getUniqueId())
					&& (!(target.hasPermission("multichat.staff.spy.bypass")))) {

				displayMessage(onlineplayer, finalmessage, message);
			}

		}

		// Update the last message map to be used for /r
		updateLastMessage(new UUID(0L, 0L), target.getUniqueId());

	}

	public Optional<ProxiedPlayer> getPartialPlayerMatch(String search) {

		// Spigot's own partial match algorithm
		Collection<ProxiedPlayer> spigotMatches = ProxyServer.getInstance().matchPlayer(search);

		if (spigotMatches != null && spigotMatches.size() > 0) {
			return Optional.of(spigotMatches.iterator().next());
		}

		// Check for names to contain the search
		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			if (p.getName().toLowerCase().contains(search.toLowerCase())) {
				return Optional.of(p);
			}
		}

		// Check for display names to contain the search
		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			if (p.getDisplayName().toLowerCase().contains(search.toLowerCase())) {
				return Optional.of(p);
			}
		}

		return Optional.empty();

	}

}
