package tanks.levels;

import tanks.*;
import tanks.team.Team;

import java.util.*;

public class TestLevelWithMultipleTeams extends Level {

    private static final int FIELD_HEIGHT = 4;
    private static final int FIELD_WIDTH = 4;
    private static final List<CellPosition> WATER_POSITIONS = null;

    @Override
    protected int fieldHeight() {
        return FIELD_HEIGHT;
    }

    @Override
    protected int fieldWidth() {
        return FIELD_WIDTH;
    }

    @Override
    protected boolean isWaterCell(CellPosition position) {
        return WATER_POSITIONS.contains(position);
    }

    @Override
    protected Map<CellPosition, AbstractCell> fieldCells() {
        Map<CellPosition, AbstractCell> cells = new HashMap<>();

        for(int y = 0; y < fieldHeight(); ++y) {
            for(int x = 0; x < fieldWidth(); ++x) {

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

    @Override
    protected void placeTeams(Field field) {
        Team team1 = new Team(new CellPosition(0, 2), new CellPosition(0, 0),
                1, 0, Direction.south(), field);
        Team team2 = new Team(new CellPosition(0, 3), new CellPosition(1, 0),
                1, 0, Direction.south(), field);
        Team team3 = new Team(new CellPosition(3, 3), new CellPosition(1, 1),
                1, 0, Direction.north(), field);
        Team team4 = new Team(new CellPosition(3, 2), new CellPosition(0, 1),
                1, 0, Direction.north(), field);
    }

    @Override
    protected void placeEnvironmentItems(Field field) {

    }
}
