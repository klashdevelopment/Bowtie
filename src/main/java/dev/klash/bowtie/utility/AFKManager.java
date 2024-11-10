package dev.klash.bowtie.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AFKManager {
    public static List<UUID> afkPlayers = new ArrayList<>();

    public static void remove(UUID uuid) {
        afkPlayers.remove(uuid);
    }
    public static void add(UUID uuid) {
        afkPlayers.add(uuid);
    }
    public static boolean isAFK(UUID uuid) {
        return afkPlayers.contains(uuid);
    }
    public static void clear() {
        afkPlayers.clear();
    }
    public static void removeIfAFK(UUID uuid) {
        if(isAFK(uuid)) {
            remove(uuid);
        }
    }
    public static boolean toggle(UUID uuid) {
        if(isAFK(uuid)) {
            remove(uuid);
            return false;
        } else {
            add(uuid);
            return true;
        }
    }
}
