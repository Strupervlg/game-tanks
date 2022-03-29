package tanks;

import org.jetbrains.annotations.NotNull;

public class BrickWall extends Unit implements CanDamaged {

    public BrickWall() {

    }

    @Override
    public void causeDamage(int damage) {
        removeCell();
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Brick wall cell is water");
        }
        return super.setCell(cell);
    }

    @Override
    public String toString() {
        return "BrickWall{" +
                super.toString() +
                '}';
    }
}
