package xyz.olivermartin.multichat.bungee;

import java.util.Optional;

import com.olivermartin410.plugins.TChatInfo;
import com.olivermartin410.plugins.TGroupChatInfo;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.proxy.common.MultiChatProxy;
import xyz.olivermartin.multichat.proxy.common.storage.ProxyDataStore;

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

	public String replaceMsgVars(String messageFormat, String message, ProxiedPlayer sender, ProxiedPlayer target) {

		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		messageFormat = messageFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			messageFormat = messageFormat.replace("%PREFIX%", opm.get().prefix);
			messageFormat = messageFormat.replace("%SUFFIX%", opm.get().suffix);
			messageFormat = messageFormat.replace("%NICK%", opm.get().nick);
		}

		messageFormat = messageFormat.replace("%DISPLAYNAMET%", target.getDisplayName());
		messageFormat = messageFormat.replace("%NAMET%", target.getName());

		Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(target.getUniqueId());
		if (opmt.isPresent()) {
			messageFormat = messageFormat.replace("%PREFIXT%", opmt.get().prefix);
			messageFormat = messageFormat.replace("%SUFFIXT%", opmt.get().suffix);
			messageFormat = messageFormat.replace("%NICKT%", opmt.get().nick);
		}

		messageFormat = messageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		messageFormat = messageFormat.replace("%SERVERT%", target.getServer().getInfo().getName());

		messageFormat = messageFormat.replace("%WORLD%", opm.get().world);
		messageFormat = messageFormat.replace("%WORLDT%", opmt.get().world);

		return messageFormat;

	}

	public String replaceMsgConsoleTargetVars(String messageFormat, String message, ProxiedPlayer sender) {

		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%DISPLAYNAME%", sender.getDisplayName());
		messageFormat = messageFormat.replace("%NAME%", sender.getName());

		Optional<PlayerMeta> opm = PlayerMetaManager.getInstance().getPlayer(sender.getUniqueId());
		if (opm.isPresent()) {
			messageFormat = messageFormat.replace("%PREFIX%", opm.get().prefix);
			messageFormat = messageFormat.replace("%SUFFIX%", opm.get().suffix);
			messageFormat = messageFormat.replace("%NICK%", opm.get().nick);
		}

		messageFormat = messageFormat.replace("%DISPLAYNAMET%", "CONSOLE");
		messageFormat = messageFormat.replace("%NAMET%", "CONSOLE");

		messageFormat = messageFormat.replace("%PREFIXT%", "");
		messageFormat = messageFormat.replace("%SUFFIXT%", "");
		messageFormat = messageFormat.replace("%NICKT%", "CONSOLE");

		messageFormat = messageFormat.replace("%SERVER%", sender.getServer().getInfo().getName());
		messageFormat = messageFormat.replace("%SERVERT%", "CONSOLE");

		messageFormat = messageFormat.replace("%WORLD%", opm.get().world);
		messageFormat = messageFormat.replace("%WORLDT%", "CONSOLE");

		return messageFormat;

	}

	public String replaceMsgConsoleSenderVars(String messageFormat, String message, ProxiedPlayer target) {

		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%DISPLAYNAME%", "CONSOLE");
		messageFormat = messageFormat.replace("%NAME%", "CONSOLE");

		messageFormat = messageFormat.replace("%PREFIX%", "");
		messageFormat = messageFormat.replace("%SUFFIX%", "");
		messageFormat = messageFormat.replace("%NICK%", "CONSOLE");

		messageFormat = messageFormat.replace("%DISPLAYNAMET%", target.getDisplayName());
		messageFormat = messageFormat.replace("%NAMET%", target.getName());

		Optional<PlayerMeta> opmt = PlayerMetaManager.getInstance().getPlayer(target.getUniqueId());
		if (opmt.isPresent()) {
			messageFormat = messageFormat.replace("%PREFIXT%", opmt.get().prefix);
			messageFormat = messageFormat.replace("%SUFFIXT%", opmt.get().suffix);
			messageFormat = messageFormat.replace("%NICKT%", opmt.get().nick);
		}

		messageFormat = messageFormat.replace("%SERVER%", "CONSOLE");
		messageFormat = messageFormat.replace("%SERVERT%", target.getServer().getInfo().getName());

		messageFormat = messageFormat.replace("%WORLD%", "CONSOLE");
		messageFormat = messageFormat.replace("%WORLDT%", opmt.get().world);

		return messageFormat;

	}

	public String replaceModChatVars(String messageFormat, String playername, String displayname, String server, String message, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		messageFormat = messageFormat.replace("%DISPLAYNAME%", displayname);
		messageFormat = messageFormat.replace("%NAME%", playername);
		messageFormat = messageFormat.replace("%SERVER%", server);
		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%CC%", "&" + ((TChatInfo)ds.getModChatPreferences().get(target.getUniqueId())).getChatColor());
		messageFormat = messageFormat.replace("%NC%", "&" + ((TChatInfo)ds.getModChatPreferences().get(target.getUniqueId())).getNameColor());
		return messageFormat;

	}

	public String replaceAdminChatVars(String messageFormat, String playername, String displayname, String server, String message, ProxiedPlayer target) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		messageFormat = messageFormat.replace("%DISPLAYNAME%",displayname);
		messageFormat = messageFormat.replace("%NAME%", playername);
		messageFormat = messageFormat.replace("%SERVER%", server);
		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%CC%", "&" + ((TChatInfo)ds.getAdminChatPreferences().get(target.getUniqueId())).getChatColor());
		messageFormat = messageFormat.replace("%NC%", "&" + ((TChatInfo)ds.getAdminChatPreferences().get(target.getUniqueId())).getNameColor());
		return messageFormat;

	}

	public String replaceGroupChatVars(String messageFormat, String sendername, String message, String groupName) {

		ProxyDataStore ds = MultiChatProxy.getInstance().getDataStore();

		messageFormat = messageFormat.replace("%NAME%", sendername);
		//messageFormat = messageFormat.replace("%MESSAGE%", message);
		messageFormat = messageFormat.replace("%CC%", "&" + ((TGroupChatInfo)ds.getGroupChats().get(groupName)).getChatColor());
		messageFormat = messageFormat.replace("%NC%", "&" + ((TGroupChatInfo)ds.getGroupChats().get(groupName)).getNameColor());
		messageFormat = messageFormat.replace("%GROUPNAME%", groupName.toUpperCase());
		return messageFormat;

	}

	public String replaceJoinMsgVars(String messageFormat, String senderName, String serverName) {

		messageFormat = messageFormat.replace("%NAME%", senderName);
		messageFormat = messageFormat.replace("%SERVER%", serverName);
		return messageFormat;

	}
}
