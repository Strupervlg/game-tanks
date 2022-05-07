package tanks;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class BarrelOfFuel extends Unit implements CanDamaged {


    private final int _explosionRadius;

    public BarrelOfFuel(int explosionRadius) {
        this._explosionRadius = explosionRadius;
    }

    public int getRadiusExplosion() {
        return this._explosionRadius;
    }

    private final int DAMAGE = 1;

    private void causeDamageNearbyUnits(AbstractCell centerCell, int radius, AbstractCell previousCenter) {
        if(radius == 0) {
            return;
        }

        if(centerCell == null) {
            return;
        }

        Map<Direction, AbstractCell> neighbors = centerCell.neighbors();
        Set<Direction> directions = neighbors.keySet();
        if(directions.size() == 0) {
            return;
        }

        for (Direction direction : directions ) {
            AbstractCell cell = neighbors.get(direction);

            if(cell == previousCenter) {
                return;
            }

            Unit unit = cell.getUnit();
            if(unit instanceof CanDamaged) {
                ((CanDamaged) unit).causeDamage(DAMAGE);
            }

            causeDamageNearbyUnits(neighbors.get(direction), radius-1, centerCell);
        }
    }

    @Override
    public void causeDamage(int damage) {
        AbstractCell currentCell = cell();
        removeCell();
        causeDamageNearbyUnits(currentCell, getRadiusExplosion(), null);
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Brick wall cell is water");
        }
        return super.setCell(cell);
    }
}
