package tanks;

import tanks.team.Tank;

public interface AbilityToStoreUnit {

    AbstractCell getCell();

    Tank getTank();
    boolean putTank(Tank tank);
    Tank extractTank();

    Tank.Bullet getBullet();
    boolean putBullet(Tank.Bullet bullet);
    Tank.Bullet extractBullet();
}
