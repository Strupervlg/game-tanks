package tanks;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.event.GameActionEvent;
import tanks.event.GameActionListener;
import tanks.levels.TestLevel;
import tanks.levels.TestLevelWithMultipleTeams;
import tanks.team.Tank;
import tanks.utils.Pare;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    private enum Event {TANK_MOVED, TANK_SKIPPED_MOVE, TANK_SHOT, GAME_OVER, BULLET_CHANGED_CELL}

    private List<Pare<Event, Unit>> events = new ArrayList<>();
    private List<Pare<Event, Unit>> expectedEvents = new ArrayList<>();

    private class EventListener implements GameActionListener {

        @Override
        public void tankMoved(@NotNull GameActionEvent event) {
            events.add(new Pare<>(Event.TANK_MOVED, event.getUnit()));
        }

        @Override
        public void tankSkippedMove(@NotNull GameActionEvent event) {
            events.add(new Pare<>(Event.TANK_SKIPPED_MOVE, event.getUnit()));
        }

        @Override
        public void tankShot(@NotNull GameActionEvent event) {
            events.add(new Pare<>(Event.TANK_SHOT, event.getUnit()));
        }

        @Override
        public void gameOver(@NotNull GameActionEvent event) {
            events.add(new Pare<>(Event.GAME_OVER, null));
        }

        @Override
        public void bulletChangedCell(@NotNull GameActionEvent event) {
            events.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        }

        @Override
        public void tankChangedDirection(@NotNull GameActionEvent event) {

        }

        @Override
        public void tankActivityChanged(@NotNull GameActionEvent event) {

        }

        @Override
        public void damageCaused(@NotNull GameActionEvent event) {

        }

        @Override
        public void objectDestroyed(@NotNull GameActionEvent event) {

        }
    }

    @BeforeEach
    void setUp() {
        events.clear();
        expectedEvents.clear();

        game = new Game(new TestLevel());
        game.addGameActionListener(new EventListener());
    }

    @Test
    public void test_tankMoved_success() {
        Tank tank = game.activeTank();
        expectedEvents.add(new Pare<>(Event.TANK_MOVED, tank));

        game.activeTank().changeDirection(Direction.east());
        game.activeTank().move();

        assertNotEquals(tank, game.activeTank());
        assertFalse(tank.isActive());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_tankMoved_incorrectDirection() {
        Tank tank = game.activeTank();

        game.activeTank().changeDirection(Direction.west());
        game.activeTank().move();

        assertEquals(tank, game.activeTank());
        assertTrue(tank.isActive());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_tankSkippedMove() {
        Tank tank = game.activeTank();
        expectedEvents.add(new Pare<>(Event.TANK_SKIPPED_MOVE, tank));

        game.activeTank().skip();

        assertNotEquals(tank, game.activeTank());
        assertFalse(tank.isActive());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_tankShot() {
        Tank tank = game.activeTank();
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.TANK_SHOT, tank));

        game.activeTank().changeDirection(Direction.east());
        game.activeTank().shoot();

        assertNotEquals(tank, game.activeTank());
        assertFalse(tank.isActive());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_passMoveToNextTank_MultipleTeams() {
        game = new Game(new TestLevelWithMultipleTeams());
        game.addGameActionListener(new EventListener());

        Tank firstTank = game.activeTank();
        game.activeTank().shoot();
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.TANK_SHOT, firstTank));

        Tank secondTank = game.activeTank();
        game.activeTank().shoot();
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.TANK_SHOT, secondTank));

        Tank thirdTank = game.activeTank();
        game.activeTank().move();
        expectedEvents.add(new Pare<>(Event.TANK_MOVED, thirdTank));

        assertNotEquals(thirdTank, game.activeTank());
        assertFalse(thirdTank.isActive());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_winner() {
        Tank tank = game.activeTank();

        game.activeTank().shoot();
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.BULLET_CHANGED_CELL, null));
        expectedEvents.add(new Pare<>(Event.GAME_OVER, null));



        assertNull(game.activeTank());
        assertFalse(tank.isActive());
        assertEquals(tank.getTeam(), game.winner());
        assertEquals(expectedEvents, events);
    }
}