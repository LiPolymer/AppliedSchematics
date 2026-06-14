package ink.lipoly.modding.appliedschematic.blockentity

import appeng.api.AECapabilities
import appeng.api.config.Actionable
import appeng.api.networking.GridHelper
import appeng.api.networking.IGridNode
import appeng.api.networking.IGridNodeListener
import appeng.api.networking.IInWorldGridNodeHost
import appeng.api.networking.IManagedGridNode
import appeng.api.networking.security.IActionHost
import appeng.api.networking.security.IActionSource
import appeng.api.stacks.AEItemKey
import appeng.api.storage.MEStorage
import appeng.api.util.AECableType
import com.simibubi.create.content.schematics.cannon.MaterialChecklist
import com.simibubi.create.content.schematics.cannon.SchematicannonBlockEntity
import ink.lipoly.modding.appliedschematic.block.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.items.IItemHandler
import java.util.EnumMap
import java.util.EnumSet

class SchematicannonPathwayBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModBlockEntities.SCHEMATICANNON_PATHWAY.get(), pos, state),
    IInWorldGridNodeHost,
    IActionHost {
    private var schematicannonItemHandler: AESchematicannonItemHandler? = null
    private val allSchematicannonItems = mutableListOf<ItemStack>()
    private val allowedDirections = EnumSet.noneOf(Direction::class.java)
    private val otherChecklists = EnumMap<Direction, MaterialChecklist>(Direction::class.java)

    private val mainNode: IManagedGridNode = GridHelper.createManagedNode(this, NODE_LISTENER)
        .setVisualRepresentation(ModBlocks.SCHEMATICANNON_PATHWAY.get())
        .setInWorldNode(true)
        .setTagName("ae2_node")
        .setIdlePowerUsage(1.0)

    fun updateCap() {
        val currentLevel = level ?: return
        if (currentLevel.isClientSide) return

        if (networkStorage() == null) {
            clearCap()
            otherChecklists.clear()
            invalidateCapabilities()
            return
        }

        var checklistsChanged = false
        val seenDirections = EnumSet.noneOf(Direction::class.java)

        for (direction in Direction.entries) {
            val otherPos = worldPosition.relative(direction)
            if (!currentLevel.isLoaded(otherPos)) continue

            val blockEntity = currentLevel.getBlockEntity(otherPos)
            if (blockEntity is SchematicannonBlockEntity) {
                seenDirections.add(direction)

                val current = blockEntity.checklist ?: continue
                val cached = otherChecklists[direction]
                if (cached == null || !sameChecklistKeys(cached, current)) {
                    otherChecklists[direction] = cloneChecklistKeys(current)
                    checklistsChanged = true
                }
            }
        }

        val iterator = otherChecklists.entries.iterator()
        while (iterator.hasNext()) {
            if (!seenDirections.contains(iterator.next().key)) {
                iterator.remove()
                checklistsChanged = true
            }
        }

        if (otherChecklists.isEmpty()) {
            if (schematicannonItemHandler != null || allSchematicannonItems.isNotEmpty() || allowedDirections.isNotEmpty()) {
                clearCap()
                invalidateCapabilities()
            }
            return
        }

        val newAllowedDirections = EnumSet.copyOf(otherChecklists.keys)
        if (!checklistsChanged && schematicannonItemHandler != null && allowedDirections == newAllowedDirections) {
            return
        }

        val newSnapshot = mutableListOf<ItemStack>()
        val seenItems = mutableSetOf<Item>()

        for (checklist in otherChecklists.values) {
            for (item in checklist.required.keys) {
                if (seenItems.add(item)) {
                    newSnapshot.add(ItemStack(item))
                }
            }
            for (item in checklist.damageRequired.keys) {
                if (seenItems.add(item)) {
                    newSnapshot.add(ItemStack(item))
                }
            }
        }

        if (seenItems.add(Items.GUNPOWDER)) {
            newSnapshot.add(ItemStack(Items.GUNPOWDER))
        }

        allSchematicannonItems.clear()
        allSchematicannonItems.addAll(newSnapshot)

        allowedDirections.clear()
        allowedDirections.addAll(newAllowedDirections)

        schematicannonItemHandler = AESchematicannonItemHandler(this, allSchematicannonItems.toList())
        invalidateCapabilities()
    }

    fun clearCap() {
        schematicannonItemHandler = null
        allSchematicannonItems.clear()
        allowedDirections.clear()
    }

    private fun networkStorage(): MEStorage? {
        if (!mainNode.isOnline || !mainNode.hasGridBooted()) return null
        return mainNode.grid?.storageService?.inventory
    }

    private fun networkItemCount(key: AEItemKey): Long {
        val grid = mainNode.grid ?: return 0
        if (!mainNode.isOnline || !mainNode.hasGridBooted()) return 0
        return grid.storageService.cachedInventory.get(key)
    }

    private fun actionSource(): IActionSource = IActionSource.ofMachine(this)

    override fun getGridNode(dir: Direction): IGridNode? = mainNode.node

    override fun getActionableNode(): IGridNode? = mainNode.node

    override fun getCableConnectionType(dir: Direction): AECableType = AECableType.SMART

    override fun clearRemoved() {
        super.clearRemoved()
        GridHelper.onFirstTick(this) { blockEntity -> blockEntity.onFirstTick() }
    }

    private fun onFirstTick() {
        val currentLevel = level ?: return
        if (!currentLevel.isClientSide) {
            mainNode.create(currentLevel, blockPos)
            updateCap()
        }
    }

    override fun onLoad() {
        super.onLoad()
        updateCap()
    }

    override fun onChunkUnloaded() {
        super.onChunkUnloaded()
        mainNode.destroy()
    }

    override fun setRemoved() {
        super.setRemoved()
        mainNode.destroy()
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        mainNode.loadFromNBT(tag)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        mainNode.saveToNBT(tag)
    }

    private class AESchematicannonItemHandler(
        private val owner: SchematicannonPathwayBlockEntity,
        private val stacksSnapshot: List<ItemStack>,
    ) : IItemHandler {
        override fun getSlots(): Int = stacksSnapshot.size

        override fun getStackInSlot(slot: Int): ItemStack {
            val key = keyForSlot(slot) ?: return ItemStack.EMPTY
            val storedAmount = owner.networkItemCount(key).coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            return key.toStack(storedAmount)
        }

        override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack {
            if (stack.isEmpty) return ItemStack.EMPTY
            val key = AEItemKey.of(stack) ?: return stack
            val inserted = owner.networkStorage()
                ?.insert(key, stack.count.toLong(), Actionable.ofSimulate(simulate), owner.actionSource())
                ?: 0
            if (inserted <= 0) return stack

            val remaining = stack.count - inserted.coerceAtMost(stack.count.toLong()).toInt()
            return if (remaining <= 0) ItemStack.EMPTY else stack.copyWithCount(remaining)
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack {
            if (amount <= 0) return ItemStack.EMPTY
            val key = keyForSlot(slot) ?: return ItemStack.EMPTY
            val extracted = owner.networkStorage()
                ?.extract(key, amount.toLong(), Actionable.ofSimulate(simulate), owner.actionSource())
                ?: 0
            return key.toStack(extracted.coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
        }

        override fun getSlotLimit(slot: Int): Int = 99

        override fun isItemValid(slot: Int, stack: ItemStack): Boolean = true

        private fun keyForSlot(slot: Int): AEItemKey? {
            if (slot !in stacksSnapshot.indices) return null
            return AEItemKey.of(stacksSnapshot[slot])
        }
    }

    companion object {
        private val NODE_LISTENER = object : IGridNodeListener<SchematicannonPathwayBlockEntity> {
            override fun onSaveChanges(nodeOwner: SchematicannonPathwayBlockEntity, node: IGridNode) {
                nodeOwner.setChanged()
            }

            override fun onGridChanged(nodeOwner: SchematicannonPathwayBlockEntity, node: IGridNode) {
                nodeOwner.updateCap()
            }

            override fun onStateChanged(
                nodeOwner: SchematicannonPathwayBlockEntity,
                node: IGridNode,
                state: IGridNodeListener.State,
            ) {
                nodeOwner.updateCap()
            }
        }

        @JvmStatic
        fun registerCapabilities(event: RegisterCapabilitiesEvent) {
            event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SCHEMATICANNON_PATHWAY.get(),
            ) { blockEntity, direction ->
                if (
                    direction != null &&
                    blockEntity.schematicannonItemHandler != null &&
                    blockEntity.allowedDirections.contains(direction)
                ) {
                    blockEntity.schematicannonItemHandler
                } else {
                    null
                }
            }

            event.registerBlockEntity(
                AECapabilities.IN_WORLD_GRID_NODE_HOST,
                ModBlockEntities.SCHEMATICANNON_PATHWAY.get(),
            ) { blockEntity, _ -> blockEntity }
        }

        @JvmStatic
        fun updateAdjacentPathways(level: Level, schematicannonPos: BlockPos) {
            if (level.isClientSide) return

            for (direction in Direction.entries) {
                val pathwayPos = schematicannonPos.relative(direction)
                if (!level.isLoaded(pathwayPos)) continue

                val blockEntity = level.getBlockEntity(pathwayPos)
                if (blockEntity is SchematicannonPathwayBlockEntity) {
                    blockEntity.updateCap()
                }
            }
        }

        private fun cloneChecklistKeys(source: MaterialChecklist): MaterialChecklist {
            val copy = MaterialChecklist()
            for (item in source.required.keys) {
                copy.required.put(item, 1)
            }
            for (item in source.damageRequired.keys) {
                copy.damageRequired.put(item, 1)
            }
            copy.blocksNotLoaded = source.blocksNotLoaded
            return copy
        }

        private fun sameChecklistKeys(first: MaterialChecklist, second: MaterialChecklist): Boolean =
            first.required.keys == second.required.keys &&
                first.damageRequired.keys == second.damageRequired.keys
    }
}
