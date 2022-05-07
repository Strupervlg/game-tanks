package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Timer;
import java.util.TimerTask;

import static org.junit.jupiter.api.Assertions.*;

class BrickWallTest {

    BrickWall brickWall;

    private TimerTask timerTask;
    private Timer timer;
    private AbstractCell checkCell;

    @BeforeEach
    void setUp() {
        brickWall = new BrickWall();
    }


    @Test
    public void test_setCell_UnitIsNotInCell() {
        AbstractCell cell = new Ground();

        brickWall.setCell(cell);

        assertEquals(cell, brickWall.cell());
        assertEquals(brickWall, cell.getUnit());
    }

    @Test
    public void test_setCell_UnitInCell() {
        AbstractCell cell = new Ground();
        AbstractCell newCell = new Ground();

        brickWall.setCell(cell);
        brickWall.setCell(newCell);


        assertEquals(newCell, brickWall.cell());
        assertEquals(brickWall, newCell.getUnit());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_setCell_CellIsWater() {
        AbstractCell cell = new Water();

        assertThrows(IllegalArgumentException.class, () -> brickWall.setCell(cell));
        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_removeCell_UnitInCell() {
        AbstractCell cell = new Ground();

        brickWall.setCell(cell);

        assertTrue(brickWall.removeCell());
        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_removeCell_UnitIsNotInCell() {
        assertFalse(brickWall.removeCell());
        assertNull(brickWall.cell());
    }

    @Test
    public void test_causeDamage_UnitInCell() {
        AbstractCell cell = new Ground();

        brickWall.setCell(cell);
        brickWall.causeDamage(0);

        timer = new Timer();
        timerTask = new TimerCheckDestroy();
        checkCell = cell;
        timer.schedule(timerTask, 500);
    }

    class TimerCheckDestroy extends TimerTask {

        @Override
        public void run() {
            assertNull(brickWall.cell());
            assertNull(checkCell.getUnit());
        }
    }

    @Test
    public void test_causeDamage_UnitIsNotInCell() {
        AbstractCell cell = new Ground();

        brickWall.addBrickWallActionListener(new BrickWallObserverTest());

        brickWall.causeDamage(0);

        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }
}