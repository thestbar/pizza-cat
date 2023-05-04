package com.junkiedan.ludumdare53.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.junkiedan.ludumdare53.LudumDare53;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                // Resizable application, uses available space in browser
//                return new GwtApplicationConfiguration(true);
                // Fixed size application:
                return new GwtApplicationConfiguration(20 * 60, 13 * 60);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new LudumDare53();
        }
}