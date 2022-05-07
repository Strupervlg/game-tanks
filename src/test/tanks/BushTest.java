package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.team.Tank;
import tanks.team.Team;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BushTest {

    private Bush bush;
    private Tank tank;
    private Field field;

    private static final int FIELD_HEIGHT = 2;
    private static final int FIELD_WIDTH = 3;

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
        bush = new Bush();

        // create field
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        field = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

        // create tank
        Team team = new Team(new CellPosition(0,1), new CellPosition(0,0),
                2, 1, Direction.south(), field, new BaseObserverTest());
        tank = team.getTank();
    }

    @Test
    public void test_putTank_InEmptyStorageUnit() {
        bush.putTank(tank);

        assertEquals(tank, bush.getTank());
        assertEquals(bush, tank.getStoreUnit());
        assertNull(tank.cell());
    }

    @Test
    public void test_putTank_StorageUnitWithUnit() {
        Team team = new Team(new CellPosition(1,1), new CellPosition(1,0),
                2, 1, Direction.north(), field, new BaseObserverTest());
        Tank tank2 = team.getTank();

        bush.putTank(tank);

        assertThrows(IllegalArgumentException.class, () -> bush.putTank(tank2));
        assertEquals(tank, bush.getTank());
        assertEquals(bush, tank.getStoreUnit());
        assertNull(tank.cell());
        assertNull(tank2.getStoreUnit());
        assertNotNull(tank2.cell());
    }

    @Test
    public void test_extractTank_FromStorageUnitWithUnit() {
        bush.putTank(tank);

        assertEquals(tank, bush.extractTank());
        assertNull(tank.getStoreUnit());
        assertNull(bush.getTank());
    }

    @Test
    public void test_extractTank_FromEmptyStorageUnit() {
        assertNull(bush.extractTank());
        assertNull(bush.getTank());
    }

    @Test
    public void test_setCell_CellIsWater() {
        AbstractCell cell = new Water();

        assertThrows(IllegalArgumentException.class, () -> bush.setCell(cell));
        assertNull(cell.getUnit());
    }

    @Test
    public void test_putBullet_tankInStorageUnit() {
        //Помещаем на поле второй танк и поворачиваем его вправо
        Team team = new Team(new CellPosition(1,1), new CellPosition(1,0),
                2, 0, Direction.east(), field, new BaseObserverTest());
        Tank tank2 = team.getTank();
        tank2.setActive(true);
        //Располагаем куст справа от второго танка
        field.getCell(new CellPosition(1, 2)).putUnit(bush);
        //Помещаем первый танк в куст
        bush.putTank(tank);
        //Исходные жизни первого танка
        int inLiveTank = tank.getLive();

        tank2.shoot();

        assertEquals(tank, bush.getTank());
        assertEquals(bush, tank.getStoreUnit());
        assertEquals(inLiveTank, tank.getLive());
    }
}