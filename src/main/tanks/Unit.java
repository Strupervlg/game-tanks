package tanks;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(_cell, unit._cell);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_cell);
    }
}
