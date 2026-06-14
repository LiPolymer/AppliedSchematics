package ink.lipoly.modding.appliedschematics.item

import ink.lipoly.modding.appliedschematics.AppliedSchematics
import ink.lipoly.modding.appliedschematics.block.ModBlocks
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(AppliedSchematics.ID)

    val SCHEMATICANNON_PATHWAY = REGISTRY.registerSimpleBlockItem(ModBlocks.SCHEMATICANNON_PATHWAY)
}
