package com.elysianrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ITeleporter;

import java.util.Optional;
import java.util.function.Function;

public class ElysianTeleporter implements ITeleporter {
    protected final ServerLevel level;

    public ElysianTeleporter(ServerLevel level) {
        this.level = level;
    }

    @Override
    public PortalInfo getPortalInfo(Entity entity, ServerLevel destWorld, Function<ServerLevel, PortalInfo> defaultPortalInfo) {
        BlockPos entityPos = entity.blockPosition();
        // Search in a 128 block radius (which our findPortal will clamp to optimized limits)
        Optional<BlockPos> existingPortal = findPortal(destWorld, entityPos, 128);

        BlockPos targetPos;
        if (existingPortal.isPresent()) {
            targetPos = existingPortal.get();
        } else {
            targetPos = createPortal(destWorld, entityPos);
        }

        // Spawn player in front of the portal (offset on Z axis to avoid instant loop)
        return new PortalInfo(
                new Vec3(targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 1.0),
                entity.getDeltaMovement(),
                entity.getYRot(),
                entity.getXRot()
        );
    }

    private Optional<BlockPos> findPortal(ServerLevel destWorld, BlockPos startPos, int radius) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        // Optimize search space to avoid CPU block scanning lag
        int horizontalRadius = Math.min(radius, 16);
        int verticalRadius = Math.min(radius, 64);

        for (int dy = 0; dy <= verticalRadius; dy = dy > 0 ? -dy : 1 - dy) {
            for (int dx = 0; dx <= horizontalRadius; dx = dx > 0 ? -dx : 1 - dx) {
                for (int dz = 0; dz <= horizontalRadius; dz = dz > 0 ? -dz : 1 - dz) {
                    mutable.set(startPos.getX() + dx, startPos.getY() + dy, startPos.getZ() + dz);
                    if (mutable.getY() >= destWorld.getMinBuildHeight() && mutable.getY() <= destWorld.getMaxBuildHeight()) {
                        BlockState state = destWorld.getBlockState(mutable);
                        if (state.is(ElysianRealm.ELYSIAN_PORTAL.get())) {
                            return Optional.of(mutable.immutable());
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    private BlockPos createPortal(ServerLevel destWorld, BlockPos entityPos) {
        int ix = entityPos.getX();
        int iz = entityPos.getZ();

        // Force generate target chunk
        destWorld.getChunkSource().getChunk(ix >> 4, iz >> 4, ChunkStatus.FULL, true);

        BlockPos heightPos = destWorld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(ix, 0, iz));
        int y = heightPos.getY();

        if (y <= destWorld.getMinBuildHeight()) {
            y = 70;
        }

        // Verify ground
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(ix, y, iz);
        boolean foundGround = false;
        while (mutablePos.getY() > destWorld.getMinBuildHeight()) {
            BlockState state = destWorld.getBlockState(mutablePos);
            if (!state.isAir() && !state.getFluidState().isSource()) {
                y = mutablePos.getY() + 1;
                foundGround = true;
                break;
            }
            mutablePos.move(Direction.DOWN);
        }

        if (!foundGround) {
            y = 70;
        }

        BlockPos origin = new BlockPos(ix, y, iz);

        // Build portal frame (4x5 external, 2x3 internal air space)
        for (int dx = -1; dx <= 2; dx++) {
            for (int dy = -1; dy <= 3; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos p = origin.offset(dx, dy, dz);
                    if (dy == -1) {
                        // Solid foundation platform
                        destWorld.setBlockAndUpdate(p, ElysianRealm.ELYSIAN_PORTAL_FRAME.get().defaultBlockState());
                    } else if (dx == -1 || dx == 2 || dy == 3) {
                        // Frame border block
                        if (dz == 0) {
                            destWorld.setBlockAndUpdate(p, ElysianRealm.ELYSIAN_PORTAL_FRAME.get().defaultBlockState());
                        } else {
                            destWorld.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                        }
                    } else {
                        // Interior space air
                        destWorld.setBlockAndUpdate(p, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        // Fill interior with portal blocks
        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 2; dy++) {
                BlockPos p = origin.offset(dx, dy, 0);
                destWorld.setBlockAndUpdate(p, ElysianRealm.ELYSIAN_PORTAL.get().defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));
            }
        }

        return origin;
    }
}
