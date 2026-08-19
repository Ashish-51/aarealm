package com.elysianrealm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

public class ElvenSettlementStructure extends Structure {
    public static final Codec<ElvenSettlementStructure> CODEC = RecordCodecBuilder.<ElvenSettlementStructure>mapCodec(instance ->
            instance.group(settingsCodec(instance)).apply(instance, ElvenSettlementStructure::new)
    ).codec();

    public ElvenSettlementStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context) {
        BlockPos pos = context.chunkPos().getMiddleBlockPosition(0);
        ChunkGenerator generator = context.chunkGenerator();
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        RandomState randomState = context.randomState();

        // 5-point check (center + 4 directions) to prevent any part of the village from generating in water
        if (isWater(generator, pos.getX(), pos.getZ(), heightAccessor, randomState) ||
            isWater(generator, pos.getX() + 20, pos.getZ(), heightAccessor, randomState) ||
            isWater(generator, pos.getX() - 20, pos.getZ(), heightAccessor, randomState) ||
            isWater(generator, pos.getX(), pos.getZ() + 20, heightAccessor, randomState) ||
            isWater(generator, pos.getX(), pos.getZ() - 20, heightAccessor, randomState)) {
            return Optional.empty();
        }

        return onTopOfChunkCenter(context, Heightmap.Types.OCEAN_FLOOR_WG, (builder) -> {
            generatePieces(builder, context, pos);
        });
    }

    private boolean isWater(ChunkGenerator generator, int x, int z, LevelHeightAccessor heightAccessor, RandomState randomState) {
        int surfaceY = generator.getFirstOccupiedHeight(x, z, Heightmap.Types.WORLD_SURFACE_WG, heightAccessor, randomState);
        int floorY = generator.getFirstOccupiedHeight(x, z, Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
        return surfaceY > floorY;
    }

    private void generatePieces(StructurePiecesBuilder builder, GenerationContext context, BlockPos pos) {
        StructureTemplateManager manager = context.structureTemplateManager();
        ChunkGenerator generator = context.chunkGenerator();
        LevelHeightAccessor heightAccessor = context.heightAccessor();
        RandomState randomState = context.randomState();

        // 13 NBT files layout
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "well", pos, Rotation.NONE);
        
        // Relative spacing layout
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "blacksmith", pos.offset(15, 0, 0), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "church", pos.offset(-15, 0, 0), Rotation.CLOCKWISE_90);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "library", pos.offset(0, 0, 15), Rotation.COUNTERCLOCKWISE_90);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "butcher_shop", pos.offset(0, 0, -15), Rotation.CLOCKWISE_180);

        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_1_house", pos.offset(12, 0, 12), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_2_house", pos.offset(-12, 0, 12), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_3_house", pos.offset(12, 0, -12), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_4_house", pos.offset(-12, 0, -12), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_5_house", pos.offset(0, 0, 28), Rotation.NONE);

        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_1_farm", pos.offset(25, 0, 5), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "tier_2_farm", pos.offset(-25, 0, 5), Rotation.NONE);

        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "streetlight", pos.offset(6, 0, 6), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "streetlight", pos.offset(-6, 0, 6), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "streetlight", pos.offset(6, 0, -6), Rotation.NONE);
        addPieceAtSurface(builder, manager, generator, heightAccessor, randomState, "streetlight", pos.offset(-6, 0, -6), Rotation.NONE);
    }

    private void addPieceAtSurface(StructurePiecesBuilder builder, StructureTemplateManager manager,
                                   ChunkGenerator generator, LevelHeightAccessor heightAccessor,
                                   RandomState randomState, String templateName, BlockPos offsetPos, Rotation rotation) {
        int groundY = generator.getFirstOccupiedHeight(offsetPos.getX(), offsetPos.getZ(),
                Heightmap.Types.OCEAN_FLOOR_WG, heightAccessor, randomState);
        BlockPos pos = new BlockPos(offsetPos.getX(), groundY, offsetPos.getZ());
        builder.addPiece(new ElvenSettlementPiece(manager, new ResourceLocation(ElysianRealm.MODID, templateName), pos, rotation));
    }

    @Override
    public StructureType<?> type() {
        return ElysianRealm.ELVEN_SETTLEMENT_STRUCTURE.get();
    }
}
