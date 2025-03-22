import javafx.scene.image.Image;

/**
 * Represents a Magic Wall tile in the game. The Magic Wall has special behavior that interacts with certain types of actors
 * (such as Boulder and Diamond). It allows for the transformation of these actors when they are positioned above the Magic Wall.
 *
 * @author Oliver
 */
public class MagicWall extends Tile {


    private static final Image IMAGE = new Image("MagicWall.jpg");

    /**
     * Constructor to create a MagicWall tile at a specific position.
     *
     * @param x The x-coordinate of the MagicWall.
     * @param y The y-coordinate of the MagicWall.
     */
    public MagicWall(int x, int y) {
        super(true, x, y, TileType.MAGIC_WALL);
    }

    /**
     * Returns the image associated with the MagicWall.
     *
     * @return The image of the MagicWall.
     */
    @Override
    public Image getImage() {
        return IMAGE;
    }

    /**
     * Updates the state of the Magic Wall tile. It checks the actor above the Magic Wall and transforms it
     * (either from Boulder to Diamond or from Diamond to Boulder) if it is one of the specified types.
     *
     * @param grid The grid where the Magic Wall and its surroundings are located.
     */
    public void update(Grid grid) {
        int x = getX();
        int y = getY();
        Tile aboveTile = grid.getTile(x, y - 1);
        Actor occupier = aboveTile.getOccupier();

        if (occupier != null) {
            if (occupier.getType() == ActorType.BOULDER) {
                transformActor(occupier, grid, ActorType.DIAMOND);
            } else if (occupier.getType() == ActorType.DIAMOND) {
                transformActor(occupier, grid, ActorType.BOULDER);
            }
        }
    }

    /**
     * Determines whether an actor can walk on the Magic Wall tile.
     * The Magic Wall tile does not allow any actor to walk on it.
     *
     * @param actor The actor attempting to walk on the tile.
     * @return False since actors cannot walk on a Magic Wall.
     */
    @Override
    public boolean actorCanWalkOn(Actor actor) {
        return false;
    }

    /**
     * Interacts with an actor that is on the Magic Wall tile. If the actor is a Boulder or Diamond,
     * the actor will be transformed to the opposite type (Boulder to Diamond or Diamond to Boulder).
     *
     * @param actor The actor interacting with the Magic Wall.
     * @param grid The grid where the Magic Wall and actor are located.
     */
    @Override
    public void interactWith(Actor actor, Grid grid) {
        if (actor.getType() == ActorType.BOULDER) {
            transformActor(actor, grid, ActorType.DIAMOND);
        } else if (actor.getType() == ActorType.DIAMOND) {
            transformActor(actor, grid, ActorType.BOULDER);
        }
    }

    /**
    * Placeholder method for reading the state of the Magic Wall from a text format.
    * This method does not perform any action as the Magic Wall tile does not need additional state parsing.
    *
    * @param text The text to read from (ignored for Magic Wall).
    */
    @Override
    public void fromText(String text) {
    }

    /**
     * Converts the Magic Wall tile's state into a string representation.
     * For Magic Wall, the string representation is "M".
     *
     * @return A string representing the Magic Wall tile.
     */
    @Override
    public String toText() {
        return "M";
    }

    /**
     * Transforms the actor (either a Boulder or Diamond) to a new type (Diamond or Boulder).
     * The transformation is applied by moving the actor to a valid target tile and updating the grid.
     *
     * @param actor The actor to be transformed.
     * @param grid The grid where the actor and the target tile are located.
     * @param newType The new type the actor will be transformed into (either Diamond or Boulder).
     */
    private void transformActor(Actor actor, Grid grid, ActorType newType) {
        int x = actor.getX();
        int y = actor.getY();
        if (actor != null) {
            Tile targetTile = grid.getTile(x, y + 2);
            if (targetTile.getType() == TileType.PATH && targetTile.getOccupier() == null) {
                grid.removeActor(x, y);
                Actor newActor;
                if (newType == ActorType.DIAMOND) {
                    newActor = new Diamond(x, y);
                } else {
                    newActor = new Boulder(x, y);
                }
                grid.addActor(x, y + 2, newActor);
            }
        }
    }
}