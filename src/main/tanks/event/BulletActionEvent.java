package tanks.event;

import org.jetbrains.annotations.NotNull;
import tanks.AbilityToStoreUnit;
import tanks.AbstractCell;
import tanks.team.Tank;

import java.util.EventObject;

public class BulletActionEvent extends EventObject {

    private Tank.Bullet _bullet;
    private AbstractCell fromCell;
    private AbstractCell toCell;
    private AbilityToStoreUnit fromStorageUnit;
    private AbilityToStoreUnit toStorageUnit;

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

    public void setBullet(@NotNull Tank.Bullet bullet) {
        this._bullet = bullet;
    }

    public Tank.Bullet getBullet() {
        return this._bullet;
    }

    public void setFromStorageUnit(AbilityToStoreUnit fromStorageUnit) {
        this.fromStorageUnit = fromStorageUnit;
    }

    public AbilityToStoreUnit getFromStorageUnit() {
        return fromStorageUnit;
    }

    public void setToStorageUnit(AbilityToStoreUnit toStorageUnit) {
        this.toStorageUnit = toStorageUnit;
    }

    public AbilityToStoreUnit getToStorageUnit() {
        return toStorageUnit;
    }

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
