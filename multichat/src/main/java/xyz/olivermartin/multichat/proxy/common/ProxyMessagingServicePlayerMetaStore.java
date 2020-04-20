package xyz.olivermartin.multichat.proxy.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProxyMessagingServicePlayerMetaStore extends ProxyPlayerMetaStore {

	private Map<UUID, String> prefixMap;
	private Map<UUID, String> suffixMap;
	private Map<UUID, String> nicknameMap;
	private Map<UUID, String> worldMap;
	private Map<UUID, String> displayNameMap;

	public ProxyMessagingServicePlayerMetaStore() {
		super(ProxyPlayerMetaStoreMode.MESSAGING_SERVICE);

		this.prefixMap = new HashMap<UUID, String>();
		this.suffixMap = new HashMap<UUID, String>();
		this.nicknameMap = new HashMap<UUID, String>();
		this.worldMap = new HashMap<UUID, String>();
		this.displayNameMap = new HashMap<UUID, String>();

	}

	@Override
	public Optional<String> getPrefix(UUID uuid) {
		if (prefixMap.containsKey(uuid)) {
			return Optional.of(prefixMap.get(uuid));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void offerPrefix(UUID uuid, String prefix) {
		if (MultiChatProxy.getInstance().getPlayerManager().isOnline(uuid)) {
			prefixMap.put(uuid, prefix);
		}
	}

	@Override
	public Optional<String> getSuffix(UUID uuid) {
		if (suffixMap.containsKey(uuid)) {
			return Optional.of(suffixMap.get(uuid));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void offerSuffix(UUID uuid, String suffix) {
		if (MultiChatProxy.getInstance().getPlayerManager().isOnline(uuid)) {
			suffixMap.put(uuid, suffix);
		}
	}

	@Override
	public Optional<String> getWorld(UUID uuid) {
		if (worldMap.containsKey(uuid)) {
			return Optional.of(worldMap.get(uuid));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void offerWorld(UUID uuid, String world) {
		if (MultiChatProxy.getInstance().getPlayerManager().isOnline(uuid)) {
			worldMap.put(uuid, world);
		}
	}

	@Override
	public Optional<String> getNickname(UUID uuid) {
		if (nicknameMap.containsKey(uuid)) {
			return Optional.of(nicknameMap.get(uuid));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void offerNickname(UUID uuid, String nickname) {
		if (MultiChatProxy.getInstance().getPlayerManager().isOnline(uuid)) {
			nicknameMap.put(uuid, nickname);
		}
	}

	@Override
	public Optional<String> getDisplayName(UUID uuid) {

		// TODO I think that it is best to NOT rebuild the displayname format on bungee for the display name...
		// We send the format to spigot / sponge already, and they set it and send it back to us.
		// This allows for a future MultiChatLocal option to override displayname format
		// So there wouldn't be much reason to actually build it here too...

		if (displayNameMap.containsKey(uuid)) {
			return Optional.of(displayNameMap.get(uuid));
		} else {
			return Optional.empty();
		}
	}

	@Override
	public void offerDisplayName(UUID uuid, String displayName) {
		if (MultiChatProxy.getInstance().getPlayerManager().isOnline(uuid)) {
			displayNameMap.put(uuid, displayName);
		}
	}

	@Override
	public void clearPlayer(UUID uuid) {
		prefixMap.remove(uuid);
		suffixMap.remove(uuid);
		worldMap.remove(uuid);
		displayNameMap.remove(uuid);
		nicknameMap.remove(uuid);
	}

}
