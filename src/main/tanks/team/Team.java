package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.*;

public class Team {

    public Team(@NotNull CellPosition tankPosition, @NotNull CellPosition basePosition,
                int liveTank, int rechargeTank, @NotNull Direction currentDirectionTank, @NotNull Field field) {
        _field = field;
        field.setTeam(this);
        Tank tank = new Tank(this, liveTank, rechargeTank, currentDirectionTank);
        AbstractCell tankCell = field.getCell(tankPosition);

        tankCell.putUnit(tank);
        _tank = tank;

        Base base = new Base(this);
        AbstractCell baseCell = field.getCell(basePosition);

        baseCell.putUnit(base);
        _base = base;
    }

    private Field _field;

    public boolean replaceField(@NotNull Field field) {
        CellPosition tankPosition = null;
        CellPosition basePosition = null;
        if(getField() != null && _field != field) {
            tankPosition = _field.getPosition(_tank.cell());
            basePosition = _field.getPosition(_base.cell());
            removeField();
        }

        if(getField() != null) {
            return false;
        }

        if(tankPosition != null && basePosition != null) {
            AbstractCell tankCell = field.getCell(tankPosition);

            tankCell.putUnit(_tank);

            AbstractCell baseCell = field.getCell(basePosition);

            baseCell.putUnit(_base);
            _field = field;
            _field.setTeam(this);
            return true;
        }
        return false;
    }

    public boolean removeField() {
        if(getField() == null) {
            return false;
        }

        Field field = _field;
        _field = null;
        _tank.removeCell();
        _base.removeCell();
        field.removeTeam(this);
        return true;
    }

    public Field getField() {
        return this._field;
    }

    private Tank _tank;

    public Tank getTank() {
        return this._tank;
    }

    private Base _base;

    public Base getBase() {
        return this._base;
    }

    public boolean isAlive() {
        return getBase().isAlive() && getTank().isAlive();
    }

    @Override
    public String toString() {
        return "Team{" +
                ", _tank=" + _tank +
                ", _base=" + _base +
                '}';
    }
}
