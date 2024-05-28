package com.westeroscraft.westerostools.tools;

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

import java.util.List;
import java.util.Arrays;
import java.util.function.Predicate;
import javax.annotation.Nullable;
 
/**
 * A mode that "extrudes" a block that has directional components (e.g., wall blocks) in
 * the direction that the player is currently facing (or the opposite direction if the secondary
 * action is used).
*/
public class Extrude implements DoubleActionBlockTool {

    @Override
    public boolean canUse(Actor player) {
        return player.hasPermission("westerostools.extrude");
    }

		private final List<String> SHAPE_VALS_F = Arrays.asList("inner_left", "inner_right", "straight");
		private final List<String> SHAPE_VALS_B = Arrays.asList("outer_left", "outer_right", "straight");

    private final Predicate<Property<?>> isUnconnect = p -> p.getName().equals("unconnect");

    private boolean handleExtrude(LocalConfiguration config, Player player, LocalSession session,
                                  Location clicked, Direction faceF, boolean forward) {

        World world = (World) clicked.getExtent();

        BlockVector3 blockPoint = clicked.toVector().toBlockPoint();
        BaseBlock block = world.getFullBlock(blockPoint);

        if (faceF == null) {
						player.printError(TextComponent.of("Invalid block face for extrude"));
						return true;
        }
        Direction faceB = Direction.findClosest(faceF.toVector().multiply(-1), Direction.Flag.ALL);

        BaseBlock newBlock = block;

        // Attempt to set unconnect state if it exists
        Property<?> unconnectProperty = block.getStates().keySet().stream().filter(isUnconnect).findFirst().orElse(null);
        if (unconnectProperty != null) {
            BooleanProperty unconnectProp = (BooleanProperty) unconnectProperty;
            newBlock = newBlock.with(unconnectProp, Boolean.valueOf(true));
        }

				// Get states corresponding to forward or backward direction, if they exist
				Predicate<Property<?>> isFace = forward ? p -> p.getName().equals(faceF.toString().toLowerCase()) :
																								  p -> p.getName().equals(faceB.toString().toLowerCase());
        Property<?> directionProperty = block.getStates().keySet().stream().filter(isFace).findFirst().orElse(null);

				// Get state corresponding to shape, if it exists
				Predicate<Property<?>> isShape = p -> p.getName().equals("shape");
				Property<?> shapeProperty = block.getStates().keySet().stream().filter(isShape).findFirst().orElse(null);

				// Cycle state for forward or backward direction if it exists
        if (directionProperty != null) {
						int index = directionProperty.getValues().indexOf(block.getState(directionProperty));
						index = (index + 1) % directionProperty.getValues().size();
						@SuppressWarnings("unchecked")
						Property<Object> objProp = (Property<Object>) directionProperty;
						newBlock = newBlock.with(objProp, directionProperty.getValues().get(index));
        }

        // Otherwise if block has shape property (i.e., stairs), cycle that
				else if (shapeProperty != null) {
					int index = shapeProperty.getValues().indexOf(block.getState(shapeProperty));
					int newIndex = (index + 1) % shapeProperty.getValues().size();
					while (true) {
						Object shapeVal = shapeProperty.getValues().get(newIndex);
						if (forward && (shapeVal instanceof String) && SHAPE_VALS_F.contains(shapeVal))
							break;
						else if (!forward && (shapeVal instanceof String) && SHAPE_VALS_B.contains(shapeVal))
							break;
						else if (index == newIndex)
							break;
						else
							newIndex = (newIndex + 1) % shapeProperty.getValues().size();
					}
					@SuppressWarnings("unchecked")
					Property<Object> objProp = (Property<Object>) shapeProperty;
					newBlock = newBlock.with(objProp, shapeProperty.getValues().get(newIndex));
				}

				else {
						player.printError(TextComponent.of("Block face cannot be extruded"));
						return true;
				}

        // Attempt to apply change
        try (EditSession editSession = session.createEditSession(player)) {
            editSession.disableBuffering();

            try {
                editSession.setBlock(blockPoint, newBlock);
            } catch (MaxChangedBlocksException e) {
                player.printError(TranslatableComponent.of("worldedit.tool.max-block-changes"));
            } finally {
                session.remember(editSession);
            }
        }

        return true;
    }

    @Override
    public boolean actPrimary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handleExtrude(config, player, session, clicked, face, true);
    }

    @Override
    public boolean actSecondary(Platform server, LocalConfiguration config, Player player, LocalSession session, Location clicked, @Nullable Direction face) {
        return handleExtrude(config, player, session, clicked, face, false);
    }

}