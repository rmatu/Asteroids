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
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception error) {
            error.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    int score;

    public void start(Stage mainStage){
        mainStage.setTitle("Asteroids");

        BorderPane root = new BorderPane();
        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        Canvas canvas = new Canvas(1200, 800);
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

        AnimationTimer gameloop = new AnimationTimer() {
            public void handle(long nanotime){
                if (keyPressedList.contains("LEFT"))
                    spaceship.rotation -= 3;
                if (keyPressedList.contains("RIGHT"))
                    spaceship.rotation += 3;
                if (keyPressedList.contains("UP")) {
                    spaceship.velocity.setLength(150);
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
                spaceship.render(context);

                for (Sprite laser : laserList)
                    laser.render(context);

                for (Sprite asteroid : asteroidList)
                    asteroid.render(context);

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
                            score += 100;
                        }
                    }
                }

                for (int n=0; n< explosionList.size(); n++){
                    Sprite explosion = explosionList.get(n);
                    explosion.update(1/60.0);
                    if(explosion.elapsedTime > 0.7)
                        explosionList.remove(n);
                }

                for (Sprite explosion : explosionList)
                    explosion.render(context);

                context.setFont(new Font("Arial Black", 48));
                context.setFill(Color.WHITE);
                context.setStroke(Color.GREEN);
                context.setLineWidth(3);
                String text = "Score: " + score;
                int textX = 900;
                int textY = 80;
                context.fillText(text, textX, textY);
                context.strokeText(text, textX, textY);


            }
        };

        gameloop.start();

        mainStage.show();
    }
}
