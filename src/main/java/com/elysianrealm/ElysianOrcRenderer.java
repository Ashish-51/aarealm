package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianOrcRenderer extends HumanoidMobRenderer<ElysianOrcEntity, HumanoidModel<ElysianOrcEntity>> {
    public static final ModelLayerLocation ORC_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_orc"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/orc/orc_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/orc/orc_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/orc/orc_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/orc/orc_3.png")
    };

    public ElysianOrcRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ORC_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianOrcEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 4));
        return TEXTURES[index];
    }
}
