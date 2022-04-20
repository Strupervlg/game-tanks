package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.AbstractCell;
import tanks.Unit;

import java.util.EventObject;

public class GameActionEvent extends EventObject {

    // ------------------ Tank ------------------
    private Unit _unit;
    private AbstractCell fromCell;
    private AbstractCell toCell;

    public void setFromCell(AbstractCell fromCell) {
        this.fromCell = fromCell;
    }

    public AbstractCell getFromCell() {
        return fromCell;
    }

    public void setToCell(AbstractCell toCell) {
        this.toCell = toCell;
    }

    public AbstractCell getToCell() {
        return toCell;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public GameActionEvent(Object source) {
        super(source);
    }

    public void setUnit(@NotNull Unit unit) {
        this._unit = unit;
    }

    public Unit getUnit() {
        return this._unit;
    }

}
