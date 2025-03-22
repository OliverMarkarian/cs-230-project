import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Menu {
    private static Game game;


    public Menu() {
    }

    public Menu(Game game) {
        this.game = game;
    }


    @FXML
    public void playPressed() throws IOException, ParseException {
        game.switchToLevel("level2.txt");
    }

    @FXML
    public void switchLevelPressed() throws IOException {
        Stage stage = game.getStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LevelSelect.fxml"));
        Parent root = loader.load();

        LevelSelect controller = loader.getController();
        controller.setGame(game);

        Scene scene = new Scene(root, 1280, 720); // full screen babyyy
        stage.setScene(scene);
        stage.show();
    }


    public static void switchLevel(int level) throws IOException, ParseException {
        String path = "level" + level + ".txt";
        game.switchToLevel(path);
    }

    @FXML
    public void loadPressed() throws IOException, ParseException {
        game.loadLevel();
    }

    @FXML
    public void newGamePressed() throws IOException {
        Stage stage = game.getStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewProfile.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1280, 720); // full screen
        stage.setScene(scene);
        stage.show();
    }
    // PROFILE CANVAS

    // IF CREATE PROFILE BUTTON IS PRESSED - DO HYPERLINK
    public void createProfile(String playerName) throws FileNotFoundException {
        if (validProfileName(playerName)) {
            Profile newprofile = new Profile(playerName, "", 1);
            saveProfile(newprofile);
        } else {
            // display on screen
            System.out.println("Invalid");
        }
    }

    public static void saveProfile(Profile newProfile) throws FileNotFoundException {
        String profilesFile = "profiles.txt";
        PrintWriter out = new PrintWriter(profilesFile);
        out.println(newProfile.toString());
        out.close();
    }

    public static Profile findProfile(String playerName) throws FileNotFoundException {
        try {
            File profilesFile = new File("profiles.txt");
            Scanner in = new Scanner(profilesFile);
            in.useDelimiter("\\s");
            while (in.hasNextLine()) {
                String name = in.next();
                int maxLevel = in.nextInt();
                if (name == playerName) {
                    Profile user = new Profile(name, "", maxLevel); //TODO
                    return user;
                } else {
                    System.out.println("Invalid");
                }
            }
            in.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return null;
    }

    public static boolean isUnique(String playerName) throws FileNotFoundException {
        boolean isUnique = true;
        try {
            File profilesFile = new File("profiles.txt");
            Scanner in = new Scanner(profilesFile);
            in.useDelimiter("\\s");
            while (in.hasNextLine()) {
                String name = in.next();
                int maxLevel = in.nextInt();
                if (name == playerName) {
                    isUnique = false;
                } else {
                    isUnique = true;
                }
            }
            in.close();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
        return isUnique;
    }

    // SHOULD HAVE THE PAGE LEFT OFF WHEN LOGGED IN
    public void login(String playerName) throws FileNotFoundException {
        if (!isUnique(playerName)) {
            System.out.println("You can login and your level is" + findProfile(playerName).getMax_level());
        } else {
            System.out.println("Invalid");
        }
    }


    public boolean validProfileName(String playerName) throws FileNotFoundException {
        if (playerName.length() > 0 && playerName.length() < 10 && isUnique(playerName)) {
            return true;
        } else {
            return false;
        }
    }
    
    public void deleteProfile(String playerName) throws FileNotFoundException {
        try {
            if (!isUnique(playerName)) {
                File profilesFile = new File("profiles.txt");
                Scanner in = new Scanner(profilesFile);
                in.useDelimiter("\\s");
                while (in.hasNextLine()) {
                    String name = in.next();
                    int maxLevel = in.nextInt();
                    if (name == playerName) {
                        // TO DO - DELETE PROFILE FROM TEXT FILE
                        System.out.println("DELETED");

                    }
                }
            } else {
                System.out.println("Player profile does not exist");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


}
