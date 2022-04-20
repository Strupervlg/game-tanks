package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.AbstractCell;
import tanks.BrickWall;

import java.util.EventObject;

public class BrickWallActionEvent extends EventObject {

    private BrickWall _brickWall;
    private AbstractCell fromCell;

    public void setBrickWall(@NotNull BrickWall brickWall) {
        this._brickWall = brickWall;
    }

    public BrickWall getBrickWall() {
        return this._brickWall;
    }

    public void setFromCell(AbstractCell fromCell) {
        this.fromCell = fromCell;
    }

    public AbstractCell getFromCell() {
        return fromCell;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public BrickWallActionEvent(Object source) {
        super(source);
    }
}
