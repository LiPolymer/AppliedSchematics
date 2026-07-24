package ink.lipoly.modding.appliedschematics.block

import ink.lipoly.modding.appliedschematics.blockentity.ExplosiveBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class ExplosiveBlock(properties: Properties) : Block(properties), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        ExplosiveBlockEntity(pos, state)

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean,
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
        if (level.hasNeighborSignal(pos)) {
            explode(level, pos, null)
        }
    }

    private fun explode(level: Level, pos: BlockPos, source: LivingEntity?) {
        if (level.isClientSide) return
        (level.getBlockEntity(pos) as? ExplosiveBlockEntity)?.explode(source)
    }
}
