import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.io.*;

// References
//https://www.youtube.com/watch?v=2ZVH1KeDSHo
/**
 *
 * A class to manage and update leaderboard data, storing the highest scores for each player per level.
 * The leaderboard is stored and retrieved from a text file, and the class allows for adding scores, saving them,
 * and reading the leaderboard for specific levels.
 *
 * @author Saniya
 */
public class Leaderboard {


    public static void updateLeaderboard(Profile profile, int scoreToAdd, int levelWanted) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File("scores.txt"));

        StringBuilder builder = new StringBuilder();

        ArrayList<LeaderboardRecord> records = new ArrayList<>();

        while (scanner.hasNext()) {
            final String name = scanner.next();


            if (scanner.hasNextInt()) { //There should be a score
            }

            final int score = scanner.nextInt();

            if (!scanner.hasNextInt()) { //There should be a a level id

            }

            final int levelID = scanner.nextInt();


            // Add wanted scores of certain level and of profiles that exists to an arraylist.
            if (levelWanted == levelID) {
                LeaderboardRecord record = new LeaderboardRecord(name, score, levelID);
                records.add(record);
            } else {
                builder.append(name + " " + score + " " + levelID + "\n");
            }
        }

        if (records.removeIf(record -> record.getLevelID() == levelWanted && record.getName().equals(profile.getName()) && record.getScore() < scoreToAdd)) {
            records.add(new LeaderboardRecord(profile.getName(), scoreToAdd, levelWanted));
        }

        records.sort(LeaderboardRecord::compare);


        if (records.isEmpty()) {
        }
        final int length = Math.max(Math.min(records.size(), 10), 0);
        records = new ArrayList<>(records.subList(0, length).reversed());

        scanner.close();


        PrintWriter writer = new PrintWriter("scores.txt");

        writer.write(builder.toString());
        records.forEach(record -> writer.println(record.getName() + " " + record.getScore() + " " + record.getLevelID()));
        writer.close();
    }

    /**
     * Returns the leaderboard for a certain level.
     * @param levelWanted The level you need the leaderboard for.
     * @throws IOException;
     */
    public static ArrayList<LeaderboardRecord> levelLeaderboard(int levelWanted) throws IOException {

        // Read scores from text file

        ArrayList<String> toSortScores = new ArrayList<>();

        ArrayList<LeaderboardRecord> records = new ArrayList<>();

        try {
            FileReader leaderboardFile = new FileReader("scores.txt");
            Scanner scanner = new Scanner(leaderboardFile);
            while (scanner.hasNext()) {
                final String name = scanner.next();


                if (scanner.hasNextInt()) { //There should be a score
                }

                final int score = scanner.nextInt();

                if (!scanner.hasNextInt()) { //There should be a a level id

                }

                final int levelID = scanner.nextInt();


                // Add wanted scores of certain level and of profiles that exists to an arraylist.
                if (levelWanted == levelID) {
                    LeaderboardRecord record = new LeaderboardRecord(name, score, levelID);
                    records.add(record);
                }
            }
            scanner.close();
            records.sort(LeaderboardRecord::compare);
            
            if (records.isEmpty()) {
                //uh oh
            }
            final int length = Math.max(Math.min(records.size(), 10), 0);
            records = new ArrayList<>(records.subList(0, length).reversed());

            records.forEach(record -> System.out.println(String.format("Name: %s, Score: %s, LevelID: %s", record.getName(), record.getScore(), record.getLevelID())));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return records;
    }

    /**
     * Saves the player's score to the leaderboard file.
     * The score is saved along with the player's name and the level.
     *
     * @param profile The player's profile.
     * @param score   The score to be saved.
     * @param level   The level number for which the score is being saved.
     * @throws FileNotFoundException If the leaderboard file is not found.
     */
    public static void saveScores(Profile profile, int score, int level) throws FileNotFoundException {
        try {
            File leaderboardFile = new File("scores.txt");
            PrintWriter out = new PrintWriter(leaderboardFile);
            out.println(profile.getName() + " " + score + " " + level);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }



    /**
     * Reads all the profiles and scores from the leaderboard file and groups them by level.
     *
     * @throws FileNotFoundException If the leaderboard file is not found.
     */
    public static void readProfiles() throws FileNotFoundException {
        Stack<Leaderboard> level1Scores = new Stack<>();
        Stack<Leaderboard> level2Scores = new Stack<>();
        Stack<Leaderboard> level3Scores = new Stack<>();
        Stack<Leaderboard> level4Scores = new Stack<>();
        Stack<Leaderboard> level5Scores = new Stack<>();
        try {
            File leaderboardFile = new File("scores.txt");
            Scanner in = new Scanner(leaderboardFile);
            in.useDelimiter("\\s");
            while (in.hasNextLine()) {
                String name = in.next();
                int score = in.nextInt();
                int level = in.nextInt();
                //Leaderboard person = new Leaderboard(score, level);

            }
            in.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }

    }
}
