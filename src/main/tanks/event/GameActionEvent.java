package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.team.Tank;

import java.util.EventObject;

public class GameActionEvent extends EventObject {

    // ------------------ Tank ------------------
    private Tank _tank;

    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public GameActionEvent(Object source) {
        super(source);
    }

    public void setTank(@NotNull Tank tank) {
        this._tank = tank;
    }

    public Tank getTank() {
        return this._tank;
    }

}
