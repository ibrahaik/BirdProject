package com.example.bird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {

    final Bird game;
    OrthographicCamera camera;

    Stage stage;
    Player player;

    boolean dead;

    Array<Pipe> obstacles;
    long lastObstacleTime;
    float score;

    public GameScreen(final Bird gam) {
        this.game = gam;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        player = new Player();
        player.setManager(game.manager);

        stage = new Stage();
        stage.getViewport().setCamera(camera);

        stage.addActor(player);

        // create the obstacles array and spawn the first obstacle
        obstacles = new Array<Pipe>();
        spawnObstacle();

        score = 0;
    }

    @Override
    public void render(float delta) {

        dead = false;

        // BLOQUE DE RENDER =================================
        ScreenUtils.clear(0.3f, 0.8f, 0.8f, 1);
        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(game.manager.get("background.png", Texture.class),
            0, 0);
        game.batch.end();

        stage.getBatch().setProjectionMatrix(camera.combined);
        stage.draw();



        // BLOQUE DE UPDATE =====================================
        stage.act();

        if (Gdx.input.justTouched()) {
            player.impulso();
        }
        // Comprova que el jugador no es surt de la pantalla.
        // Si surt per la part inferior, game over

        if (player.getBounds().y > 480 - player.getHeight())
            player.setY( 480 - player.getHeight() );
        if (player.getBounds().y < 0 - player.getHeight()) {
            dead = true;

     }

        // Comprova si cal generar un obstacle nou
        if (TimeUtils.nanoTime() - lastObstacleTime > 1500000000)
            spawnObstacle();
        // Comprova si les tuberies colisionen amb el jugador
        Iterator<Pipe> iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getBounds().overlaps(player.getBounds())) {
                dead = true;
            }
        }
        // Treure de l'array les tuberies que estan fora de pantalla
        iter = obstacles.iterator();
        while (iter.hasNext()) {
            Pipe pipe = iter.next();
            if (pipe.getX() < -64) {
                obstacles.removeValue(pipe, true);
            }
        }

        if (dead) {
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
    @Override
    public void dispose() {
    }

    private void spawnObstacle() {

        float holey = MathUtils.random(50, 230);
        Pipe pipe1 = new Pipe();

        pipe1.setX(800);
        pipe1.setY(holey - 230);
        pipe1.setUpsideDown(true);
        pipe1.setManager(game.manager);
        obstacles.add(pipe1);
        stage.addActor(pipe1);
        Pipe pipe2 = new Pipe();
        pipe2.setX(800);
        pipe2.setY(holey + 200);

        pipe2.setUpsideDown(false);
        pipe2.setManager(game.manager);
        obstacles.add(pipe2);
        stage.addActor(pipe2);

        lastObstacleTime = TimeUtils.nanoTime();
    }
}
