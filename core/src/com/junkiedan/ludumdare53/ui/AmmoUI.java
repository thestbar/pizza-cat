package com.junkiedan.ludumdare53.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.junkiedan.ludumdare53.Player;

public class AmmoUI {
    private final Label label;
    private final Player player;

    @SuppressWarnings("DefaultLocale")
    public AmmoUI(Skin skin, Player player) {
        this.player = player;
        label = new Label("Pizza Slices: " + player.getAmmo(), skin);
//        label.setDebug(true);
    }

    @SuppressWarnings("DefaultLocale")
    public void update() {
        label.setText("Pizza Slices: " + player.getAmmo());
    }

    public Label getLabel() {
        return label;
    }
}
