package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BarrelOfFuelTest {

    BarrelOfFuel barrelOfFuel;

    @BeforeEach
    void setUp() {
        barrelOfFuel = new BarrelOfFuel(1);
    }

    @Test
    public void test_causeDamage_neighborsIsNull() {
        AbstractCell cell = new Ground();
        barrelOfFuel.setCell(cell);

        barrelOfFuel.causeDamage(0);

        assertNull(barrelOfFuel.cell());
        assertNull(cell.getUnit());
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

        assertNull(barrelOfFuel.cell());
        assertNull(cell.getUnit());
        assertNull(barrelOfFuel2.cell());
        assertNull(cell2.getUnit());
    }

    // бочка взрывает другие предметы
//    @Test
//    public void test_causeDamage_neighborsIsBrickWall() {
//        AbstractCell cell = new Ground();
//        AbstractCell cell2 = new Ground();
//        cell.setNeighbor(Direction.north(), cell2);
//        barrelOfFuel.setCell(cell);
//        BrickWall brickWall = new BrickWall();
//        brickWall.setCell(cell2);
//
//        barrelOfFuel.causeDamage(1);
//
//        assertNull(barrelOfFuel.cell());
//        assertNull(cell.getUnit());
//        assertNull(brickWall.cell());
//        assertNull(cell2.getUnit());
//    }

    @Test
    public void test_causeDamage_neighborsIsBush() {
        AbstractCell cell = new Ground();
        AbstractCell cell2 = new Ground();
        cell.setNeighbor(Direction.north(), cell2);
        barrelOfFuel.setCell(cell);
        Bush bush = new Bush();
        bush.setCell(cell2);

        barrelOfFuel.causeDamage(0);

        assertNull(barrelOfFuel.cell());
        assertNull(cell.getUnit());
        assertEquals(cell2, bush.cell());
        assertEquals(cell2.getUnit(), bush);
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

        assertNull(barrelOfFuel.cell());
        assertNull(cell.getUnit());
        assertNull(barrelOfFuel2.cell());
        assertNull(cell3.getUnit());
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