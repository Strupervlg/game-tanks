package tanks.team;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.*;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TeamTest {

    Team team;
    Field field;

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
        field = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

        team = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), field);
    }


    @Test
    public void test_create_tankCellDoesNotExist() {
        //Входные данные
        CellPosition tankPosition = new CellPosition(10,10);
        CellPosition basePosition = new CellPosition(1,1);

        //Проверка
        assertThrows(NullPointerException.class, () -> new Team(tankPosition, basePosition,
                1, 0, Direction.north(), field));
    }

    @Test
    public void test_create_baseCellDoesNotExist() {
        //Входные данные
        CellPosition tankPosition = new CellPosition(1,1);
        CellPosition basePosition = new CellPosition(10,10);

        //Проверка
        assertThrows(NullPointerException.class, () -> new Team(tankPosition, basePosition,
                1, 0, Direction.north(), field));
    }

    @Test
    public void test_replaceField_IsNoFieldInTeam() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        team.removeField();

        //Проверка
        assertFalse(team.replaceField(otherField));
        assertFalse(otherField.getTeams().contains(team));
        assertNull(team.getField());
    }

    @Test
    public void test_replaceField_IsTeamInField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);

        //Проверка
        assertTrue(team.replaceField(otherField));
        assertFalse(field.getTeams().contains(team));
        assertEquals(otherField, team.getField());
        assertEquals(new CellPosition(0,0), otherField.getPosition(team.getTank().cell()));
        assertEquals(new CellPosition(0,1), otherField.getPosition(team.getBase().cell()));
    }

    @Test
    public void test_replaceField_sameCellsInTheUnitsOfTeams() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team2 = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), otherField);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> team2.replaceField(field));
    }

    @Test
    public void test_replaceField_IsNoNewCellForTank() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH+1, FIELD_HEIGHT+1);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team2 = new Team(new CellPosition(9,9), new CellPosition(0,1),
                1, 0, Direction.north(), otherField);

        //Проверка
        assertThrows(NullPointerException.class, () -> team2.replaceField(field));
    }

    @Test
    public void test_replaceField_IsNoNewCellForBase() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH+1, FIELD_HEIGHT+1);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team2 = new Team(new CellPosition(1,1), new CellPosition(9,9),
                1, 0, Direction.north(), otherField);

        //Проверка
        assertThrows(NullPointerException.class, () -> team2.replaceField(field));
    }

    @Test
    public void test_removeField_IsFieldInTeam() {
        assertTrue(team.removeField());
        assertNull(team.getField());
        assertFalse(field.getTeams().contains(team));
    }

    @Test
    public void test_removeField_IsNoFieldInTeam() {
        //Входные данные
        team.removeField();

        assertFalse(team.removeField());
        assertNull(team.getField());
        assertFalse(field.getTeams().contains(team));
    }

    @Test
    public void test_isAlive_BaseIsDead() {
        //Входные данные
        team.getBase().causeDamage(1);

        assertFalse(team.isAlive());
        assertTrue(team.getTank().isAlive());
        assertFalse(team.getBase().isAlive());
    }

    @Test
    public void test_isAlive_TankIsDead() {
        //Входные данные
        team.getTank().causeDamage(3);

        assertFalse(team.isAlive());
        assertFalse(team.getTank().isAlive());
        assertTrue(team.getBase().isAlive());
    }

    @Test
    public void test_isAlive_TeamIsAlive() {
        assertTrue(team.isAlive());
        assertTrue(team.getTank().isAlive());
        assertTrue(team.getBase().isAlive());
    }

    @Test
    public void test_isAlive_TeamIsDead() {
        //Входные данные
        team.getTank().causeDamage(3);
        team.getBase().causeDamage(1);

        assertFalse(team.isAlive());
        assertFalse(team.getTank().isAlive());
        assertFalse(team.getBase().isAlive());
    }
}