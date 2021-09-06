package ru.etheryx;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.FloatingTextParticle;
import cn.nukkit.level.particle.LavaParticle;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.BlockEventPacket;
import cn.nukkit.scheduler.Task;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.TextFormat;

import java.util.Random;


public class EventListener implements Listener {

    private TaskHandler taskHandler;
    private TaskHandler taskHandlerSkull;
    private FloatingTextParticle ftpItem;
    private LavaParticle lava;
    private boolean isOpen = false;
    private int timer = 10;
    private final Block chestCase = Block.get(Block.ENDER_CHEST);
    protected int stay_time = 0;


    @EventHandler
    public void onJoin (PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPlayedBefore()) {
            Cases.getInstance().keys.set(player.getName(), 0);
            Cases.getInstance().keys.save();
        }
        Cases.getInstance().loadCase(player);
    }

    @EventHandler
    public void onInteract (PlayerInteractEvent e) {
        e.setCancelled();
        Block block = e.getBlock();
        Player player = e.getPlayer();
        Location loc = new Location(block.x, block.y+1, block.z);

        if (block.getId() == Block.ENDER_CHEST) {
            e.setCancelled();
            int keys = (int) Cases.getInstance().keys.get(player.getName());
            if (keys == 0) {
                player.sendMessage("§eУ вас нет ключей для открытия кейса!");
            } else {
                if (!isOpen) {
                    Cases.getInstance().keys.set(player.getName(), keys - 1);
                    Cases.getInstance().keys.save();
                    Cases.getInstance().floatingTextParticle.setText("§aУ вас §b" + Cases.getInstance().keys.get(player.getName()) + "§a ключей");
                    block.getLevel().addParticle(Cases.getInstance().floatingTextParticle, player);
                    player.sendMessage("§aКейс открыт!");
                    BlockEventPacket pk = new BlockEventPacket();
                    pk.x = e.getBlock().getFloorX();
                    pk.y = e.getBlock().getFloorY();
                    pk.z = e.getBlock().getFloorZ();
                    pk.case1 = 1;
                    pk.case2 = 2;
                    player.getLevel().addChunkPacket(pk.x >> 4, pk.z >> 4, pk);
                    setItem(player);
                    ftpItem.setInvisible(false);
                    block.getLevel().addParticle(ftpItem, Server.getInstance().getOnlinePlayers().values());
                    Entity entity = Entity.createEntity(EntityWitherSkull.class.getSimpleName(), e.getBlock().getLocation().add(0.5, 0, 0.5));
                    entity.spawnToAll();
                    isOpen = true;

                    this.taskHandlerSkull = Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {
                        @Override
                        public void onRun(int i) {
                            stay_time++;
                            if (stay_time < 100) {

                                entity.y += 0.03;
                            }
                            else if (stay_time < 200) {
                                entity.y -= 0.03;
                            }
                            else {
                                taskHandlerSkull.cancel();
                                stay_time = 0;
                                entity.getLevel().addParticle(lava, Server.getInstance().getOnlinePlayers().values());
                                entity.close();
                            }
                            if (entity.yaw > 360) entity.yaw = 0;
                            else {
                                entity.yaw += 15;
                            }
                        }
                    }, 1);

                    this.taskHandler = Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {
                        @Override
                        public void onRun (int i) {

                            lava = new LavaParticle(entity.getLocation());
                            entity.getLevel().addParticle(lava, Server.getInstance().getOnlinePlayers().values());
                            if (timer == 2 || timer == 4 || timer == 6 || timer == 8 || timer == 10) {
                                entity.getLevel().addSound(entity.getLocation(), Sound.FIREWORK_LAUNCH);
                            }

                            if (timer != 0) {
                                timer--;
                                e.setCancelled(true);
                            }else {
                                BlockEventPacket pk = new BlockEventPacket();
                                pk.x = e.getBlock().getFloorX();
                                pk.y = e.getBlock().getFloorY();
                                pk.z = e.getBlock().getFloorZ();
                                pk.case1 = 1;
                                pk.case2 = 0;
                                AddItemEntityPacket pkItem = new AddItemEntityPacket();
                                pkItem.x = (float) (chestCase.getFloorX()-0.5);
                                pkItem.y = chestCase.getFloorY()+1;
                                pkItem.z = (float) (chestCase.getFloorZ()-0.5);
                                pkItem.item = null;
                                player.dataPacket(pkItem);
                                player.getLevel().addChunkPacket(pk.x >> 4, pk.z >> 4, pk);

                                taskHandler.cancel();
                                timer = 10;
                                isOpen = false;
                                ftpItem.setInvisible(true);
                                block.getLevel().addParticle(ftpItem, Server.getInstance().getOnlinePlayers().values());
                            }
                        }
                    }, 20);
                }
                else {
                    player.sendMessage("§eКто-то уже открывает кейс!");
                }
            }

        }
    }

    public void setItem(Player player) {

        Random random = new Random();
        int chance = random.nextInt(100) + 1;

        Item item1 = new Item(Item.DIAMOND);
        item1.setCount(16);
        Item item2 = new Item(Item.GOLD_INGOT);
        item2.setCount(32);
        Item item3 = new Item(Item.IRON_INGOT);
        item3.setCount(32);
        Item item4 = new Item(Item.CARROT);
        item4.setCount(16);

        AddItemEntityPacket pk = new AddItemEntityPacket();
        int x = (int) Cases.getInstance().caseloc.get("x");
        int y = (int) Cases.getInstance().caseloc.get("y");
        int z = (int) Cases.getInstance().caseloc.get("z");
        pk.x = (float) (x+0.5);
        pk.y = (float) (y+0.5);
        pk.z = (float) (z+0.5);
        Location location = new Location(pk.x, pk.y+1, pk.z);

        if (chance < 10) {
            player.getInventory().addItem(item1);
            pk.item = Item.get(Item.DIAMOND);
            ftpItem = new FloatingTextParticle(location, "§7Алмаз (16)");

        }
        else if (chance < 30) {
            player.getInventory().addItem(item2);
            pk.item = Item.get(Item.GOLD_INGOT);
            ftpItem = new FloatingTextParticle(location, "§7Золотой (32)");
        }
        else if (chance < 60) {
            player.getInventory().addItem(item3);
            pk.item = Item.get(Item.IRON_INGOT);
            ftpItem = new FloatingTextParticle(location, "§7Железный слиток (32)");
        }
        else {
            player.getInventory().addItem(item4);
            pk.item = Item.get(Item.CARROT);
            ftpItem = new FloatingTextParticle(location, "§7Морковь (16)");
        }

        for (Player p : Server.getInstance().getOnlinePlayers().values()) {
            p.dataPacket(pk);
        }

    }

}
