
package Method.Client.module.misc;

import Method.Client.managers.Setting;
import Method.Client.module.Category;
import Method.Client.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemShears;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import static Method.Client.Main.setmgr;

public class Livestock extends Module {

    /////////////////////
    public Livestock() {
        super("Livestock Mod", Keyboard.KEY_NONE, Category.MISC, "Auto Sheepmod");
    }

    public Setting Dye = setmgr.add(new Setting("Auto Dye", this, true));
    public Setting Shear = setmgr.add(new Setting("Auto Shear", this, false));
    public Setting Breed = setmgr.add(new Setting("Auto Breed", this, false));

    /////////////////////
    @Override
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if ((mc.player.inventory.getCurrentItem().getItem() instanceof ItemDye && Dye.getValBoolean())
                || (Shear.getValBoolean() && mc.player.inventory.getCurrentItem().getItem() instanceof ItemShears) || Breed.getValBoolean()) {
            for (Entity e : mc.world.loadedEntityList) {
                if (Breed.getValBoolean()) {
                    if (e instanceof EntityAnimal) {
                        final EntityAnimal animal = (EntityAnimal) e;
                        if (animal.getHealth() > 0) {
                            if (!animal.isChild() && !animal.isInLove() && mc.player.getDistance(animal) <= 4.5f && animal.isBreedingItem(mc.player.inventory.getCurrentItem())) {
                                mc.playerController.interactWithEntity(mc.player, animal, EnumHand.MAIN_HAND);
                            }
                        }
                    }
                }
                if (e instanceof EntitySheep) {
                    final EntitySheep sheep = (EntitySheep) e;
                    if (sheep.getHealth() > 0) {
                        if (Dye.getValBoolean() ? sheep.getFleeceColor() != EnumDyeColor.byDyeDamage(mc.player.inventory.getCurrentItem().getMetadata()) :
                                !sheep.getSheared() && mc.player.getDistance(sheep) <= 4.5f) {
                            mc.playerController.interactWithEntity(mc.player, sheep, EnumHand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}
