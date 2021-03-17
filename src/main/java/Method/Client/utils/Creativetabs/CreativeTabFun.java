package Method.Client.utils.Creativetabs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;


public class CreativeTabFun extends CreativeTabs {


    ItemStack Blankspot = new ItemStack(Items.BRICK);

    public CreativeTabFun() {
        super("Fun");
    }

    @Override
    public String getTabLabel() {
        return "Fun";
    }


    ArrayList<Enchantment> Enchants = new ArrayList<>();
    ArrayList<Integer> Levels = new ArrayList<>();

    @Override
    public void displayAllRelevantItems(NonNullList<ItemStack> itemList) {
        Blankspot.setCount(-1);
        try {
            Creativetabhelper.Attributeitems(Items.NETHERBRICK, Enchants, Levels, itemList, EntityEquipmentSlot.HEAD, false);
            Creativetabhelper.Attributeitems(Items.NETHERBRICK, Enchants, Levels, itemList, EntityEquipmentSlot.OFFHAND, false);
            final ItemStack trollPotion = new ItemStack(Items.SPLASH_POTION);
            trollPotion.setItemDamage(16395);
            final NBTTagList trollPotionEffects = new NBTTagList();
            for (int i = 1; i <= 27; ++i) {
                final NBTTagCompound effect = new NBTTagCompound();
                effect.setInteger("Amplifier", Integer.MAX_VALUE);
                effect.setInteger("Duration", Integer.MAX_VALUE);
                effect.setInteger("Id", i);
                trollPotionEffects.appendTag(effect);
            }
            trollPotion.setTagInfo("CustomPotionEffects", trollPotionEffects);
            trollPotion.setStackDisplayName("§c§lTroll§6§lPotion");
            itemList.add(trollPotion);


            final ItemStack killPotion = new ItemStack(Items.SPLASH_POTION);
            killPotion.setItemDamage(16395);
            final NBTTagCompound effect = new NBTTagCompound();
            effect.setInteger("Amplifier", 125);
            effect.setInteger("Duration", 1);
            effect.setInteger("Id", 6);
            final NBTTagList effects = new NBTTagList();
            effects.appendTag(effect);
            killPotion.setTagInfo("CustomPotionEffects", effects);
            killPotion.setStackDisplayName("§c§lKill§6§lPotion");
            itemList.add(killPotion);


            final ItemStack crashAnvil = new ItemStack(Blocks.ANVIL);
            crashAnvil.setStackDisplayName("§8Crash§c§lAnvil §7| §cmc1.8-mc1.8");
            crashAnvil.setItemDamage(16384);
            itemList.add(crashAnvil);


            final ItemStack crashHead = new ItemStack(Items.SKULL);
            final NBTTagCompound compound = new NBTTagCompound();
            compound.setString("SkullOwner", " ");
            crashHead.setTagCompound(compound);
            crashHead.setStackDisplayName("§8Crash§6§lHead §7| §cmc1.8-mc1.10");
            itemList.add(crashHead);

            ItemStack Armorstand = new ItemStack(Items.ARMOR_STAND);
            Armorstand.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Equipment:[{},{},{},{},{id:\"skull\",Count:1b,Damage:3b,tag:{SkullOwner:\"Test\"}}]}}"));
            itemList.add(Armorstand);


            ItemStack Armorstand2 = new ItemStack(Items.ARMOR_STAND);
            Armorstand2.setStackDisplayName("§c§lArmor stand++");
            Armorstand2.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{NoBasePlate:1,ShowArms:1}}"));
            itemList.add(Armorstand2);

            ItemStack InstaCreeper = new ItemStack(Items.SPAWN_EGG);
            InstaCreeper.setStackDisplayName("§c§lInsta Creeper");
            InstaCreeper.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Fuse:-1,id:\"minecraft:creeper\",ignited:1,ExplosionRadius:127}}"));
            itemList.add(InstaCreeper);

            ItemStack CrashSlime = new ItemStack(Items.SPAWN_EGG);
            CrashSlime.setStackDisplayName("§c§lCrash Slime");
            CrashSlime.setTagCompound(JsonToNBT.getTagFromJson("{EntityTag:{Size:32767,id:\"minecraft:slime\"}}"));
            itemList.add(CrashSlime);

            ItemStack Firework = new ItemStack(Items.FIREWORKS);
            Firework.setStackDisplayName("§c§lLong Firework");
            Firework.setTagCompound(JsonToNBT.getTagFromJson("{Fireworks:{Flight:127,Explosions:[{Type:0,Trail:1b,Colors:[I;16711680],FadeColors:[I;16711680]}]}}"));
            itemList.add(Firework);

            ItemStack Fwork = new ItemStack(Items.FIREWORKS);
            Fwork.setTagCompound(JsonToNBT.getTagFromJson("{Fireworks:{Flight:3}}"));
            itemList.add(Fwork);

            ItemStack CrashSkull = new ItemStack(Item.getItemById(397), 1, 3);
            NBTTagCompound nbt = new NBTTagCompound();
            NBTTagCompound c = new NBTTagCompound();
            GameProfile prof = new GameProfile(null, "name");
            prof.getProperties().put("textures", new Property("Value", "eyJ0ZXh0\u00addXJlcyI6eyJTS0lOIjp7InVybCI6IiJ9fX0="));
            c.setString("Id", "9d744c33-f3c4-4040-a7fc-73b47c840f0c");
            NBTUtil.writeGameProfile(c, prof);
            nbt.setTag("SkullOwner", c);
            nbt.setBoolean("crash", true);
            CrashSkull.stackTagCompound = nbt;
            CrashSkull.setStackDisplayName("Hold me :D");

            itemList.add(CrashSkull);


            ItemStack Head = new ItemStack(Item.getItemById(397), 1, 3);
            Head.setTagInfo("SkullOwner", new NBTTagString(Minecraft.getMinecraft().player.getName()));
            itemList.add(Head);

            ItemStack Crashhopper = new ItemStack(Blocks.HOPPER);
            Crashhopper.setStackDisplayName("§c§lCrash hopper");
            Crashhopper.setTagCompound(JsonToNBT.getTagFromJson("{BlockEntityTag:{Items:[{Slot:0,id:\"skull\",Count:64,tag:{SkullOwner:{Id:\"0\"}}}]}}"));
            itemList.add(Crashhopper);


            ItemStack Potion = new ItemStack(Items.SPLASH_POTION);
            Potion.setTagCompound(JsonToNBT.getTagFromJson("{CustomPotionEffects:[{Duration:20,Id:6,Amplifier:253}]}"));
            itemList.add(Potion);

            ItemStack Linger = new ItemStack(Items.LINGERING_POTION);
            Linger.setTagCompound(JsonToNBT.getTagFromJson("{CustomPotionEffects:[{Radius:100,Duration:20,Id:6,Amplifier:253}],HideFlags:32}"));
            itemList.add(Linger);


            final StringBuilder lagStringBuilder = new StringBuilder();
            for (int j = 0; j < 500; ++j) {
                lagStringBuilder.append("/(!§()%/§)=/(!§()%/§)=/(!§()%/§)=");
            }

            ItemStack sign = new ItemStack(Items.SIGN);
            sign.setStackDisplayName("§c§lCrash sign");
            sign.setTagCompound(JsonToNBT.getTagFromJson("{BlockEntityTag:{Text1:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text2:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text3:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\",Text4:\"{\\\"text\\\":\\\"" + lagStringBuilder.toString() + "\\\"}\"}}"));
            itemList.add(sign);

            ItemStack spawn = new ItemStack(Items.NAME_TAG);
            spawn.setTagCompound(JsonToNBT.getTagFromJson("{display:{Name: \"" + lagStringBuilder.toString() + "\"}}"));
            itemList.add(spawn);


            ItemStack ches = new ItemStack(Blocks.MOB_SPAWNER);
            ches.setStackDisplayName("§c§lEnder Dragon!");
            ches.setTagCompound(JsonToNBT.getTagFromJson("{BlockEntityTag:{Items:[0:{Slot:0b, id:\"minecraft:mob_spawner\",Count:64b,tag:{BlockEntityTag:{EntityId:\"FallingSand\",MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,SpawnData:{Motion:[0:0.0d,1:0.0d,2:0.0d],Block:\"mob_spawner\",Time:1,Data:0,TileEntityData:{EntityId:\"FallingSand\",MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,SpawnData:{Motion:[0:0.0d,1:0.0d,2:0.0d],Block:\"mob_spawner\",Time:1,Data:0,TileEntityData:{EntityId:\"EnderDragon\",MaxNearbyEntities:1000,RequiredPlayerRange:100,SpawnCount:100,MaxSpawnDelay:20,SpawnRange:100,MinSpawnDelay:20},DropItem:0},MaxSpawnDelay:20,SpawnRange:500,MinSpawnDelay:20},DropItem:0},MaxSpawnDelay:5,SpawnRange:500,Delay:20,MinSpawnDelay:5}},Damage:0s}],value:\"Chest\",Lock:\"\"}}"));
            itemList.add(ches);


        } catch (NBTException e) {
            e.printStackTrace();
        }

        super.displayAllRelevantItems(itemList);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Items.TNT_MINECART);
    }
}
