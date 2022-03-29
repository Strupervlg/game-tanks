package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.team.Team;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    private Field field;

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
    }

    @Test
    public void test_create_rectangularField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH-2, FIELD_HEIGHT);

        //Проверка
        Field newField = new Field(FIELD_WIDTH-2, FIELD_HEIGHT, cells);
    }

    @Test
    public void test_create_numberOfCellsDoesNotMatchWidthOfField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH-2, FIELD_HEIGHT);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(FIELD_WIDTH, FIELD_HEIGHT, cells));

    }

    @Test
    public void test_create_numberOfCellsDoesNotMatchHeightOfField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT-2);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(FIELD_WIDTH, FIELD_HEIGHT, cells));
    }

    @Test
    public void test_create_cellInMiddleOfFieldIsMissing() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        cells.remove(new CellPosition(2, 2));

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(FIELD_WIDTH, FIELD_HEIGHT, cells));
    }

    @Test
    public void test_create_withNegativeWidth() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(-1, 1, cells));
    }

    @Test
    public void test_create_withZeroWidth() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(0, 1, cells));
    }

    @Test
    public void test_create_withNegativeHeight() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(1, -1, cells));
    }

    @Test
    public void test_create_withZeroHeight() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);

        //Проверка
        assertThrows(IllegalArgumentException.class, () -> new Field(1, 0, cells));
    }

    @Test
    public void test_setTeam_IsNoTeamInField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), otherField);

        //Действие
        field.setTeam(team);

        //Проверка
        assertEquals(field.getTeams().get(0), team);
        assertEquals(field, team.getField());
    }

    @Test
    public void test_setTeam_IsTeamInField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team1 = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), field);

        Team team2 = new Team(new CellPosition(1,0), new CellPosition(1,1),
                1, 0, Direction.north(), otherField);

        //Действие
        field.setTeam(team2);

        //Проверка
        assertEquals(field.getTeams().get(0), team1);
        assertEquals(field, team1.getField());
        assertEquals(field.getTeams().get(1), team2);
        assertEquals(field, team2.getField());
    }

    @Test
    public void test_removeTeam_IsOneTeamInField() {
        //Входные данные
        Team team = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), field);

        //Действие
        field.removeTeam(team);

        //Проверка
        assertFalse(field.getTeams().contains(team));
        assertNull(team.getField());
    }

    @Test
    public void test_removeTeam_IsTwoTeamInField() {
        //Входные данные
        Team team1 = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), field);
        Team team2 = new Team(new CellPosition(1,0), new CellPosition(1,1),
                1, 0, Direction.north(), field);

        //Действие
        field.removeTeam(team1);

        //Проверка
        assertFalse(field.getTeams().contains(team1));
        assertNull(team1.getField());
    }

    @Test
    public void test_removeTeam_IsNoTeamInField() {
        //Входные данные
        Map<CellPosition, AbstractCell> cells = buildCells(FIELD_WIDTH, FIELD_HEIGHT);
        Field otherField = new Field(FIELD_WIDTH, FIELD_HEIGHT, cells);
        Team team1 = new Team(new CellPosition(0,0), new CellPosition(0,1),
                1, 0, Direction.north(), otherField);

        //Проверка
        assertFalse(field.removeTeam(team1));
        assertFalse(field.getTeams().contains(team1));
        assertEquals(otherField, team1.getField());
    }

    @Test
    public void test_getCell_CorrectPosition() {
        //Входные данные
        CellPosition position = new CellPosition(0,0);
        AbstractCell expCell = field.getCell(position);

        //Проверка
        assertEquals(expCell, field.getCell(position));
    }

    @Test
    public void test_getCell_IncorrectPosition() {
        //Входные данные
        CellPosition position = new CellPosition(10,10);

        //Проверка
        assertNull(field.getCell(position));
    }

    @Test
    public void test_getPosition_CorrectCell() {
        //Входные данные
        CellPosition position = new CellPosition(0,0);
        AbstractCell cell = field.getCell(position);

        //Проверка
        assertEquals(position, field.getPosition(cell));
    }

    @Test
    public void test_getPosition_IncorrectCell() {
        //Входные данные
        AbstractCell cell = new Ground();

        //Проверка
        assertNull(field.getPosition(cell));
    }

}