package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.team.Tank;

import static org.junit.jupiter.api.Assertions.*;

class GroundTest {

    Ground cell;

    @BeforeEach
    void setUp() {
        cell = new Ground();
    }


    @Test
    public void test_putUnit_InEmptyCell() {
        BrickWall brickWall = new BrickWall();

        cell.putUnit(brickWall);

        assertEquals(brickWall, cell.getUnit());
        assertEquals(cell, brickWall.cell());
    }

    @Test
    public void test_putUnit_ToCellWithUnit() {
        BrickWall brickWall = new BrickWall();
        BrickWall newBrickWall = new BrickWall();

        cell.putUnit(brickWall);

        assertThrows(IllegalArgumentException.class, () -> cell.putUnit(newBrickWall));
        assertEquals(brickWall, cell.getUnit());
        assertEquals(cell, brickWall.cell());
        assertNull(newBrickWall.cell());
    }

    @Test
    public void test_extractUnit_FromCellWithUnit() {
        BrickWall brickWall = new BrickWall();

        cell.putUnit(brickWall);

        assertEquals(brickWall, cell.extractUnit());
        assertNull(brickWall.cell());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_extractUnit_FromEmptyCell() {
        assertNull(cell.extractUnit());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_setNeighborCell() {
        Ground neighborCell = new Ground();
        Direction direction = Direction.north();

        cell.setNeighbor(direction, neighborCell);

        assertEquals(neighborCell, cell.neighbor(direction));
        assertEquals(cell, neighborCell.neighbor(direction.opposite()));
    }

    @Test
    public void test_setNeighborCell_doubleSided() {
        Ground neighborCell = new Ground();
        Direction direction = Direction.north();

        cell.setNeighbor(direction, neighborCell);
        neighborCell.setNeighbor(direction.opposite(), cell);
        assertEquals(neighborCell, cell.neighbor(direction));
        assertEquals(cell, neighborCell.neighbor(direction.opposite()));
    }

    @Test
    public void test_setNeighborCell_twoTimesInOneDirection() {
        Ground neighborCell = new Ground();
        Ground anotherCell = new Ground();
        Direction direction = Direction.north();

        cell.setNeighbor(direction, neighborCell);
        assertThrows(IllegalArgumentException.class, () -> cell.setNeighbor(direction, anotherCell));
        assertEquals(neighborCell, cell.neighbor(direction));
        assertEquals(cell, neighborCell.neighbor(direction.opposite()));
    }

    @Test
    public void test_setNeighborCell_ThisCell() {
        Direction direction = Direction.north();

        cell.setNeighbor(direction, cell);
        assertNull(cell.neighbor(direction));
    }

    @Test
    public void test_isNeighbor_WhenNeighborCellExists() {
        Ground neighborCell = new Ground();
        Direction direction = Direction.north();

        cell.setNeighbor(direction, neighborCell);
        assertTrue(cell.isNeighbor(neighborCell));
    }

    @Test
    public void test_isNeighbor_WhenNeighborCellNotExists() {
        Ground neighborCell = new Ground();

        assertFalse(cell.isNeighbor(neighborCell));
    }
}