package tanks;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCell {

    // ---------------------- Соседи -----------------------
    private final Map<Direction, AbstractCell> _neighbors = new HashMap<>();

    public AbstractCell neighbor(Direction direction) {
        if(_neighbors.containsKey(direction)) {
            return _neighbors.get(direction);
        }

        return null;
    }

    public boolean isNeighbor(AbstractCell otherCell) {
        return _neighbors.containsValue(otherCell);
    }

    public void setNeighbor(@NotNull Direction direction, @NotNull AbstractCell neighbor) {
        if(_neighbors.containsKey(direction) && _neighbors.containsValue(neighbor) || neighbor == this) return;
        if(_neighbors.containsKey(direction)) throw new IllegalArgumentException("There is already a cell in this direction");
        _neighbors.put(direction, neighbor);
        if(neighbor.neighbor(direction.opposite()) == null) {
            neighbor.setNeighbor(direction.opposite(), this);
        }
    }


    // ------------------------------- Владение юнитом ---------------------------------
    private Unit _unit = null;

    public Unit getUnit() {
        return this._unit;
    }

    public boolean putUnit(@NotNull Unit unit) {
        if(!isEmpty() && _unit != unit) {
            throw new IllegalArgumentException("Cell is occupied");
        }
        if(!isEmpty()) {
            return false;
        }

        _unit = unit;
        _unit.setCell(this);
        return true;
    }

    public Unit extractUnit() {
        if(isEmpty()) {
            return null;
        }

        Unit unit = _unit;
        _unit = null;
        unit.removeCell();
        return unit;
    }

    public boolean isEmpty() {
        return _unit == null;
    }
}
