package xyz.olivermartin.multichat.bungee.events;

import java.util.Optional;
import java.util.regex.Pattern;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import xyz.olivermartin.multichat.bungee.PlayerMeta;
import xyz.olivermartin.multichat.bungee.PlayerMetaManager;

/**
 * An event that triggers AFTER a staff chat message has been sent via MultiChat
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class PostStaffChatEvent extends Event implements Cancellable {

	private boolean cancelled;
	private String message; // The message that was sent
	private CommandSender sender; // The sender of the message
	private String type; // The type of staff chat, currently "mod" or "admin"

	/**
	 * Trigger a new PostStaffChatEvent
	 * 
	 * @param type The type of staff chat, currently "mod" or "admin"
	 * @param sender The sender of the command, could be a player or the console
	 * @param message The message that was sent
	 */
	public PostStaffChatEvent(String type, CommandSender sender, String message) {

		cancelled = false;
		this.message = message;
		this.sender = sender;
		this.type = type;

	}

	/**
	 * @return "mod" or "admin" depending on the type of staff chat
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return The sender of the message, could be a player or the console
	 */
	public CommandSender getSender() {
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
	 * <p>Allows you to change the message in this PostStaffChatEvent ONLY</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST staff chat event, meaning the chat message has already happened by the time this event triggers.</p>
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return The display name of the sender including any format codes in the '&' notation
	 */
	public String getSenderDisplayName() {
		if (sender instanceof ProxiedPlayer) return ((ProxiedPlayer) sender).getDisplayName();
		return "CONSOLE";
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
		if (sender instanceof ProxiedPlayer) {

			return sender.getName();

		} else {
			return "CONSOLE";
		}
	}

	/**
	 * @return The nickname of the sender including any format codes in the '&' notation
	 */
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
		if (sender instanceof ProxiedPlayer) {

			ProxiedPlayer player = (ProxiedPlayer) sender;

			return player.getServer().getInfo().getName();

		} else {
			return "CONSOLE";
		}
	}

	/**
	 * <p>Returns true if this PostStaffChatEvent has been cancelled</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST staff chat event, meaning the message has already happened by the time this event triggers.</p>
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * <p>Allows you to cancel this PostStaffChatEvent</p>
	 * <p>THIS WILL HAVE NO AFFECT ON WHAT MESSAGE MULTICHAT SENDS!</p>
	 * <p>This is a POST staff chat event, meaning the message has already happened by the time this event triggers.</p>
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