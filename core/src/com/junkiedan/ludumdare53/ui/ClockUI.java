package com.junkiedan.ludumdare53.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.junkiedan.ludumdare53.Player;

public class ClockUI {
    private final Label label;
    private final Player player;

    @SuppressWarnings("DefaultLocale")
    public ClockUI(Skin skin, Player player) {
        this.player = player;
        label = new Label("Time Left: " + (int)player.getTime(), skin);
//        label.setDebug(true);
    }

    @SuppressWarnings("DefaultLocale")
    public void update(float passedTime) {
        label.setText("Time Left: " + (int)player.getTime());
    }

    public Label getLabel() {
        return label;
    }
}
