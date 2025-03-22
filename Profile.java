import java.util.ArrayList;

/**
 * Represents a user's profile in the application, including their name, password,
 * highest level reached, and high scores for each level.
 *
 * @author Saniya
 */
public class Profile {
    private String name;
    private int maxLevel;
    private ArrayList<Integer> levelHighScores;

    /**
     * Constructs a new Profile instance with the specified name, password, and maximum level.
     *
     * @param name       the name of the user.
     * @param maxLevel  the highest level the user has reached.
     */
    public Profile(final String name, final int maxLevel) {
        this.name = name;
        this.maxLevel = maxLevel;
    }

    /**
     * Returns the name of the user.
     *
     * @return the user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Updates the name of the user.
     *
     * @param name the new name to set for the user.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Returns the highest level the user has reached.
     *
     * @return the user's highest level.
     */
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Updates the highest level the user has reached.
     *
     * @param maxLevel the new highest level to set.
     */
    public void setMaxLevel(final int maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * Returns a string representation of the profile, that is the name
     * and the highest level reached by this player.
     * @return a string representation of the user's profile.
     */
    @Override
    public String toString() {
        return name + " "
                +  "" + maxLevel;
    }
}
