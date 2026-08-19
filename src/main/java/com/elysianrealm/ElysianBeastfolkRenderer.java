package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianBeastfolkRenderer extends HumanoidMobRenderer<ElysianBeastfolkEntity, HumanoidModel<ElysianBeastfolkEntity>> {
    public static final ModelLayerLocation BEASTFOLK_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_beastfolk"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_3.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_4.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_5.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_6.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_7.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_8.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/beastfolk/beastfolk_9.png")
    };

    public ElysianBeastfolkRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(BEASTFOLK_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianBeastfolkEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 10));
        return TEXTURES[index];
    }
}
