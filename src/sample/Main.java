package sample;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    int score;
    int lifes;
    private static final int APP_WIDTH = 1200;
    private static final int APP_HEIGHT = 800;
    private static final int USER_SPEED = 400;
    private static final int USER_LIFES = 3;

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    private void spawnAsteroids(ArrayList<Sprite> asteroidList, Sprite spaceship){
        if(asteroidList.size() == 0){
            int asteroidCount = 6;
            for (int n = 0; n < asteroidCount; n++) {
                Sprite asteroid = new Sprite("images/asteroid.png");
                double x = 800 * Math.random() - spaceship.position.x;
                double y = 600 * Math.random() - spaceship.position.y;
                asteroid.position.set(x,y);
                double angle = 360 * Math.random();
                asteroid.velocity.setLength(60);
                asteroid.velocity.setAngle(angle);
                asteroidList.add(asteroid);
            }
        }
    }

    private void asteroidHitsTheUser(ArrayList<Sprite> asteroidList, ArrayList<Sprite> explosionList, Sprite spaceship){
        for (int asteroidNum = 0; asteroidNum < asteroidList.size(); asteroidNum++){
            Sprite asteroid = asteroidList.get(asteroidNum);
            if(spaceship.overlaps(asteroid)){
                Sprite explosion = new Sprite("images/explosion.gif");
                asteroidList.remove(asteroidNum);
                explosion.position.x = asteroid.position.x;
                explosion.position.y = asteroid.position.y;
                explosionList.add(explosion);
                lifes--;
            }
        }
    }


    private void endGame (int lifes, Sprite spaceship, GraphicsContext context) {
        if (lifes == 0) {
            context.setFill(Color.RED);
            context.setStroke(Color.RED);
            context.setLineWidth(3);
            String text = "You died, your score: " + score;
            int textX = APP_WIDTH/4;
            int textY = APP_HEIGHT/2;
            context.fillText(text, textX, textY);
            context.strokeText(text, textX, textY);
            spaceship.position.x = 100000;
            spaceship.position.y = 100000;
        }
    }

    public void start(Stage mainStage){
        mainStage.setTitle("Asteroids");

        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        Canvas canvas = new Canvas(APP_WIDTH, APP_HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        ArrayList<String> keyPressedList = new ArrayList<String>();
        ArrayList<String> keyJustPressedList = new ArrayList<String>();

        mainScene.setOnKeyPressed(
                (KeyEvent event) -> {
                    String keyName = event.getCode().toString();
                    if ( !keyPressedList.contains(keyName) ){
                        keyPressedList.add(keyName);
                        keyJustPressedList.add(keyName);
                    }
                }
        );

        mainScene.setOnKeyReleased(
                (KeyEvent event) -> {
                    String keyName = event.getCode().toString();
                    if ( keyPressedList.contains(keyName) )
                        keyPressedList.remove(keyName);
                }
        );

        Sprite background = new Sprite("images/background.png");
        background.position.set(600, 400);
        background.render(context);

        Sprite spaceship = new Sprite("images/spaceship.png");
        spaceship.position.set(100, 300);

        ArrayList<Sprite> laserList = new ArrayList<Sprite>();
        ArrayList<Sprite> asteroidList = new ArrayList<Sprite>();
        ArrayList<Sprite> explosionList = new ArrayList<Sprite>();

        int asteroidCount = 6;
        for (int n = 0; n<asteroidCount; n++) {
            Sprite asteroid = new Sprite("images/asteroid.png");
            double x = 800 * Math.random() + 300;
            double y = 600 * Math.random() + 100;
            asteroid.position.set(x,y);
            double angle = 360 * Math.random();
            asteroid.velocity.setLength(60);
            asteroid.velocity.setAngle(angle);
            asteroidList.add(asteroid);
        }

        score = 0;
        lifes = USER_LIFES;

        AnimationTimer gameloop = new AnimationTimer() {
            public void handle(long nanotime){
                if (keyPressedList.contains("LEFT"))
                    spaceship.rotation -= 3;
                if (keyPressedList.contains("RIGHT"))
                    spaceship.rotation += 3;
                if (keyPressedList.contains("UP")) {
                    spaceship.velocity.setLength(USER_SPEED);
                    spaceship.velocity.setAngle(spaceship.rotation);
                } else {
                    spaceship.velocity.setLength(0);
                }

                if (keyPressedList.contains("SPACE")){
                    Sprite laser = new Sprite("images/laserbeam.png");
                    laser.position.set(spaceship.position.x, spaceship.position.y);
                    laser.velocity.setLength(400);
                    laser.velocity.setAngle(spaceship.rotation);
                    laserList.add(laser);
                }

                keyJustPressedList.clear();
                for (Sprite asteroid : asteroidList){
                    asteroid.update(1/60.0);
                }

                spaceship.update(1/60.0);
                for (int n=0; n< laserList.size(); n++){
                    Sprite laser = laserList.get(n);
                    laser.update(1/60.0);
                    if(laser.elapsedTime > 2)
                        laserList.remove(n);
                }

                background.render(context);

                if(lifes > 0){
                    spaceship.render(context);
                }

                for (Sprite laser : laserList)
                    laser.render(context);

                for (Sprite asteroid : asteroidList)
                    asteroid.render(context);

                // USER HITS THE ASTEROID
                for (int laserNum = 0; laserNum < laserList.size(); laserNum++){
                    Sprite laser = laserList.get(laserNum);
                    for (int asteroidNum = 0; asteroidNum < asteroidList.size(); asteroidNum++){
                        Sprite asteroid = asteroidList.get(asteroidNum);
                        if (laser.overlaps(asteroid)){
                            Sprite explosion = new Sprite("images/explosion.gif");
                            laserList.remove(laserNum);
                            asteroidList.remove(asteroidNum);
                            explosion.position.x = asteroid.position.x;
                            explosion.position.y = asteroid.position.y;
                            explosionList.add(explosion);
                            score += 10;
                        }
                    }
                }

                // ASTEROID HITS THE USER
                asteroidHitsTheUser(asteroidList, explosionList, spaceship);

                for (int n=0; n< explosionList.size(); n++){
                    Sprite explosion = explosionList.get(n);
                    explosion.update(1/60.0);
                    if(explosion.elapsedTime > 0.7)
                        explosionList.remove(n);
                }

                for (Sprite explosion : explosionList)
                    explosion.render(context);

                context.setFont(new Font(48));
                context.setFill(Color.RED);
                context.setStroke(Color.RED);
                context.setLineWidth(3);
                String text = "Score: " + score;
                String textLifes = "Lifes: " + lifes;
                int textX = 800;
                int textY = 80;
                int lifesxX = 50;
                int lifesY = 80;
                context.fillText(text, textX, textY);
                context.strokeText(text, textX, textY);

                context.fillText(textLifes, lifesxX, lifesY);
                context.strokeText(textLifes, lifesxX, lifesY);

                spawnAsteroids(asteroidList, spaceship);

                endGame(lifes, spaceship, context);
            }
        };

        gameloop.start();

        mainStage.show();
    }
}
