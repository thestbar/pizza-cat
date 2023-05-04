package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private final Music music;

    private final Sound walkSound;
    private boolean isWalking;
    private long activeWalkingSoundId;

    public Sound firePizzaSound;
    public Sound catSound;
    public Sound interactionSound;
    public Sound gameOverSound;
    public Sound buttonSound;

    public SoundManager() {
        music = Gdx.audio.newMusic(Gdx.files.internal("soundtrack/loop_track.ogg"));
        walkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/footsteps.wav"));
        isWalking = false;
        activeWalkingSoundId = -1;

        firePizzaSound = Gdx.audio.newSound(Gdx.files.internal("sounds/firePizzaSound.wav"));
        catSound = Gdx.audio.newSound(Gdx.files.internal("sounds/catSound.wav"));
        interactionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/interactionSound.ogg"));
        gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOverSound.wav"));
        buttonSound = Gdx.audio.newSound(Gdx.files.internal("sounds/buttonSound.ogg"));
    }

    public void startMusic() {
        music.setVolume(0.3f);
        music.setLooping(true);
        music.play();
    }

    public void restart() {
        music.stop();
        music.play();
    }

    public void stop() {
        music.stop();
    }

    public void dispose() {
        music.dispose();
    }

    public void playWalkSound() {
        if(!isWalking) {
            isWalking = true;
            activeWalkingSoundId = walkSound.play();
            walkSound.setLooping(activeWalkingSoundId, true);
        }
    }

    public void stopWalkSound() {
        walkSound.stop(activeWalkingSoundId);
        activeWalkingSoundId = -1;
        isWalking = false;
    }
}
