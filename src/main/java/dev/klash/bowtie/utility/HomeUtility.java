package dev.klash.bowtie.utility;

import dev.klash.bowtie.Bowtie;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class HomeUtility {

    public record Home(String name, double x, double y, double z, String world) {
        public void saveTo(UUID player) {
            FileConfiguration config = Bowtie.homesConfig.getData();
            config.set(player.toString() + "." + name + ".x", x);
            config.set(player.toString() + "." + name + ".y", y);
            config.set(player.toString() + "." + name + ".z", z);
            config.set(player.toString() + "." + name + ".world", world);
            Bowtie.homesConfig.saveData();
        }
        public Location toLocation() {
            return new Location(Bowtie.tie.getServer().getWorld(world), x, y, z);
        }

        // rounded
        public int rx() {
            return (int) Math.round(x);
        }
        public int ry() {
            return (int) Math.round(y);
        }
        public int rz() {
            return (int) Math.round(z);
        }
    }

    public static @NotNull List<Home> getHomes(UUID player) {
        FileConfiguration config = Bowtie.homesConfig.getData();
        List<Home> homes = new ArrayList<>();
        if(config.getConfigurationSection(player.toString()) == null) return homes;
        for(String home : config.getConfigurationSection(player.toString()).getKeys(false)) {
            double x = config.getDouble(player.toString() + "." + home + ".x");
            double y = config.getDouble(player.toString() + "." + home + ".y");
            double z = config.getDouble(player.toString() + "." + home + ".z");
            String world = config.getString(player.toString() + "." + home + ".world");
            homes.add(new Home(home, x, y, z, world));
        }
        return homes;
    }

    public static @Nullable Home getHome(UUID player, String name) {
        FileConfiguration config = Bowtie.homesConfig.getData();
        if(config.getConfigurationSection(player.toString()) == null) return null;
        if(config.getConfigurationSection(player.toString()).getKeys(false).contains(name)) {
            double x = config.getDouble(player.toString() + "." + name + ".x");
            double y = config.getDouble(player.toString() + "." + name + ".y");
            double z = config.getDouble(player.toString() + "." + name + ".z");
            String world = config.getString(player.toString() + "." + name + ".world");
            return new Home(name, x, y, z, world);
        }
        return null;
    }

    public static void deleteHome(UUID player, String name) {
        FileConfiguration config = Bowtie.homesConfig.getData();
        config.set(player.toString() + "." + name, null);
        Bowtie.homesConfig.saveData();
    }

    public static Home locationToHome(Location location, String name) {
        return new Home(name, location.getX(), location.getY(), location.getZ(), location.getWorld().getName());
    }
}
