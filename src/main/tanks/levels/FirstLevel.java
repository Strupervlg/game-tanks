package tanks.levels;

import tanks.*;
import tanks.team.Team;

import java.util.*;

public class FirstLevel extends Level {

    private static final int FIELD_HEIGHT = 9;
    private static final int FIELD_WIDTH = 9;
    private static final List<CellPosition> WATER_POSITIONS = Collections.unmodifiableList(
            Arrays.asList(new CellPosition(4, 0),
                    new CellPosition(4, 8)));


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
    protected void placeTeams(Field field) {
        Team team1 = new Team(new CellPosition(1, 4), new CellPosition(0, 4),
                3, 0, Direction.south(), field);
        Team team2 = new Team(new CellPosition(7, 4), new CellPosition(8, 4),
                3, 0, Direction.north(), field);
    }

    @Override
    protected void placeEnvironmentItems(Field field) {
        field.getCell(new CellPosition(2, 4)).putUnit(new BrickWall());
        field.getCell(new CellPosition(3, 6)).putUnit(new BrickWall());
        field.getCell(new CellPosition(6, 2)).putUnit(new BrickWall());
        field.getCell(new CellPosition(6, 4)).putUnit(new BrickWall());

    }

}
