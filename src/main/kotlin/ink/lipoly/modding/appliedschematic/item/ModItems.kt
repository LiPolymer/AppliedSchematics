package ink.lipoly.modding.appliedschematic.item

import ink.lipoly.modding.appliedschematic.AppliedSchematic
import ink.lipoly.modding.appliedschematic.block.ModBlocks
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(AppliedSchematic.ID)

    val SCHEMATICANNON_PATHWAY = REGISTRY.registerSimpleBlockItem(ModBlocks.SCHEMATICANNON_PATHWAY)
}
