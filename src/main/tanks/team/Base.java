package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.*;
import tanks.event.DamageActionEvent;
import tanks.event.DamageActionListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Base extends Unit implements CanDamaged {

    private Timer timer;

    Base(Team team) {
        this._isAlive = true;
        this._team = team;
    }


    // ------------------------------- Команда ---------------------------------
    private Team _team;

    public Team getTeam() {
        return this._team;
    }


    // ------------------------------- Жизнь базы ---------------------------------
    private boolean _isAlive;

    public boolean isAlive() {
        return this._isAlive;
    }

    @Override
    public void causeDamage(int damage) {
        this._isAlive = false;
        fireDamageCaused();
        timer = new Timer();
        timer.schedule(new TimerDestroy(), 200);
    }

    class TimerDestroy extends TimerTask {

        @Override
        public void run() {
            fireObjectDestroyed();
            timer.cancel();
        }
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Base cell is water");
        }
        return super.setCell(cell);
    }

    @Override
    public String toString() {
        return "Base{" +
                super.toString() +
                ", _isAlive=" + _isAlive +
                '}';
    }

    // ------------------------------- События ---------------------------------
    private ArrayList<DamageActionListener> baseListListener = new ArrayList<>();

    public void addBaseActionListener(DamageActionListener listener) {
        baseListListener.add(listener);
    }

    public void removeBaseActionListener(DamageActionListener listener) {
        baseListListener.remove(listener);
    }

    private void fireDamageCaused() {
        for(DamageActionListener listener: baseListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            listener.damageCaused(event);
        }
    }

    private void fireObjectDestroyed() {
        for(DamageActionListener listener: baseListListener) {
            DamageActionEvent event = new DamageActionEvent(listener);
            event.setCanDamagedUnit(this);
            listener.objectDestroyed(event);
        }
    }
}
