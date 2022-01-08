package xyz.olivermartin.multichat.velocity;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * UUID - NAME Manager
 * <p>Manages storage of UUIDS with their currently associated username</p>
 *
 * @author Oliver Martin (Revilo410)
 */
public class UUIDNameManager {

    private static final Map<UUID, String> uuidname = new HashMap<>();

    public static void addNew(UUID uuid, String name) {
        uuidname.put(uuid, name);
    }

    public static void removeUUID(UUID uuid) {
        uuidname.remove(uuid);
    }

    public static String getName(UUID uuid) {
        return uuidname.get(uuid);
    }

    public static void saveUUIDS() {

        try {
            File file = new File(MultiChat.configDir, "MultiChatUUIDName.dat");
            FileOutputStream saveFile = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(saveFile);
            out.writeObject(uuidname);
            out.close();
        } catch (IOException e) {
            System.out.println("[MultiChat] [Save Error] An error has occured writing the uuid-name file!");
            e.printStackTrace();
        }

    }

    @SuppressWarnings("unchecked")
    public static HashMap<UUID, String> loadUUIDS() {

        HashMap<UUID, String> result = null;

        try {
            File file = new File(MultiChat.configDir, "MultiChatUUIDName.dat");
            FileInputStream saveFile = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(saveFile);
            result = (HashMap<UUID, String>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[ActivityMonitor] [Load Error] An error has occured reading the uuid-name file!");
            e.printStackTrace();
        }

        return result;

    }

    public static void Startup() {

        File f = new File(MultiChat.configDir, "MultiChatUUIDName.dat");

        if ((f.exists()) && (!f.isDirectory())) {
            uuidname.putAll(loadUUIDS());
        } else {
            System.out.println("[MultiChat] File for uuid-name conversion does not exist to load. Must be first startup!");
            System.out.println("[MultiChat] Attempting to create hash file!");
            saveUUIDS();
            System.out.println("[MultiChat] The uuid-name file was created!");
        }

    }

    public static boolean existsUUID(UUID uuid) {
        return uuidname.containsKey(uuid);
    }
}
