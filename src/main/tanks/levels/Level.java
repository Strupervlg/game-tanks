package tanks.levels;

import tanks.AbstractCell;
import tanks.CellPosition;
import tanks.Field;

import java.util.Map;

public abstract class Level {

    public Field buildField() {

        Field field = new Field(fieldWidth(), fieldHeight(), fieldCells());

        placeEnvironmentItems(field);
        placeTeams(field);

        return field;
    }

    protected abstract int fieldHeight();

    protected abstract int fieldWidth();

    protected abstract boolean isWaterCell(CellPosition position);

    protected abstract Map<CellPosition, AbstractCell> fieldCells();

    protected abstract void placeTeams(Field field);

    protected abstract void placeEnvironmentItems(Field field);


}
