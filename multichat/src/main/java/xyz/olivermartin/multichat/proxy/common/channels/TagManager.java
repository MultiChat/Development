package xyz.olivermartin.multichat.proxy.common.channels;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.proxy.common.ProxyJsonUtils;

public class TagManager {

	public static final String REGEX = "(?i)@([A-Z0-9_]{3,})";

	private Pattern tagPattern;

	public TagManager() {
		tagPattern = Pattern.compile(REGEX);
	}

	public void handleTags(String message, String tagger) {
		notifyTaggedPlayers(getTaggedPlayers(getPotentialTags(message)), tagger);
	}

	private List<String> getPotentialTags(String message) {

		List<String> potentialTags = new ArrayList<String>();
		Matcher tagMatcher = tagPattern.matcher(message);

		while (tagMatcher.find()) {
			potentialTags.add(tagMatcher.group(1));
		}

		return potentialTags;

	}

	private List<ProxiedPlayer> getTaggedPlayers(List<String> potentialTags) {

		List<ProxiedPlayer> taggedPlayers = new ArrayList<ProxiedPlayer>();

		for (String potentialTag : potentialTags) {
			ProxiedPlayer potentialPlayer = ProxyServer.getInstance().getPlayer(potentialTag);
			if (potentialPlayer != null) taggedPlayers.add(potentialPlayer);
		}

		return taggedPlayers;

	}

	private void notifyTaggedPlayers(List<ProxiedPlayer> taggedPlayers, String tagger) {
		for (ProxiedPlayer taggedPlayer : taggedPlayers) {
			notifyPlayer(taggedPlayer, tagger);
		}
	}

	private void notifyPlayer(ProxiedPlayer player, String tagger) {
		player.sendMessage(ChatMessageType.ACTION_BAR, ProxyJsonUtils.parseMessage(MultiChatUtil.translateColorCodes("&6You were mentioned by %SPECIAL%"), "%SPECIAL%", MultiChatUtil.translateColorCodes(tagger)));
	}

}
