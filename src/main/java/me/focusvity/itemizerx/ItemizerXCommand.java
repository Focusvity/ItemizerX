package me.focusvity.itemizerx;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ItemizerXCommand implements CommandExecutor {

    final List<Material> POTIONS = Arrays.asList(Material.POTION, Material.LINGERING_POTION, Material.SPLASH_POTION);

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if (!sender.hasPermission("itemizer.use")) {
            sender.sendMessage("&4You don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "ItemizerX " + ChatColor.GOLD + "v"
                + ItemizerX.plugin.getDescription().getVersion()
                + ChatColor.AQUA + " by " + ChatColor.GOLD
                + StringUtils.join(ItemizerX.plugin.getDescription().getAuthors(), ", "));
            sender.sendMessage(ChatColor.AQUA + "Type " + ChatColor.GOLD + "/itemizer help "
                + ChatColor.AQUA + "for help");
            return true;
        }

        if (!(sender instanceof final Player player)) {
            sender.sendMessage(colorize("&4You must be a player to execute this command!"));
            return true;
        }

        final ItemStack item = player.getInventory().getItemInMainHand();
        final boolean hasItem = item.getType() != Material.AIR;
        final boolean hasPotion = POTIONS.contains(item.getType());
        final boolean hasBook = item.getType() == Material.WRITTEN_BOOK;
        final ItemMeta meta = item.getItemMeta();

        switch (args[0]) {
            case "help" -> {
                sender.sendMessage(colorize("""
                    &3=============&f[&dItemizerX Commands&f]&3=============
                    &b/itemizer name <&fname&b> &c- &6Name your item
                    &b/itemizer id <&fid&b> &c- &6Change the item's material
                    &b/itemizer lore &c- &6Lore editing command
                    &b/itemizer potion &c- &6Potion editing command
                    &b/itemizer attr &c- &6Attribute editing command
                    &b/itemizer flag &c- &6Flag editing command
                    &b/itemizer enchant &c- &6Enchant editing command
                    &b/itemizer title <&fname&b> &c- &6Set the book's title
                    &b/itemizer author <&fname&b> &c- &6Set the book's author
                    &b/itemizer head <&fname&b> &c- &6Set the player of the head
                    &b/itemizer sign <&fline&b> <&ftext&b> &c- &6Change the line on the sign
                    &b/itemizer clearall &c- &6Clears all metadata from your item"""));
                return true;
            }
            case "name" -> {
                if (!sender.hasPermission("itemizer.name")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("&3===============&f[&dName Commands&f]&3===============\n"
                        + "&b/itemizer name <&fname&b> &c - &6Name your item"));
                } else {
                    if (!hasItem) {
                        sender.sendMessage("Get an ITEM in hand!");
                        return true;
                    }
                    String name = colorize(StringUtils.join(args, " ", 1, args.length));
                    assert meta != null;
                    meta.setDisplayName(name);
                    item.setItemMeta(meta);
                    sender.sendMessage(colorize("&2The name of the item in your hand has been set to &f'" + name + "&f'"));
                }
                return true;
            }
            case "id" -> {
                if (!sender.hasPermission("itemizer.id")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("&3===============&f[&dID Commands&f]&3===============\n"
                        + "&b/itemizer id <&fid&b> &c- &6Change the item's material"));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                Material material = Material.matchMaterial(args[1].toUpperCase());
                if (material == null) {
                    sender.sendMessage(colorize("&4The material &f\"" + args[1] + "&f\"&4 does not exist!"));
                    return true;
                }
                item.setType(material);
                sender.sendMessage(colorize("&2The material of the item has changed to &f'" + material.name() + "'"));
                return true;
            }
            case "lore" -> {
                if (!sender.hasPermission("itemizer.lore")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("""
                        &3===============&f[&dLore Commands&f]&3===============
                        &b/itemizer lore add <&ftext&b> &c- &6Add a line of text to your item's lore
                        &b/itemizer lore remove <&findex&b> &c- &6Remove a line of text from your item's lore
                        &b/itemizer lore change <&findex&b> <&ftext&b> &c- &6Change a line of text in your item's lore
                        &b/itemizer lore clear &c- &6Clear the item's lore"""));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                } else {
                    switch (args[1]) {
                        case "add" -> {
                            if (!sender.hasPermission("itemizer.lore.add")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length == 2) {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                    + "&b/itemizer lore add <&ftext&b> &c- &6Add a line of text to your item's lore"));
                                return true;
                            }
                            String lore = colorize(StringUtils.join(args, " ", 2, args.length));
                            assert meta != null;
                            List<String> lores = new ArrayList<>();
                            if (meta.getLore() != null) {
                                lores = meta.getLore();
                            }
                            lores.add(lore);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + lore + "&f'&2 added to the item's lore"));
                            return true;
                        }
                        case "remove" -> {
                            if (!sender.hasPermission("itemizer.lore.remove")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length == 2) {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                    + "&b/itemizer lore remove <&findex&b> &c- &6Remove a line of text from your item's lore"));
                                return true;
                            }
                            Integer index = parseInt(sender, args[2]);
                            if (index == null) {
                                return true;
                            }
                            assert meta != null;
                            List<String> lores;
                            if (meta.getLore() != null) {
                                lores = meta.getLore();
                            } else {
                                sender.sendMessage(colorize("&eThis item has no lores."));
                                return true;
                            }
                            if (index > lores.size()) {
                                sender.sendMessage(colorize("&4The item's lore doesn't have line &f'" + index + "'"));
                                return true;
                            }
                            lores.remove(index - 1);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + index + "&f'&2 removed from the item's lore"));
                            return true;
                        }
                        case "change" -> {
                            if (!sender.hasPermission("itemizer.lore.change")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length < 4) {
                                sender.sendMessage(colorize("&3===============&f[&dLore Commands&f]&3===============\n"
                                    + "&b/itemizer lore change <&findex&b> <&ftext&b> &c- &6Change a line of text in your item's lore"));
                                return true;
                            }
                            Integer index = parseInt(sender, args[2]);
                            if (index == null) {
                                return true;
                            }
                            String lore = colorize(StringUtils.join(args, " ", 3, args.length));
                            assert meta != null;
                            List<String> lores;
                            if (meta.getLore() != null) {
                                lores = meta.getLore();
                            } else {
                                sender.sendMessage(colorize("&eThis item has no lores."));
                                return true;
                            }
                            if (index > lores.size()) {
                                sender.sendMessage(colorize("&4The item's lore doesn't have line &f'" + index + "'"));
                                return true;
                            }
                            lores.set(index - 1, lore);
                            meta.setLore(lores);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2Line &f'" + index + "'&2 has changed to &f'" + lore + "&f'"));
                            return true;
                        }
                        case "clear" -> {
                            if (!sender.hasPermission("itemizer.lore.clear")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            assert meta != null;
                            if (meta.getLore() == null || meta.getLore().isEmpty()) {
                                sender.sendMessage(colorize("&4The item has no lores."));
                                return true;
                            }
                            meta.setLore(null);
                            item.setItemMeta(meta);
                            sender.sendMessage(colorize("&2The item's lore has been cleared!"));
                            return true;
                        }
                        default -> {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer lore &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "potion" -> {
                if (!sender.hasPermission("itemizer.potion")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("""
                        &3===============&f[&dPotion Commands&f]&3===============
                        &b/itemizer potion add <&feffect&b> <&flevel&b> <&ftime[tick]&b> &c- &6Add a potion effect
                        &b/itemizer potion remove <&feffect&b> &c- &6Remove a potion effect
                        &b/itemizer potion change <&fname&b> &c- &6Change the potion type
                        &b/itemizer potion color <&fhexcolor&b> &c- &6Set the potion color
                        &b/itemizer potion list &c- &6List all potion effects"""));
                    return true;
                }
                if (!hasPotion) {
                    sender.sendMessage("Get a POTION in hand!");
                    return true;
                } else {
                    switch (args[1]) {
                        case "add" -> {
                            if (!sender.hasPermission("itemizer.potion.add")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length < 5) {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                    + "&b/itemizer potion add <&feffect&b> <&flevel&b> <&ftime[tick]&b> &c- &6Add a potion effect"));
                                return true;
                            }
                            PotionEffectType potType = PotionEffectType.getByName(args[2].toUpperCase());
                            if (potType == null) {
                                sender.sendMessage(colorize("&4The potion &f\"" + args[2] + "&f\"&4 does not exist!"));
                                return true;
                            }
                            Integer level = parseInt(sender, args[3]);
                            Integer tick = parseInt(sender, args[4]);
                            if (level == null || tick == null) {
                                return true;
                            }
                            final PotionEffect pot = new PotionEffect(potType, tick, level);
                            final PotionMeta potionMeta = (PotionMeta) meta;
                            assert potionMeta != null;
                            if (potionMeta.hasCustomEffect(pot.getType())) {
                                sender.sendMessage(colorize("&4This potion already has &f" + pot.getType().getName()));
                                return true;
                            }
                            potionMeta.addCustomEffect(pot, false);
                            item.setItemMeta(potionMeta);
                            sender.sendMessage(colorize(pot.getType().getName() + " &2has been added to the potion"));
                            return true;
                        }
                        case "remove" -> {
                            if (!sender.hasPermission("itemizer.potion.remove")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length == 2) {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                    + "&b/itemizer potion remove <&feffect&b> &c- &6Remove a potion effect"));
                                return true;
                            }
                            PotionEffectType potType = PotionEffectType.getByName(args[2].toUpperCase());
                            if (potType == null) {
                                sender.sendMessage("&4The potion effect &f\"" + args[2] + "&f\"&4 does not exist!");
                                return true;
                            }
                            final PotionMeta potionMeta = (PotionMeta) meta;
                            assert potionMeta != null;
                            if (!potionMeta.hasCustomEffect(potType)) {
                                sender.sendMessage(colorize("This potion does not have &f" + potType.getName()));
                                return true;
                            }
                            potionMeta.removeCustomEffect(potType);
                            item.setItemMeta(potionMeta);
                            sender.sendMessage(colorize(potType.getName() + " &2has been removed from the potion"));
                            return true;
                        }
                        case "change" -> {
                            if (!sender.hasPermission("itemizer.potion.change")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length == 2) {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                    + "&b/itemizer potion change <&fname&b> &c- &6Change the potion type"));
                                return true;
                            }
                            Material material = Material.matchMaterial(args[2]);
                            if (material == null || !POTIONS.contains(material)) {
                                sender.sendMessage(colorize(material != null ?
                                    "&f'" + material.name() + "' &4is not a potion type!"
                                    :
                                        "&4That material doesn't exist!"));
                                return true;
                            }
                            item.setType(material);
                            sender.sendMessage(colorize("&2The potion in hand has changed to &f'" + material.name() + "'"));
                            return true;
                        }
                        case "color" -> {
                            if (!sender.hasPermission("itemizer.potion.color")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            if (args.length < 3) {
                                sender.sendMessage(colorize("&3===============&f[&dPotion Commands&f]&3===============\n"
                                    + "&b/itemizer potion color <&fhexcolor&b> &c- &6Set a potion color"));
                                return true;
                            }
                            final PotionMeta potionMeta = (PotionMeta) meta;
                            assert potionMeta != null;
                            try {
                                java.awt.Color awtColor = java.awt.Color.decode(args[2]);
                                Color color = Color.fromRGB(awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue());
                                potionMeta.setColor(color);
                                item.setItemMeta(potionMeta);
                                sender.sendMessage(colorize(args[2] + " &2has been set as potion color"));
                            } catch (NumberFormatException ignored) {
                                sender.sendMessage(colorize("&4The hex &f\"" + args[2] + "&f\"&4 is invalid!"));
                                return true;
                            }
                            return true;
                        }
                        case "list" -> {
                            if (!sender.hasPermission("itemizer.potion.list")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            StringBuilder sb = new StringBuilder();
                            PotionEffectType[] effects;
                            for (int i = 0; i < (effects = PotionEffectType.values()).length; i++) {
                                sb.append(", ").append(effects[i].getName());
                            }
                            sender.sendMessage(colorize("&2Available potion effects: &e"
                                + sb.toString().replaceFirst(", ", "")));
                            return true;
                        }
                        default -> {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer potion &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "attr" -> {
                if (!sender.hasPermission("itemizer.attr")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("""
                        &3===============&f[&dAttribute Commands&f]&3===============
                        &b/itemizer attr add <&fname&b> <&fstrength&b> [&fslot&b] &c - &6Add an attribute
                        &b/itemizer attr remove <&fname&b> &c- &6Remove an attribute
                        &b/itemizer attr list &c- &6List all item's attributes
                        &b/itemizer attr listall &c- &6List all supported attributes"""));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                } else {
                    switch (args[1]) {
                        case "add" -> {
                            if (!sender.hasPermission("itemizer.attr.add")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            AttributeManager.addAttr(player, args);
                            return true;
                        }
                        case "remove" -> {
                            if (!sender.hasPermission("itemizer.attr.remove")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            AttributeManager.removeAttr(player, args[2]);
                            return true;
                        }
                        case "list" -> {
                            if (!sender.hasPermission("itemizer.attr.list")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            AttributeManager.listAttr(player);
                            return true;
                        }
                        case "listall" -> {
                            if (!sender.hasPermission("itemizer.attr.listall")) {
                                sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                                return true;
                            }
                            sender.sendMessage(colorize("&2Supported attributes: "
                                + "&e" + AttributeManager.Attributes.getAttributes()));
                            return true;
                        }
                        default -> {
                            sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer attr &bfor help."));
                            return true;
                        }
                    }
                }
            }
            case "flag" -> {
                if (!sender.hasPermission("itemizer.flag")) {
                    sender.sendMessage("&4You don't have permission to use this command!");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("""
                        &3===============&f[&dFlag Commands&f]&3===============
                        &b/itemizer flag add <&fname&b> &c- &6Add a flag
                        &b/itemizer flag remove <&fname&b> &c- &6Remove a flag
                        &b/itemizer flag list &c- &6List all item's flag
                        &b/itemizer flag listall &c- &6List all available flags"""));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                switch (args[1]) {
                    case "add" -> {
                        if (!sender.hasPermission("itemizer.flag.add")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        if (args.length == 2) {
                            sender.sendMessage(colorize("&3===============&f[&dFlag Commands&f]&3===============\n"
                                + "&b/itemizer flag add <&fname&b> &c- &6Add a flag"));
                            return true;
                        }
                        ItemFlag flag = null;
                        try {
                            flag = ItemFlag.valueOf(args[2].toUpperCase());
                        } catch (Exception ignored) {
                        }
                        if (flag == null) {
                            sender.sendMessage(colorize("&4The flag &f\"" + args[2] + "&f\" does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (meta.getItemFlags().contains(flag)) {
                            sender.sendMessage(colorize("&4The flag &f'" + args[2].toUpperCase() + "' &4already added to the item!"));
                            return true;
                        }
                        meta.addItemFlags(flag);
                        item.setItemMeta(meta);
                        sender.sendMessage(colorize("&2The flag &f'" + args[2].toUpperCase() + "' &2has been added to your item!"));
                        return true;
                    }
                    case "remove" -> {
                        if (!sender.hasPermission("itemizer.flag.remove")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        if (args.length == 2) {
                            sender.sendMessage(colorize("&3===============&f[&dFlag Commands&f]&3===============\n"
                                + "&b/itemizer flag remove <&fname&b> &c- &6remove a flag"));
                            return true;
                        }
                        ItemFlag flag = null;
                        try {
                            flag = ItemFlag.valueOf(args[2].toUpperCase());
                        } catch (Exception ignored) {
                        }
                        if (flag == null) {
                            sender.sendMessage(colorize("&4The flag &f\"" + args[2] + "&f\" does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (!meta.getItemFlags().contains(flag)) {
                            sender.sendMessage(colorize("&4The flag &f'" + args[2].toUpperCase() + "' &4has not been added the item!"));
                            return true;
                        }
                        meta.removeItemFlags(flag);
                        item.setItemMeta(meta);
                        sender.sendMessage(colorize("&2The flag &f'" + args[2].toUpperCase() + "' &2has been removed from your item!"));
                        return true;
                    }
                    case "list" -> {
                        if (!sender.hasPermission("itemizer.flag.list")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        assert meta != null;
                        if (Objects.requireNonNull(meta.getItemFlags()).isEmpty()) {
                            sender.sendMessage(colorize("&4The item in your hand does not have any flags"));
                            return true;
                        }
                        sender.sendMessage(colorize("&2Item flags: &e"
                            + StringUtils.join(meta.getItemFlags(), ", ")));
                        return true;
                    }
                    case "listall" -> {
                        if (!sender.hasPermission("itemizer.flag.listall")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        sender.sendMessage(colorize("&2Available item flags: &e"
                            + StringUtils.join(ItemFlag.values(), ", ")));
                        return true;
                    }
                    default -> {
                        sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer flag &bfor help"));
                        return true;
                    }
                }
            }
            case "enchant" -> {
                if (!sender.hasPermission("itemizer.enchant")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("""
                        &3===============&f[&dEnchant Commands&f]&3===============
                        &b/itemizer enchant add <&fname&b> <&flevel&b> &c- &6Add an enchant
                        &b/itemizer enchant remove <&fname&b> &c- &6Remove an enchant
                        &b/itemizer enchant list &c- &6List all item's enchants
                        &b/itemizer enchant listall &c- &6List all available enchants"""));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                switch (args[1]) {
                    case "add" -> {
                        if (!sender.hasPermission("itemizer.enchant.add")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        if (args.length < 4) {
                            sender.sendMessage(colorize("&3===============&f[&dEnchant Commands&f]&3===============\n"
                                + "&b/itemizer enchant add <&fname&b> <&flevel&b> &c- &6Add an enchant"));
                            return true;
                        }
                        final Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[2].toLowerCase()));
                        if (ench == null) {
                            sender.sendMessage(colorize("&4The enchantment &f'" + args[2] + "&f' &4does not exist!"));
                            return true;
                        }
                        Integer level = parseInt(sender, args[3]);
                        if (level == null) {
                            return true;
                        }
                        item.addUnsafeEnchantment(ench, level);
                        sender.sendMessage(colorize("&2The enchant &f'" + ench.getKey().getKey() + "' &2has been added to your item"));
                        return true;
                    }
                    case "remove" -> {
                        if (!sender.hasPermission("itemizer.enchant.remove")) {
                            sender.sendMessage("&4You don't have permission to use this command!");
                            return true;
                        }
                        if (args.length == 2) {
                            sender.sendMessage(colorize("&3===============&f[&dEnchant Commands&f]&3===============\n"
                                + "&b/itemizer enchant remove <&fname&b> &c- &6Remove an enchant"));
                            return true;
                        }
                        final Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(args[2].toLowerCase()));
                        if (ench == null) {
                            sender.sendMessage(colorize("&4The enchantment &f'" + args[2] + "&f' &4does not exist!"));
                            return true;
                        }
                        assert meta != null;
                        if (Objects.requireNonNull(meta.getEnchants()).isEmpty()) {
                            sender.sendMessage(colorize("&4This item doesn't hold any enchants"));
                            return true;
                        }
                        if (!meta.getEnchants().containsKey(ench)) {
                            sender.sendMessage(colorize("&4This item doesn't have &f'" + ench.getKey().getKey() + "' &4enchant!"));
                            return true;
                        }
                        item.removeEnchantment(ench);
                        sender.sendMessage(colorize("&2The enchant &f'" + ench.getKey().getKey() + "' &2has been removed from your item"));
                        return true;
                    }
                    case "list" -> {
                        if (!sender.hasPermission("itemizer.enchant.list")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        assert meta != null;
                        if (Objects.requireNonNull(meta.getEnchants()).isEmpty()) {
                            sender.sendMessage(colorize("&4This item doesn't hold any enchants"));
                            return true;
                        }
                        sender.sendMessage(colorize("&2Item enchants: &e"
                            + StringUtils.join(meta.getEnchants().keySet(), ", ")));
                        return true;
                    }
                    case "listall" -> {
                        if (!sender.hasPermission("itemizer.enchant.listall")) {
                            sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                            return true;
                        }
                        StringBuilder sb = new StringBuilder();
                        Enchantment[] enchantments;
                        for (int i = 0; i < (enchantments = Enchantment.values()).length; i++) {
                            sb.append(", ").append(enchantments[i].getKey().getKey());
                        }
                        sender.sendMessage(colorize("&2Available item enchants: &e"
                            + sb.toString().replaceFirst(", ", "")));
                        return true;
                    }
                    default -> {
                        sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer enchant &bfor help."));
                        return true;
                    }
                }
            }
            case "title" -> {
                if (!sender.hasPermission("itemizer.title")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("&3===============&f[&dTitle Command&f]&3===============\n"
                        + "&b/itemizer title <&fname&b> &c- &6Set the book's title"));
                    return true;
                }
                if (!hasBook) {
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
            case "author" -> {
                if (!sender.hasPermission("itemizer.author")) {
                    sender.sendMessage("&4You don't have permission to use this command!");
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("&3===============&f[&dAuthor Command&f]&3===============\n"
                        + "&b/itemizer author <&fname&b> &c- &6Set the book's title"));
                    return true;
                }
                if (!hasBook) {
                    sender.sendMessage("Get a WRITTEN_BOOK in hand!");
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
            case "head" -> {
                if (!sender.hasPermission("itemizer.head")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (args.length == 1) {
                    sender.sendMessage(colorize("&3===============&f[&dHead Command&f]&3===============\n"
                        + "&b/itemizer head <&fname&b> &c- &6Set the player of the head"));
                    return true;
                }
                if (item.getType() != Material.PLAYER_HEAD) {
                    sender.sendMessage("Get a SKULL in hand!");
                    return true;
                }
                String name = args[1];
                if (name.length() > 16) {
                    name = name.substring(0, 16);
                }
                final SkullMeta skullMeta = (SkullMeta) meta;
                assert skullMeta != null;
                skullMeta.setOwner(name);
                item.setItemMeta(skullMeta);
                sender.sendMessage(colorize("&2The player of the head has been set to &f'" + name + "&f'"));
                return true;
            }
            case "sign" -> {
                if (!sender.hasPermission("itemizer.sign")) {
                    sender.sendMessage("&4You don't have permission to use this command!");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(colorize("&3===============&f[&dSign Command&f]&3===============\n"
                        + "&b/itemizer sign <&fline&b> <&ftext&b> &c- &6Change the line on the sign"));
                    return true;
                }
                final Block block = player.getTargetBlockExact(20);
                if (block == null || block.getType() == Material.AIR
                    || !block.getType().toString().contains("SIGN")) {
                    sender.sendMessage(colorize("&4Please look at a sign!"));
                    return true;
                }
                Integer line = parseInt(sender, args[1]);
                if (line == null) {
                    return true;
                } else if (line > 4) {
                    sender.sendMessage(colorize("&4There's maximum of 4 lines on a sign"));
                    return true;
                }
                String text = colorize(StringUtils.join(args, " ", 2, args.length));
                if (CoreProtectBridge.getAPI() != null) {
                    CoreProtectBridge.getAPI().logRemoval(player.getName(), block.getLocation(), block.getType(), block.getBlockData());
                }
                Sign sign = (Sign) block.getState();
                sign.setLine(line - 1, text);
                sign.update();
                if (CoreProtectBridge.getAPI() != null) {
                    CoreProtectBridge.getAPI().logPlacement(player.getName(), sign.getLocation(), sign.getType(), sign.getBlockData());
                }
                sender.sendMessage(colorize("&2Line &f'" + line + "'&2 has successfully changed to &f'" + text + "&f'"));
                return true;
            }
            case "clearall" -> {
                if (!sender.hasPermission("itemizer.clearall")) {
                    sender.sendMessage(colorize("&4You don't have permission to use this command!"));
                    return true;
                }
                if (!hasItem) {
                    sender.sendMessage("Get an ITEM in hand!");
                    return true;
                }
                item.setItemMeta(null);
                sender.sendMessage(colorize("&2All data cleared from your item"));
                return true;
            }
            default -> {
                sender.sendMessage(colorize("&bUnknown sub-command. Type &6/itemizer help &bfor help."));
                return true;
            }
        }
    }

    private String colorize(String string) {
        Matcher matcher = Pattern.compile("&#[a-fA-F0-9]{6}").matcher(string);
        while (matcher.find()) {
            String code = matcher.group().replace("&", "");
            string = string.replace("&" + code, net.md_5.bungee.api.ChatColor.of(code) + "");
        }
        string = ChatColor.translateAlternateColorCodes('&', string);
        return string;
    }

    private Integer parseInt(CommandSender sender, String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException ex) {
            sender.sendMessage(colorize("&f\"" + string + "&f\"&4 is not a valid number!"));
        }
        return null;
    }
}
