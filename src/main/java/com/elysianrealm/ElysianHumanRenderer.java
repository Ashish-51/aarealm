package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianHumanRenderer extends HumanoidMobRenderer<ElysianHumanEntity, HumanoidModel<ElysianHumanEntity>> {
    public static final ModelLayerLocation HUMAN_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_human"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/human/human_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/human/human_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/human/human_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/human/human_3.png")
    };

    public ElysianHumanRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(HUMAN_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianHumanEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 4));
        return TEXTURES[index];
    }
}
