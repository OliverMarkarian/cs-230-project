/**
 * Score class that contains the ranking, name, score, and level of the user.
 */
public class Score {
    private final int ranking;
    private final String name;
    private final int score;
    private final int level;

    /**
     * Constructor for Score.
     * @param ranking is the ranking for the user
     * @param name is the name of the user
     * @param score is the score of the user
     * @param level is the level of the user
     */
    public Score(int ranking, String name, int score, int level) {
        this.ranking = ranking;
        this.name = name;
        this.score = score;
        this.level = level;
    }

    /**
     * @return ranking for user
     */
    public int getRanking() {
        return ranking;
    }

    /**
     * @return name for user
     */
    public String getName() {
        return name;
    }

    /**
     * @return score for user
     */
    public int getScore() {
        return score;
    }

    /**
     * @return level for user
     */
    public int getLevel() {
        return level;
    }
}
