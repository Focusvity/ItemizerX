package me.focusvity.itemizerx;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemizerXTab implements TabCompleter
{

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String string, String[] args)
    {
        if (args.length == 1)
        {
            return Arrays.asList("help", "id", "lore", "potion", "attr", "flag", "enchant", "title", "author",
                    "head", "sign", "clearall");
        }

        if (args.length == 2)
        {
            switch (args[0])
            {
                case "id":
                {
                    List<String> materials = new ArrayList<>();
                    for (Material material : Material.values())
                    {
                        materials.add(material.name());
                    }
                    return materials;
                }
                case "lore":
                {
                    return Arrays.asList("add", "remove", "change", "clear");
                }
                case "potion":
                {
                    return Arrays.asList("add", "remove", "change", "color", "list");
                }
                case "attr":
                case "flag":
                case "enchant":
                {
                    return Arrays.asList("add", "remove", "list", "listall");
                }
                default:
                {
                    return Collections.emptyList();
                }
            }
        }

        if (args.length == 3)
        {
            switch (args[0])
            {
                case "potion":
                {
                    switch (args[1])
                    {
                        case "add":
                        case "remove":
                        {
                            List<String> potions = new ArrayList<>();
                            for (PotionEffectType effect : PotionEffectType.values())
                            {
                                potions.add(effect.getName());
                            }
                            return potions;
                        }
                    }
                }
                case "attr":
                {
                    switch (args[1])
                    {
                        case "add":
                        case "remove":
                        {
                            return AttributeManager.Attributes.getAttributeList();
                        }
                    }
                }
                case "flag":
                {
                    switch (args[1])
                    {
                        case "add":
                        case "remove":
                        {
                            List<String> flags = new ArrayList<>();
                            for (ItemFlag flag : ItemFlag.values())
                            {
                                flags.add(flag.name());
                            }
                            return flags;
                        }
                    }
                }
                case "enchant":
                {
                    switch (args[1])
                    {
                        case "add":
                        case "remove":
                        {
                            List<String> enchantments = new ArrayList<>();
                            for (Enchantment enchantment : Enchantment.values())
                            {
                                enchantments.add(enchantment.getKey().getKey());
                            }
                            return enchantments;
                        }
                    }
                }
                default:
                {
                    return Collections.emptyList();
                }
            }
        }

        return Collections.emptyList();
    }
}
