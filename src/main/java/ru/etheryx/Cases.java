package ru.etheryx;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ru.etheryx.Commands.GiveKeysCommand;

import java.io.File;

public class Cases extends PluginBase {

    public Config keys;
    public Config caseloc;

    private static Cases instance;
    public boolean command;
    public FloatingTextParticle floatingTextParticle;

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().info(TextFormat.GREEN + "Плагин Cases включен");
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register(this.getName(), new GiveKeysCommand());
        Entity.registerEntity(EntityWitherSkull.class.getSimpleName(), EntityWitherSkull.class);

        if(!new File("plugins/Cases").exists()) {
            new File("plugins/Cases").mkdirs();
        }

        this.keys = new Config("plugins/Cases/keys.yml", Config.YAML);
        this.caseloc = new Config("plugins/Cases/Case.yml", Config.YAML);
        if (!this.caseloc.exists("x") || !this.caseloc.exists("y") || !this.caseloc.exists("z")) {
            this.caseloc.set("x", 2);
            this.caseloc.set("y", 2);
            this.caseloc.set("z", 2);
            this.caseloc.save();
        }

    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED + "Плагин Cases выключен");
    }

    public static Cases getInstance() {
        return instance;
    }

    public void loadCase(Player player) {
        int x = (int) this.caseloc.get("x");
        int y = (int) this.caseloc.get("y");
        int z = (int) this.caseloc.get("z");
        Location location = new Location(x, y, z);
        player.getLevel().setBlock(location, Block.get(Block.ENDER_CHEST));
        floatingTextParticle = new FloatingTextParticle(location.add(0.5, 2, 0.5), "§eКейс с ресурсами");
        floatingTextParticle.setText("§aУ вас §b" + Cases.getInstance().keys.get(player.getName()) + "§a ключей");
        player.getLevel().addParticle(Cases.getInstance().floatingTextParticle, Server.getInstance().getOnlinePlayers().values());
    }
}
