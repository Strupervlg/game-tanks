package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.AbstractCell;
import tanks.CanDamaged;
import tanks.Unit;
import tanks.Water;

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
}
