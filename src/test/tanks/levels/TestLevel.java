package tanks.levels;

import tanks.*;
import tanks.event.BaseActionListener;
import tanks.event.BrickWallActionListener;
import tanks.team.Team;

import java.util.*;

public class TestLevel extends Level {

    private static final int FIELD_HEIGHT = 4;
    private static final int FIELD_WIDTH = 4;
    private static final List<CellPosition> WATER_POSITIONS = Collections.unmodifiableList(
            Arrays.asList(new CellPosition(1, 0),
                    new CellPosition(2, 3)));

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
                AbstractCell cell = isWaterCell(position)? new Water() : new Ground();

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
    protected void placeTeams(Field field, BaseActionListener baseActionListener) {
        Team team1 = new Team(new CellPosition(0, 2), new CellPosition(0, 1),
                1, 0, Direction.south(), field, baseActionListener);
        Team team2 = new Team(new CellPosition(3, 2), new CellPosition(3, 1),
                1, 0, Direction.north(), field, baseActionListener);
    }

    @Override
    protected void placeEnvironmentItems(Field field, BrickWallActionListener brickWallActionListener) {
        field.getCell(new CellPosition(2, 0)).putUnit(new BrickWall());
        field.getCell(new CellPosition(1, 3)).putUnit(new BrickWall());

    }
}