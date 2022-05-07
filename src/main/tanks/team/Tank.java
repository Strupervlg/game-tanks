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
        if(cell() == null && getStoreUnit() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if(canShoot() && isActive() && !isBlocked()) {
            AbstractCell currentCell = cell() == null ? _storageUnit.getCell() : cell();

            AbstractCell neighborCell = currentCell.neighbor(this._currentDirection);
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
        if(cell() == null && getStoreUnit() == null) {
            throw new NullPointerException("Tank cell is null");
        }
        if (!isActive() || isBlocked()) {
            return;
        }

        AbstractCell currentCell = cell() == null ? _storageUnit.getCell() : cell();
        AbstractCell neighborCell = currentCell.neighbor(this._currentDirection);

        if (!canMoveTo(neighborCell)) {
            return;
        }
        fireTankMoved(currentCell, neighborCell);
        if (neighborCell.getUnit() instanceof AbilityToStoreUnit) {
            this.setStoreUnit((AbilityToStoreUnit) neighborCell.getUnit());
        } else {
            this.setCell(neighborCell);
        }

        reduceRecharge();
    }

    public void skip() {
        if(isActive() && !isBlocked()) {
            reduceRecharge();
            fireTankSkippedMove();
        }
    }

    public boolean canMoveTo(AbstractCell cell) {
        return !(cell == null || cell instanceof Water ||
                (cell.getUnit() != null && (!(cell.getUnit() instanceof AbilityToStoreUnit) ||
                        ((AbilityToStoreUnit) cell.getUnit()).getTank() != null)));
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Tank cell is water");
        }
        if(getStoreUnit() != null) {
            removeStoreUnit();
        }
        return super.setCell(cell);
    }


    // ------------------------------- Юнит с хранением танка ---------------------------------
    private AbilityToStoreUnit _storageUnit;

    public AbilityToStoreUnit getStoreUnit() {
        return this._storageUnit;
    }

    public boolean setStoreUnit(@NotNull AbilityToStoreUnit storeUnit) {
        if(cell() != null) {
            removeCell();
        }
        if(getStoreUnit() != null && _storageUnit != storeUnit) {
            removeStoreUnit();
        }

        if(getStoreUnit() != null) {
            return false;
        }

        _storageUnit = storeUnit;
        _storageUnit.putTank(this);
        return true;
    }

    public boolean removeStoreUnit() {
        if(getStoreUnit() == null) {
            return false;
        }

        AbilityToStoreUnit storeUnit = _storageUnit;
        _storageUnit = null;
        storeUnit.extractTank();
        return true;
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
            if(oldPosition.getUnit() != null && oldPosition.getUnit() instanceof AbilityToStoreUnit) {
                event.setFromStorageUnit((AbilityToStoreUnit) oldPosition.getUnit());
            }
            else {
                event.setFromCell(oldPosition);
            }
            if(newPosition.getUnit() != null && newPosition.getUnit() instanceof AbilityToStoreUnit) {
                event.setToStorageUnit((AbilityToStoreUnit) newPosition.getUnit());
            }
            else {
                event.setToCell(newPosition);
            }
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

    private void fireBulletChangeCell(@NotNull BulletActionEvent bulletEvent) {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setBullet(bulletEvent.getBullet());
            if(bulletEvent.getFromStorageUnit() == null) {
                event.setFromCell(bulletEvent.getFromCell());
            }
            else {
                event.setFromStorageUnit(bulletEvent.getFromStorageUnit());
            }
            if(bulletEvent.getToStorageUnit() == null) {
                event.setToCell(bulletEvent.getToCell());
            }
            else {
                event.setToStorageUnit(bulletEvent.getToStorageUnit());
            }
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

    private void fireObjectDestroyed(Bullet bullet, @NotNull AbilityToStoreUnit oldPosition) {
        for(TankActionListener listener: tankListListener) {
            TankActionEvent event = new TankActionEvent(listener);
            event.setBullet(bullet);
            event.setFromStorageUnit(oldPosition);
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
            fireBulletChangeCell(event);
        }

        @Override
        public void objectDestroyed(@NotNull BulletActionEvent event) {
            if(event.getFromCell() == null) {
                fireObjectDestroyed(event.getBullet(), event.getFromStorageUnit());
            }
            else {
                fireObjectDestroyed(null, event.getBullet(), event.getFromCell());
            }
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
                if(cell.getUnit() instanceof AbilityToStoreUnit) {
                    ((AbilityToStoreUnit) cell.getUnit()).putBullet(this);
                }
                else {
                    this.setCell(cell);
                }
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
            AbstractCell currentCell;
            if (cell() == null) {
                currentCell = _storageUnit.getCell();
            }
            else {
                currentCell = cell();
            }

            AbstractCell neighborCell = currentCell.neighbor(this.getDirection());
            if (canFlyTo(neighborCell)) {
                fireBulletChangeCell(currentCell, neighborCell);
                if (neighborCell.getUnit() instanceof AbilityToStoreUnit) {
                    this.setStoreUnit((AbilityToStoreUnit) neighborCell.getUnit());
                } else {
                    this.setCell(neighborCell);
                }
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
            return !(cell == null ||
                    (cell.getUnit() != null &&
                            (cell.getUnit() instanceof CanDamaged || !(cell.getUnit() instanceof AbilityToStoreUnit))));
        }

        private boolean _isDestroyed;

        public boolean isDestroyed() {
            return this._isDestroyed;
        }

        private void destroy() {
            if(cell() == null) {
                fireObjectDestroyed(getStoreUnit());
                getStoreUnit().extractBullet();
            }
            else {
                fireObjectDestroyed(cell());
                cell().extractUnit();
            }
            timer.cancel();
            fireTankShot();
            this._isDestroyed = true;
        }


        // ------------------------------- Юнит с хранением Снаряда ---------------------------------
        private AbilityToStoreUnit _storageUnit;

        public AbilityToStoreUnit getStoreUnit() {
            return this._storageUnit;
        }

        public boolean setStoreUnit(@NotNull AbilityToStoreUnit storeUnit) {
            if(cell() != null) {
                removeCell();
            }
            if(getStoreUnit() != null && _storageUnit != storeUnit) {
                removeStoreUnit();
            }

            if(getStoreUnit() != null) {
                return false;
            }

            _storageUnit = storeUnit;
            _storageUnit.putBullet(this);
            return true;
        }

        public boolean removeStoreUnit() {
            if(getStoreUnit() == null) {
                return false;
            }

            AbilityToStoreUnit storeUnit = _storageUnit;
            _storageUnit = null;
            storeUnit.extractBullet();
            return true;
        }

        @Override
        public boolean setCell(@NotNull AbstractCell cell) {
            if(getStoreUnit() != null) {
                removeStoreUnit();
            }
            return super.setCell(cell);
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
                if(oldPosition != null && oldPosition.getUnit() != null
                        && oldPosition.getUnit() instanceof AbilityToStoreUnit) {
                    event.setFromStorageUnit((AbilityToStoreUnit) oldPosition.getUnit());
                }
                else {
                    event.setFromCell(oldPosition);
                }
                if(newPosition.getUnit() != null && newPosition.getUnit() instanceof AbilityToStoreUnit) {
                    event.setToStorageUnit((AbilityToStoreUnit) newPosition.getUnit());
                }
                else {
                    event.setToCell(newPosition);
                }
                listener.bulletChangedCell(event);
            }
        }

        private void fireObjectDestroyed(@NotNull AbstractCell oldPosition) {
            for(BulletActionListener listener: bulletListListener) {
                BulletActionEvent event = new BulletActionEvent(listener);
                event.setBullet(this);
                event.setFromCell(oldPosition);
                listener.objectDestroyed(event);
            }
        }

        private void fireObjectDestroyed(@NotNull AbilityToStoreUnit oldPosition) {
            for(BulletActionListener listener: bulletListListener) {
                BulletActionEvent event = new BulletActionEvent(listener);
                event.setBullet(this);
                event.setFromStorageUnit(oldPosition);
                listener.objectDestroyed(event);
            }
        }
    }
}
