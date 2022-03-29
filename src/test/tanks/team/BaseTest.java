package tanks.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BaseTest {

    Base base;

    private static final int FIELD_HEIGHT = 9;
    private static final int FIELD_WIDTH = 9;

    private Map<CellPosition, AbstractCell> buildCells(int width, int height) {
        Map<CellPosition, AbstractCell> cells = new HashMap<>();

        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {

                CellPosition position = new CellPosition(y, x);
                AbstractCell cell = new Ground();

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
    void setUp() {
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field field = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

        Team team = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), field);
        base = team.getBase();
    }

    @Test
    public void test_setCell_UnitIsNotInCell() {
        AbstractCell cell = new Ground();
        base.removeCell();

        base.setCell(cell);

        assertEquals(cell, base.cell());
        assertEquals(base, cell.getUnit());
    }

    @Test
    public void test_setCell_UnitInCell() {
        AbstractCell cell = new Ground();
        AbstractCell newCell = new Ground();

        base.setCell(cell);
        base.setCell(newCell);


        assertEquals(newCell, base.cell());
        assertEquals(base, newCell.getUnit());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_setCell_CellIsWater() {
        AbstractCell cell = new Water();

        assertThrows(IllegalArgumentException.class, () -> base.setCell(cell));
        assertNull(cell.getUnit());
    }

    @Test
    public void test_removeCell_UnitInCell() {
        AbstractCell cell = new Ground();

        base.setCell(cell);

        assertTrue(base.removeCell());
        assertNull(base.cell());
        assertNull(cell.getUnit());
    }

    @Test
    public void test_removeCell_UnitIsNotInCell() {
        base.removeCell();

        assertFalse(base.removeCell());
        assertNull(base.cell());
    }

    @Test
    public void test_causeDamage_baseIsAlive() {
        base.causeDamage(0);

        assertFalse(base.isAlive());
    }

    @Test
    public void test_causeDamage_UnitIsNotInCell() {
        base.causeDamage(0);
        base.causeDamage(0);
        assertFalse(base.isAlive());

    }
}