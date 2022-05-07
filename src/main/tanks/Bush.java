package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.team.Tank;

public class Bush extends Unit implements AbilityToStoreUnit {

    @Override
    public AbstractCell getCell() {
        return cell();
    }

    @Override
    public boolean setCell(@NotNull AbstractCell cell) {
        if(cell instanceof Water) {
            throw new IllegalArgumentException("Brick wall cell is water");
        }
        return super.setCell(cell);
    }

    // -------------------------------Хранение Танка ---------------------------------
    private Tank _tank;

    @Override
    public Tank getTank() {
        return this._tank;
    }

    @Override
    public boolean putTank(Tank tank) {
        if(!isEmptyTank() && _tank != tank) {
            throw new IllegalArgumentException("Storage unit is occupied");
        }
        if(!isEmptyTank()) {
            return false;
        }

        _tank = tank;
        _tank.setStoreUnit(this);
        return true;
    }

    @Override
    public Tank extractTank() {
        if(isEmptyTank()) {
            return null;
        }

        Tank tank = _tank;
        _tank = null;
        tank.removeStoreUnit();
        return tank;
    }

    public boolean isEmptyTank() {
        return _tank == null;
    }


    // ------------------------------- Хранение Танка ---------------------------------
    private Tank.Bullet _bullet;

    @Override
    public Tank.Bullet getBullet() {
        return this._bullet;
    }

    @Override
    public boolean putBullet(Tank.Bullet bullet) {
        if(!isEmptyBullet()) {
            return false;
        }

        _bullet = bullet;
        _bullet.setStoreUnit(this);
        return true;
    }

    @Override
    public Tank.Bullet extractBullet() {
        if(isEmptyBullet()) {
            return null;
        }

        Tank.Bullet bullet = _bullet;
        _bullet = null;
        bullet.removeStoreUnit();
        return bullet;
    }

    public boolean isEmptyBullet() {
        return _bullet == null;
    }
}
