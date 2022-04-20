package tanks.levels;

import tanks.AbstractCell;
import tanks.CellPosition;
import tanks.Field;
import tanks.event.BaseActionEvent;
import tanks.event.BaseActionListener;
import tanks.event.BrickWallActionListener;
import tanks.event.TankActionListener;

import java.util.Map;

public abstract class Level {

    public Field buildField(BrickWallActionListener brickWallActionListener, BaseActionListener baseActionListener) {

        Field field = new Field(fieldWidth(), fieldHeight(), fieldCells());

        placeEnvironmentItems(field, brickWallActionListener);
        placeTeams(field, baseActionListener);

        return field;
    }

    protected abstract int fieldHeight();

    protected abstract int fieldWidth();

    protected abstract boolean isWaterCell(CellPosition position);

    protected abstract Map<CellPosition, AbstractCell> fieldCells();

    protected abstract void placeTeams(Field field, BaseActionListener baseActionListener);

    protected abstract void placeEnvironmentItems(Field field, BrickWallActionListener brickWallActionListener);


}
