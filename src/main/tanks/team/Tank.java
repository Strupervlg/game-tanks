package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.*;
import tanks.event.BulletActionEvent;
import tanks.event.BulletActionListener;
import tanks.event.TankActionEvent;
import tanks.event.TankActionListener;

import java.util.ArrayList;

public class Tank extends Unit implements CanDamaged {

    Tank(@NotNull Team team, int live, int recharge, @NotNull Direction currentDirection) {
        if(live <= 0) {
            throw new IllegalArgumentException("Life is less than or equal to zero");
        }
        if(recharge < 0) {
            throw new IllegalArgumentException("Negative recharge");
        }
        this._live = live;
        this._recharge = recharge;
        this._currentDirection = currentDirection;
        this._team = team;
    }


    // ------------------------------- Команда ---------------------------------
    private Team _team;

    public Team getTeam() {
        return this._team;
    }

    // ------------------------------- Жизнь танка ---------------------------------
    private int _live;

    public boolean isAlive() {
        return getLive() != 0;
    }

    public int getLive() {
        return this._live;
    }

    private void reduceLive(int countLive) {
        if(countLive > getLive()) {
            countLive = getLive();
        }
        this._live -= countLive;
    }

    @Override
    public void causeDamage(int damage) {
        reduceLive(damage);
    }


    // ------------------------------- Стрельба ---------------------------------
    private int _recharge;
    private final int RECHARGE_DURATION = 5;

    public boolean canShoot() {
        return this._recharge == 0;
    }

    public void shoot() {
        if(cell() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if(canShoot() && isActive()) {
            AbstractCell neighborCell = cell().neighbor(this._currentDirection);
            if(neighborCell != null) {
                Bullet bullet = new Bullet(neighborCell, this._currentDirection, new Tank.BulletObserver());
                setRecharge(RECHARGE_DURATION);

                fireTankShot();
            }
        }
    }

    private void setRecharge(int recharge) {
        _recharge = recharge;
    }

    private void reduceRecharge() {
        if(this._recharge > 0) {
            this._recharge--;
        }
    }


    // ------------------------------- Активность танка ---------------------------------
    private boolean _active;

    public boolean isActive() {
        return this._active;
    }

    public void setActive(boolean state) {
        this._active = state;
    }


    // ------------------------------- Текущее направление танка ---------------------------------
    private Direction _currentDirection;

    public Direction getCurrentDirection() {
        return _currentDirection.clone();
    }

    public void changeDirection(@NotNull Direction direction) {
        if(isActive()) {
            this._currentDirection = direction;
        }
    }


    // ------------------------------- Передвижение танка ---------------------------------
    public void move() {
        if(cell() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if(isActive()) {
            AbstractCell neighborCell = cell().neighbor(this._currentDirection);

            if(canMoveTo(neighborCell)) {
                cell().extractUnit();
                neighborCell.putUnit(this);
                reduceRecharge();

                fireTankMoved();
            }
        }
    }

    public void skip() {
        if(isActive()) {
            reduceRecharge();
            fireTankSkippedMove();
        }
    }

    public boolean canMoveTo(AbstractCell cell) {
        return !(cell == null || cell instanceof Water || cell.getUnit() != null);
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Tank cell is water");
        }
        return super.setCell(cell);
    }

    @Override
    public String toString() {
        return "Tank{" +
                super.toString() +
                ", _live=" + _live +
                ", _recharge=" + _recharge +
                ", _active=" + _active +
                ", _currentDirection=" + _currentDirection +
                ", tankListListener=" + tankListListener +
                '}';
    }

    // ------------------------------- События ---------------------------------
    private ArrayList<TankActionListener> tankListListener = new ArrayList<>();

    public void addTankActionListener(TankActionListener listener) {
        tankListListener.add(listener);
    }

    public void removeTankActionListener(TankActionListener listener) {
        tankListListener.remove(listener);
    }

    private void fireTankMoved() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankMoved(event);
        }
    }

    private void fireTankSkippedMove() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankSkippedMove(event);
        }
    }

    private void fireTankShot() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankShot(event);
        }
    }

    private void fireBulletChangeCell() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.bulletChangedCell(event);
        }
    }


    // ------------------------------- События ---------------------------------
    private class BulletObserver implements BulletActionListener {
        @Override
        public void bulletChangedCell(@NotNull BulletActionEvent event) {
            fireBulletChangeCell();
        }
    }


    // ------------------------------- Снаряд ---------------------------------
    private class Bullet extends Unit {

        private final int DAMAGE = 1;

        public Bullet(@NotNull AbstractCell cell, @NotNull Direction direction, @NotNull BulletActionListener listener) {
            if(canFlyTo(cell)) {
                cell.putUnit(this);
                this._isDestroyed = false;
                this.addBulletActionListener(listener);
                fireBulletChangeCell();
                fly(direction);
            }
            else {
                Unit unit = cell.getUnit();
                if(unit instanceof CanDamaged) {
                    ((CanDamaged) unit).causeDamage(DAMAGE);
                }
                this._isDestroyed = true;
            }
        }

        private void fly(Direction direction) {
            while (!isDestroyed()) {
                AbstractCell neighborCell = cell().neighbor(direction);

                if(canFlyTo(neighborCell)) {
                    cell().extractUnit();
                    neighborCell.putUnit(this);
                    fireBulletChangeCell();
                }
                else if(neighborCell == null) {
                    destroy();
                }
                else {
                    Unit unit = neighborCell.getUnit();

                    if(unit instanceof CanDamaged) {
                        ((CanDamaged) unit).causeDamage(DAMAGE);
                    }
                    destroy();
                }
            }
        }

        public boolean canFlyTo(AbstractCell cell) {
            return cell != null && cell.getUnit() == null;
        }

        private boolean _isDestroyed;

        public boolean isDestroyed() {
            return this._isDestroyed;
        }

        private void destroy() {
            cell().extractUnit();
            this._isDestroyed = true;
        }

        @Override
        public String toString() {
            return "Bullet{" +
                    super.toString() +
                    "DAMAGE=" + DAMAGE +
                    ", _isDestroyed=" + _isDestroyed +
                    '}';
        }

        // ------------------------------- События ---------------------------------
        private ArrayList<BulletActionListener> bulletListListener = new ArrayList<>();

        public void addBulletActionListener(BulletActionListener listener) {
            bulletListListener.add(listener);
        }

        public void removeBulletActionListener(BulletActionListener listener) {
            bulletListListener.remove(listener);
        }

        private void fireBulletChangeCell() {
            for(BulletActionListener listener: bulletListListener) {
                BulletActionEvent event = new BulletActionEvent(listener);
                listener.bulletChangedCell(event);
            }
        }
    }
}
