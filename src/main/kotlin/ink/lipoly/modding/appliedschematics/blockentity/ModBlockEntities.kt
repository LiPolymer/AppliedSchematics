package ink.lipoly.modding.appliedschematics.blockentity

import ink.lipoly.modding.appliedschematics.AppliedSchematics
import ink.lipoly.modding.appliedschematics.block.ModBlocks
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlockEntities {
    val REGISTRY: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, AppliedSchematics.ID)

    val SCHEMATICANNON_PATHWAY: DeferredHolder<BlockEntityType<*>, BlockEntityType<SchematicannonPathwayBlockEntity>> =
        REGISTRY.register("schematicannon_pathway") { ->
            BlockEntityType.Builder.of(
                ::SchematicannonPathwayBlockEntity,
                ModBlocks.SCHEMATICANNON_PATHWAY.get(),
            ).build(null)
        }

    val EXPLOSIVE: DeferredHolder<BlockEntityType<*>, BlockEntityType<ExplosiveBlockEntity>> =
        REGISTRY.register("explosive") { ->
            BlockEntityType.Builder.of(
                ::ExplosiveBlockEntity,
                ModBlocks.EXPLOSIVE.get(),
            ).build(null)
        }
}
