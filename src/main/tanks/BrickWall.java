package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.BrickWallActionEvent;
import tanks.event.BrickWallActionListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BrickWall extends Unit implements CanDamaged {

    private TimerTask timerTask = new TimerDestroy();
    private Timer timer;

    public BrickWall() {

    }

    @Override
    public void causeDamage(int damage) {
        fireDamageCaused();
        timer = new Timer();
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
    private ArrayList<BrickWallActionListener> brickWallListListener = new ArrayList<>();

    public void addBrickWallActionListener(BrickWallActionListener listener) {
        brickWallListListener.add(listener);
    }

    public void removeBrickWallActionListener(BrickWallActionListener listener) {
        brickWallListListener.remove(listener);
    }

    private void fireDamageCaused() {
        for(BrickWallActionListener listener: brickWallListListener) {
            BrickWallActionEvent event = new BrickWallActionEvent(listener);
            event.setBrickWall(this);
            listener.damageCaused(event);
        }
    }

    private void fireObjectDestroyed(@NotNull AbstractCell oldPosition) {
        for(BrickWallActionListener listener: brickWallListListener) {
            BrickWallActionEvent event = new BrickWallActionEvent(listener);
            event.setBrickWall(this);
            event.setFromCell(oldPosition);
            listener.objectDestroyed(event);
        }
    }
}
