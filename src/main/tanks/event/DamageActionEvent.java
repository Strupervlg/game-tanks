package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.AbstractCell;
import tanks.CanDamaged;

import java.util.EventObject;

public class DamageActionEvent extends EventObject {

    private CanDamaged _canDamagedUnit;
    private AbstractCell fromCell;

    public void setCanDamagedUnit(@NotNull CanDamaged canDamagedUnit) {
        this._canDamagedUnit = canDamagedUnit;
    }

    public CanDamaged getCanDamagedUnit() {
        return this._canDamagedUnit;
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
    public DamageActionEvent(Object source) {
        super(source);
    }
}
