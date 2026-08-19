package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ElysianElfRenderer extends HumanoidMobRenderer<ElysianElfEntity, HumanoidModel<ElysianElfEntity>> {
    public static final ModelLayerLocation ELF_LAYER = new ModelLayerLocation(
            new ResourceLocation(ElysianRealm.MODID, "elysian_elf"), "main");

    private static final ResourceLocation[] TEXTURES = new ResourceLocation[] {
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_0.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_1.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_2.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_3.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_4.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_5.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_6.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_7.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_8.png"),
            new ResourceLocation(ElysianRealm.MODID, "textures/entity/elf/elf_9.png")
    };

    public ElysianElfRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ELF_LAYER)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(ElysianElfEntity entity) {
        int index = Math.abs((int) (entity.getUUID().getMostSignificantBits() % 10));
        return TEXTURES[index];
    }
}
