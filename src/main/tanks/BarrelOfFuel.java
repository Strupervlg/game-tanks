package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.DamageActionEvent;
import tanks.event.DamageActionListener;

import java.util.*;

public class BarrelOfFuel extends Unit implements CanDamaged {

    private Timer timer;
    private Timer timer2;

    private final int _explosionRadius;

    public BarrelOfFuel(int explosionRadius) {
        this._explosionRadius = explosionRadius;
         this._isDestroy = false;
    }

    public int getRadiusExplosion() {
        return this._explosionRadius;
    }

    private final int DAMAGE = 1;


    private void causeDamageNearbyUnits(AbstractCell centerCell, int radius, ArrayList<AbstractCell> previousCells) {
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
            if(previousCells.contains(cell)) {
                continue;
            }

            Unit unit = cell.getUnit();
            if(unit instanceof CanDamaged) {
                timer2 = new Timer();
                timer2.schedule(new TimerCauseDamage(unit), 200);
            }
            previousCells.add(cell);
            causeDamageNearbyUnits(neighbors.get(direction), radius-1, previousCells);
        }
    }

    class TimerCauseDamage extends TimerTask {

        private final Unit unit;

        TimerCauseDamage(Unit unit) {
            this.unit = unit;
        }

        @Override
        public void run() {
            ((CanDamaged) this.unit).causeDamage(DAMAGE);
        }
    }

    private boolean _isDestroy;

    public boolean isDestroy() {
        return this._isDestroy;
    }

    @Override
    public void causeDamage(int damage) {
        if(!isDestroy()) {
            this._isDestroy = true;
            AbstractCell currentCell = cell();
            fireDamageCaused();
            timer = new Timer();
            timer.schedule(new TimerDestroy(), 200);
            causeDamageNearbyUnits(currentCell, getRadiusExplosion(), new ArrayList<AbstractCell>());
        }
    }

    class TimerDestroy extends TimerTask {

        @Override
        public void run() {
            fireObjectDestroyed(cell());
            removeCell();
            timer.cancel();
        }
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Brick wall cell is water");
        }
        return super.setCell(cell);
    }

    // ------------------------------- События ---------------------------------
    private ArrayList<DamageActionListener> barrelOfFuelListListener = new ArrayList<>();

    public void addBarrelOfFuelActionListener(DamageActionListener listener) {
        barrelOfFuelListListener.add(listener);
    }

    public void removeBarrelOfFuelActionListener(DamageActionListener listener) {
        barrelOfFuelListListener.remove(listener);
    }

    private void fireDamageCaused() {
        for(DamageActionListener listener: barrelOfFuelListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            listener.damageCaused(event);
        }
    }

    private void fireObjectDestroyed(@NotNull AbstractCell oldPosition) {
        for(DamageActionListener listener: barrelOfFuelListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            event.setFromCell(oldPosition);
            listener.objectDestroyed(event);
        }
    }
}
