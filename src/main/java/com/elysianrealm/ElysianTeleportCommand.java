package com.elysianrealm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.function.Function;

public class ElysianTeleportCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("elysian")
                .requires(source -> source.hasPermission(2)) // Operator level 2
                .then(
                    Commands.literal("teleport")
                        .executes(ElysianTeleportCommand::teleportToElysian)
                )
        );
    }

    private static int teleportToElysian(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            ServerLevel targetLevel = source.getServer().getLevel(ElysianRealm.ELYSIAN_LEVEL_KEY);
            if (targetLevel != null) {
                player.changeDimension(targetLevel, new ITeleporter() {
                    @Override
                    public PortalInfo getPortalInfo(net.minecraft.world.entity.Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
                        BlockPos safePos = findSafeSpawnPosition(destWorld, entity.getX(), entity.getZ());
                        return new PortalInfo(new Vec3(safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5), Vec3.ZERO, entity.getYRot(), entity.getXRot());
                    }
                });
                source.sendSuccess(() -> Component.literal("Teleported to the Elysian Realm!"), false);
                return 1;
            } else {
                source.sendFailure(Component.literal("Failed to find the Elysian Realm dimension. Verify JSON data files are loaded."));
                return 0;
            }
        } else {
            source.sendFailure(Component.literal("Only players can teleport to the Elysian Realm."));
            return 0;
        }
    }

    private static BlockPos findSafeSpawnPosition(ServerLevel level, double x, double z) {
        int ix = (int) Math.floor(x);
        int iz = (int) Math.floor(z);

        // Force load/generate the chunk so heightmap and block states are populated
        level.getChunkSource().getChunk(ix >> 4, iz >> 4, ChunkStatus.FULL, true);

        // Get highest block
        BlockPos heightPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(ix, 0, iz));
        int y = heightPos.getY();

        if (y <= level.getMinBuildHeight()) {
            y = 70;
        }

        // Search downwards from heightmap position for a solid floor
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(ix, y, iz);
        while (mutablePos.getY() > level.getMinBuildHeight()) {
            BlockState state = level.getBlockState(mutablePos);
            BlockState aboveState1 = level.getBlockState(mutablePos.above());
            BlockState aboveState2 = level.getBlockState(mutablePos.above(2));

            // Ground must not be air or fluid source, and there must be 2 blocks of air above it
            if (!state.isAir() && !state.getFluidState().isSource() && aboveState1.isAir() && aboveState2.isAir()) {
                return mutablePos.immutable().above();
            }
            mutablePos.move(Direction.DOWN);
        }

        // Search upwards from y=70 if downwards search failed
        mutablePos.set(ix, 70, iz);
        while (mutablePos.getY() < level.getMaxBuildHeight()) {
            BlockState state = level.getBlockState(mutablePos);
            BlockState aboveState1 = level.getBlockState(mutablePos.above());
            BlockState aboveState2 = level.getBlockState(mutablePos.above(2));

            if (state.isAir() && aboveState1.isAir() && aboveState2.isAir()) {
                BlockPos ground = mutablePos.below();
                if (!level.getBlockState(ground).isAir() && !level.getBlockState(ground).getFluidState().isSource()) {
                    return mutablePos.immutable();
                }
            }
            mutablePos.move(Direction.UP);
        }

        // Fallback: spawn on a generated block platform if nothing else works
        BlockPos fallbackPos = new BlockPos(ix, 80, iz);
        level.setBlockAndUpdate(fallbackPos.below(), Blocks.GRASS_BLOCK.defaultBlockState());
        level.setBlockAndUpdate(fallbackPos, Blocks.AIR.defaultBlockState());
        level.setBlockAndUpdate(fallbackPos.above(), Blocks.AIR.defaultBlockState());
        return fallbackPos;
    }
}

