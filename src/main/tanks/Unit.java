package tanks;

import org.jetbrains.annotations.NotNull;

public abstract class Unit {

    private AbstractCell _cell;

    public AbstractCell cell() {
        return this._cell;
    }

    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell() != null && _cell != cell) {
            removeCell();
        }

        if(cell() != null) {
            return false;
        }

        _cell = cell;
        _cell.putUnit(this);
        return true;
    }

    public boolean removeCell() {
        if(cell() == null) {
            return false;
        }

        AbstractCell cell = _cell;
        _cell = null;
        cell.extractUnit();
        return true;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "_cell=" + _cell +
                '}';
    }
}
