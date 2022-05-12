package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.DamageActionEvent;
import tanks.event.DamageActionListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BrickWall extends Unit implements CanDamaged {

    private TimerTask timerTask = new TimerDestroy();
    private Timer timer;

    public BrickWall() {

    }
    private boolean _isDestroy;

    public boolean isDestroy() {
        return this._isDestroy;
    }

    @Override
    public void causeDamage(int damage) {
        if(isDestroy()) {
            return;
        }
        _isDestroy = true;
        fireDamageCaused();
        timer = new Timer();
        timerTask = new TimerDestroy();
        timer.schedule(timerTask, 200);
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

    @Override
    public String toString() {
        return "BrickWall{" +
                super.toString() +
                '}';
    }

    // ------------------------------- События ---------------------------------
    private ArrayList<DamageActionListener> brickWallListListener = new ArrayList<>();

    public void addBrickWallActionListener(DamageActionListener listener) {
        brickWallListListener.add(listener);
    }

    public void removeBrickWallActionListener(DamageActionListener listener) {
        brickWallListListener.remove(listener);
    }

    private void fireDamageCaused() {
        for(DamageActionListener listener: brickWallListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            listener.damageCaused(event);
        }
    }

    private void fireObjectDestroyed(@NotNull AbstractCell oldPosition) {
        for(DamageActionListener listener: brickWallListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            event.setFromCell(oldPosition);
            listener.objectDestroyed(event);
        }
    }
}
