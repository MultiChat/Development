package xyz.olivermartin.multichat.bungee;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UUID - NAME Manager
 * <p>Manages storage of UUIDS with their currently associated username</p>
 * 
 * @author Oliver Martin (Revilo410)
 *
 */
public class UUIDNameManager {

	public static Map<UUID, String> uuidname = new HashMap<UUID, String>();

	public static void addNew(UUID uuid, String name) {
		uuidname.put(uuid, name);
	}

	public static void removeUUID(UUID uuid) {
		uuidname.remove(uuid);
	}

	public static String getName(UUID uuid) {
		return (String)uuidname.get(uuid);
	}

	public static boolean existsUUID(UUID uuid) {

		if (uuidname.containsKey(uuid)) {
			return true;
		}
		return false;

	}
}
