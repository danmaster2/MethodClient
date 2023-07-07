package Method.Client.utils.proxy.renderers;

import Method.Client.Main;
import Method.Client.module.render.ArmorRender;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

import static Method.Client.utils.proxy.Overrides.EntityrenderOverride.*;

@SideOnly(Side.CLIENT)
public abstract class ModLayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
    protected static final ResourceLocation RES_ITEM_GLINT_RUNE = new ResourceLocation(Main.MODID, "enchanted_item_glint_rune.png");
    protected static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation(Main.MODID, "enchanted_item_glint.png");
    protected T modelLeggings;
    protected T modelArmor;
    private final RenderLivingBase<?> renderer;
    private boolean skipRenderGlint;
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    public ModLayerArmorBase(RenderLivingBase<?> rendererIn) {
        renderer = rendererIn;
        initArmor();
    }


    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.CHEST);
        renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.LEGS);
        renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.FEET);
        renderArmorLayer(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, EntityEquipmentSlot.HEAD);
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }

    private void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn) {
        ItemStack itemstack = entityLivingBaseIn.getItemStackFromSlot(slotIn);
        if (ArmorRender.RenderArmor.getValBoolean())
            if (itemstack.getItem() instanceof ItemArmor) {
                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();

                if (itemarmor.getEquipmentSlot() == slotIn) {
                    T model = getModelFromSlot(slotIn);
                    model = getArmorModelHook(entityLivingBaseIn, itemstack, slotIn, model);
                    model.setModelAttributes(renderer.getMainModel());
                    model.setLivingAnimations(entityLivingBaseIn, limbSwing, limbSwingAmount, partialTicks);
                    setModelSlotVisible(model, slotIn);
                    renderer.bindTexture(getArmorResource(entityLivingBaseIn, itemstack, slotIn, null));
                    {
                        float alpha = 1.0F;
                        float colorR = 1.0F;
                        float colorG = 1.0F;
                        float colorB = 1.0F;
                        if (itemarmor.hasOverlay(itemstack)) // Allow this for anything, not only cloth
                        {
                            int itemColor = itemarmor.getColor(itemstack);
                            float itemRed = (itemColor >> 16 & 255) / 255.0F;
                            float itemGreen = (itemColor >> 8 & 255) / 255.0F;
                            float itemBlue = (itemColor & 255) / 255.0F;
                            GlStateManager.color(colorR * itemRed, colorG * itemGreen, colorB * itemBlue, alpha);
                            model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                            renderer.bindTexture(getArmorResource(entityLivingBaseIn, itemstack, slotIn, "overlay"));
                        }
                        { // Non-colored
                            GlStateManager.color(colorR, colorG, colorB, alpha);
                            model.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
                        } // Default
                        if (!skipRenderGlint && itemstack.hasEffect()) {
                            renderEnchantedGlint(renderer, entityLivingBaseIn, model, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale, getColorForEnchantment(EnchantmentHelper.getEnchantments(itemstack)));
                        }
                    }
                }
            }
    }


    public T getModelFromSlot(EntityEquipmentSlot slotIn) {
        return isLegSlot(slotIn) ? modelLeggings : modelArmor;
    }

    private boolean isLegSlot(EntityEquipmentSlot slotIn) {
        return slotIn == EntityEquipmentSlot.LEGS;
    }

    public static void renderEnchantedGlint(RenderLivingBase<?> parRenderLivingBase, EntityLivingBase parEntityLivingBase, ModelBase model, float parLimbSwing, float parLimbSwingAmount, float parPartialTicks, float parAgeInTicks, float parHeadYaw, float parHeadPitch, float parScale, int parColor) {
        float f = parEntityLivingBase.ticksExisted + parPartialTicks;
        if (ArmorRender.useRuneTexture.getValBoolean()) {
            parRenderLivingBase.bindTexture(RES_ITEM_GLINT_RUNE);
        } else {
            parRenderLivingBase.bindTexture(RES_ITEM_GLINT);
        }

        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.color(redFromColor(parColor), greenFromColor(parColor), blueFromColor(parColor), alphaFromColor()); // originally was 0.5F, 0.5F, 0.5F, 1.0F);

        for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
            GlStateManager.color(redFromColor(parColor), greenFromColor(parColor), blueFromColor(parColor), alphaFromColor()); // originally was 0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(3.0F, 3.0F, 3.0F);
            GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + i * 0.003F) * 5.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            model.render(parEntityLivingBase, parLimbSwing, parLimbSwingAmount, parAgeInTicks, parHeadYaw, parHeadPitch, parScale);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
    }

    protected abstract void initArmor();

    protected abstract void setModelSlotVisible(T p_188359_1_, EntityEquipmentSlot slotIn);

    /*=================================== FORGE START =========================================*/

    /**
     * Hook to allow item-sensitive armor model. for LayerBipedArmor.
     */
    protected T getArmorModelHook(EntityLivingBase entity, ItemStack itemStack, EntityEquipmentSlot slot, T model) {
        return model;
    }

    /**
     * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
     *
     * @param entity Entity wearing the armor
     * @param stack  ItemStack for the armor
     * @param slot   Slot ID that the item is in
     * @param type   Subtype, can be null or "overlay"
     * @return ResourceLocation pointing at the armor's texture
     */
    public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EntityEquipmentSlot slot, String type) {
        ItemArmor item = (ItemArmor) stack.getItem();
        String texture = item.getArmorMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (isLegSlot(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

        s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
        ResourceLocation resourcelocation = ARMOR_TEXTURE_RES_MAP.get(s1);

        if (resourcelocation == null) {
            resourcelocation = new ResourceLocation(s1);
            ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
        }

        return resourcelocation;
    }
    /*=================================== FORGE END ===========================================*/
}