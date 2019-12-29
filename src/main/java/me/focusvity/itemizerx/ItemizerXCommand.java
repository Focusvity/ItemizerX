package me.focusvity.itemizerx;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemizerXCommand implements CommandExecutor
{

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args)
    {
        if (args.length == 0)
        {
            sender.sendMessage(ChatColor.AQUA + "ItemizerX " + ChatColor.GOLD + "v"
                    + ItemizerX.plugin.getDescription().getVersion()
                    + ChatColor.AQUA + " by " + ChatColor.GOLD
                    + StringUtils.join(ItemizerX.plugin.getDescription().getAuthors(), ", "));
            sender.sendMessage(ChatColor.AQUA + "Type " + ChatColor.GOLD + "/itemizer help "
                    + ChatColor.AQUA + "for help");
            return true;
        }

        if (!(sender instanceof Player))
        {
            sender.sendMessage(colorize("&4You must be a player to execute this command!"));
            return true;
        }

        final Player player = (Player) sender;
        final ItemStack item = player.getInventory().getItemInMainHand();
        final boolean hasItem = item.getType() != Material.AIR;
        final boolean hasPotion = (item.getType() == Material.POTION
                || item.getType() == Material.SPLASH_POTION
                || item.getType() == Material.LINGERING_POTION);
        final boolean hasBook = item.getType() == Material.WRITTEN_BOOK;
        final ItemMeta meta = item.getItemMeta();

        switch (args[0])
        {
            case "help":
            {
                sender.sendMessage(colorize("&3===============&f[&dItemizerX Commands&f]&3===============\n"
                        + "&b/itemizer name <&fname&b> &c- &6Name your item\n"
                        + "&b/itemizer id <&fid&b> &c- &6Change the item's material\n"
                        + "&b/itemizer lore &c- &6Lore editing command\n"
                        + "&b/itemizer potion &c- &6Potion editing command\n"
                        + "&b/itemizer attr &c- &6Attribute editing command\n"
                        + "&b/itemizer flag &c- &6Flag editing command\n"
                        + "&b/itemizer enchant &c- &6Enchant editing command\n"
                        + "&b/itemizer title <&fname&b> &c- &6Set the book's title\n"
                        + "&b/itemizer author <&fname&b> &c- &6Set the book's author\n"
                        + "&b/itemizer head <&fname&b> &c- &6Set the player of the head"
                        + "&b/itemizer clearall &c- &6Clears all metadata from your item"));
                return true;
            }
            case "name":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dName Commands&f]&3===============\n"
                            + "&b/itemizer name <&fname&b> &c - &6Name your item"));
                    return true;
                }
                else if (args.length >= 2)
                {
                    if (!hasItem)
                    {
                        sender.sendMessage("Get an ITEM in hand!");
                        return true;
                    }
                    String name = colorize(StringUtils.join(args, " ", 1, args.length));
                    assert meta != null;
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                    sender.sendMessage(colorize("&2The name of the item in your hand has been set to &f'" + name + "&f'"));
                    return true;
                }
            }
            case "id":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dID Commands&f]&3===============\n"
                            + "&b/itemizer id <&fid&b> &c- &6Change the item's material"));
                    return true;
                }
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                Material material = Material.getMaterial(args[1]);
                if (material == null)
                {
                    sender.sendMessage(colorize("&4The material &f\"" + args[1] + "&f\"&4 does not exist!"));
                    return true;
                }
                item.setType(material);
                sender.sendMessage(colorize("&2The material of the item has changed to &f" + material));
                return true;
            }
            case "lore":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                            + "&b/itemizer lore add <&ftext&b> &c- &6Add a line of text to your item's lore\n"
                            + "&b/itemizer lore remove <&findex&b> &c- &6Remove a line of text from your item's lore\n"
                            + "&b/itemizer lore change <&findex&b> <&ftext&b> &c- &6Change a line of text in your item's lore"));
                    return true;
                }
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                else if (args.length >= 2)
                {
                    switch (args[1])
                    {
                        case "add":
                        {
                            if (args.length == 2)
                            {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                        + "&b/itemizer lore add <&ftext&b> &c- &6Add a line of text to your item's lore"));
                                return true;
                            }
                            String lore = colorize(StringUtils.join(args, " ", 2, args.length));
                            assert meta != null;
                            List<String> lores = new ArrayList<>();
                            if (meta.getLore() != null)
                            {
                                lores = meta.getLore();
                            }
                            lores.add(lore);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + lore + "&f'&2 added to the item's lore"));
                            return true;
                        }
                        case "remove":
                        {
                            if (args.length == 2)
                            {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                        + "&b/itemizer lore remove <&findex&b> &c- &6Remove a line of text from your item's lore"));
                                return true;
                            }
                            int index = parseInt(sender, args[2]);
                            if (index == -1)
                            {
                                return true;
                            }
                            assert meta != null;
                            List<String> lores = new ArrayList<>(meta.getLore());
                            if (meta.getLore() == null || meta.getLore().isEmpty())
                            {
                                sender.sendMessage(colorize("&eThis item has no lores."));
                                return true;
                            }
                            lores.remove(index - 1);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + index + "&f'&2 removed from the item's lore"));
                            return true;
                        }
                        case "change":
                        {
                            if (args.length < 4)
                            {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                        + "&b/itemizer lore change <&findex&b> <&ftext&b> &c- &6Change a line of text in your item's lore"));
                                return true;
                            }
                            int index = parseInt(sender, args[2]);
                            if (index == -1)
                            {
                                return true;
                            }
                            String lore = colorize(StringUtils.join(args, " ", 3, args.length));
                            assert meta != null;
                            List<String> lores = new ArrayList<>(meta.getLore());
                            if (lores == null || lores.isEmpty())
                            {
                                sender.sendMessage(colorize("&eThis item has no lores."));
                                return true;
                            }
                            lores.set(index - 1, lore);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + index + "'&2 has changed to &f'" + lore + "&f'"));
                            return true;
                        }
                        default:
                        {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer lore &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "potion":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                            + "&b/itemizer potion add <&feffect&b> <&flevel&b> <&ftime[tick]&b> &c- &6Add a potion effect\n"
                            + "&b/itemizer potion remove <&feffect&b> &c- &6Remove a potion effect\n"
                            + "&b/itemizer potion list &c- &6List all potion effects"));
                    return true;
                }
                if (!hasPotion)
                {
                    sender.sendMessage("Get a POTION in hand!");
                    return true;
                }
                else if (args.length >= 2)
                {
                    switch (args[1])
                    {
                        case "add":
                        {
                            if (args.length < 5)
                            {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                        + "&b/itemizer potion add <&feffect&b> <&flevel&b> <&ftime[tick]&b> &c- &6Add a potion effect"));
                                return true;
                            }
                            PotionEffectType potType = PotionEffectType.getByName(args[2].toUpperCase());
                            if (potType == null)
                            {
                                sender.sendMessage(colorize("&4The potion &f\"" + args[2] + "&f\"&4 does not exist!"));
                                return true;
                            }
                            int level = parseInt(sender, args[4]);
                            int tick = parseInt(sender, args[3]);
                            if (level == -1 || tick == -1)
                            {
                                return true;
                            }
                            final PotionEffect pot = new PotionEffect(potType, level, tick);
                            final PotionMeta potionMeta = (PotionMeta) meta;
                            assert potionMeta != null;
                            if (potionMeta.hasCustomEffect(pot.getType()))
                            {
                                sender.sendMessage(colorize("&4This potion already has &f" + pot.getType().getName()));
                                return true;
                            }
                            potionMeta.addCustomEffect(pot, false);
                            item.setItemMeta(potionMeta);
                            sender.sendMessage(colorize(pot.getType().getName() + " &2 added to the potion"));
                            return true;
                        }
                        case "remove":
                        {
                            if (args.length == 2)
                            {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                        + "&b/itemizer potion remove <&feffect&b> &c- &6Remove a potion effect"));
                                return true;
                            }
                            PotionEffectType potType = PotionEffectType.getByName(args[2].toUpperCase());
                            if (potType == null)
                            {
                                sender.sendMessage("&4The potion effect &f\"" + args[2] + "&f\"&4 does not exist!");
                                return true;
                            }
                            final PotionMeta potionMeta = (PotionMeta) meta;
                            assert potionMeta != null;
                            if (!potionMeta.hasCustomEffect(potType))
                            {
                                sender.sendMessage(colorize("This potion does not have &f" + potType.getName()));
                                return true;
                            }
                            potionMeta.removeCustomEffect(potType);
                            item.setItemMeta(potionMeta);
                            sender.sendMessage(colorize(potType.getName() + " &2has been removed from the potion"));
                            return true;
                        }
                        case "list":
                        {
                            StringBuilder sb = new StringBuilder();
                            PotionEffectType[] effects;
                            for (int i = 0; i < (effects = PotionEffectType.values()).length; i++)
                            {
                                sb.append(", ").append(effects[i].getName());
                            }
                            sender.sendMessage(colorize("&2Available potion effects: &e"
                                    + sb.toString().replaceFirst(", ", "")));
                            return true;
                        }
                        default:
                        {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer potion &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "attr":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dAttribute Commands&f]&3===============\n"
                            + "&b/itemizer attr add <&fname&b> <&fstrength&b> [&fslot&b] &c - &6Add an attribute\n"
                            + "&b/itemizer attr remove <&fname&b> &c- &6Remove an attribute\n"
                            + "&b/itemizer attr list &c- &6List all item's attributes\n"
                            + "&b/itemizer attr listall &c- &6List all supported attributes"));
                    return true;
                }
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                else if (args.length >= 2)
                {
                    switch (args[1])
                    {
                        case "add":
                        {
                            AttributeManager.addAttr(player, args);
                            return true;
                        }
                        case "remove":
                        {
                            AttributeManager.removeAttr(player, args[2]);
                            return true;
                        }
                        case "list":
                        {
                            AttributeManager.listAttr(player);
                            return true;
                        }
                        case "listall":
                        {
                            sender.sendMessage(colorize("&2Supported attributes: "
                                    + "&e" + AttributeManager.Attributes.getAttributes()));
                            return true;
                        }
                        default:
                        {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer attr &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "flag":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dFlag Commands&f]&3===============\n"
                            + "&b/itemizer flag add <&fname&b> &c- &6Add a flag\n"
                            + "&b/itemizer flag remove <&fname&b> &c- &6Remove a flag\n"
                            + "&b/itemizer flag list &c- &6List all item's flag\n"
                            + "&b/itemizer flag listall &c- &6Listt all available flags"));
                    return true;
                }
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                switch (args[1])
                {
                    case "add":
                    {
                        if (args.length == 2)
                        {
                            sender.sendMessage(colorize("&3===============&f[&dFlag Commands&f]&3===============\n"
                                    + "&b/itemizer flag add <&fname&b> &c- &6Add a flag"));
                            return true;
                        }
                        final ItemFlag flag = ItemFlag.valueOf(args[2].toUpperCase());
                        if (flag == null)
                        {
                            sender.sendMessage(colorize("&4The flag &f'" + args[2].toUpperCase() + "&f' does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (meta.getItemFlags().contains(flag))
                        {
                            sender.sendMessage(colorize("&4The flag &f'" + args[2].toUpperCase() + "' &4already added to the item!"));
                            return true;
                        }
                        meta.addItemFlags(new ItemFlag[]{flag});
                        item.setItemMeta(meta);
                        sender.sendMessage(colorize("&2The flag &f'" + args[2].toUpperCase() + "' &2has been added to your item!"));
                        return true;
                    }
                    case "remove":
                    {
                        if (args.length == 2)
                        {
                            sender.sendMessage(colorize("&3===============&f[&dFlag Commands&f]&3===============\n"
                                    + "&b/itemizer flag remove <&fname&b> &c- &6remove a flag"));
                            return true;
                        }
                        final ItemFlag flag = ItemFlag.valueOf(args[2].toUpperCase());
                        if (flag == null)
                        {
                            sender.sendMessage(colorize("&4The flag &f\"" + args[2] + "&f\" does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (!meta.getItemFlags().contains(flag))
                        {
                            sender.sendMessage(colorize("&4The flag &f'" + args[2].toUpperCase() + "' &4has not been added the item!"));
                            return true;
                        }
                        meta.removeItemFlags(new ItemFlag[]{flag});
                        item.setItemMeta(meta);
                        sender.sendMessage(colorize("&2The flag &f'" + args[2].toUpperCase() + "' &2has been removed from your item!"));
                        return true;
                    }
                    case "list":
                    {
                        assert meta != null;
                        if (meta.getItemFlags() == null || meta.getItemFlags().isEmpty())
                        {
                            sender.sendMessage(colorize("&4The item in your hand does not have any flags"));
                            return true;
                        }
                        sender.sendMessage(colorize("&2Item flags: &e"
                                + StringUtils.join(meta.getItemFlags(), ", ")));
                        return true;
                    }
                    case "listall":
                    {
                        sender.sendMessage("&2Available item flags: &e"
                                + StringUtils.join(ItemFlag.values(), ", "));
                        return true;
                    }
                    default:
                    {
                        sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer flag &bfor help"));
                        return true;
                    }
                }
            }
            case "enchant":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dEnchant Commands&f]&3===============\n"
                            + "&b/itemizer enchant add <&fname&b> <&flevel&b> &c- &6Add an enchant\n"
                            + "&b/itemizer enchant remove <&fname&b> &c- &6Remove an enchant\n"
                            + "&b/itemizer enchant list &c- &6List all item's enchants\n"
                            + "&b/itemizer enchant listall &c- &6Listt all available enchants"));
                    return true;
                }
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                switch (args[1])
                {
                    case "add":
                    {
                        if (args.length < 4)
                        {
                            sender.sendMessage(colorize("&3===============&f[&dEnchant Commands&f]&3===============\n"
                                    + "&b/itemizer enchant add <&fname&b> <&flevel&b> &c- &6Add an enchant"));
                            return true;
                        }
                        final Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[2].toLowerCase()));
                        if (ench == null)
                        {
                            sender.sendMessage(colorize("&4The enchantment &f'" + args[2] + "&f' &4does not exist!"));
                            return true;
                        }
                        int level = parseInt(sender, args[3]);
                        if (level == -1)
                        {
                            return true;
                        }
                        item.addUnsafeEnchantment(ench, level);
                        sender.sendMessage(colorize("&2The enchant &f'" + ench.getKey().getKey() + "' &2has been added to your item"));
                        return true;
                    }
                    case "remove":
                    {
                        if (args.length == 2)
                        {
                            sender.sendMessage(colorize("&3===============&f[&dEnchant Commands&f]&3===============\n"
                                    + "&b/itemizer enchant remove <&fname&b> &c- &6Remove an enchant"));
                            return true;
                        }
                        final Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[2].toLowerCase()));
                        if (ench == null)
                        {
                            sender.sendMessage(colorize("&4The enchantment &f'" + args[2] + "&f' &4does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (meta.getEnchants() == null || meta.getEnchants().isEmpty())
                        {
                            sender.sendMessage(colorize("&4This item doesn't hold any enchants"));
                            return true;
                        }
                        if (!meta.getEnchants().containsKey(ench))
                        {
                            sender.sendMessage(colorize("&4This item doesn't have &f'" + ench.getKey().getKey() + "' &4enchant!"));
                            return true;
                        }
                        item.removeEnchantment(ench);
                        sender.sendMessage(colorize("&2The enchant &f'" + ench.getKey().getKey() + "' &2has been removed from your item"));
                        return true;
                    }
                    case "list":
                    {
                        assert meta != null;
                        if (meta.getEnchants() == null || meta.getEnchants().isEmpty())
                        {
                            sender.sendMessage(colorize("&4This item doesn't hold any enchants"));
                            return true;
                        }
                        sender.sendMessage(colorize("&2Item enchants: &e"
                                + StringUtils.join(meta.getEnchants().keySet(), ", ")));
                        return true;
                    }
                    case "listall":
                    {
                        StringBuilder sb = new StringBuilder();
                        Enchantment[] enchantments;
                        for (int i = 0; i < (enchantments = Enchantment.values()).length; i++)
                        {
                            sb.append(", ").append(enchantments[i].getKey().getKey());
                        }
                        sender.sendMessage(colorize("&2Available item enchants: &e"
                                + sb.toString().replaceFirst(", ", "")));
                        return true;
                    }
                    default:
                    {
                        sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer enchant &bfor help."));
                        return true;
                    }
                }
            }
            case "title":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dTitle Command&f]&3===============\n"
                            + "&b/itemizer title <&fname&b> &c- &6Set the book's title"));
                    return true;
                }
                if (!hasBook)
                {
                    sender.sendMessage("Get a WRITTEN_BOOK in hand!");
                    return true;
                }
                String name = colorize(StringUtils.join(args, " ", 1, args.length));
                final BookMeta bookMeta = (BookMeta) meta;
                assert bookMeta != null;
                bookMeta.setTitle(name);
                item.setItemMeta(bookMeta);
                sender.sendMessage(colorize("&2The title of the book has been set to &f'" + name + "&f'"));
                return true;
            }
            case "author":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dAuthor Command&f]&3===============\n"
                            + "&b/itemizer author <&fname&b> &c- &6Set the book's title"));
                    return true;
                }
                String name = colorize(args[1]);
                final BookMeta bookMeta = (BookMeta) meta;
                assert bookMeta != null;
                bookMeta.setAuthor(name);
                item.setItemMeta(bookMeta);
                sender.sendMessage(colorize("&2The author of the book has been set to &f'" + name + "&f'"));
                return true;
            }
            case "head":
            {
                if (args.length == 1)
                {
                    sender.sendMessage(colorize("&3===============&f[&dHead Command&f]&3===============\n"
                            + "&b/itemizer head <&fname&b> &c- &6Set the player of the head"));
                    return true;
                }
                if (item.getType() != Material.PLAYER_HEAD)
                {
                    sender.sendMessage("Get a SKULL in hand!");
                    return true;
                }
                final SkullMeta skullMeta = (SkullMeta) meta;
                assert skullMeta != null;
                skullMeta.setOwner(args[1]);
                item.setItemMeta(skullMeta);
                sender.sendMessage(colorize("&2The player of the head has been set to &f'" + args[1] + "&f'"));
            }
            case "clearall":
            {
                if (!hasItem)
                {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                item.setItemMeta(null);
                sender.sendMessage(colorize("&2All data cleared from your item"));
                return true;
            }
            default:
            {
                sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer help &bfor help."));
                return true;
            }
        }
    }

    private String colorize(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private int parseInt(CommandSender sender, String string)
    {
        try
        {
            return Integer.parseInt(string);
        }
        catch (NumberFormatException ex)
        {
            sender.sendMessage(colorize("&f\"" + string + "&f\"&4 is not a valid number!"));
        }
        return -1;
    }
}
