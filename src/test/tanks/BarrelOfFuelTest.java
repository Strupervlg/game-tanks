package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;

class BarrelOfFuelTest {

    BarrelOfFuel barrelOfFuel;

    private TimerTask timerTask;
    private Timer timer;
    private AbstractCell checkCell;
    private Unit checkUnit;
    private AbstractCell checkCell2;

    @BeforeEach
    void setUp() {
        barrelOfFuel = new BarrelOfFuel(1);
    }

    @Test
    public void test_causeDamage_neighborsIsNull() {
        AbstractCell cell = new Ground();
        barrelOfFuel.setCell(cell);

        barrelOfFuel.causeDamage(0);

        timer = new Timer();
        timerTask = new TimerCheckDestroy2();
        checkCell = cell;
        timer.schedule(timerTask, 500);
    }

    class TimerCheckDestroy2 extends TimerTask {

        @Override
        public void run() {
            assertNull(barrelOfFuel.cell());
            assertNull(checkCell.getUnit());
        }
    }

    @Test
    public void test_causeDamage_neighborsIsBarrelOfFuel() {
        AbstractCell cell = new Ground();
        AbstractCell cell2 = new Ground();
        cell.setNeighbor(Direction.north(), cell2);
        barrelOfFuel.setCell(cell);
        BarrelOfFuel barrelOfFuel2 = new BarrelOfFuel(1);
        barrelOfFuel2.setCell(cell2);

        barrelOfFuel.causeDamage(0);

        timer = new Timer();
        timerTask = new TimerCheckDestroy();
        checkCell = cell;
        checkUnit = barrelOfFuel2;
        checkCell2 = cell2;
        timer.schedule(timerTask, 500);
    }

    @Test
    public void test_causeDamage_neighborsIsBrickWall() {
        AbstractCell cell = new Ground();
        AbstractCell cell2 = new Ground();
        cell.setNeighbor(Direction.north(), cell2);
        barrelOfFuel.setCell(cell);
        BrickWall brickWall = new BrickWall();
        brickWall.setCell(cell2);

        barrelOfFuel.causeDamage(1);

        timer = new Timer();
        timerTask = new TimerCheckDestroy();
        checkCell = cell;
        checkUnit = brickWall;
        checkCell2 = cell2;
        timer.schedule(timerTask, 500);
    }

    @Test
    public void test_causeDamage_neighborsIsBush() {
        AbstractCell cell = new Ground();
        AbstractCell cell2 = new Ground();
        cell.setNeighbor(Direction.north(), cell2);
        barrelOfFuel.setCell(cell);
        Bush bush = new Bush();
        bush.setCell(cell2);

        barrelOfFuel.causeDamage(0);

        timer = new Timer();
        timerTask = new TimerCheckDestroy1();
        checkCell = cell;
        checkUnit = bush;
        checkCell2 = cell2;
        timer.schedule(timerTask, 500);
    }

    class TimerCheckDestroy1 extends TimerTask {

        @Override
        public void run() {
            assertNull(barrelOfFuel.cell());
            assertNull(checkCell.getUnit());
            assertEquals(checkCell2, checkUnit.cell());
            assertEquals(checkCell2.getUnit(), checkUnit);
        }
    }

    @Test
    public void test_causeDamage_BarrelOfFuelThroughCell() {
        AbstractCell cell = new Ground();
        AbstractCell cell2 = new Ground();
        AbstractCell cell3 = new Ground();
        cell.setNeighbor(Direction.north(), cell2);
        cell2.setNeighbor(Direction.north(), cell3);
        barrelOfFuel.setCell(cell3);
        BarrelOfFuel barrelOfFuel2 = new BarrelOfFuel(2);
        barrelOfFuel2.setCell(cell);

        barrelOfFuel2.causeDamage(0);

        timer = new Timer();
        timerTask = new TimerCheckDestroy();
        checkCell = cell;
        checkUnit = barrelOfFuel2;
        checkCell2 = cell3;
        timer.schedule(timerTask, 500);
    }

    class TimerCheckDestroy extends TimerTask {

        @Override
        public void run() {
            assertNull(barrelOfFuel.cell());
            assertNull(checkCell.getUnit());
            assertNull(checkUnit.cell());
            assertNull(checkCell2.getUnit());
        }
    }

    @Test
    public void test_causeDamage_UnitIsNotInCell() {
        barrelOfFuel.causeDamage(0);

        assertNull(barrelOfFuel.cell());
    }

    @Test
    public void test_setCell_CellIsWater() {
        AbstractCell cell = new Water();

        assertThrows(IllegalArgumentException.class, () -> barrelOfFuel.setCell(cell));
        assertNull(barrelOfFuel.cell());
        assertNull(cell.getUnit());
    }
}