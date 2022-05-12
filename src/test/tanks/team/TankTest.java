package tanks.team;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import tanks.*;
import tanks.event.TankActionEvent;
import tanks.event.TankActionListener;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class TankTest {

    private enum EVENT {TANK_MOVED, TANK_SKIPPED_MOVE, TANK_SHOT}

    private List<EVENT> events = new ArrayList<>();
    private List<EVENT> expectedEvents = new ArrayList<>();

    private TimerTask timerTask;
    private Timer timer;

    private class EventsListener implements TankActionListener {

        @Override
        public void tankMoved(@NotNull TankActionEvent event) {
            events.add(EVENT.TANK_MOVED);
        }

        @Override
        public void tankSkippedMove(@NotNull TankActionEvent event) {
            events.add(EVENT.TANK_SKIPPED_MOVE);
        }

        @Override
        public void tankShot(@NotNull TankActionEvent event) {
            events.add(EVENT.TANK_SHOT);
        }

        @Override
        public void bulletChangedCell(@NotNull TankActionEvent event) {

        }

        @Override
        public void tankChangedDirection(@NotNull TankActionEvent event) {

        }

        @Override
        public void tankActivityChanged(@NotNull TankActionEvent event) {

        }

        @Override
        public void damageCaused(@NotNull TankActionEvent event) {

        }

        @Override
        public void objectDestroyed(@NotNull TankActionEvent event) {

        }
    }

    private Tank tank;
    private Field field;

    private static final int FIELD_HEIGHT = 2;
    private static final int FIELD_WIDTH = 3;

    private Map<CellPosition, AbstractCell> buildCells(int width, int height) {
        Map<CellPosition, AbstractCell> cells = new HashMap<>();

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {

                CellPosition position = new CellPosition(y, x);
                AbstractCell cell = (y==1 && x==1)? new Water() : new Ground();

                if(x > 0) {
                    cells.get(new CellPosition(position.row(), position.col()-1))
                            .setNeighbor(Direction.east(), cell);
                }

                if(y > 0) {
                    cells.get(new CellPosition(position.row()-1, position.col()))
                            .setNeighbor(Direction.south(), cell);
                }

                cells.put(position, cell);
            }
        }

        return cells;
    }

    @BeforeEach
    public void setUp() {
        // clean events
        events.clear();
        expectedEvents.clear();

        // create field
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        field = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

        // create tank
        Team team = new Team(new CellPosition(0,1), new CellPosition(0,0),
                2, 1, Direction.north(), field, new DamageObserverTest());
        tank = team.getTank();
        tank.setActive(true);
        tank.addTankActionListener(new EventsListener());
    }

    @Test
    public void test_create_zeroLive() {
        assertThrows(IllegalArgumentException.class, () ->
                new Team(new CellPosition(0,2), new CellPosition(1,0),
                0, 1, Direction.south(), field, new DamageObserverTest()));
    }

    @Test
    public void test_create_negativeLive() {
        assertThrows(IllegalArgumentException.class, () ->
                new Team(new CellPosition(0,2), new CellPosition(1,0),
                        -2, 1, Direction.south(), field, new DamageObserverTest()));
    }

    @Test
    public void test_create_negativeRecharge() {
        assertThrows(IllegalArgumentException.class, () ->
                new Team(new CellPosition(0,2), new CellPosition(1,0),
                        1, -2, Direction.south(), field, new DamageObserverTest()));
    }

    @Test
    public void test_setActiveAndIsActive() {
        tank.setActive(true);

        assertTrue(tank.isActive());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_canMoveTo_emptyCell() {
        AbstractCell cell = field.getCell(new CellPosition(0, 2));

        assertTrue(tank.canMoveTo(cell));
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_canMoveTo_cellWithUnit() {
        AbstractCell cell = field.getCell(new CellPosition(0, 0));

        assertFalse(tank.canMoveTo(cell));
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_canMoveTo_nullCell() {
        assertFalse(tank.canMoveTo(null));
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_emptyCellInDirection() {
        Team team = new Team(new CellPosition(0,2), new CellPosition(1,0),
                1, 1, Direction.south(), field, new DamageObserverTest());

        tank = team.getTank();
        tank.addTankActionListener(new EventsListener());
        tank.setActive(true);
        tank.move();

        expectedEvents.add(EVENT.TANK_MOVED);
        AbstractCell cell = field.getCell(new CellPosition(0, 2));
        AbstractCell nextCell = field.getCell(new CellPosition(1, 2));

        assertEquals(tank, nextCell.getUnit());
        assertEquals(nextCell, tank.cell());
        assertNull(cell.getUnit());
        assertTrue(tank.canShoot());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_move_noCellInDirection() {
        tank.changeDirection(Direction.north());
        tank.move();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_WaterCellInDirection() {
        tank.changeDirection(Direction.south());
        tank.move();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_UnitInCellInDirection() {
        tank.changeDirection(Direction.west());
        tank.move();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_emptyCellInDirectionAndTankNotActive() {
        tank.setActive(false);
        tank.changeDirection(Direction.east());
        tank.move();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));
        AbstractCell nextCell = field.getCell(new CellPosition(0, 2));

        assertEquals(tank, cell.getUnit());
        assertEquals(cell, tank.cell());
        assertNull(nextCell.getUnit());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_emptyCellInDirectionAndTankIsBeingRecharged() {
        tank.changeDirection(Direction.east());
        tank.move();

        expectedEvents.add(EVENT.TANK_MOVED);
        AbstractCell cell = field.getCell(new CellPosition(0, 1));
        AbstractCell nextCell = field.getCell(new CellPosition(0, 2));


        assertEquals(tank, nextCell.getUnit());
        assertEquals(nextCell, tank.cell());
        assertNull(cell.getUnit());
        assertTrue(tank.canShoot());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_move_currentCellIsNull() {
        tank.changeDirection(Direction.east());
        tank.removeCell();

        assertThrows(NullPointerException.class, () -> tank.move());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_skipStep_tankActive() {
        tank.skip();

        expectedEvents.add(EVENT.TANK_SKIPPED_MOVE);
        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertTrue(tank.canShoot());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_skipStep_tankNotActive() {
        tank.setActive(false);
        tank.skip();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertFalse(tank.canShoot());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_causeDamage_DamageIsLessThanLife() {
        tank.causeDamage(1);

        assertTrue(tank.isAlive());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_causeDamage_DamageEqualsLife() {
        tank.causeDamage(2);

        assertFalse(tank.isAlive());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_causeDamage_DamageIsMoreThanLife() {
        tank.causeDamage(3);

        assertFalse(tank.isAlive());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_shoot_TankActive() {
        field.removeTeam(tank.getTeam());
        Team team = new Team(new CellPosition(0,1), new CellPosition(0,0),
                2, 0, Direction.north(), field, new DamageObserverTest());
        tank = team.getTank();
        tank.setActive(true);
        tank.addTankActionListener(new EventsListener());

        tank.changeDirection(Direction.east());
        tank.shoot();

        expectedEvents.add(EVENT.TANK_SHOT);

        timer = new Timer();
        timerTask = new TimerCheckShot();
        timer.schedule(timerTask, 500);
    }

    class TimerCheckShot extends TimerTask {

        @Override
        public void run() {
            assertFalse(tank.canShoot());
            assertEquals(expectedEvents, events);
        }
    }

    @Test
    public void test_shoot_TankNotActive() {
        tank.changeDirection(Direction.east());
        tank.setActive(false);
        tank.shoot();

        assertFalse(tank.canShoot());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_shoot_rechargeIsNotOver() {
        tank.shoot();

        assertFalse(tank.canShoot());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_shoot_currentCellIsNull() {
        tank.changeDirection(Direction.east());
        tank.removeCell();

        assertThrows(NullPointerException.class, () -> tank.shoot());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_changeDirection_TankNotActive() {
        Direction currentDirection = tank.getCurrentDirection();
        tank.setActive(false);

        tank.changeDirection(currentDirection.opposite());

        assertEquals(currentDirection, tank.getCurrentDirection());
    }

    @Test
    public void test_setCell_CellIsWater() {
        AbstractCell cell = new Water();

        assertThrows(IllegalArgumentException.class, () -> tank.setCell(cell));
        assertNull(cell.getUnit());
    }

    @Test
    public void test_move_nextPositionIsEmptyStorageUnit() {
        tank.changeDirection(Direction.east());
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        tank.move();

        expectedEvents.add(EVENT.TANK_MOVED);
        AbstractCell cell = field.getCell(new CellPosition(0, 1));


        assertEquals(tank, bush.getTank());
        assertEquals(bush, tank.getStoreUnit());
        assertNull(cell.getUnit());
        assertNull(tank.cell());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_move_nextPositionIsOccupiedStorageUnit() {
        tank.changeDirection(Direction.east());
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        Team team = new Team(new CellPosition(1,2), new CellPosition(1,0),
                2, 0, Direction.north(), field, new DamageObserverTest());
        Tank tank2 = team.getTank();
        bush.putTank(tank2);

        tank.move();

        AbstractCell cell = field.getCell(new CellPosition(0, 1));

        assertEquals(cell, tank.cell());
        assertEquals(tank, cell.getUnit());
        assertEquals(bush, tank2.getStoreUnit());
        assertEquals(tank2, bush.getTank());
        assertTrue(events.isEmpty());
    }

    @Test
    public void test_move_currentPositionIsStorageUnit() {
        tank.changeDirection(Direction.west());
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        bush.putTank(tank);
        tank.move();

        expectedEvents.add(EVENT.TANK_MOVED);
        AbstractCell nextCell = field.getCell(new CellPosition(0, 1));


        assertEquals(tank, nextCell.getUnit());
        assertEquals(nextCell, tank.cell());
        assertNull(bush.getTank());
        assertNull(tank.getStoreUnit());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_shoot_currentPositionIsStorageUnit() {
        field.removeTeam(tank.getTeam());
        Team team = new Team(new CellPosition(0,1), new CellPosition(0,0),
                2, 0, Direction.north(), field, new DamageObserverTest());
        tank = team.getTank();
        tank.setActive(true);
        tank.addTankActionListener(new EventsListener());
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        bush.putTank(tank);
        tank.changeDirection(Direction.west());
        tank.shoot();

        expectedEvents.add(EVENT.TANK_SHOT);

        timer = new Timer();
        timerTask = new TimerCheckShot();
        timer.schedule(timerTask, 500);
    }

    @Test
    public void test_move_twoBushesInRow() {
        tank.changeDirection(Direction.east());
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        Bush bush2 = new Bush();
        field.getCell(new CellPosition(1, 2)).putUnit(bush2);
        tank.move();
        tank.changeDirection(Direction.south());
        tank.move();

        expectedEvents.add(EVENT.TANK_MOVED);
        expectedEvents.add(EVENT.TANK_MOVED);
        AbstractCell cell = field.getCell(new CellPosition(0, 1));


        assertEquals(tank, bush2.getTank());
        assertEquals(bush2, tank.getStoreUnit());
        assertNull(cell.getUnit());
        assertNull(tank.cell());
        assertEquals(expectedEvents, events);
    }

    @Test
    public void test_setCell_TankInStorageUnit() {
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        bush.putTank(tank);
        AbstractCell cell = new Ground();

        cell.putUnit(tank);


        assertEquals(tank, cell.getUnit());
        assertNull(bush.getTank());
        assertNull(tank.getStoreUnit());
    }

    @Test
    public void test_setStoreUnit_TankInStorageUnit() {
        Bush bush = new Bush();
        field.getCell(new CellPosition(0, 2)).putUnit(bush);
        bush.putTank(tank);
        Bush bush2 = new Bush();
        field.getCell(new CellPosition(1, 2)).putUnit(bush2);

        tank.setStoreUnit(bush2);

        assertEquals(tank, bush2.getTank());
        assertEquals(bush2, tank.getStoreUnit());
        assertNull(bush.getTank());
    }

    @Nested
    class BulletTest {

        private CellPosition checkPosition;

        @BeforeEach
        public void setUp() {
            // clean events
            events.clear();
            expectedEvents.clear();

            // create field
            Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH+2, FIELD_HEIGHT+1);
            field = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

            // create tank
            Team team = new Team(new CellPosition(0,1), new CellPosition(0,0),
                    2, 0, Direction.north(), field, new DamageObserverTest());
            tank = team.getTank();
            tank.setActive(true);
            tank.addTankActionListener(new EventsListener());
        }

        @Test
        public void test_shoot_NeighborCellIsNull() {
            tank.changeDirection(Direction.north());
            tank.shoot();

            assertTrue(tank.canShoot());
            assertTrue(events.isEmpty());
        }

        @Test
        public void test_shoot_NeighborCellIsEmpty() {
            tank.changeDirection(Direction.east());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_NeighborCellIsWater() {
            tank.changeDirection(Direction.south());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_inNextCellIsBase() {
            tank.changeDirection(Direction.west());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            assertFalse(tank.canShoot());
            assertFalse(field.getTeams().get(0).getBase().isAlive());
            assertEquals(expectedEvents, events);
        }

        @Test
        public void test_shoot_inNextCellIsTank() {
            Team team = new Team(new CellPosition(0,2), new CellPosition(1,2),
                    1, 0, Direction.north(), field, new DamageObserverTest());
            tank.changeDirection(Direction.east());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            assertFalse(tank.canShoot());
            assertFalse(field.getTeams().get(1).getTank().isAlive());
            assertEquals(expectedEvents, events);
        }

        class TimerCheckDestroyUnit extends TimerTask {

            @Override
            public void run() {
                assertFalse(tank.canShoot());
                assertNull(field.getCell(checkPosition).getUnit());
                assertEquals(expectedEvents, events);
            }
        }

        @Test
        public void test_shoot_inNextCellIsBrickWall() {
            tank.changeDirection(Direction.east());
            field.getCell(new CellPosition(0, 2)).putUnit(new BrickWall());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new java.util.Timer();
            timerTask = new TimerCheckDestroyUnit();
            checkPosition = new CellPosition(0, 2);
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_throughOneCellBrickWall() {
            tank.changeDirection(Direction.east());
            field.getCell(new CellPosition(0, 3)).putUnit(new BrickWall());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new java.util.Timer();
            timerTask = new TimerCheckDestroyUnit();
            checkPosition = new CellPosition(0, 3);
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_throughOneCellStorageUnit() {
            tank.changeDirection(Direction.east());
            field.getCell(new CellPosition(0, 3)).putUnit(new Bush());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_inNextCellIsStorageUnit() {
            tank.changeDirection(Direction.east());
            field.getCell(new CellPosition(0, 2)).putUnit(new Bush());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_inLastCellIsStorageUnit() {
            field.removeTeam(tank.getTeam());
            Team team = new Team(new CellPosition(0,3), new CellPosition(1,0),
                    1, 0, Direction.east(), field, new DamageObserverTest());
            tank = team.getTank();
            tank.addTankActionListener(new EventsListener());
            tank.setActive(true);
            field.getCell(new CellPosition(0, 4)).putUnit(new Bush());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }

        @Test
        public void test_shoot_twoBushesInRow() {
            tank.changeDirection(Direction.east());
            field.getCell(new CellPosition(0, 3)).putUnit(new Bush());
            field.getCell(new CellPosition(0, 2)).putUnit(new Bush());
            tank.shoot();

            expectedEvents.add(EVENT.TANK_SHOT);

            timer = new Timer();
            timerTask = new TimerCheckShot();
            timer.schedule(timerTask, 500);
        }
    }
}