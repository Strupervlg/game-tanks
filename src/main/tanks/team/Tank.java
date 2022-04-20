package tanks.team;

import org.jetbrains.annotations.NotNull;
import tanks.*;
import tanks.event.*;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    private TimerTask timerTask;
    private Timer timer;

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
        fireDamageCaused();
        if(!isAlive()) {
            timer = new Timer();
            timerTask = new TimerDestroy();
            timer.schedule(timerTask, 200);
        }
    }

    class TimerDestroy extends TimerTask {

        @Override
        public void run() {
            fireObjectDestroyed(Tank.this, null, cell());
            removeCell();
            timer.cancel();
        }
    }


    // ------------------------------- Стрельба ---------------------------------
    private boolean _blocker = false;
    private int _recharge;
    private final int RECHARGE_DURATION = 5;

    public boolean canShoot() {
        return this._recharge == 0;
    }

    public boolean isBlocked() {
        return this._blocker;
    }

    public void shoot() {
        if(cell() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if(canShoot() && isActive() && !isBlocked()) {
            AbstractCell neighborCell = cell().neighbor(this._currentDirection);
            if(neighborCell != null) {
                _blocker = true;
                Bullet bullet = new Bullet(neighborCell, this._currentDirection, new Tank.BulletObserver());
                setRecharge(RECHARGE_DURATION);
            }
        }
    }

    private void setRecharge(int recharge) {
        _recharge = recharge;
    }

    public int getRecharge() {
        return this._recharge;
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
        fireTankActivityChanged();
    }


    // ------------------------------- Текущее направление танка ---------------------------------
    private Direction _currentDirection;

    public Direction getCurrentDirection() {
        return _currentDirection.clone();
    }

    public void changeDirection(@NotNull Direction direction) {
        if(isActive() && !isBlocked()) {
            this._currentDirection = direction;
            fireTankChangedDirection();
        }
    }


    // ------------------------------- Передвижение танка ---------------------------------
    public void move() {
        if(cell() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if(isActive() && !isBlocked()) {
            AbstractCell neighborCell = cell().neighbor(this._currentDirection);

            if(canMoveTo(neighborCell)) {
                AbstractCell oldCell = cell();
                fireTankMoved(oldCell, neighborCell);
                cell().extractUnit();
                neighborCell.putUnit(this);
                reduceRecharge();
            }
        }
    }

    public void skip() {
        if(isActive() && !isBlocked()) {
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

    // ------------------------------- События ---------------------------------
    private ArrayList<TankActionListener> tankListListener = new ArrayList<>();

    public void addTankActionListener(TankActionListener listener) {
        tankListListener.add(listener);
    }

    public void removeTankActionListener(TankActionListener listener) {
        tankListListener.remove(listener);
    }

    private void fireTankMoved(@NotNull AbstractCell oldPosition, @NotNull AbstractCell newPosition) {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            event.setFromCell(oldPosition);
            event.setToCell(newPosition);
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
        _blocker = false;
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankShot(event);
        }
    }

    private void fireBulletChangeCell(@NotNull Bullet bullet, AbstractCell oldPosition, @NotNull AbstractCell newPosition) {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setBullet(bullet);
            event.setFromCell(oldPosition);
            event.setToCell(newPosition);
            listener.bulletChangedCell(event);
        }
    }

    private void fireTankChangedDirection() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankChangedDirection(event);
        }
    }

    private void fireTankActivityChanged() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.tankActivityChanged(event);
        }
    }

    private void fireObjectDestroyed(Tank tank, Bullet bullet, @NotNull AbstractCell oldPosition) {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            if(tank != null) {
                event.setTank(this);
            }
            else {
                event.setBullet(bullet);
            }
            event.setFromCell(oldPosition);
            listener.objectDestroyed(event);
        }
    }

    private void fireDamageCaused() {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setTank(this);
            listener.damageCaused(event);
        }
    }


    // ------------------------------- События ---------------------------------
    private class BulletObserver implements BulletActionListener {
        @Override
        public void bulletChangedCell(@NotNull BulletActionEvent event) {
            fireBulletChangeCell(event.getBullet(), event.getFromCell(), event.getToCell());
        }

        @Override
        public void objectDestroyed(@NotNull BulletActionEvent event) {
            fireObjectDestroyed(null, event.getBullet(), event.getFromCell());
        }
    }


    // ------------------------------- Снаряд ---------------------------------
    public class Bullet extends Unit {

        private final int DAMAGE = 1;

        private Direction _direction;
        private TimerTask timerTask = new TimerFly();
        private Timer timer;

        public Bullet(@NotNull AbstractCell cell, @NotNull Direction direction, @NotNull BulletActionListener listener) {
            if(canFlyTo(cell)) {
                this._isDestroyed = false;
                this._direction = direction;
                this.addBulletActionListener(listener);
                fireBulletChangeCell(null, cell);
                cell.putUnit(this);
                timer = new Timer();
                timer.schedule(timerTask, 100, 250);
            }
            else {
                Unit unit = cell.getUnit();
                if(unit instanceof CanDamaged) {
                    ((CanDamaged) unit).causeDamage(DAMAGE);
                }
                fireTankShot();
                this._isDestroyed = true;
            }
        }

        public Direction getDirection() {
            return _direction;
        }

        private void flyOneStep() {
            AbstractCell neighborCell = cell().neighbor(this.getDirection());
            if (canFlyTo(neighborCell)) {
                AbstractCell oldCell = cell();
                fireBulletChangeCell(oldCell, neighborCell);
                cell().extractUnit();
                neighborCell.putUnit(this);
            } else if (neighborCell == null) {
                destroy();
            } else {
                Unit unit = neighborCell.getUnit();

                if (unit instanceof CanDamaged) {
                    ((CanDamaged) unit).causeDamage(DAMAGE);
                }
                destroy();
            }
        }


        class TimerFly extends TimerTask {

            @Override
            public void run() {
                flyOneStep();
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
            fireObjectDestroyed(cell());
            cell().extractUnit();
            timer.cancel();
            fireTankShot();
            this._isDestroyed = true;
        }

        // ------------------------------- События ---------------------------------
        private ArrayList<BulletActionListener> bulletListListener = new ArrayList<>();

        public void addBulletActionListener(BulletActionListener listener) {
            bulletListListener.add(listener);
        }

        public void removeBulletActionListener(BulletActionListener listener) {
            bulletListListener.remove(listener);
        }

        private void fireBulletChangeCell(AbstractCell oldPosition, @NotNull AbstractCell newPosition) {
            for(BulletActionListener listener: bulletListListener) {
                BulletActionEvent event = new BulletActionEvent(listener);
                event.setBullet(this);
                event.setFromCell(oldPosition);
                event.setToCell(newPosition);
                listener.bulletChangedCell(event);
            }
        }

        private void fireObjectDestroyed(@NotNull AbstractCell oldPosition) {
            for(TankActionListener listener: tankListListener) {
                TankActionEvent event = new TankActionEvent(listener);
                event.setBullet(this);
                event.setFromCell(oldPosition);
                listener.objectDestroyed(event);
            }
        }
    }
}
