package ru.etheryx;

import cn.nukkit.entity.Entity;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import ru.etheryx.Commands.CreateCaseCommand;
import ru.etheryx.Commands.GiveKeysCommand;

import java.io.File;

public class Cases extends PluginBase {

    public Config keys;

    private static Cases instance;
    public boolean command;
    public FloatingTextParticle floatingTextParticle;

    @Override
    public void onEnable() {
        instance = this;
        this.getLogger().info(TextFormat.GREEN + "Плагин Cases включен");
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getCommandMap().register(this.getName(), new CreateCaseCommand());
        this.getServer().getCommandMap().register(this.getName(), new GiveKeysCommand());
        Entity.registerEntity(EntityWitherSkull.class.getSimpleName(), EntityWitherSkull.class);

        if(!new File("plugins/UserData").exists()) {
            new File("plugins/UserData").mkdirs();
        }

        this.keys = new Config("plugins/UserData/keys.yml", Config.YAML);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.RED + "Плагин Cases выключен");
    }

    public static Cases getInstance() {
        return instance;
    }
}
