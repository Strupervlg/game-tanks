package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.team.Base;

import java.util.EventObject;

public class BaseActionEvent extends EventObject {

    private Base _base;

    public void setBase(@NotNull Base base) {
        this._base = base;
    }

    public Base getBase() {
        return this._base;
    }

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public BaseActionEvent(Object source) {
        super(source);
    }
}
