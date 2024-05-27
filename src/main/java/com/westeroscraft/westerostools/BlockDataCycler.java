package com.westeroscraft.westerostools;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalConfiguration;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.entity.Player;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extension.platform.Platform;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.registry.state.Property;
import com.sk89q.worldedit.registry.state.BooleanProperty;
import com.sk89q.worldedit.util.Direction;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldedit.util.formatting.text.TextComponent;
import com.sk89q.worldedit.util.formatting.text.TranslatableComponent;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.command.tool.DoubleActionBlockTool;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
 
/**
 * A mode that cycles the data values of supported blocks.
 * 
 * This is identical to the WorldEdit BlockDataCycler tool, except in that
 * it automatically sets the state "Unconnect" to "True" if that property
 * exists for a particular block.
*/
public class BlockDataCycler implements DoubleActionBlockTool {

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("westerostools.data-cycler");
    }

    private final Predicate<Property<?>> isUnconnect = p -> p.getName().equals("unconnect");

    private final Map<UUID, Property<?>> selectedProperties = new HashMap<>();

    private boolean handleCycle(LocalConfiguration config, Player player, LocalSession session,
                                Location clicked, boolean forward) {

        World world = (World) clicked.getExtent();

        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);

        if (!config.allowedDataCycleBlocks.isEmpty()
                && !player.hasPermission("worldedit.override.data-cycler")
                && !config.allowedDataCycleBlocks.contains(block.getBlockType().getId())) {
            player.printError(TranslatableComponent.of("worldedit.tool.data-cycler.block-not-permitted"));
            return true;
        }

        if (!block.getStates().keySet().stream().filter(Predicate.not(isUnconnect)).findAny().isPresent()) {
            player.printError(TranslatableComponent.of("worldedit.tool.data-cycler.cant-cycle"));
        } else {
            Property<?> currentProperty = selectedProperties.get(player.getUniqueId());

            if (currentProperty == null || (forward && block.getState(currentProperty) == null)) {
                currentProperty = block.getStates().keySet().stream().filter(Predicate.not(isUnconnect)).findFirst().get();
                selectedProperties.put(player.getUniqueId(), currentProperty);
            }

            if (forward) {
                block.getState(currentProperty);
                int index = currentProperty.getValues().indexOf(block.getState(currentProperty));
                index = (index + 1) % currentProperty.getValues().size();
                @SuppressWarnings("unchecked")
                Property<Object> objProp = (Property<Object>) currentProperty;
                BaseBlock newBlock = block.with(objProp, currentProperty.getValues().get(index));

                // Attempt to set unconnect state if it exists
                Property<?> unconnectProperty = block.getStates().keySet().stream().filter(isUnconnect).findFirst().orElse(null);
                if (unconnectProperty != null) {
                    BooleanProperty unconnectProp = (BooleanProperty) unconnectProperty;
                    newBlock = newBlock.with(unconnectProp, Boolean.valueOf(true));
                }

                try (EditSession editSession = session.createEditSession(player)) {
                    editSession.disableBuffering();

                    try {
                        editSession.setBlock(blockPoint, newBlock);
                        player.printInfo(TranslatableComponent.of(
                                "worldedit.tool.data-cycler.new-value",
                                TextComponent.of(currentProperty.getName()),
                                TextComponent.of(String.valueOf(currentProperty.getValues().get(index)))
                        ));
                    } catch (MaxChangedBlocksException e) {
                        player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
                    } finally {
                        session.remember(editSession);
                    }
                }

            } else {
                List<Property<?>> properties = Lists.newArrayList(block.getStates().keySet().stream().filter(Predicate.not(isUnconnect)).toList());
                int index = properties.indexOf(currentProperty);
                index = (index + 1) % properties.size();
                currentProperty = properties.get(index);
                selectedProperties.put(player.getUniqueId(), currentProperty);
                player.printInfo(TranslatableComponent.of("worldedit.tool.data-cycler.cycling", TextComponent.of(currentProperty.getName())));

                // // Attempt to set unconnect state if it exists
                // Property<?> unconnectProperty = block.getStates().keySet().stream().filter(isUnconnect).findFirst().orElse(null);
                // if (unconnectProperty != null) {
                //     BooleanProperty unconnectProp = (BooleanProperty) unconnectProperty;
                //     BaseBlock newBlock = block.with(unconnectProp, Boolean.valueOf(true));

                //     try (EditSession editSession = session.createEditSession(player)) {
                //         editSession.disableBuffering();
    
                //         try {
                //             editSession.setBlock(blockPoint, newBlock);
                //         } catch (MaxChangedBlocksException e) {
                //             player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
                //         } finally {
                //             session.remember(editSession);
                //         }
                //     }
                // }

            }
        }

        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handleCycle(config, player, session, clicked, true);
    }

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handleCycle(config, player, session, clicked, false);
    }

}
