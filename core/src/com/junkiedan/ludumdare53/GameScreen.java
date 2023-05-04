package com.junkiedan.ludumdare53;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.junkiedan.ludumdare53.ai.Graph;
import com.junkiedan.ludumdare53.ai.Node;
import com.junkiedan.ludumdare53.ai.NodeGraph;
import com.junkiedan.ludumdare53.ui.AmmoUI;
import com.junkiedan.ludumdare53.ui.ClockUI;
import com.junkiedan.ludumdare53.ui.TargetArrow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GameScreen implements Screen {
    final LudumDare53 game;

    private final OrthographicCamera camera;
    private float cameraZoom;

    private final LevelMap levelMap;

    private final Vector2 tileSize;

    private final HashMap<Character, TextureRegion> textureMap;

    private final HashSet<Character> walkableTiles;

    private final Texture spriteSheet;

    private final Player player;

    private final DelayedRemovalArray<PizzaSlice> activePizzaSlices;

    private final DelayedRemovalArray<EvilCat> activeCats;

    private final int textureSize;

    private final Vector2 playerSize;

    private final TextureRegion[] pizzaSliceAnimationFrames;

    private final ArrayList<Rectangle> tileColliders;

    private Node[][] mapNodes;

    // Pathfinding (AI) Handlers
    private NodeGraph nodeGraph;
    private GraphPath<Node> nodePath;

    private Vector2 playerGridPosition;

    private Array<CatSpawn> catSpawnArray;

    private final float catEatingPizzaAnimationTime;

    private final DelayedRemovalArray<CatEatingPizza> activeCatEatsPizza;

    private final ClockUI clockUI;
    private final AmmoUI ammoUI;

    private PizzaSpawn pizzaSpawn;
    private PizzaTarget pizzaTarget;

    private final TargetArrow targetArrow;

    public GameScreen(LudumDare53 game) {
        TargetHouseMap.init();
        catEatingPizzaAnimationTime = 3f;
        textureSize = 16;
        this.game = game;

        game.skin = new Skin(Gdx.files.internal("ui/default/uiskin.json"));
        game.skin.getFont("default-font").getData().setScale(2);
        game.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(game.stage);

        // Load sprite sheet
        spriteSheet = new Texture(Gdx.files.internal("spritesheet/LudumDare53_v2.png"));

        // Create the camera and the spritebatch
        tileSize = new Vector2(128, 128);
        camera = new OrthographicCamera();
        cameraZoom = 3f;
        camera.setToOrtho(true, tileSize.x * 20 / cameraZoom, tileSize.y * 13 / cameraZoom);

        // Create reference to level map
        levelMap = new LevelMap();

        textureMap = new HashMap<>();
        textureMap.put('S', new TextureRegion(spriteSheet, 3 * textureSize, textureSize, textureSize, textureSize));
        textureMap.put('T', new TextureRegion(spriteSheet,  2 * textureSize, 0, textureSize, textureSize));
        textureMap.put('R', new TextureRegion(spriteSheet, 0, 0, textureSize, textureSize));
        textureMap.put('H', new TextureRegion(spriteSheet, 2 * textureSize, textureSize, textureSize, textureSize));
        textureMap.put('A', new TextureRegion(spriteSheet, 3 * textureSize, 0, textureSize, textureSize));
        textureMap.put('G', new TextureRegion(spriteSheet, textureSize, 0, textureSize, textureSize));
        textureMap.put('P', new TextureRegion(spriteSheet, 0, textureSize, textureSize, textureSize));
        textureMap.put('Z', new TextureRegion(spriteSheet, textureSize, textureSize, textureSize, textureSize));
        textureMap.put('E', new TextureRegion(spriteSheet, 0, 2 * textureSize, textureSize, textureSize));
        textureMap.put('X', new TextureRegion(spriteSheet, textureSize, 2 * textureSize, textureSize, textureSize));
        textureMap.put('1', new TextureRegion(spriteSheet, 2 * textureSize, 2 * textureSize, textureSize, textureSize));
        textureMap.put('2', new TextureRegion(spriteSheet, 3 * textureSize, 2 * textureSize, textureSize, textureSize));
        textureMap.put('3', new TextureRegion(spriteSheet, 2 * textureSize, 3 * textureSize, textureSize, textureSize));
        textureMap.put('4', new TextureRegion(spriteSheet, 3 * textureSize, 3 * textureSize, textureSize, textureSize));
        textureMap.put('5', new TextureRegion(spriteSheet, 0, 3 * textureSize, textureSize, textureSize));
        textureMap.put('6', new TextureRegion(spriteSheet, textureSize, 3 * textureSize, textureSize, textureSize));
        textureMap.put('7', new TextureRegion(spriteSheet, 0, 4 * textureSize, textureSize, textureSize));
        textureMap.put('8', new TextureRegion(spriteSheet, textureSize, 4 * textureSize, textureSize, textureSize));
        textureMap.put('9', new TextureRegion(spriteSheet, 2 * textureSize, 4 * textureSize, textureSize, textureSize));

        walkableTiles = new HashSet<>();
        walkableTiles.add('T');
        walkableTiles.add('R');
        walkableTiles.add('A');
        walkableTiles.add('G');
        walkableTiles.add('5');
        walkableTiles.add('6');
        walkableTiles.add('7');
        walkableTiles.add('8');
        walkableTiles.add('9');

        // Flip texture regions
        for(Map.Entry<Character, TextureRegion> entry : textureMap.entrySet()) {
            entry.getValue().flip(false, true);
        }

        tileColliders = new ArrayList<>();
        initializeColliders();

        playerSize = new Vector2(12, 12);
        player = new Player(7 * tileSize.x, 6 * tileSize.y, playerSize.x, playerSize.y, 300, camera, 0, 30);

        pizzaSliceAnimationFrames = player.getPizzaSliceFrames();

        activePizzaSlices = new DelayedRemovalArray<>();
        activeCats = new DelayedRemovalArray<>();
        activeCatEatsPizza = new DelayedRemovalArray<>();

        // Initialize AI
        initializePathfinding();

        // Find player's node
        playerGridPosition = new Vector2(player.getPosition().x / tileSize.x, player.getPosition().y / tileSize.y);

        catSpawnArray = new Array<>();
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 4, tileSize.y * 0), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 8, tileSize.y * 0), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 17, tileSize.y * 0), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 10, tileSize.y * 3), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 12, tileSize.y * 3), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 6, tileSize.y * 4), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 8, tileSize.y * 4), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 16, tileSize.y * 4), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 15, tileSize.y * 7), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 7, tileSize.y * 10), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 8, tileSize.y * 10), activeCats, nodeGraph, mapNodes,
                player, tileSize));
        catSpawnArray.add(new CatSpawn(new Vector2(tileSize.x * 17, tileSize.y * 10), activeCats, nodeGraph, mapNodes,
                player, tileSize));

        EvilCat cat = new EvilCat(32, 32, nodeGraph, player, mapNodes, tileSize);
//        System.out.println(playerGridPosition);
//        cat.setGoal(mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x]);
//        System.out.println(mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x].getPosition());
//        System.out.println(mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x].getGridPosition());
        activeCats.add(cat);

//        nodePath = nodeGraph.findPath(mapNodes[0][0], mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x]);
//        System.out.println(nodePath.getCount());

        // Initialize UI
        Table root = new Table();
        root.setFillParent(true);
        game.stage.addActor(root);

        root.pad(50);

        clockUI = new ClockUI(game.skin, player);
        root.add(clockUI.getLabel()).expandX().right().expandY().top();

        root.row();

        ammoUI = new AmmoUI(game.skin, player);
        root.add(ammoUI.getLabel()).expandX().right().expandY().bottom();

        // Initialize pizza spawn
        pizzaSpawn = new PizzaSpawn(new Vector2(950,680));

        pizzaTarget = null;

        targetArrow = new TargetArrow(player);

        game.soundManager.restart();
    }

    @Override
    public void show() {
        // When the scene starts initiate the background music
        // TODO
    }

    @Override
    public void render(float delta) {
        // Check if it is game over
        if(player.getTime() <= 0) {
            game.setScreen(new GameOverScreen(game, player.getScore()));
        }

        // Clear the screen with black color
        ScreenUtils.clear(0, 0, 0, 1);

        playerGridPosition.x = player.getPosition().x / tileSize.x;
        playerGridPosition.y = player.getPosition().y / tileSize.y;
        nodePath = nodeGraph.findPath(mapNodes[0][0], mapNodes[(int)playerGridPosition.y][(int)playerGridPosition.x]);

        // Tell camera to update its matrices
        camera.update();

        // Update camera position
        updateCameraPosition();

        // Tell the spritebatch to render in the
        // coordinate system specified by the camera
        game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // Render map
        renderMap();

        // Pizza spawn
        if(pizzaSpawn != null) {
            pizzaSpawn.render(game.batch);

            int playerX = (int)playerGridPosition.x;
            int playerY = (int)playerGridPosition.y;
            if((playerX == 6 && playerY == 5) ||
                    (playerX == 6 && playerY == 6) ||
                    (playerX == 7 && playerY == 6) ||
                    (playerX == 8 && playerY == 6)) {
                game.batch.begin();
                game.font.setColor(255, 255, 255, 255);
                game.font.getData().setScale(2.5f);
                game.font.draw(game.batch, "Press \"E\" to take Pizza", pizzaSpawn.getX() - 100, pizzaSpawn.getY());
                game.batch.end();
            }
        }

        // Pizza target
        if(pizzaTarget != null) {
            pizzaTarget.render(game.batch, game.font);
        }

        // Draw player
        game.batch.begin();
        player.draw(game.batch, game.shapeRenderer);
        game.batch.end();

        // Draw cats
        game.batch.begin();
        for(int i = 0; i < activeCats.size; i++) {
            EvilCat cat = activeCats.get(i);
            cat.render(game.batch, game.shapeRenderer);
        }
        game.batch.end();

        // Draw pizza slices
        game.batch.begin();
        for(int i = 0; i < activePizzaSlices.size; i++) {
            PizzaSlice pizzaSlice = activePizzaSlices.get(i);

            // Check for each slice if it collided with wall
            boolean collided = false;
            for(Rectangle collider : tileColliders) {
                if(collider.overlaps(pizzaSlice.getColliderRectangle())) {
                    collided = true;
                    break;
                }
            }

            boolean destroyedPizzaSlice = false;
            Vector2 catAndPizzaCollisionPosition = new Vector2(0, 0);
            // Check if pizza slice collided with cat
            for(int j = 0; j < activeCats.size; j++) {
                EvilCat cat = activeCats.get(j);
                if(pizzaSlice.getColliderRectangle().overlaps(cat.getHitbox())) {
                    destroyedPizzaSlice = true;
                    catAndPizzaCollisionPosition = cat.getPosition().cpy();
                    activeCats.removeIndex(j);
                    game.soundManager.catSound.play();
                }
            }

            if(collided || destroyedPizzaSlice) {
                activePizzaSlices.removeIndex(i);
            }
            else {
                pizzaSlice.render(delta, game.batch);
            }

            if(destroyedPizzaSlice) {
                activeCatEatsPizza.add(new CatEatingPizza(catAndPizzaCollisionPosition));
            }
        }
        game.batch.end();

        // Check if any cat collided with player (only when there is an active target house
        if(pizzaTarget != null && player.getAmmo() > 0) {
            for(int i = 0; i < activeCats.size; i++) {
                EvilCat cat = activeCats.get(i);
                if(cat.getHitbox().overlaps(player.getHitbox())) {
                    player.removeAmmo();
                    activeCatEatsPizza.add(new CatEatingPizza(cat.getPosition().cpy()));
                    activeCats.removeIndex(i);
                    game.soundManager.catSound.play();
                }
            }
        }

        // Update cat spawns
        for(CatSpawn catSpawn : catSpawnArray) {
            catSpawn.update();
        }

        // Update cats that eat pizza
        for(int i = 0; i < activeCatEatsPizza.size; i++) {
            CatEatingPizza cat = activeCatEatsPizza.get(i);
            if(cat.getStateTime() > catEatingPizzaAnimationTime) {
                activeCatEatsPizza.removeIndex(i);
            }
            else {
                cat.render(game.batch);
            }
        }

        // Handle user input
        handleInput(delta);

        // Update UI
        clockUI.update(Gdx.graphics.getDeltaTime());
        ammoUI.update();

        // Draw UI
        game.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        game.stage.draw();

        targetArrow.render(game.batch, game.shapeRenderer);

        // Render pathfinding
//        renderPathfinding(delta);


        // Show FPS
//        showFps(delta);
    }

    private void updateCameraPosition() {
        float tileSizeHeight = levelMap.getHeight() * tileSize.y;
        float tileSizeWidth = levelMap.getWidth() * tileSize.x;

        float innerWidth = camera.viewportWidth / 2f;
        float innerHeight = camera.viewportHeight / 2f;

        Vector3 cameraPosition = new Vector3(player.getPosition(), -10);

        // Check for x
        if(cameraPosition.x - innerWidth < 0 || cameraPosition.x + innerWidth > tileSizeWidth) {
            cameraPosition.x = camera.position.x;
        }

        // Check for y
        if(cameraPosition.y - innerHeight < 0 || cameraPosition.y + innerHeight > tileSizeHeight) {
            cameraPosition.y = camera.position.y;
        }

        camera.position.lerp(cameraPosition, 0.1f);
    }

    private void handleInput(float delta) {
        boolean isMovingOnXAxis = false;
        boolean isMovingOnYAxis = false;
        Vector2 playerMove = new Vector2();
        if(Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerMove.y = -1;
            isMovingOnYAxis = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerMove.y = 1;
            isMovingOnYAxis = true;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerMove.x = -1;
            player.setAnimationState(PlayerAnimationState.RUNNING_LEFT);
            isMovingOnXAxis = true;
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerMove.x = 1;
            player.setAnimationState(PlayerAnimationState.RUNNING_RIGHT);
            isMovingOnXAxis = true;
        }

        if(isMovingOnXAxis && isMovingOnYAxis) {
            playerMove.x = playerMove.x > 0 ? (float)Math.sqrt(2) / 2 : -(float)Math.sqrt(2) / 2;
            playerMove.y = playerMove.y > 0 ? (float)Math.sqrt(2) / 2 : -(float)Math.sqrt(2) / 2;
        }

        if(isMovingOnXAxis || isMovingOnYAxis) {
            game.soundManager.playWalkSound();
        }
        else {
            game.soundManager.stopWalkSound();
        }

        // Before you perform the move check if the move is viable
        // Find the tile row and column

        player.move(playerMove.x, playerMove.y, delta, tileColliders);

        // Fire pizza slice
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            if(player.getAmmo() > 0) {
                activePizzaSlices.add(new PizzaSlice(player.getPosition(), player.getWeaponDirection(), 600, pizzaSliceAnimationFrames));
                player.fire();
                game.soundManager.firePizzaSound.play();
            }
        }

        // Collect pizza box
        int playerX = (int)playerGridPosition.x;
        int playerY = (int)playerGridPosition.y;
        if(pizzaSpawn != null && ((playerX == 6 && playerY == 5) ||
                (playerX == 6 && playerY == 6) ||
                (playerX == 7 && playerY == 6) ||
                (playerX == 8 && playerY == 6)) &&
                Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.addAmmo(16);
            pizzaSpawn = null;
            // Set house target
            pizzaTarget = new PizzaTarget(tileSize);

            Vector2 targetHousePosition = new Vector2(pizzaTarget.getPositionX(), pizzaTarget.getPositionY());
            targetArrow.displayArrow(true, targetHousePosition);

            game.soundManager.interactionSound.play();
        }

        // Deliver pizza box
        int deliveryX = pizzaTarget != null ? pizzaTarget.getTileX() : -1;
        int deliveryY = pizzaTarget != null ? pizzaTarget.getTileY() : -1;
        if(pizzaTarget != null && ((playerX == deliveryX - 1 && playerY == deliveryY) ||
                (playerX == deliveryX + 1 && playerY == deliveryY) ||
                (playerX == deliveryX - 1 && playerY == deliveryY + 1) ||
                (playerX == deliveryX && playerY == deliveryY + 1) ||
                (playerX == deliveryX + 1 && playerY == deliveryY + 1) ||
                (playerX == deliveryX - 1 && playerY == deliveryY - 1) ||
                (playerX == deliveryX && playerY == deliveryY - 1) ||
                (playerX == deliveryX + 1 && playerY == deliveryY - 1)) &&
                Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            player.addTime(player.getAmmo());
            player.setAmmo(0);
            pizzaTarget = null;
            pizzaSpawn = new PizzaSpawn(new Vector2(950,680));

            targetArrow.displayArrow(false, null);

            game.soundManager.interactionSound.play();
        }
    }

    private void showFps(float delta) {
        System.out.println("FPS: " + 1/delta);
    }

    private void initializeColliders() {
        Rectangle topMapBarrierRectangle = new Rectangle(0, -tileSize.y, tileSize.x * 20, tileSize.y);
        tileColliders.add(topMapBarrierRectangle);

        Rectangle rightMapBarrierRectangle = new Rectangle(19 * tileSize.x, 0, tileSize.x, tileSize.y * 13);
        tileColliders.add(rightMapBarrierRectangle);

        Rectangle bottomMapBarrierRectangle = new Rectangle(0, 12 * tileSize.y, tileSize.x * 20, tileSize.y);
        tileColliders.add(bottomMapBarrierRectangle);

        Rectangle leftMapBarrierRectangle = new Rectangle(-tileSize.x, 0, tileSize.x, tileSize.y * 13);
        tileColliders.add(leftMapBarrierRectangle);

        tileColliders.add(new Rectangle(4 * tileSize.x, 0, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(8 * tileSize.x, 0, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(12 * tileSize.x, 0, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(13 * tileSize.x, 0, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(17 * tileSize.x, 0, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(0, tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(2 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(5 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(8 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(13 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(16 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(18 * tileSize.x, 2 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(10 * tileSize.x, 3 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(12 * tileSize.x, 3 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(13 * tileSize.x, 3 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(0, 4 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(6 * tileSize.x, 4 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(8 * tileSize.x, 4 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(16 * tileSize.x, 4 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(7 * tileSize.x, 5 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(8 * tileSize.x, 5 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(10 * tileSize.x, 5 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(18 * tileSize.x, 6 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(0, 7 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(3 * tileSize.x, 7 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(4 * tileSize.x, 7 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(11 * tileSize.x, 7 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(12 * tileSize.x, 7 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(15 * tileSize.x, 7 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(0, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(3 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(4 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(6 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(9 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(11 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(12 * tileSize.x, 8 * tileSize.y, tileSize.x, tileSize.y));

        tileColliders.add(new Rectangle(2 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(3 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(4 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(7 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(8 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(12 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(13 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
        tileColliders.add(new Rectangle(17 * tileSize.x, 10 * tileSize.y, tileSize.x, tileSize.y));
    }

    private void renderMap() {
        game.batch.begin();
        for(int i = 0; i < levelMap.getHeight(); ++i) {
            for(int j = 0; j < levelMap.getWidth(); ++j) {
                game.batch.draw(textureMap.get(levelMap.getTileValue(i, j)), j * tileSize.y, i * tileSize.x, tileSize.x, tileSize.y);
//                game.batch.draw(textureMap.get('A'), 0, 0);
            }
        }
        game.batch.end();
    }

    private void renderPathfinding(float delta) {
        for(Graph graph : nodeGraph.getGraphs()) {
            graph.render(game.shapeRenderer);
        }

        for(Node node : nodeGraph.getNodes()) {
            node.render(game.shapeRenderer, game.batch, game.font, false);
        }

        for(Node node : nodePath) {
            node.render(game.shapeRenderer, game.batch, game.font, true);
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        spriteSheet.dispose();
    }

    private void initializePathfinding() {
        nodeGraph = new NodeGraph();
        mapNodes = new Node[13][20];

        Node node_0_0 = new Node(0,0);
        Node node_0_1 = new Node(0,1);
        Node node_0_2 = new Node(0,2);
        Node node_0_3 = new Node(0,3);
        Node node_0_4 = new Node(0,4);
        Node node_0_5 = new Node(0,5);
        Node node_0_6 = new Node(0,6);
        Node node_0_7 = new Node(0,7);
        Node node_0_8 = new Node(0,8);
        Node node_0_9 = new Node(0,9);
        Node node_0_10 = new Node(0,10);
        Node node_0_11 = new Node(0,11);
        Node node_0_14 = new Node(0,14);
        Node node_0_15 = new Node(0,15);
        Node node_0_16 = new Node(0,16);
        Node node_0_17 = new Node(0,17);
        Node node_0_18 = new Node(0,18);

        Node node_1_1 = new Node(1,1);
        Node node_1_2 = new Node(1,2);
        Node node_1_3 = new Node(1,3);
        Node node_1_4 = new Node(1,4);
        Node node_1_5 = new Node(1,5);
        Node node_1_6 = new Node(1,6);
        Node node_1_7 = new Node(1,7);
        Node node_1_8 = new Node(1,8);
        Node node_1_9 = new Node(1,9);
        Node node_1_10 = new Node(1,10);
        Node node_1_11 = new Node(1,11);
        Node node_1_12 = new Node(1,12);
        Node node_1_13 = new Node(1,13);
        Node node_1_14 = new Node(1,14);
        Node node_1_15 = new Node(1,15);
        Node node_1_16 = new Node(1,16);
        Node node_1_17 = new Node(1,17);
        Node node_1_18 = new Node(1,18);

        Node node_2_0 = new Node(2,0);
        Node node_2_1 = new Node(2,1);
        Node node_2_3 = new Node(2,3);
        Node node_2_4 = new Node(2,4);
        Node node_2_6 = new Node(2,6);
        Node node_2_7 = new Node(2,7);
        Node node_2_9 = new Node(2,9);
        Node node_2_10 = new Node(2,10);
        Node node_2_11 = new Node(2,11);
        Node node_2_12 = new Node(2,12);
        Node node_2_14 = new Node(2,14);
        Node node_2_15 = new Node(2,15);
        Node node_2_17 = new Node(2,17);

        Node node_3_0 = new Node(3,0);
        Node node_3_1 = new Node(3,1);
        Node node_3_2 = new Node(3,2);
        Node node_3_3 = new Node(3,3);
        Node node_3_4 = new Node(3,4);
        Node node_3_5 = new Node(3,5);
        Node node_3_6 = new Node(3,6);
        Node node_3_7 = new Node(3,7);
        Node node_3_8 = new Node(3,8);
        Node node_3_9 = new Node(3,9);
        Node node_3_10 = new Node(3,10);
        Node node_3_11 = new Node(3,11);
        Node node_3_12 = new Node(3,12);
        Node node_3_14 = new Node(3,14);
        Node node_3_15 = new Node(3,15);
        Node node_3_16 = new Node(3,16);
        Node node_3_17 = new Node(3,17);
        Node node_3_18 = new Node(3,18);

        Node node_4_1 = new Node(4,1);
        Node node_4_2 = new Node(4,2);
        Node node_4_3 = new Node(4,3);
        Node node_4_4 = new Node(4,4);
        Node node_4_5 = new Node(4,5);
        Node node_4_6 = new Node(4,6);
        Node node_4_7 = new Node(4,7);
        Node node_4_8 = new Node(4,8);
        Node node_4_9 = new Node(4,9);
        Node node_4_10 = new Node(4,10);
        Node node_4_11 = new Node(4,11);
        Node node_4_12 = new Node(4,12);
        Node node_4_13 = new Node(4,13);
        Node node_4_14 = new Node(4,14);
        Node node_4_15 = new Node(4,15);
        Node node_4_16 = new Node(4,16);
        Node node_4_17 = new Node(4,17);
        Node node_4_18 = new Node(4,18);

        Node node_5_0 = new Node(5,0);
        Node node_5_1 = new Node(5,1);
        Node node_5_2 = new Node(5,2);
        Node node_5_3 = new Node(5,3);
        Node node_5_4 = new Node(5,4);
        Node node_5_5 = new Node(5,5);
        Node node_5_6 = new Node(5,6);
        Node node_5_9 = new Node(5,9);
        Node node_5_11 = new Node(5,11);
        Node node_5_12 = new Node(5,12);
        Node node_5_13 = new Node(5,13);
        Node node_5_14 = new Node(5,14);
        Node node_5_15 = new Node(5,15);
        Node node_5_16 = new Node(5,16);
        Node node_5_17 = new Node(5,17);
        Node node_5_18 = new Node(5,18);

        Node node_6_0 = new Node(6,0);
        Node node_6_1 = new Node(6,1);
        Node node_6_2 = new Node(6,2);
        Node node_6_3 = new Node(6,3);
        Node node_6_4 = new Node(6,4);
        Node node_6_5 = new Node(6,5);
        Node node_6_6 = new Node(6,6);
        Node node_6_7 = new Node(6,7);
        Node node_6_8 = new Node(6,8);
        Node node_6_9 = new Node(6,9);
        Node node_6_10 = new Node(6,10);
        Node node_6_11 = new Node(6,11);
        Node node_6_12 = new Node(6,12);
        Node node_6_13 = new Node(6,13);
        Node node_6_14 = new Node(6,14);
        Node node_6_15 = new Node(6,15);
        Node node_6_16 = new Node(6,16);
        Node node_6_17 = new Node(6,17);

        Node node_7_1 = new Node(7,1);
        Node node_7_2 = new Node(7,2);
        Node node_7_5 = new Node(7,5);
        Node node_7_6 = new Node(7,6);
        Node node_7_7 = new Node(7,7);
        Node node_7_8 = new Node(7,8);
        Node node_7_9 = new Node(7,9);
        Node node_7_10 = new Node(7,10);
        Node node_7_13 = new Node(7,13);
        Node node_7_14 = new Node(7,14);
        Node node_7_15 = new Node(7,15);
        Node node_7_16 = new Node(7,16);
        Node node_7_17 = new Node(7,17);
        Node node_7_18 = new Node(7,18);

        Node node_8_1 = new Node(8,1);
        Node node_8_2 = new Node(8,2);
        Node node_8_5 = new Node(8,5);
        Node node_8_7 = new Node(8,7);
        Node node_8_8 = new Node(8,8);
        Node node_8_10 = new Node(8,10);
        Node node_8_13 = new Node(8,13);
        Node node_8_14 = new Node(8,14);
        Node node_8_15 = new Node(8,15);
        Node node_8_16 = new Node(8,16);
        Node node_8_17 = new Node(8,17);
        Node node_8_18 = new Node(8,18);

        Node node_9_0 = new Node(9,0);
        Node node_9_1 = new Node(9,1);
        Node node_9_2 = new Node(9,2);
        Node node_9_3 = new Node(9,3);
        Node node_9_4 = new Node(9,4);
        Node node_9_5 = new Node(9,5);
        Node node_9_6 = new Node(9,6);
        Node node_9_7 = new Node(9,7);
        Node node_9_8 = new Node(9,8);
        Node node_9_9 = new Node(9,9);
        Node node_9_10 = new Node(9,10);
        Node node_9_11 = new Node(9,11);
        Node node_9_12 = new Node(9,12);
        Node node_9_13 = new Node(9,13);
        Node node_9_14 = new Node(9,14);
        Node node_9_15 = new Node(9,15);
        Node node_9_16 = new Node(9,16);
        Node node_9_17 = new Node(9,17);
        Node node_9_18 = new Node(9,18);

        Node node_10_0 = new Node(10,0);
        Node node_10_1 = new Node(10,1);
        Node node_10_5 = new Node(10,5);
        Node node_10_6 = new Node(10,6);
        Node node_10_7 = new Node(10,7);
        Node node_10_8 = new Node(10,8);
        Node node_10_9 = new Node(10,9);
        Node node_10_10 = new Node(10,10);
        Node node_10_11 = new Node(10,11);
        Node node_10_14 = new Node(10,14);
        Node node_10_15 = new Node(10,15);
        Node node_10_16 = new Node(10,16);
        Node node_10_17 = new Node(10,17);
        Node node_10_18 = new Node(10,18);

        Node node_11_0 = new Node(11,0);
        Node node_11_1 = new Node(11,1);
        Node node_11_2 = new Node(11,2);
        Node node_11_3 = new Node(11,3);
        Node node_11_4 = new Node(11,4);
        Node node_11_5 = new Node(11,5);
        Node node_11_6 = new Node(11,6);
        Node node_11_7 = new Node(11,7);
        Node node_11_8 = new Node(11,8);
        Node node_11_9 = new Node(11,9);
        Node node_11_10 = new Node(11,10);
        Node node_11_11 = new Node(11,11);
        Node node_11_12 = new Node(11,12);
        Node node_11_13 = new Node(11,13);
        Node node_11_14 = new Node(11,14);
        Node node_11_15 = new Node(11,15);
        Node node_11_16 = new Node(11,16);
        Node node_11_17 = new Node(11,17);
        Node node_11_18 = new Node(11,18);

        mapNodes[0][0]  = node_0_0 ;
        mapNodes[0][1]  = node_0_1 ;
        mapNodes[0][2]  = node_0_2 ;
        mapNodes[0][3]  = node_0_3 ;
        mapNodes[0][4]  = node_0_4 ;
        mapNodes[0][5]  = node_0_5 ;
        mapNodes[0][6]  = node_0_6 ;
        mapNodes[0][7]  = node_0_7 ;
        mapNodes[0][8]  = node_0_8 ;
        mapNodes[0][9]  = node_0_9 ;
        mapNodes[0][10] = node_0_10;
        mapNodes[0][11] = node_0_11;
        mapNodes[0][14] = node_0_14;
        mapNodes[0][15] = node_0_15;
        mapNodes[0][16] = node_0_16;
        mapNodes[0][17] = node_0_17;
        mapNodes[0][18] = node_0_18;

        mapNodes[1][1]  = node_1_1 ;
        mapNodes[1][2]  = node_1_2 ;
        mapNodes[1][3]  = node_1_3 ;
        mapNodes[1][4]  = node_1_4 ;
        mapNodes[1][5]  = node_1_5 ;
        mapNodes[1][6]  = node_1_6 ;
        mapNodes[1][7]  = node_1_7 ;
        mapNodes[1][8]  = node_1_8 ;
        mapNodes[1][9]  = node_1_9 ;
        mapNodes[1][10] = node_1_10;
        mapNodes[1][11] = node_1_11;
        mapNodes[1][12] = node_1_12;
        mapNodes[1][13] = node_1_13;
        mapNodes[1][14] = node_1_14;
        mapNodes[1][15] = node_1_15;
        mapNodes[1][16] = node_1_16;
        mapNodes[1][17] = node_1_17;
        mapNodes[1][18] = node_1_18;

        mapNodes[2][0]  = node_2_0 ;
        mapNodes[2][1]  = node_2_1 ;
        mapNodes[2][3]  = node_2_3 ;
        mapNodes[2][4]  = node_2_4 ;
        mapNodes[2][6]  = node_2_6 ;
        mapNodes[2][7]  = node_2_7 ;
        mapNodes[2][9]  = node_2_9 ;
        mapNodes[2][10] = node_2_10;
        mapNodes[2][11] = node_2_11;
        mapNodes[2][12] = node_2_12;
        mapNodes[2][14] = node_2_14;
        mapNodes[2][15] = node_2_15;
        mapNodes[2][17] = node_2_17;

        mapNodes[3][0]  = node_3_0 ;
        mapNodes[3][1]  = node_3_1 ;
        mapNodes[3][2]  = node_3_2 ;
        mapNodes[3][3]  = node_3_3 ;
        mapNodes[3][4]  = node_3_4 ;
        mapNodes[3][5]  = node_3_5 ;
        mapNodes[3][6]  = node_3_6 ;
        mapNodes[3][7]  = node_3_7 ;
        mapNodes[3][8]  = node_3_8 ;
        mapNodes[3][9]  = node_3_9 ;
        mapNodes[3][10] = node_3_10;
        mapNodes[3][11] = node_3_11;
        mapNodes[3][12] = node_3_12;
        mapNodes[3][14] = node_3_14;
        mapNodes[3][15] = node_3_15;
        mapNodes[3][16] = node_3_16;
        mapNodes[3][17] = node_3_17;
        mapNodes[3][18] = node_3_18;

        mapNodes[4][1]  = node_4_1 ;
        mapNodes[4][2]  = node_4_2 ;
        mapNodes[4][3]  = node_4_3 ;
        mapNodes[4][4]  = node_4_4 ;
        mapNodes[4][5]  = node_4_5 ;
        mapNodes[4][6]  = node_4_6 ;
        mapNodes[4][7]  = node_4_7 ;
        mapNodes[4][8]  = node_4_8 ;
        mapNodes[4][9]  = node_4_9 ;
        mapNodes[4][10] = node_4_10;
        mapNodes[4][11] = node_4_11;
        mapNodes[4][12] = node_4_12;
        mapNodes[4][13] = node_4_13;
        mapNodes[4][14] = node_4_14;
        mapNodes[4][15] = node_4_15;
        mapNodes[4][16] = node_4_16;
        mapNodes[4][17] = node_4_17;
        mapNodes[4][18] = node_4_18;

        mapNodes[5][0]  = node_5_0 ;
        mapNodes[5][1]  = node_5_1 ;
        mapNodes[5][2]  = node_5_2 ;
        mapNodes[5][3]  = node_5_3 ;
        mapNodes[5][4]  = node_5_4 ;
        mapNodes[5][5]  = node_5_5 ;
        mapNodes[5][6]  = node_5_6 ;
        mapNodes[5][9]  = node_5_9 ;
        mapNodes[5][11] = node_5_11;
        mapNodes[5][12] = node_5_12;
        mapNodes[5][13] = node_5_13;
        mapNodes[5][14] = node_5_14;
        mapNodes[5][15] = node_5_15;
        mapNodes[5][16] = node_5_16;
        mapNodes[5][17] = node_5_17;
        mapNodes[5][18] = node_5_18;

        mapNodes[6][0]  = node_6_0 ;
        mapNodes[6][1]  = node_6_1 ;
        mapNodes[6][2]  = node_6_2 ;
        mapNodes[6][3]  = node_6_3 ;
        mapNodes[6][4]  = node_6_4 ;
        mapNodes[6][5]  = node_6_5 ;
        mapNodes[6][6]  = node_6_6 ;
        mapNodes[6][7]  = node_6_7 ;
        mapNodes[6][8]  = node_6_8 ;
        mapNodes[6][9]  = node_6_9 ;
        mapNodes[6][10] = node_6_10;
        mapNodes[6][11] = node_6_11;
        mapNodes[6][12] = node_6_12;
        mapNodes[6][13] = node_6_13;
        mapNodes[6][14] = node_6_14;
        mapNodes[6][15] = node_6_15;
        mapNodes[6][16] = node_6_16;
        mapNodes[6][17] = node_6_17;

        mapNodes[7][1]  = node_7_1 ;
        mapNodes[7][2]  = node_7_2 ;
        mapNodes[7][5]  = node_7_5 ;
        mapNodes[7][6]  = node_7_6 ;
        mapNodes[7][7]  = node_7_7 ;
        mapNodes[7][8]  = node_7_8 ;
        mapNodes[7][9]  = node_7_9 ;
        mapNodes[7][10] = node_7_10;
        mapNodes[7][13] = node_7_13;
        mapNodes[7][14] = node_7_14;
        mapNodes[7][15] = node_7_15;
        mapNodes[7][16] = node_7_16;
        mapNodes[7][17] = node_7_17;
        mapNodes[7][18] = node_7_18;

        mapNodes[8][1]  = node_8_1 ;
        mapNodes[8][2]  = node_8_2 ;
        mapNodes[8][5]  = node_8_5 ;
        mapNodes[8][7]  = node_8_7 ;
        mapNodes[8][8]  = node_8_8 ;
        mapNodes[8][10] = node_8_10;
        mapNodes[8][13] = node_8_13;
        mapNodes[8][14] = node_8_14;
        mapNodes[8][15] = node_8_15;
        mapNodes[8][16] = node_8_16;
        mapNodes[8][17] = node_8_17;
        mapNodes[8][18] = node_8_18;

        mapNodes[9][0]  = node_9_0 ;
        mapNodes[9][1]  = node_9_1 ;
        mapNodes[9][2]  = node_9_2 ;
        mapNodes[9][3]  = node_9_3 ;
        mapNodes[9][4]  = node_9_4 ;
        mapNodes[9][5]  = node_9_5 ;
        mapNodes[9][6]  = node_9_6 ;
        mapNodes[9][7]  = node_9_7 ;
        mapNodes[9][8]  = node_9_8 ;
        mapNodes[9][9]  = node_9_9 ;
        mapNodes[9][10] = node_9_10;
        mapNodes[9][11] = node_9_11;
        mapNodes[9][12] = node_9_12;
        mapNodes[9][13] = node_9_13;
        mapNodes[9][14] = node_9_14;
        mapNodes[9][15] = node_9_15;
        mapNodes[9][16] = node_9_16;
        mapNodes[9][17] = node_9_17;
        mapNodes[9][18] = node_9_18;

        mapNodes[10][0]  = node_10_0 ;
        mapNodes[10][1]  = node_10_1 ;
        mapNodes[10][5]  = node_10_5 ;
        mapNodes[10][6]  = node_10_6 ;
        mapNodes[10][7]  = node_10_7 ;
        mapNodes[10][8]  = node_10_8 ;
        mapNodes[10][9]  = node_10_9 ;
        mapNodes[10][10] = node_10_10;
        mapNodes[10][11] = node_10_11;
        mapNodes[10][14] = node_10_14;
        mapNodes[10][15] = node_10_15;
        mapNodes[10][16] = node_10_16;
        mapNodes[10][17] = node_10_17;
        mapNodes[10][18] = node_10_18;

        mapNodes[11][0]  = node_11_0 ;
        mapNodes[11][1]  = node_11_1 ;
        mapNodes[11][2]  = node_11_2 ;
        mapNodes[11][3]  = node_11_3 ;
        mapNodes[11][4]  = node_11_4 ;
        mapNodes[11][5]  = node_11_5 ;
        mapNodes[11][6]  = node_11_6 ;
        mapNodes[11][7]  = node_11_7 ;
        mapNodes[11][8]  = node_11_8 ;
        mapNodes[11][9]  = node_11_9 ;
        mapNodes[11][10] = node_11_10;
        mapNodes[11][11] = node_11_11;
        mapNodes[11][12] = node_11_12;
        mapNodes[11][13] = node_11_13;
        mapNodes[11][14] = node_11_14;
        mapNodes[11][15] = node_11_15;
        mapNodes[11][16] = node_11_16;
        mapNodes[11][17] = node_11_17;
        mapNodes[11][18] = node_11_18;

        nodeGraph.addNode(node_0_0);
        nodeGraph.addNode(node_0_1);
        nodeGraph.addNode(node_0_2);
        nodeGraph.addNode(node_0_3);
        nodeGraph.addNode(node_0_4);
        nodeGraph.addNode(node_0_5);
        nodeGraph.addNode(node_0_6);
        nodeGraph.addNode(node_0_7);
        nodeGraph.addNode(node_0_8);
        nodeGraph.addNode(node_0_9);
        nodeGraph.addNode(node_0_10);
        nodeGraph.addNode(node_0_11);
        nodeGraph.addNode(node_0_14);
        nodeGraph.addNode(node_0_15);
        nodeGraph.addNode(node_0_16);
        nodeGraph.addNode(node_0_17);
        nodeGraph.addNode(node_0_18);

        nodeGraph.addNode(node_1_1);
        nodeGraph.addNode(node_1_2);
        nodeGraph.addNode(node_1_3);
        nodeGraph.addNode(node_1_4);
        nodeGraph.addNode(node_1_5);
        nodeGraph.addNode(node_1_6);
        nodeGraph.addNode(node_1_7);
        nodeGraph.addNode(node_1_8);
        nodeGraph.addNode(node_1_9);
        nodeGraph.addNode(node_1_10);
        nodeGraph.addNode(node_1_11);
        nodeGraph.addNode(node_1_12);
        nodeGraph.addNode(node_1_13);
        nodeGraph.addNode(node_1_14);
        nodeGraph.addNode(node_1_15);
        nodeGraph.addNode(node_1_16);
        nodeGraph.addNode(node_1_17);
        nodeGraph.addNode(node_1_18);

        nodeGraph.addNode(node_2_0);
        nodeGraph.addNode(node_2_1);
        nodeGraph.addNode(node_2_3);
        nodeGraph.addNode(node_2_4);
        nodeGraph.addNode(node_2_6);
        nodeGraph.addNode(node_2_7);
        nodeGraph.addNode(node_2_9);
        nodeGraph.addNode(node_2_10);
        nodeGraph.addNode(node_2_11);
        nodeGraph.addNode(node_2_12);
        nodeGraph.addNode(node_2_14);
        nodeGraph.addNode(node_2_15);
        nodeGraph.addNode(node_2_17);

        nodeGraph.addNode(node_3_0);
        nodeGraph.addNode(node_3_1);
        nodeGraph.addNode(node_3_2);
        nodeGraph.addNode(node_3_3);
        nodeGraph.addNode(node_3_4);
        nodeGraph.addNode(node_3_5);
        nodeGraph.addNode(node_3_6);
        nodeGraph.addNode(node_3_7);
        nodeGraph.addNode(node_3_8);
        nodeGraph.addNode(node_3_9);
        nodeGraph.addNode(node_3_10);
        nodeGraph.addNode(node_3_11);
        nodeGraph.addNode(node_3_12);
        nodeGraph.addNode(node_3_14);
        nodeGraph.addNode(node_3_15);
        nodeGraph.addNode(node_3_16);
        nodeGraph.addNode(node_3_17);
        nodeGraph.addNode(node_3_18);

        nodeGraph.addNode(node_4_1);
        nodeGraph.addNode(node_4_2);
        nodeGraph.addNode(node_4_3);
        nodeGraph.addNode(node_4_4);
        nodeGraph.addNode(node_4_5);
        nodeGraph.addNode(node_4_6);
        nodeGraph.addNode(node_4_7);
        nodeGraph.addNode(node_4_8);
        nodeGraph.addNode(node_4_9);
        nodeGraph.addNode(node_4_10);
        nodeGraph.addNode(node_4_11);
        nodeGraph.addNode(node_4_12);
        nodeGraph.addNode(node_4_13);
        nodeGraph.addNode(node_4_14);
        nodeGraph.addNode(node_4_15);
        nodeGraph.addNode(node_4_16);
        nodeGraph.addNode(node_4_17);
        nodeGraph.addNode(node_4_18);

        nodeGraph.addNode(node_5_0);
        nodeGraph.addNode(node_5_1);
        nodeGraph.addNode(node_5_2);
        nodeGraph.addNode(node_5_3);
        nodeGraph.addNode(node_5_4);
        nodeGraph.addNode(node_5_5);
        nodeGraph.addNode(node_5_6);
        nodeGraph.addNode(node_5_9);
        nodeGraph.addNode(node_5_11);
        nodeGraph.addNode(node_5_12);
        nodeGraph.addNode(node_5_13);
        nodeGraph.addNode(node_5_14);
        nodeGraph.addNode(node_5_15);
        nodeGraph.addNode(node_5_16);
        nodeGraph.addNode(node_5_17);
        nodeGraph.addNode(node_5_18);

        nodeGraph.addNode(node_6_0);
        nodeGraph.addNode(node_6_1);
        nodeGraph.addNode(node_6_2);
        nodeGraph.addNode(node_6_3);
        nodeGraph.addNode(node_6_4);
        nodeGraph.addNode(node_6_5);
        nodeGraph.addNode(node_6_6);
        nodeGraph.addNode(node_6_7);
        nodeGraph.addNode(node_6_8);
        nodeGraph.addNode(node_6_9);
        nodeGraph.addNode(node_6_10);
        nodeGraph.addNode(node_6_11);
        nodeGraph.addNode(node_6_12);
        nodeGraph.addNode(node_6_13);
        nodeGraph.addNode(node_6_14);
        nodeGraph.addNode(node_6_15);
        nodeGraph.addNode(node_6_16);
        nodeGraph.addNode(node_6_17);

        nodeGraph.addNode(node_7_1);
        nodeGraph.addNode(node_7_2);
        nodeGraph.addNode(node_7_5);
        nodeGraph.addNode(node_7_6);
        nodeGraph.addNode(node_7_7);
        nodeGraph.addNode(node_7_8);
        nodeGraph.addNode(node_7_9);
        nodeGraph.addNode(node_7_10);
        nodeGraph.addNode(node_7_13);
        nodeGraph.addNode(node_7_14);
        nodeGraph.addNode(node_7_15);
        nodeGraph.addNode(node_7_16);
        nodeGraph.addNode(node_7_17);
        nodeGraph.addNode(node_7_18);

        nodeGraph.addNode(node_8_1);
        nodeGraph.addNode(node_8_2);
        nodeGraph.addNode(node_8_5);
        nodeGraph.addNode(node_8_7);
        nodeGraph.addNode(node_8_8);
        nodeGraph.addNode(node_8_10);
        nodeGraph.addNode(node_8_13);
        nodeGraph.addNode(node_8_14);
        nodeGraph.addNode(node_8_15);
        nodeGraph.addNode(node_8_16);
        nodeGraph.addNode(node_8_17);
        nodeGraph.addNode(node_8_18);

        nodeGraph.addNode(node_9_0);
        nodeGraph.addNode(node_9_1);
        nodeGraph.addNode(node_9_2);
        nodeGraph.addNode(node_9_3);
        nodeGraph.addNode(node_9_4);
        nodeGraph.addNode(node_9_5);
        nodeGraph.addNode(node_9_6);
        nodeGraph.addNode(node_9_7);
        nodeGraph.addNode(node_9_8);
        nodeGraph.addNode(node_9_9);
        nodeGraph.addNode(node_9_10);
        nodeGraph.addNode(node_9_11);
        nodeGraph.addNode(node_9_12);
        nodeGraph.addNode(node_9_13);
        nodeGraph.addNode(node_9_14);
        nodeGraph.addNode(node_9_15);
        nodeGraph.addNode(node_9_16);
        nodeGraph.addNode(node_9_17);
        nodeGraph.addNode(node_9_18);

        nodeGraph.addNode(node_10_0);
        nodeGraph.addNode(node_10_1);
        nodeGraph.addNode(node_10_5);
        nodeGraph.addNode(node_10_6);
        nodeGraph.addNode(node_10_7);
        nodeGraph.addNode(node_10_8);
        nodeGraph.addNode(node_10_9);
        nodeGraph.addNode(node_10_10);
        nodeGraph.addNode(node_10_11);
        nodeGraph.addNode(node_10_14);
        nodeGraph.addNode(node_10_15);
        nodeGraph.addNode(node_10_16);
        nodeGraph.addNode(node_10_17);
        nodeGraph.addNode(node_10_18);

        nodeGraph.addNode(node_11_0);
        nodeGraph.addNode(node_11_1);
        nodeGraph.addNode(node_11_2);
        nodeGraph.addNode(node_11_3);
        nodeGraph.addNode(node_11_4);
        nodeGraph.addNode(node_11_5);
        nodeGraph.addNode(node_11_6);
        nodeGraph.addNode(node_11_7);
        nodeGraph.addNode(node_11_8);
        nodeGraph.addNode(node_11_9);
        nodeGraph.addNode(node_11_10);
        nodeGraph.addNode(node_11_11);
        nodeGraph.addNode(node_11_12);
        nodeGraph.addNode(node_11_13);
        nodeGraph.addNode(node_11_14);
        nodeGraph.addNode(node_11_15);
        nodeGraph.addNode(node_11_16);
        nodeGraph.addNode(node_11_17);
        nodeGraph.addNode(node_11_18);

        nodeGraph.connectNodes(node_0_0, node_0_1);
        nodeGraph.connectNodes(node_0_1, node_0_2);
        nodeGraph.connectNodes(node_0_2, node_0_3);
        nodeGraph.connectNodes(node_0_3, node_0_4);
        nodeGraph.connectNodes(node_0_4, node_0_5);
        nodeGraph.connectNodes(node_0_5, node_0_6);
        nodeGraph.connectNodes(node_0_6, node_0_7);
        nodeGraph.connectNodes(node_0_7, node_0_8);
        nodeGraph.connectNodes(node_0_8, node_0_9);
        nodeGraph.connectNodes(node_0_9, node_0_10);
        nodeGraph.connectNodes(node_0_10, node_0_11);
        nodeGraph.connectNodes(node_0_14, node_0_15);
        nodeGraph.connectNodes(node_0_15, node_0_16);
        nodeGraph.connectNodes(node_0_16, node_0_17);
        nodeGraph.connectNodes(node_0_17, node_0_18);

        nodeGraph.connectNodes(node_1_1, node_1_2);
        nodeGraph.connectNodes(node_1_2, node_1_3);
        nodeGraph.connectNodes(node_1_3, node_1_4);
        nodeGraph.connectNodes(node_1_4, node_1_5);
        nodeGraph.connectNodes(node_1_5, node_1_6);
        nodeGraph.connectNodes(node_1_6, node_1_7);
        nodeGraph.connectNodes(node_1_7, node_1_8);
        nodeGraph.connectNodes(node_1_8, node_1_9);
        nodeGraph.connectNodes(node_1_9, node_1_10);
        nodeGraph.connectNodes(node_1_10, node_1_11);
        nodeGraph.connectNodes(node_1_11, node_1_12);
        nodeGraph.connectNodes(node_1_12, node_1_13);
        nodeGraph.connectNodes(node_1_13, node_1_14);
        nodeGraph.connectNodes(node_1_14, node_1_15);
        nodeGraph.connectNodes(node_1_15, node_1_16);
        nodeGraph.connectNodes(node_1_16, node_1_17);
        nodeGraph.connectNodes(node_1_17, node_1_18);

        nodeGraph.connectNodes(node_2_0, node_2_1);
        nodeGraph.connectNodes(node_2_3, node_2_4);
        nodeGraph.connectNodes(node_2_6, node_2_7);
        nodeGraph.connectNodes(node_2_9, node_2_10);
        nodeGraph.connectNodes(node_2_10, node_2_11);
        nodeGraph.connectNodes(node_2_11, node_2_12);
        nodeGraph.connectNodes(node_2_14, node_2_15);

        nodeGraph.connectNodes(node_3_0, node_3_1);
        nodeGraph.connectNodes(node_3_1, node_3_2);
        nodeGraph.connectNodes(node_3_2, node_3_3);
        nodeGraph.connectNodes(node_3_3, node_3_4);
        nodeGraph.connectNodes(node_3_4, node_3_5);
        nodeGraph.connectNodes(node_3_5, node_3_6);
        nodeGraph.connectNodes(node_3_6, node_3_7);
        nodeGraph.connectNodes(node_3_7, node_3_8);
        nodeGraph.connectNodes(node_3_8, node_3_9);
        nodeGraph.connectNodes(node_3_9, node_3_10);
        nodeGraph.connectNodes(node_3_10, node_3_11);
        nodeGraph.connectNodes(node_3_11, node_3_12);
        nodeGraph.connectNodes(node_3_14, node_3_15);
        nodeGraph.connectNodes(node_3_15, node_3_16);
        nodeGraph.connectNodes(node_3_16, node_3_17);
        nodeGraph.connectNodes(node_3_17, node_3_18);

        nodeGraph.connectNodes(node_4_1, node_4_2);
        nodeGraph.connectNodes(node_4_2, node_4_3);
        nodeGraph.connectNodes(node_4_3, node_4_4);
        nodeGraph.connectNodes(node_4_4, node_4_5);
        nodeGraph.connectNodes(node_4_5, node_4_6);
        nodeGraph.connectNodes(node_4_6, node_4_7);
        nodeGraph.connectNodes(node_4_7, node_4_8);
        nodeGraph.connectNodes(node_4_8, node_4_9);
        nodeGraph.connectNodes(node_4_9, node_4_10);
        nodeGraph.connectNodes(node_4_10, node_4_11);
        nodeGraph.connectNodes(node_4_11, node_4_12);
        nodeGraph.connectNodes(node_4_12, node_4_13);
        nodeGraph.connectNodes(node_4_13, node_4_14);
        nodeGraph.connectNodes(node_4_14, node_4_15);
        nodeGraph.connectNodes(node_4_15, node_4_16);
        nodeGraph.connectNodes(node_4_16, node_4_17);
        nodeGraph.connectNodes(node_4_17, node_4_18);

        nodeGraph.connectNodes(node_5_0, node_5_1);
        nodeGraph.connectNodes(node_5_1, node_5_2);
        nodeGraph.connectNodes(node_5_2, node_5_3);
        nodeGraph.connectNodes(node_5_3, node_5_4);
        nodeGraph.connectNodes(node_5_4, node_5_5);
        nodeGraph.connectNodes(node_5_5, node_5_6);
        nodeGraph.connectNodes(node_5_11, node_5_12);
        nodeGraph.connectNodes(node_5_12, node_5_13);
        nodeGraph.connectNodes(node_5_13, node_5_14);
        nodeGraph.connectNodes(node_5_14, node_5_15);
        nodeGraph.connectNodes(node_5_15, node_5_16);
        nodeGraph.connectNodes(node_5_16, node_5_17);
        nodeGraph.connectNodes(node_5_17, node_5_18);

        nodeGraph.connectNodes(node_6_0, node_6_1);
        nodeGraph.connectNodes(node_6_1, node_6_2);
        nodeGraph.connectNodes(node_6_2, node_6_3);
        nodeGraph.connectNodes(node_6_3, node_6_4);
        nodeGraph.connectNodes(node_6_4, node_6_5);
        nodeGraph.connectNodes(node_6_5, node_6_6);
        nodeGraph.connectNodes(node_6_6, node_6_7);
        nodeGraph.connectNodes(node_6_7, node_6_8);
        nodeGraph.connectNodes(node_6_8, node_6_9);
        nodeGraph.connectNodes(node_6_9, node_6_10);
        nodeGraph.connectNodes(node_6_10, node_6_11);
        nodeGraph.connectNodes(node_6_11, node_6_12);
        nodeGraph.connectNodes(node_6_12, node_6_13);
        nodeGraph.connectNodes(node_6_13, node_6_14);
        nodeGraph.connectNodes(node_6_14, node_6_15);
        nodeGraph.connectNodes(node_6_15, node_6_16);
        nodeGraph.connectNodes(node_6_16, node_6_17);

        nodeGraph.connectNodes(node_7_1, node_7_2);
        nodeGraph.connectNodes(node_7_5, node_7_6);
        nodeGraph.connectNodes(node_7_6, node_7_7);
        nodeGraph.connectNodes(node_7_7, node_7_8);
        nodeGraph.connectNodes(node_7_8, node_7_9);
        nodeGraph.connectNodes(node_7_9, node_7_10);
        nodeGraph.connectNodes(node_7_13, node_7_14);
        nodeGraph.connectNodes(node_7_14, node_7_15);
        nodeGraph.connectNodes(node_7_15, node_7_16);
        nodeGraph.connectNodes(node_7_16, node_7_17);
        nodeGraph.connectNodes(node_7_17, node_7_18);

        nodeGraph.connectNodes(node_8_1, node_8_2);
        nodeGraph.connectNodes(node_8_13, node_8_14);
        nodeGraph.connectNodes(node_8_14, node_8_15);
        nodeGraph.connectNodes(node_8_15, node_8_16);
        nodeGraph.connectNodes(node_8_16, node_8_17);
        nodeGraph.connectNodes(node_8_17, node_8_18);

        nodeGraph.connectNodes(node_9_0, node_9_1);
        nodeGraph.connectNodes(node_9_1, node_9_2);
        nodeGraph.connectNodes(node_9_2, node_9_3);
        nodeGraph.connectNodes(node_9_3, node_9_4);
        nodeGraph.connectNodes(node_9_4, node_9_5);
        nodeGraph.connectNodes(node_9_5, node_9_6);
        nodeGraph.connectNodes(node_9_6, node_9_7);
        nodeGraph.connectNodes(node_9_7, node_9_8);
        nodeGraph.connectNodes(node_9_8, node_9_9);
        nodeGraph.connectNodes(node_9_9, node_9_10);
        nodeGraph.connectNodes(node_9_10, node_9_11);
        nodeGraph.connectNodes(node_9_11, node_9_12);
        nodeGraph.connectNodes(node_9_12, node_9_13);
        nodeGraph.connectNodes(node_9_13, node_9_14);
        nodeGraph.connectNodes(node_9_14, node_9_15);
        nodeGraph.connectNodes(node_9_15, node_9_16);
        nodeGraph.connectNodes(node_9_16, node_9_17);
        nodeGraph.connectNodes(node_9_17, node_9_18);

        nodeGraph.connectNodes(node_10_0, node_10_1);
        nodeGraph.connectNodes(node_10_5, node_10_6);
        nodeGraph.connectNodes(node_10_6, node_10_7);
        nodeGraph.connectNodes(node_10_7, node_10_8);
        nodeGraph.connectNodes(node_10_8, node_10_9);
        nodeGraph.connectNodes(node_10_9, node_10_10);
        nodeGraph.connectNodes(node_10_10, node_10_11);
        nodeGraph.connectNodes(node_10_14, node_10_15);
        nodeGraph.connectNodes(node_10_15, node_10_16);
        nodeGraph.connectNodes(node_10_16, node_10_17);
        nodeGraph.connectNodes(node_10_17, node_10_18);

        nodeGraph.connectNodes(node_11_0, node_11_1);
        nodeGraph.connectNodes(node_11_1, node_11_2);
        nodeGraph.connectNodes(node_11_2, node_11_3);
        nodeGraph.connectNodes(node_11_3, node_11_4);
        nodeGraph.connectNodes(node_11_4, node_11_5);
        nodeGraph.connectNodes(node_11_5, node_11_6);
        nodeGraph.connectNodes(node_11_6, node_11_7);
        nodeGraph.connectNodes(node_11_7, node_11_8);
        nodeGraph.connectNodes(node_11_8, node_11_9);
        nodeGraph.connectNodes(node_11_9, node_11_10);
        nodeGraph.connectNodes(node_11_10, node_11_11);
        nodeGraph.connectNodes(node_11_11, node_11_12);
        nodeGraph.connectNodes(node_11_12, node_11_13);
        nodeGraph.connectNodes(node_11_13, node_11_14);
        nodeGraph.connectNodes(node_11_14, node_11_15);
        nodeGraph.connectNodes(node_11_15, node_11_16);
        nodeGraph.connectNodes(node_11_16, node_11_17);
        nodeGraph.connectNodes(node_11_17, node_11_18);

        nodeGraph.connectNodes(node_0_1, node_1_1);
        nodeGraph.connectNodes(node_0_2, node_1_2);
        nodeGraph.connectNodes(node_0_3, node_1_3);
        nodeGraph.connectNodes(node_0_4, node_1_4);
        nodeGraph.connectNodes(node_0_5, node_1_5);
        nodeGraph.connectNodes(node_0_6, node_1_6);
        nodeGraph.connectNodes(node_0_7, node_1_7);
        nodeGraph.connectNodes(node_0_8, node_1_8);
        nodeGraph.connectNodes(node_0_9, node_1_9);
        nodeGraph.connectNodes(node_0_10, node_1_10);
        nodeGraph.connectNodes(node_0_11, node_1_11);
        nodeGraph.connectNodes(node_0_14, node_1_14);
        nodeGraph.connectNodes(node_0_15, node_1_15);
        nodeGraph.connectNodes(node_0_16, node_1_16);
        nodeGraph.connectNodes(node_0_17, node_1_17);
        nodeGraph.connectNodes(node_0_18, node_1_18);

        nodeGraph.connectNodes(node_1_1, node_2_1);
        nodeGraph.connectNodes(node_1_3, node_2_3);
        nodeGraph.connectNodes(node_1_4, node_2_4);
        nodeGraph.connectNodes(node_1_6, node_2_6);
        nodeGraph.connectNodes(node_1_7, node_2_7);
        nodeGraph.connectNodes(node_1_9, node_2_9);
        nodeGraph.connectNodes(node_1_10, node_2_10);
        nodeGraph.connectNodes(node_1_11, node_2_11);
        nodeGraph.connectNodes(node_1_12, node_2_12);
        nodeGraph.connectNodes(node_1_14, node_2_14);
        nodeGraph.connectNodes(node_1_15, node_2_15);
        nodeGraph.connectNodes(node_1_17, node_2_17);

        nodeGraph.connectNodes(node_2_0, node_3_0);
        nodeGraph.connectNodes(node_2_1, node_3_1);
        nodeGraph.connectNodes(node_2_3, node_3_3);
        nodeGraph.connectNodes(node_2_4, node_3_4);
        nodeGraph.connectNodes(node_2_6, node_3_6);
        nodeGraph.connectNodes(node_2_7, node_3_7);
        nodeGraph.connectNodes(node_2_9, node_3_9);
        nodeGraph.connectNodes(node_2_10, node_3_10);
        nodeGraph.connectNodes(node_2_11, node_3_11);
        nodeGraph.connectNodes(node_2_12, node_3_12);
        nodeGraph.connectNodes(node_2_14, node_3_14);
        nodeGraph.connectNodes(node_2_15, node_3_15);
        nodeGraph.connectNodes(node_2_17, node_3_17);

        nodeGraph.connectNodes(node_3_1, node_4_1);
        nodeGraph.connectNodes(node_3_2, node_4_2);
        nodeGraph.connectNodes(node_3_3, node_4_3);
        nodeGraph.connectNodes(node_3_4, node_4_4);
        nodeGraph.connectNodes(node_3_5, node_4_5);
        nodeGraph.connectNodes(node_3_6, node_4_6);
        nodeGraph.connectNodes(node_3_7, node_4_7);
        nodeGraph.connectNodes(node_3_8, node_4_8);
        nodeGraph.connectNodes(node_3_9, node_4_9);
        nodeGraph.connectNodes(node_3_10, node_4_10);
        nodeGraph.connectNodes(node_3_11, node_4_11);
        nodeGraph.connectNodes(node_3_12, node_4_12);
        nodeGraph.connectNodes(node_3_14, node_4_14);
        nodeGraph.connectNodes(node_3_15, node_4_15);
        nodeGraph.connectNodes(node_3_16, node_4_16);
        nodeGraph.connectNodes(node_3_17, node_4_17);
        nodeGraph.connectNodes(node_3_18, node_4_18);

        nodeGraph.connectNodes(node_4_1, node_5_1);
        nodeGraph.connectNodes(node_4_2, node_5_2);
        nodeGraph.connectNodes(node_4_3, node_5_3);
        nodeGraph.connectNodes(node_4_4, node_5_4);
        nodeGraph.connectNodes(node_4_5, node_5_5);
        nodeGraph.connectNodes(node_4_6, node_5_6);
        nodeGraph.connectNodes(node_4_9, node_5_9);
        nodeGraph.connectNodes(node_4_11, node_5_11);
        nodeGraph.connectNodes(node_4_12, node_5_12);
        nodeGraph.connectNodes(node_4_13, node_5_13);
        nodeGraph.connectNodes(node_4_14, node_5_14);
        nodeGraph.connectNodes(node_4_15, node_5_15);
        nodeGraph.connectNodes(node_4_16, node_5_16);
        nodeGraph.connectNodes(node_4_17, node_5_17);
        nodeGraph.connectNodes(node_4_18, node_5_18);

        nodeGraph.connectNodes(node_5_0, node_6_0);
        nodeGraph.connectNodes(node_5_1, node_6_1);
        nodeGraph.connectNodes(node_5_2, node_6_2);
        nodeGraph.connectNodes(node_5_3, node_6_3);
        nodeGraph.connectNodes(node_5_4, node_6_4);
        nodeGraph.connectNodes(node_5_5, node_6_5);
        nodeGraph.connectNodes(node_5_6, node_6_6);
        nodeGraph.connectNodes(node_5_9, node_6_9);
        nodeGraph.connectNodes(node_5_11, node_6_11);
        nodeGraph.connectNodes(node_5_12, node_6_12);
        nodeGraph.connectNodes(node_5_13, node_6_13);
        nodeGraph.connectNodes(node_5_14, node_6_14);
        nodeGraph.connectNodes(node_5_15, node_6_15);
        nodeGraph.connectNodes(node_5_16, node_6_16);
        nodeGraph.connectNodes(node_5_17, node_6_17);

        nodeGraph.connectNodes(node_6_1, node_7_1);
        nodeGraph.connectNodes(node_6_2, node_7_2);
        nodeGraph.connectNodes(node_6_5, node_7_5);
        nodeGraph.connectNodes(node_6_6, node_7_6);
        nodeGraph.connectNodes(node_6_7, node_7_7);
        nodeGraph.connectNodes(node_6_8, node_7_8);
        nodeGraph.connectNodes(node_6_9, node_7_9);
        nodeGraph.connectNodes(node_6_10, node_7_10);
        nodeGraph.connectNodes(node_6_13, node_7_13);
        nodeGraph.connectNodes(node_6_14, node_7_14);
        nodeGraph.connectNodes(node_6_15, node_7_15);
        nodeGraph.connectNodes(node_6_16, node_7_16);
        nodeGraph.connectNodes(node_6_17, node_7_17);

        nodeGraph.connectNodes(node_7_1, node_8_1);
        nodeGraph.connectNodes(node_7_2, node_8_2);
        nodeGraph.connectNodes(node_7_5, node_8_5);
        nodeGraph.connectNodes(node_7_7, node_8_7);
        nodeGraph.connectNodes(node_7_8, node_8_8);
        nodeGraph.connectNodes(node_7_10, node_8_10);
        nodeGraph.connectNodes(node_7_13, node_8_13);
        nodeGraph.connectNodes(node_7_14, node_8_14);
        nodeGraph.connectNodes(node_7_15, node_8_15);
        nodeGraph.connectNodes(node_7_16, node_8_16);
        nodeGraph.connectNodes(node_7_17, node_8_17);
        nodeGraph.connectNodes(node_7_18, node_8_18);

        nodeGraph.connectNodes(node_8_1, node_9_1);
        nodeGraph.connectNodes(node_8_2, node_9_2);
        nodeGraph.connectNodes(node_8_5, node_9_5);
        nodeGraph.connectNodes(node_8_7, node_9_7);
        nodeGraph.connectNodes(node_8_8, node_9_8);
        nodeGraph.connectNodes(node_8_10, node_9_10);
        nodeGraph.connectNodes(node_8_13, node_9_13);
        nodeGraph.connectNodes(node_8_14, node_9_14);
        nodeGraph.connectNodes(node_8_15, node_9_15);
        nodeGraph.connectNodes(node_8_16, node_9_16);
        nodeGraph.connectNodes(node_8_17, node_9_17);
        nodeGraph.connectNodes(node_8_18, node_9_18);

        nodeGraph.connectNodes(node_9_0, node_10_0);
        nodeGraph.connectNodes(node_9_1, node_10_1);
        nodeGraph.connectNodes(node_9_5, node_10_5);
        nodeGraph.connectNodes(node_9_6, node_10_6);
        nodeGraph.connectNodes(node_9_7, node_10_7);
        nodeGraph.connectNodes(node_9_8, node_10_8);
        nodeGraph.connectNodes(node_9_9, node_10_9);
        nodeGraph.connectNodes(node_9_10, node_10_10);
        nodeGraph.connectNodes(node_9_11, node_10_11);
        nodeGraph.connectNodes(node_9_14, node_10_14);
        nodeGraph.connectNodes(node_9_15, node_10_15);
        nodeGraph.connectNodes(node_9_16, node_10_16);
        nodeGraph.connectNodes(node_9_17, node_10_17);
        nodeGraph.connectNodes(node_9_18, node_10_18);

        nodeGraph.connectNodes(node_10_0, node_11_0);
        nodeGraph.connectNodes(node_10_1, node_11_1);
        nodeGraph.connectNodes(node_10_5, node_11_5);
        nodeGraph.connectNodes(node_10_6, node_11_6);
        nodeGraph.connectNodes(node_10_7, node_11_7);
        nodeGraph.connectNodes(node_10_8, node_11_8);
        nodeGraph.connectNodes(node_10_9, node_11_9);
        nodeGraph.connectNodes(node_10_10, node_11_10);
        nodeGraph.connectNodes(node_10_11, node_11_11);
        nodeGraph.connectNodes(node_10_14, node_11_14);
        nodeGraph.connectNodes(node_10_15, node_11_15);
        nodeGraph.connectNodes(node_10_16, node_11_16);
        nodeGraph.connectNodes(node_10_17, node_11_17);
        nodeGraph.connectNodes(node_10_18, node_11_18);
    }
}
