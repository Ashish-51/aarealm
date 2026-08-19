package com.elysianrealm;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ElysianRealm.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ElysianRealmClient {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_ELF.get(), ElysianElfRenderer::new);
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_BEASTFOLK.get(), ElysianBeastfolkRenderer::new);
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_HUMAN.get(), ElysianHumanRenderer::new);
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_DWARF.get(), ElysianDwarfRenderer::new);
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_GOBLIN.get(), ElysianGoblinRenderer::new);
        event.registerEntityRenderer(ElysianRealm.ELYSIAN_ORC.get(), ElysianOrcRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ElysianElfRenderer.ELF_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
        event.registerLayerDefinition(ElysianBeastfolkRenderer.BEASTFOLK_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
        event.registerLayerDefinition(ElysianHumanRenderer.HUMAN_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
        event.registerLayerDefinition(ElysianDwarfRenderer.DWARF_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
        event.registerLayerDefinition(ElysianGoblinRenderer.GOBLIN_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
        event.registerLayerDefinition(ElysianOrcRenderer.ORC_LAYER, () -> 
            LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F), 64, 64)
        );
    }
}
