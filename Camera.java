import javafx.scene.canvas.GraphicsContext;

/**
 * Represents a camera that follows a specified {@link Actor}
 * and updates its position on the canvas based on the actor's movements.
 * Provides basic camera translation functionality.
 *
 * @author Sam
 */
public class Camera {
    private GraphicsContext graphicsContext;
    private Actor follow;
    private int previousX = 0;
    private int previousY = 0;
    private static final int FACTOR = 150;
    private static final int THRESHOLD = 14;
    private static final double TRANSLATION_SCALAR = 0.75;
    private static double SCALE_BIG = 2;
    private boolean isBig;

    /**
     * Constructs a Camera instance with the specified {@link GraphicsContext}.
     *
     * @param gc the {@link GraphicsContext} used for rendering and translation
     */
    public Camera(final GraphicsContext gc) {
        this.graphicsContext = gc;
        isBig = false;
    }

    /**
     * A method to set whether the camera needs to adjust for
     * the size of the level.
     * @param isBig Whether the level is big (i.e. is randomly generated).
     */
    public void setIsBigLevel(boolean isBig) {
        this.isBig = isBig;
    }
    /**
     * Sets the target {@link Actor} that the camera will follow.
     *
     * @param actor the {@link Actor} to follow
     */
    public void setTarget(final Actor actor) {
        follow = actor;
        previousX = actor.getX();
        previousY = actor.getY();

        if (previousX > THRESHOLD) {
            graphicsContext.translate(-1 * previousX * Level.DRAW_ENTITY_SIZE * TRANSLATION_SCALAR,
                    0);
        }

        if (previousY > THRESHOLD) {
            graphicsContext.translate(0, -1 * previousY * Level.DRAW_ENTITY_SIZE * TRANSLATION_SCALAR);
        }
    }

    /**
     * Updates the camera's position by translating the view based on the target
     * actor's movements. It adjusts the translation only if the actor's position
     * has changed since the last update.
     */
    public void update() {
        if (follow.getX() - previousX > THRESHOLD / ((isBig) ? SCALE_BIG : 1)) {
            previousX = follow.getX();
            graphicsContext.translate(-1 * FACTOR, 0);
        } else if (follow.getX() - previousX < -1 * THRESHOLD / ((isBig) ? SCALE_BIG : 1)) {
            previousX = follow.getX();
            graphicsContext.translate(FACTOR, 0);
        }

        if (follow.getY() - previousY > (THRESHOLD / ((isBig) ? SCALE_BIG : 1))) {
            previousY = follow.getY();
            graphicsContext.translate(0, -FACTOR);
        } else if (follow.getY() - previousY < -1 * ((isBig) ? SCALE_BIG : 1)) {
            previousY = follow.getY();
            graphicsContext.translate(0, FACTOR);
        }
    }
}
