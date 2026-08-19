package com.elysianrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

public class GoblinSettlementPiece extends TemplateStructurePiece {
    public GoblinSettlementPiece(StructureTemplateManager manager, ResourceLocation templateId, BlockPos pos, Rotation rotation) {
        super(ElysianRealm.GOBLIN_SETTLEMENT_PIECE.get(), 0, manager, templateId, templateId.toString(), makeSettings(rotation), pos);
    }

    public GoblinSettlementPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ElysianRealm.GOBLIN_SETTLEMENT_PIECE.get(), tag, context.structureTemplateManager(), (id) -> makeSettings(Rotation.valueOf(tag.getString("Rot"))));
    }

    private static StructurePlaceSettings makeSettings(Rotation rotation) {
        return new StructurePlaceSettings().setRotation(rotation).setIgnoreEntities(false);
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putString("Rot", this.placeSettings.getRotation().name());
    }

    @Override
    protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor level, RandomSource random, BoundingBox box) {
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator, RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pos) {
        super.postProcess(level, structureManager, generator, random, box, chunkPos, pos);

        if (!level.isClientSide()) {
            BlockPos spawnPos = this.templatePosition.above();
            if (box.isInside(spawnPos)) {
                String id = this.templateName;
                int count = 0;
                if (id.contains("well")) {
                    count = 2;
                } else if (id.contains("blacksmith") || id.contains("church") || id.contains("library") || id.contains("butcher") || id.contains("tier_5")) {
                    count = 1;
                } else if (id.contains("house") && random.nextFloat() < 0.5f) {
                    count = 1;
                }

                BlockPos.MutableBlockPos spawnMpos = new BlockPos.MutableBlockPos(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ());
                while (!level.isEmptyBlock(spawnMpos) && spawnMpos.getY() < level.getMaxBuildHeight()) {
                    spawnMpos.move(net.minecraft.core.Direction.UP);
                }

                for (int i = 0; i < count; i++) {
                    ElysianGoblinEntity entity = ElysianRealm.ELYSIAN_GOBLIN.get().create(level.getLevel());
                    if (entity != null) {
                        entity.moveTo(spawnMpos.getX() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D,
                                     spawnMpos.getY(),
                                     spawnMpos.getZ() + 0.5D + (random.nextDouble() - 0.5D) * 2.0D,
                                     random.nextFloat() * 360.0F, 0.0F);
                        entity.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnMpos), MobSpawnType.STRUCTURE, null, null);
                        level.addFreshEntityWithPassengers(entity);
                    }
                }
            }
        }
    }
}
