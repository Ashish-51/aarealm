package com.elysianrealm;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ElysianFactionSavedData extends SavedData {
    private static final String DATA_NAME = "elysian_factions";
    
    private final Map<UUID, Map<String, Integer>> reputationMap = new HashMap<>();

    public static ElysianFactionSavedData get(ServerLevel level) {
        DimensionDataStorage storage = level.getServer().overworld().getDataStorage();
        return storage.computeIfAbsent(ElysianFactionSavedData::load, ElysianFactionSavedData::new, DATA_NAME);
    }

    public ElysianFactionSavedData() {}

    public int getReputation(UUID playerUuid, String faction) {
        return this.reputationMap
                .computeIfAbsent(playerUuid, k -> new HashMap<>())
                .getOrDefault(faction.toLowerCase(), 0);
    }

    public void setReputation(UUID playerUuid, String faction, int value) {
        this.reputationMap
                .computeIfAbsent(playerUuid, k -> new HashMap<>())
                .put(faction.toLowerCase(), value);
        this.setDirty();
    }

    public void addReputation(UUID playerUuid, String faction, int amount) {
        int current = getReputation(playerUuid, faction);
        setReputation(playerUuid, faction, current + amount);
    }

    public Map<String, Integer> getPlayerReputations(UUID playerUuid) {
        return new HashMap<>(this.reputationMap.computeIfAbsent(playerUuid, k -> new HashMap<>()));
    }

    public static ElysianFactionSavedData load(CompoundTag nbt) {
        ElysianFactionSavedData data = new ElysianFactionSavedData();
        ListTag playerList = nbt.getList("Players", Tag.TAG_COMPOUND);
        for (int i = 0; i < playerList.size(); i++) {
            CompoundTag playerTag = playerList.getCompound(i);
            UUID uuid = playerTag.getUUID("UUID");
            Map<String, Integer> factionMap = new HashMap<>();
            ListTag factionList = playerTag.getList("Factions", Tag.TAG_COMPOUND);
            for (int j = 0; j < factionList.size(); j++) {
                CompoundTag factionTag = factionList.getCompound(j);
                factionMap.put(factionTag.getString("Name"), factionTag.getInt("Value"));
            }
            data.reputationMap.put(uuid, factionMap);
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag nbt) {
        ListTag playerList = new ListTag();
        for (Map.Entry<UUID, Map<String, Integer>> entry : this.reputationMap.entrySet()) {
            CompoundTag playerTag = new CompoundTag();
            playerTag.putUUID("UUID", entry.getKey());
            ListTag factionList = new ListTag();
            for (Map.Entry<String, Integer> factionEntry : entry.getValue().entrySet()) {
                CompoundTag factionTag = new CompoundTag();
                factionTag.putString("Name", factionEntry.getKey());
                factionTag.putInt("Value", factionEntry.getValue());
                factionList.add(factionTag);
            }
            playerTag.put("Factions", factionList);
            playerList.add(playerTag);
        }
        nbt.put("Players", playerList);
        return nbt;
    }
}
