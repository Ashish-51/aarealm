package com.elysianrealm;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ElysianRealm.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ElysianFactionEventHandler {

    public static String getFactionForEntity(Entity entity) {
        if (entity instanceof ElysianElfEntity) return "elves";
        if (entity instanceof ElysianBeastfolkEntity) return "beastfolk";
        if (entity instanceof ElysianHumanEntity) return "humans";
        if (entity instanceof ElysianDwarfEntity) return "dwarves";
        if (entity instanceof ElysianGoblinEntity) return "goblins";
        if (entity instanceof ElysianOrcEntity) return "orcs";
        return null;
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (target != null && attacker instanceof ServerPlayer player) {
            String faction = getFactionForEntity(target);
            if (faction != null) {
                ServerLevel level = (ServerLevel) target.level();
                ElysianFactionSavedData.get(level).addReputation(player.getUUID(), faction, -5);
                player.sendSystemMessage(Component.literal("§cLost 5 reputation with " + faction + " for attacking their citizen."));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        Entity attacker = event.getSource().getEntity();
        if (target != null && attacker instanceof ServerPlayer player) {
            String faction = getFactionForEntity(target);
            if (faction != null) {
                ServerLevel level = (ServerLevel) target.level();
                ElysianFactionSavedData.get(level).addReputation(player.getUUID(), faction, -20); // -25 total
                player.sendSystemMessage(Component.literal("§4Lost 20 reputation with " + faction + " for murdering their citizen."));
            }
        }
    }
}
