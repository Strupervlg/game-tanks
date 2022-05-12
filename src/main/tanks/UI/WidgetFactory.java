package tanks.UI;

import org.jetbrains.annotations.NotNull;
import tanks.*;
import tanks.team.Base;
import tanks.team.Tank;
import tanks.team.Team;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class WidgetFactory {

    private final Map<AbstractCell, CellWidget> _cells = new HashMap<>();
    private final Map<Tank, TankWidget> _tanks = new HashMap<>();
    private final Map<Base, BaseWidget> _bases = new HashMap<>();
    private final Map<BrickWall, BrickWallWidget> _brickWalls = new HashMap<>();
    private final Map<Bush, BushWidget> _bushes = new HashMap<>();
    private final Map<BarrelOfFuel, BarrelOfFuelWidget> _barrelsOfFuel = new HashMap<>();
    private final Map<Tank.Bullet, BulletWidget> _bullets = new HashMap<>();
    private final Map<Team, Color> _teamColor = new HashMap<>();

    public CellWidget create(@NotNull AbstractCell cell) {
        if(_cells.containsKey(cell)) return _cells.get(cell);

        CellWidget item;
        if(cell instanceof Water) {
            item = new WaterWidget();
        }
        else {
            item = new GroundWidget();
        }

        Unit unit = cell.getUnit();

        if(unit instanceof Tank) {
            TankWidget tankWidget = create((Tank)unit);
            item.addItem(tankWidget);
        }
        else if(unit instanceof Base) {
            BaseWidget baseWidget = create((Base) unit);
            item.addItem(baseWidget);
        }
        else if(unit instanceof BrickWall) {
            BrickWallWidget brickWallWidget = create((BrickWall) unit);
            item.addItem(brickWallWidget);
        }
        else if(unit instanceof Tank.Bullet) {
            BulletWidget bulletWidget = create((Tank.Bullet) unit);
            item.addItem(bulletWidget);
        }
        else if(unit instanceof BarrelOfFuel) {
            BarrelOfFuelWidget barrelOfFuelWidget = create((BarrelOfFuel) unit);
            item.addItem(barrelOfFuelWidget);
        }
        else if(unit instanceof Bush) {
            BushWidget bushWidget = create((Bush) unit);
            item.addItem(bushWidget);
        }

        _cells.put(cell, item);
        return item;
    }

    public CellWidget getWidget(AbstractCell cell) {
        return _cells.get(cell);
    }

    public TankWidget create(@NotNull Tank tank) {
        if(_tanks.containsKey(tank)) return _tanks.get(tank);

        Color color = null;
        if(!_teamColor.containsKey(tank.getTeam()) && !_teamColor.containsValue(Color.YELLOW)) {
            color = Color.YELLOW;
            _teamColor.put(tank.getTeam(), color);
        }
        else if(_teamColor.containsKey(tank.getTeam())) {
            color = _teamColor.get(tank.getTeam());
        }
        else if(!_teamColor.containsKey(tank.getTeam()) && _teamColor.containsValue(Color.YELLOW)) {
            color = Color.GREEN;
            _teamColor.put(tank.getTeam(), color);
        }

        TankWidget item = new TankWidget(tank, color);
        _tanks.put(tank, item);
        return item;
    }

    public TankWidget getWidget(@NotNull Tank tank) {
        return _tanks.get(tank);
    }

    public void remove(@NotNull Tank tank) {
        _tanks.remove(tank);
    }

    public BaseWidget create(@NotNull Base base) {
        if(_bases.containsKey(base)) return _bases.get(base);

        Color color = null;
        if(!_teamColor.containsKey(base.getTeam()) && !_teamColor.containsValue(Color.YELLOW)) {
            color = Color.YELLOW;
            _teamColor.put(base.getTeam(), color);
        }
        else if(_teamColor.containsKey(base.getTeam())) {
            color = _teamColor.get(base.getTeam());
        }
        else if(!_teamColor.containsKey(base.getTeam()) && _teamColor.containsValue(Color.YELLOW)) {
            color = Color.GREEN;
            _teamColor.put(base.getTeam(), color);
        }

        BaseWidget item = new BaseWidget(color);
        _bases.put(base, item);
        return item;
    }

    public BaseWidget getWidget(@NotNull Base base) {
        return _bases.get(base);
    }

    public BrickWallWidget create(@NotNull BrickWall brickWall) {
        if(_brickWalls.containsKey(brickWall)) return _brickWalls.get(brickWall);

        BrickWallWidget item = new BrickWallWidget();
        _brickWalls.put(brickWall, item);
        return item;
    }

    public BrickWallWidget getWidget(@NotNull BrickWall brickWall) {
        return _brickWalls.get(brickWall);
    }

    public void remove(@NotNull BrickWall brickWall) {
        _brickWalls.remove(brickWall);
    }

    public BushWidget create(@NotNull Bush bush) {
        if(_bushes.containsKey(bush)) return _bushes.get(bush);

        BushWidget item = new BushWidget();
        _bushes.put(bush, item);
        return item;
    }

    public BushWidget getWidget(@NotNull Bush bush) {
        return _bushes.get(bush);
    }

    public BarrelOfFuelWidget create(@NotNull BarrelOfFuel barrelOfFuel) {
        if(_barrelsOfFuel.containsKey(barrelOfFuel)) return _barrelsOfFuel.get(barrelOfFuel);

        BarrelOfFuelWidget item = new BarrelOfFuelWidget();
        _barrelsOfFuel.put(barrelOfFuel, item);
        return item;
    }

    public BarrelOfFuelWidget getWidget(@NotNull BarrelOfFuel barrelOfFuel) {
        return _barrelsOfFuel.get(barrelOfFuel);
    }

    public void remove(@NotNull BarrelOfFuel barrelOfFuel) {
        _barrelsOfFuel.remove(barrelOfFuel);
    }

    public BulletWidget create(@NotNull Tank.Bullet bullet) {
        if(_bullets.containsKey(bullet)) return _bullets.get(bullet);

        BulletWidget item = new BulletWidget(bullet);
        _bullets.put(bullet, item);
        return item;
    }

    public BulletWidget getWidget(@NotNull Tank.Bullet bullet) {
        return _bullets.get(bullet);
    }

    public void remove(@NotNull Tank.Bullet bullet) {
        _bullets.remove(bullet);
    }

    public Color getColorTeam(Team team) {
        return _teamColor.get(team);
    }
}
