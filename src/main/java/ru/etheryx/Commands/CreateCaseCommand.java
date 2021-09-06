package ru.etheryx.Commands;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import ru.etheryx.Cases;

public class CreateCaseCommand extends Command {

    public CreateCaseCommand() {
        super("cc", "Создать кейс", "/cc");
        this.commandParameters.clear();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Cases.getInstance().command = true;
        Player player = (Player) sender;
        player.sendMessage("Тапните туда, куда хотите поставить кейс");
        return true;
    }
}
