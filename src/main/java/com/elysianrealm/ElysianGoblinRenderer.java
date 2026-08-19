package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianGoblinRenderer extends HumanoidMobRenderer<ElysianGoblinEntity, HumanoidModel<ElysianGoblinEntity>> {
    public static final ModelLayerLocation GOBLIN_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_goblin"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/goblin/goblin_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/goblin/goblin_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/goblin/goblin_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/goblin/goblin_3.png")
    };

    public ElysianGoblinRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(GOBLIN_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianGoblinEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 4));
        return TEXTURES[index];
    }
}
