package tanks;

import java.util.Objects;

public class CellPosition {

    private final int _row;
    private final int _col;

    public CellPosition(int row, int col) {
        this._row = row;
        this._col = col;
    }

    public int row() {
        return this._row;
    }

    public int col() {
        return this._col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellPosition point = (CellPosition) o;
        return this._row == point._row &&
                this._col == point._col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this._row, this._col);
    }

    @Override
    public String toString() {
        return "CellPosition{" +
                "row=" + this._row +
                ", col=" + this._col +
                '}';
    }
}
