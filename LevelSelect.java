import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Allows the player to choose a level to play, and displays high scores for each level.
 */
public class LevelSelect {
    private Game game;
    private Profile profile;

    @FXML
    private TableView<Score> scoreTable1;
    @FXML
    private TableColumn<Score, Integer> rankingColumn1;
    @FXML
    private TableColumn<Score, String> nameColumn1;
    @FXML
    private TableColumn<Score, Integer> scoreColumn1;
    @FXML
    private TableColumn<Score, Integer> levelColumn1;

    @FXML
    private TableView<Score> scoreTable2;
    @FXML
    private TableColumn<Score, Integer> rankingColumn2;
    @FXML
    private TableColumn<Score, String> nameColumn2;
    @FXML
    private TableColumn<Score, Integer> scoreColumn2;
    @FXML
    private TableColumn<Score, Integer> levelColumn2;

    @FXML
    private TableView<Score> scoreTable3;
    @FXML
    private TableColumn<Score, Integer> rankingColumn3;
    @FXML
    private TableColumn<Score, String> nameColumn3;
    @FXML
    private TableColumn<Score, Integer> scoreColumn3;
    @FXML
    private TableColumn<Score, Integer> levelColumn3;

    @FXML
    private TableView<Score> scoreTable4;
    @FXML
    private TableColumn<Score, Integer> rankingColumn4;
    @FXML
    private TableColumn<Score, String> nameColumn4;
    @FXML
    private TableColumn<Score, Integer> scoreColumn4;
    @FXML
    private TableColumn<Score, Integer> levelColumn4;

    @FXML
    private TableView<Score> scoreTable5;
    @FXML
    private TableColumn<Score, Integer> rankingColumn5;
    @FXML
    private TableColumn<Score, String> nameColumn5;
    @FXML
    private TableColumn<Score, Integer> scoreColumn5;
    @FXML
    private TableColumn<Score, Integer> levelColumn5;

    public void setGame(Game game) {
        this.game = game;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    @FXML
    public void initialize() {
        initializeTable(scoreTable1, rankingColumn1, nameColumn1, scoreColumn1, levelColumn1, 1);
        initializeTable(scoreTable2, rankingColumn2, nameColumn2, scoreColumn2, levelColumn2, 2);
        initializeTable(scoreTable3, rankingColumn3, nameColumn3, scoreColumn3, levelColumn3, 3);
        initializeTable(scoreTable4, rankingColumn4, nameColumn4, scoreColumn4, levelColumn4, 4);
        initializeTable(scoreTable5, rankingColumn5, nameColumn5, scoreColumn5, levelColumn5, 5);
    }

    private void initializeTable(TableView<Score> table, TableColumn<Score, Integer> rankingColumn, TableColumn<Score, String> nameColumn, TableColumn<Score, Integer> scoreColumn, TableColumn<Score, Integer> levelColumn, int level) {
        rankingColumn.setCellValueFactory(new PropertyValueFactory<>("ranking"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        loadScores(table, level);
    }

    private void loadScores(TableView<Score> table, int level) {
        List<Score> scores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("scores.txt"))) {
            String line;
            int ranking = 1;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    int lvl = Integer.parseInt(parts[2]);
                    if (lvl == level) {
                        scores.add(new Score(ranking++, name, score, lvl));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        table.getItems().setAll(scores);
    }

    @FXML
    public void level1Pressed() throws IOException, ParseException {
        if (profile.getMaxLevel() + 1 >= 1) {
            game.runGame(profile, 1);
        }
    }

    @FXML
    public void level2Pressed() throws IOException, ParseException {
        if (profile.getMaxLevel() + 1 >= 2) {
            game.runGame(profile, 2);
        }
    }

    @FXML
    public void level3Pressed() throws IOException, ParseException {
        if (profile.getMaxLevel() + 1 >= 3) {
            game.runGame(profile, 3);
        }
    }

    @FXML
    public void level4Pressed() throws IOException, ParseException {
        if (profile.getMaxLevel() + 1 >= 4) {
            game.runGame(profile, 4);
        }
    }

    @FXML
    public void level5Pressed() throws IOException, ParseException {
        if (profile.getMaxLevel() + 1 >= 5) {
            game.runGame(profile, 5);
        }
    }
}