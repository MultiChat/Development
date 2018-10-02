package xyz.olivermartin.multichat.bungee.events;

import java.util.Optional;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;

/**
 * An event that could be used by IRC plugins to relay chat events from MultiChat
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class MultiChatGlobalIRCEvent extends Event implements Cancellable {

	private boolean cancelled;
	private String message;
	private String format;
	private ProxiedPlayer sender;

	public MultiChatGlobalIRCEvent(ProxiedPlayer sender, String format, String message) {

		cancelled = false;
		this.message = message;
		this.sender = sender;
		this.format = format;

	}

	public ProxiedPlayer getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the format that was used to send this message on the network
	 * 
	 * %NICK% -> Nickname
	 * %NAME% -> Name
	 * %DISPLAYNAME% -> DisplayName
	 * %PREFIX% -> Prefix
	 * %SUFFIX% -> Suffix
	 * %SERVER% -> Server
	 * %MODE% -> Chat Mode
	 * %M% -> Short Chat Mode
	 * 
	 * @return The format MultiChat used to send this message
	 */
	public String getFormat() {
		return format;
	}

	public String getSenderDisplayName() {
		return sender.getDisplayName();
	}

	public String getSenderPrefix() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().prefix;
		} else {
			return "";
		}
	}

	public String getSenderSuffix() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().suffix;
		} else {
			return "";
		}
	}

	public String getSenderName() {
		return sender.getName();
	}

	public String getSenderNickname() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().nick;
		} else {
			return "";
		}
	}

	public String getSenderServer() {
		return sender.getServer().getInfo().getName();
	}

	/**
	 * Returns "Local" if the player is in local mode and only seeing chat from players on the same server
	 * Returns "Global" if the player is in global mode and seeing chat from everyone
	 */
	public String getSenderChatMode() {
		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(false)) {
			return "Local";
		} else {
			return "Global";
		}
	}

	/**
	 * Returns "L" if the player is in local mode and only seeing chat from players on the same server
	 * Returns "G" if the player is in global mode and seeing chat from everyone
	 */
	public String getSenderShortChatMode() {
		if (MultiChat.globalplayers.get(sender.getUniqueId()).equals(false)) {
			return "L";
		} else {
			return "G";
		}
	}

	/**
	 * Returns true if the player has the MultiChat permission to use format codes in global chat
	 */
	public boolean hasColorChatPermission() {
		return ( sender.hasPermission("multichat.chat.color") || sender.hasPermission("multichat.chat.colour") );
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