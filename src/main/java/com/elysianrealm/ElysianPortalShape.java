package com.elysianrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class ElysianPortalShape {
    private static final int MIN_WIDTH = 2;
    private static final int MAX_WIDTH = 21;
    private static final int MIN_HEIGHT = 3;
    private static final int MAX_HEIGHT = 21;

    private final LevelAccessor level;
    private final Direction.Axis axis;
    private final Direction rightDir;
    private final int numPortalBlocks;
    private final BlockPos bottomLeft;
    private final int width;
    private final int height;

    public ElysianPortalShape(LevelAccessor level, BlockPos pos, Direction.Axis axis) {
        this.level = level;
        this.axis = axis;
        this.rightDir = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        this.bottomLeft = this.calculateBottomLeft(pos);

        if (this.bottomLeft == null) {
            this.width = 0;
            this.height = 0;
            this.numPortalBlocks = 0;
        } else {
            this.width = this.calculateWidth();
            if (this.width > 0) {
                this.height = this.calculateHeight();
                this.numPortalBlocks = this.width * this.height;
            } else {
                this.height = 0;
                this.numPortalBlocks = 0;
            }
        }
    }

    public static Optional<ElysianPortalShape> findShape(LevelAccessor level, BlockPos pos) {
        // Try along X axis
        ElysianPortalShape shapeX = new ElysianPortalShape(level, pos, Direction.Axis.X);
        if (shapeX.isValid() && shapeX.numPortalBlocks > 0) {
            return Optional.of(shapeX);
        }

        // Try along Z axis
        ElysianPortalShape shapeZ = new ElysianPortalShape(level, pos, Direction.Axis.Z);
        if (shapeZ.isValid() && shapeZ.numPortalBlocks > 0) {
            return Optional.of(shapeZ);
        }

        return Optional.empty();
    }

    private BlockPos calculateBottomLeft(BlockPos pos) {
        BlockPos current = pos;
        while (current.getY() > this.level.getMinBuildHeight() && this.isEmpty(this.level.getBlockState(current.below()))) {
            current = current.below();
        }

        if (!this.isFrameBlock(this.level.getBlockState(current.below()))) {
            return null;
        }

        Direction leftDir = this.rightDir.getOpposite();
        while (this.isEmpty(this.level.getBlockState(current.relative(leftDir)))) {
            current = current.relative(leftDir);
        }

        if (!this.isFrameBlock(this.level.getBlockState(current.relative(leftDir)))) {
            return null;
        }

        return current;
    }

    private int calculateWidth() {
        int w = 0;
        while (w <= MAX_WIDTH) {
            BlockPos current = this.bottomLeft.relative(this.rightDir, w);
            BlockState state = this.level.getBlockState(current);

            if (!this.isEmpty(state)) {
                break;
            }

            if (!this.isFrameBlock(this.level.getBlockState(current.below()))) {
                return 0;
            }

            w++;
        }

        BlockPos rightBoundary = this.bottomLeft.relative(this.rightDir, w);
        if (w >= MIN_WIDTH && w <= MAX_WIDTH && this.isFrameBlock(this.level.getBlockState(rightBoundary))) {
            return w;
        }

        return 0;
    }

    private int calculateHeight() {
        int h = 0;
        labelHeight:
        for (h = 0; h <= MAX_HEIGHT; h++) {
            for (int w = 0; w < this.width; w++) {
                BlockPos current = this.bottomLeft.relative(this.rightDir, w).above(h);
                BlockState state = this.level.getBlockState(current);

                if (!this.isEmpty(state)) {
                    if (h >= MIN_HEIGHT && this.checkTopFrame(h)) {
                        return h;
                    }
                    break labelHeight;
                }

                if (w == 0 && !this.isFrameBlock(this.level.getBlockState(current.relative(this.rightDir.getOpposite())))) {
                    break labelHeight;
                }
                if (w == this.width - 1 && !this.isFrameBlock(this.level.getBlockState(current.relative(this.rightDir)))) {
                    break labelHeight;
                }
            }
        }

        return 0;
    }

    private boolean checkTopFrame(int h) {
        for (int w = 0; w < this.width; w++) {
            BlockPos current = this.bottomLeft.relative(this.rightDir, w).above(h);
            if (!this.isFrameBlock(this.level.getBlockState(current))) {
                return false;
            }
        }
        return true;
    }

    private boolean isEmpty(BlockState state) {
        return state.isAir();
    }

    private boolean isFrameBlock(BlockState state) {
        return state.is(ElysianRealm.ELYSIAN_PORTAL_FRAME.get());
    }

    public boolean isValid() {
        return this.bottomLeft != null && this.width >= MIN_WIDTH && this.width <= MAX_WIDTH && this.height >= MIN_HEIGHT && this.height <= MAX_HEIGHT;
    }

    public BlockPos getBottomLeft() {
        return this.bottomLeft;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    public Direction getRightDir() {
        return this.rightDir;
    }
}
