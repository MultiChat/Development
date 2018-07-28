package xyz.olivermartin.multichat.bungee;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * LEGACY ** TO BE REMOVED ** Chat Manipulation Class
 * <p>This class now only serves the purpose of replacing the placeholders in message formats</p>
 * 
 * <p>It used to manage "fixing format codes" and "getting URLBIT" before these were made redundant</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class ChatManipulation {

	public String replaceMsgVars(String MessageFormat, String Message, ProxiedPlayer sender, ProxiedPlayer target) {

		MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
		MessageFormat = MessageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		MessageFormat = MessageFormat.replace("%NAME%", sender.getName());
		MessageFormat = MessageFormat.replace("%DISPLAYNAMET%", target.getDisplayName());
		MessageFormat = MessageFormat.replace("%NAMET%", target.getName());
		MessageFormat = MessageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		MessageFormat = MessageFormat.replace("%SERVERT%", target.getServer().getInfo().getName());
		return MessageFormat;

	}

	public String replaceChatVars(String MessageFormat, ProxiedPlayer sender) {

		MessageFormat = MessageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		MessageFormat = MessageFormat.replace("%NAME%", sender.getName());
		MessageFormat = MessageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		return MessageFormat;

	}

	public String replaceModChatVars(String MessageFormat, String playername, String displayname, String server, String Message, ProxiedPlayer target) {

		MessageFormat = MessageFormat.replace("%DISPLAYNAME%", displayname);
		MessageFormat = MessageFormat.replace("%NAME%", playername);
		MessageFormat = MessageFormat.replace("%SERVER%", server);
		MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
		MessageFormat = MessageFormat.replace("%CC%", "&" + ((TChatInfo)MultiChat.modchatpreferences.get(target.getUniqueId())).getChatColor());
		MessageFormat = MessageFormat.replace("%NC%", "&" + ((TChatInfo)MultiChat.modchatpreferences.get(target.getUniqueId())).getNameColor());
		return MessageFormat;

	}

	public String replaceAdminChatVars(String MessageFormat, String playername, String displayname, String server, String Message, ProxiedPlayer target) {

		MessageFormat = MessageFormat.replace("%DISPLAYNAME%",displayname);
		MessageFormat = MessageFormat.replace("%NAME%", playername);
		MessageFormat = MessageFormat.replace("%SERVER%", server);
		MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
		MessageFormat = MessageFormat.replace("%CC%", "&" + ((TChatInfo)MultiChat.adminchatpreferences.get(target.getUniqueId())).getChatColor());
		MessageFormat = MessageFormat.replace("%NC%", "&" + ((TChatInfo)MultiChat.adminchatpreferences.get(target.getUniqueId())).getNameColor());
		return MessageFormat;

	}

	public String replaceGroupChatVars(String MessageFormat, String sendername, String Message, String GroupName) {

		MessageFormat = MessageFormat.replace("%NAME%", sendername);
		MessageFormat = MessageFormat.replace("%MESSAGE%", Message);
		MessageFormat = MessageFormat.replace("%CC%", "&" + ((TGroupChatInfo)MultiChat.groupchats.get(GroupName)).getChatColor());
		MessageFormat = MessageFormat.replace("%NC%", "&" + ((TGroupChatInfo)MultiChat.groupchats.get(GroupName)).getNameColor());
		MessageFormat = MessageFormat.replace("%GROUPNAME%", GroupName.toUpperCase());
		return MessageFormat;

	}

	public String replaceJoinMsgVars(String MessageFormat, String sendername) {

		MessageFormat = MessageFormat.replace("%NAME%", sendername);
		return MessageFormat;

	}
}
