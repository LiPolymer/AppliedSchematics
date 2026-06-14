package ink.lipoly.modding.appliedschematic.block

import ink.lipoly.modding.appliedschematic.blockentity.SchematicannonPathwayBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class SchematicannonPathwayBlock(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        SchematicannonPathwayBlockEntity(pos, state)

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean,
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
        if (!level.isClientSide) {
            (level.getBlockEntity(pos) as? SchematicannonPathwayBlockEntity)?.updateCap()
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (!level.isClientSide && !state.`is`(newState.block)) {
            (level.getBlockEntity(pos) as? SchematicannonPathwayBlockEntity)?.run {
                clearCap()
                invalidateCapabilities()
            }
        }
        super.onRemove(state, level, pos, newState, isMoving)
    }
}
