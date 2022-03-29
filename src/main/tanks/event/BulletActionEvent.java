package tanks.event;

import java.util.EventObject;

public class BulletActionEvent extends EventObject {
    /**
     * Constructs a prototypical Event.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public BulletActionEvent(Object source) {
        super(source);
    }
}
