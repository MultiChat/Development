package xyz.olivermartin.multichat.bungee.events;

import java.util.Optional;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;

/**
 * An event that could be used by IRC plugins to relay staff chat events from MultiChat
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatStaffIRCEvent extends Event implements Cancellable {

	private boolean cancelled;
	private String message;
	private CommandSender sender;
	private String type;

	public MultiChatStaffIRCEvent(String type, CommandSender sender, String message) {

		cancelled = false;
		this.message = message;
		this.sender = sender;
		this.type = type;

	}
	
	/**
	 * Can be "mod" or "admin"
	 */
	public String getType() {
		return type;
	}

	public CommandSender getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSenderDisplayName() {
		if (sender instanceof ProxiedPlayer) return ((ProxiedPlayer) sender).getDisplayName();
		return "CONSOLE";
	}

	public String getSenderPrefix() {

		if (sender instanceof ProxiedPlayer) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(player.getUniqueId());
			if (opm.isPresent()) {
				return opm.get().prefix;
			} else {
				return "";
			}

		} else {
			return "";
		}
	}

	public String getSenderSuffix() {
		if (sender instanceof ProxiedPlayer) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(player.getUniqueId());
			if (opm.isPresent()) {
				return opm.get().suffix;
			} else {
				return "";
			}

		} else {
			return "";
		}
	}

	public String getSenderName() {
		if (sender instanceof ProxiedPlayer) {

			return sender.getName();

		} else {
			return "CONSOLE";
		}
	}

	public String getSenderNickname() {
		if (sender instanceof ProxiedPlayer) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(player.getUniqueId());
			if (opm.isPresent()) {
				return opm.get().nick;
			} else {
				return "";
			}

		} else {
			return "CONSOLE";
		}
	}

	public String getSenderServer() {
		if (sender instanceof ProxiedPlayer) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			return player.getServer().getInfo().getName();

		} else {
			return "CONSOLE";
		}
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

}