package ink.lipoly.modding.appliedschematic.blockentity

import ink.lipoly.modding.appliedschematic.AppliedSchematic
import ink.lipoly.modding.appliedschematic.block.ModBlocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlockEntities {
    val REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AppliedSchematic.ID)

    val SCHEMATICANNON_PATHWAY: DeferredHolder<BlockEntityType<*>, BlockEntityType<SchematicannonPathwayBlockEntity>> =
        REGISTRY.register("schematicannon_pathway") { ->
            BlockEntityType.Builder.of(
                ::SchematicannonPathwayBlockEntity,
                ModBlocks.SCHEMATICANNON_PATHWAY.get(),
            ).build(null)
        }
}
