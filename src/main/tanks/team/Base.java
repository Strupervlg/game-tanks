package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.*;
import tanks.event.BaseActionEvent;
import tanks.event.BaseActionListener;
import tanks.event.BrickWallActionEvent;
import tanks.event.BrickWallActionListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Base extends Unit implements CanDamaged {

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
        fireDamageCaused();
        this._isAlive = false;
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
    private ArrayList<BaseActionListener> baseListListener = new ArrayList<>();

    public void addBaseActionListener(BaseActionListener listener) {
        baseListListener.add(listener);
    }

    public void removeBaseActionListener(BaseActionListener listener) {
        baseListListener.remove(listener);
    }

    private void fireDamageCaused() {
        for(BaseActionListener listener: baseListListener) {
            BaseActionEvent event = new BaseActionEvent(listener);
            event.setBase(this);
            listener.damageCaused(event);
        }
    }
}
