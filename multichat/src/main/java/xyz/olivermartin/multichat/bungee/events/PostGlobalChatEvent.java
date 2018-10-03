package xyz.olivermartin.multichat.bungee.events;

import java.util.Optional;
import java.util.regex.Pattern;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import xyz.olivermartin.multichat.bungee.MultiChat;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;

/**
 * An event that triggers AFTER a global chat message has been sent via MultiChat
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class PostGlobalChatEvent extends Event implements Cancellable {

	private boolean cancelled;
	private String message; // The message that was sent in global chat
	private String format; // The global chat format that was used
	private ProxiedPlayer sender; // The sender of the message

	/**
	 * Trigger a new PostGlobalChatEvent
	 * 
	 * @param sender The sender of the message
	 * @param format The format used to send the message
	 * @param message The message that was sent
	 */
	public PostGlobalChatEvent(ProxiedPlayer sender, String format, String message) {

		cancelled = false;
		this.message = message;
		this.sender = sender;
		this.format = format;

	}

	/**
	 * @return The player who sent the message
	 */
	public ProxiedPlayer getSender() {
		return sender;
	}

	/**
	 * @return The message that was sent including any format codes in the '&' notation
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return The message that was sent EXCLUDING any format codes in the '&' notation
	 */
	public String getRawMessage() {
		return stripAllFormattingCodes(getMessage());
	}

	/**
	 * <p>Allows you to change the message in this PostGlobalChatEvent ONLY</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST global chat event, meaning the chat message has already happened by the time this event triggers.</p>
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Returns the format that was used to send this message on the network
	 * (i.e. The bit that appears before the actual message)
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

	/**
	 * @return The display name of the sender including any format codes in the '&' notation
	 */
	public String getSenderDisplayName() {
		return sender.getDisplayName();
	}

	/**
	 * @return The display name of the sender EXCLUDING any format codes in the '&' notation
	 */
	public String getRawSenderDisplayName() {
		return stripAllFormattingCodes(getSenderDisplayName());
	}

	/**
	 * @return The prefix of the sender including any format codes in the '&' notation
	 */
	public String getSenderPrefix() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().prefix;
		} else {
			return "";
		}
	}

	/**
	 * @return The prefix of the sender EXCLUDING any format codes in the '&' notation
	 */
	public String getRawSenderPrefix() {
		return stripAllFormattingCodes(getSenderPrefix());
	}

	/**
	 * @return The suffix of the sender including any format codes in the '&' notation
	 */
	public String getSenderSuffix() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().suffix;
		} else {
			return "";
		}
	}

	/**
	 * @return The suffix of the sender EXCLUDING any format codes in the '&' notation
	 */
	public String getRawSenderSuffix() {
		return stripAllFormattingCodes(getSenderSuffix());
	}

	/**
	 * @return The username of the sender
	 */
	public String getSenderName() {
		return sender.getName();
	}

	/**
	 * @return The nickname of the sender including any format codes in the '&' notation
	 */
	public String getSenderNickname() {
		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			return opm.get().nick;
		} else {
			return "";
		}
	}

	/**
	 * @return The nickname of the sender EXCLUDING any format codes in the '&' notation
	 */
	public String getRawSenderNickname() {
		return stripAllFormattingCodes(getSenderNickname());
	}

	/**
	 * @return The server name of the sender
	 */
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

	/**
	 * <p>Returns true if this PostGlobalChatEvent has been cancelled</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST global chat event, meaning the message has already happened by the time this event triggers.</p>
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * <p>Allows you to cancel this PostGlobalChatEvent</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST global chat event, meaning the message has already happened by the time this event triggers.</p>
	 */
	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	// Remove all formatting codes from the text (&a, &l etc.)
	private static String stripAllFormattingCodes(String input) {

		char COLOR_CHAR = '&';
		Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf(COLOR_CHAR) + "[0-9A-FK-OR]");

		if (input == null) {
			return null;
		}

		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");

	}

}