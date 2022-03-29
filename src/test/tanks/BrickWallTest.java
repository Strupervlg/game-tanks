package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrickWallTest {

    BrickWall brickWall;

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

        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_causeDamage_UnitIsNotInCell() {
        AbstractCell cell = new Ground();

        brickWall.causeDamage(0);

        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }
}