package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianDwarfRenderer extends HumanoidMobRenderer<ElysianDwarfEntity, HumanoidModel<ElysianDwarfEntity>> {
    public static final ModelLayerLocation DWARF_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_dwarf"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/dwarf/dwarf_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/dwarf/dwarf_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/dwarf/dwarf_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/dwarf/dwarf_3.png")
    };

    public ElysianDwarfRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(DWARF_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianDwarfEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 4));
        return TEXTURES[index];
    }
}
