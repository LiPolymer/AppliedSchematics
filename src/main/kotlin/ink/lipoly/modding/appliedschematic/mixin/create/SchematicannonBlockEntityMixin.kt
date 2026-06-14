package ink.lipoly.modding.appliedschematic.mixin.create

import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity
import ink.lipoly.modding.appliedschematic.blockentity.SchematicannonPathwayBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

@Mixin(value = [SchematicannonBlockEntity::class], remap = false)
abstract class SchematicannonBlockEntityMixin(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
) : BlockEntity(type, pos, blockState) {
    @Inject(
        method = ["updateChecklist"],
        at = [
            At(
                value = "INVOKE",
                target = "Lcom/simibubi/create/content/schematics/cannon/SchematicannonBlockEntity;findInventories()V",
            ),
        ],
    )
    private fun refreshPathwayBeforeInventoryScan(callbackInfo: CallbackInfo) {
        val currentLevel = level ?: return
        SchematicannonPathwayBlockEntity.updateAdjacentPathways(currentLevel, blockPos)
    }
}
