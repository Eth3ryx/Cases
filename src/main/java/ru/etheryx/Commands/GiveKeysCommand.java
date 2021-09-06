package ru.etheryx.Commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.etheryx.Cases;

public class GiveKeysCommand extends Command {

    public GiveKeysCommand() {
        super("givekeys", "Выдать ключи игроку", "§e Используйте /givekeys <player> <count>");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (player.hasPermission("keys.give")) {
            if (args.length == 2) {
                String name = args[0];
                int count = Integer.parseInt(args[1]);

                Player target = Server.getInstance().getPlayer(name);

                if(target != null) {
                    int keys = (int) Cases.getInstance().keys.get(player.getName());
                    Cases.getInstance().keys.set(target.getName(), keys+count);
                    Cases.getInstance().keys.save();
                    Cases.getInstance().floatingTextParticle.setText("§aУ вас §b" + Cases.getInstance().keys.get(target.getName()) + "§a ключей");
                    player.getLevel().addParticle(Cases.getInstance().floatingTextParticle, target);
                    player.sendMessage("§aВы выдали игроку §e" + target.getName() + "§b " + count + "§a ключей");
                }
            } else {
                player.sendMessage(this.usageMessage);
            }
        }
        return false;
    }
}
