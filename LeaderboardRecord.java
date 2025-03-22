/**
 * A class to represent a entry in the leaderboard.
 * @author Sam
 * @see Leaderboard
 */
public class LeaderboardRecord {
    private String name;
    private int score;
    private int levelID;

    /**
     * A constructor to create a new LeaderbordRecord.
     * @param name The name of the record owner.
     * @param score The score of this entry.
     * @param levelID The level this record is associated with.
     * @see Leaderboard
     */
    public LeaderboardRecord(String name, int score, int levelID) {
        this.name = name;
        this.score = score;
        this.levelID = levelID;
    }

    /**
     * A method to get the score of this record.
     * @return The score.
     */
    public int getScore() {
        return score;
    }

    /**
     * A method to get the ID of the level this record is associated with.
     * @return The ID.
     */
    public int getLevelID() {
        return levelID;
    }

    /**
     * A method to get the name of the record owner.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * A method to be used for comparing two leaderboards in sorting.
     * @param x The first record.
     * @param y The second record.
     * @return Follows the typical Java convention,
     * -1 if x < y, 0 if x == y and 1 if x > y.
     */
    public static int compare(LeaderboardRecord x, LeaderboardRecord y) {
        if (x.getScore() < y.getScore()) {
            return -1;
        } else if (x.getScore() == y.getScore()) {
            return 0;
        } else {
            return 1;
        }
    }
}
