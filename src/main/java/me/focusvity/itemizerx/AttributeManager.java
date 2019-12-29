package me.focusvity.itemizerx;

import net.minecraft.server.v1_15_R1.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AttributeManager
{

    public static NBTTagList getAttrList(final ItemStack item)
    {
        NBTTagList attrmod = item.getOrCreateTag().getList("AttributeModifiers", 10);
        if (attrmod == null)
        {
            item.getTag().set("AttributeModifiers", new NBTTagList());
        }
        return item.getTag().getList("AttributeModifiers", 10);
    }

    public static void addAttr(final Player player, final String[] args)
    {
        int op = -1;
        if (args.length < 4)
        {
            player.sendMessage(colorize("&b/itemizer attr add <&fname&b> <&fstrength&b> [&fslot&b] &c- "
                    + "&6Add an attribute"));
            return;
        }
        final Attributes a = Attributes.get(args[2]);
        if (a == null)
        {
            player.sendMessage(colorize("&4\"" + args[2] + "\" is not a valid attribute type."));
            return;
        }
        double amount = 0.0;
        try
        {
            amount = Double.parseDouble(args[3]);
        }
        catch (NumberFormatException ex)
        {
            player.sendMessage(colorize("&4\"" + args[3] + "\" is not a valid number."));
            return;
        }
        final ItemStack nms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        final NBTTagList attrmod = getAttrList(nms);
        for (int i = 0; i < attrmod.size(); i++)
        {
            final NBTTagCompound c = (NBTTagCompound) attrmod.get(i);
            if (c.getString("Name").equals(args[2]))
            {
                player.sendMessage(colorize("&4An attribute with the name \"&f" + args[2] + "&4\"  already exists!"));
                return;
            }
        }
        final NBTTagCompound c = new NBTTagCompound();
        c.set("Name", NBTTagString.a(args[2]));
        c.set("AttributeName", NBTTagString.a(args[2]));
        c.set("Amount", NBTTagDouble.a(amount));
        if (op == -1)
        {
            op = a.op;
        }
        c.set("Operation", NBTTagInt.a(op));
        final UUID uuid = UUID.randomUUID();
        c.set("UUIDMost", NBTTagLong.a(uuid.getMostSignificantBits()));
        c.set("UUIDLeast", NBTTagLong.a(uuid.getLeastSignificantBits()));
        if (args.length == 5)
        {
            final List<String> options = new ArrayList<>();
            options.add("mainhand");
            options.add("offhand");
            options.add("head");
            options.add("chest");
            options.add("legs");
            options.add("feet");
            if (!options.contains(args[4].toLowerCase()))
            {
                player.sendMessage(colorize("&2Supported options:\n"
                        + "&e" + StringUtils.join(options, ", ")));
                return;
            }
            c.set("Slot", NBTTagString.a(args[4].toLowerCase()));
        }
        attrmod.add(c);
        nms.getTag().set("AttributeModifiers", attrmod);
        final org.bukkit.inventory.ItemStack is = CraftItemStack.asCraftMirror(nms);
        player.getInventory().setItemInMainHand(is);
        player.sendMessage(colorize("&2Attribute added!"));
    }

    public static void removeAttr(final Player player, final String string)
    {
        final ItemStack nms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        final NBTTagList attrmod = getAttrList(nms);
        final NBTTagList newList = new NBTTagList();
        boolean r = false;
        for (int i = 0; i < attrmod.size(); i++)
        {
            final NBTTagCompound c = (NBTTagCompound) attrmod.get(i);
            if (!c.getString("Name").equals(string))
            {
                newList.add(attrmod.get(i));
            }
            else
            {
                r = true;
            }
        }
        if (!r)
        {
            player.sendMessage(colorize("&4The attribute \"" + string + "\" doesn't exist!"));
            return;
        }
        nms.getTag().set("AttributeModifiers", newList);
        final org.bukkit.inventory.ItemStack is = CraftItemStack.asCraftMirror(nms);
        player.getInventory().setItemInMainHand(is);
        player.sendMessage(colorize("&2Attribute removed!"));
    }

    public static void listAttr(final Player player)
    {
        final ItemStack nms = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        final NBTTagList attrmod = getAttrList(nms);
        if (attrmod.size() == 0)
        {
            player.sendMessage(colorize("&eThis item has no attributes."));
            return;
        }
        player.sendMessage(colorize("&2Item attributes: "));
        for (int i = 0; i < attrmod.size(); i++)
        {
            final NBTTagCompound c = (NBTTagCompound) attrmod.get(i);
            player.sendMessage(colorize("&e" + Attributes.get(c.getString("AttributeName")).mcName
                    + ", " + c.getDouble("Amount")));
        }
    }

    private static String colorize(String string)
    {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public enum Attributes
    {

        MAX_HEALTH("generic.maxHealth", 0),
        FOLLOW_RANGE("generic.followRange", 1),
        KNOCKBACK_RESISTANCE("generic.knockbackResistance", 1),
        MOVEMENT_SPEED("generic.movementSpeed", 1),
        DAMAGE("generic.attackDamage", 0),
        ARMOR("generic.armor", 0),
        ARMOR_TOUGHNESS("generic.armorToughness", 0),
        FLYING_SPEED("generic.flyingSpeed", 1),
        ATTACK_SPEED("generic.attackSpeed", 1),
        LUCK("generic.luck", 0),
        HORSE_JUMP("horse.jumpStrength", 1),
        ZOMBIE_REINFORCEMENTS("zombie.spawnReinforcements", 1);

        private final String mcName;
        private final int op;

        Attributes(String mcName, int op)
        {
            this.mcName = mcName;
            this.op = op;
        }

        public static Attributes get(String name)
        {
            for (Attributes attr : values())
            {
                if (attr.name().equalsIgnoreCase(name) || attr.mcName.equalsIgnoreCase(name))
                {
                    return attr;
                }
            }
            return null;
        }

        public static String getAttributes()
        {
            return StringUtils.join(values(), ", ");
        }
    }
}