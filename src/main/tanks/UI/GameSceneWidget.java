package tanks.UI;

import org.jetbrains.annotations.NotNull;
import tanks.BarrelOfFuel;
import tanks.BrickWall;
import tanks.Bush;
import tanks.Game;
import tanks.event.GameActionEvent;
import tanks.event.GameActionListener;
import tanks.team.Base;
import tanks.team.Tank;

import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class GameSceneWidget extends JPanel {

    private final Game _game;
    private final WidgetFactory _widgetFactory;
    private final InfoAboutActiveTankWidget _infoAboutActiveTankWidget;
    private TimerTask _timerTask;
    private Timer _timer;
    private TankWidget _tankWidget;

    public GameSceneWidget(Game game, WidgetFactory widgetFactory) {
        this._game = game;
        this._widgetFactory = widgetFactory;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        FieldWidget fieldWidget = new FieldWidget(_game.getField(), widgetFactory);
        add(fieldWidget);
        _infoAboutActiveTankWidget = new InfoAboutActiveTankWidget(game.activeTank(), widgetFactory);
        add(_infoAboutActiveTankWidget);
        game.addGameActionListener(new GameObserver());
    }

    class TimerDamagedTank extends TimerTask {

        @Override
        public void run() {
            _tankWidget.setState(State.ALIVE);
            _timer.cancel();
        }
    }

    private class GameObserver implements GameActionListener {

        @Override
        public void tankMoved(@NotNull GameActionEvent event) {
            Tank tank = (Tank)event.getUnit();
            TankWidget tankWidget = _widgetFactory.getWidget(tank);
            if(event.getFromStorageUnit() == null) {
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
            }
            else {
                BushWidget from = _widgetFactory.getWidget((Bush) event.getFromStorageUnit());
                from.removeTankWidget();
            }
            if(event.getToStorageUnit() == null) {
                CellWidget to = _widgetFactory.getWidget(event.getToCell());
                to.addItem(tankWidget);
            }
            else {
                BushWidget to = _widgetFactory.getWidget((Bush) event.getToStorageUnit());
                to.addTankWidget(tankWidget);
            }
        }

        @Override
        public void tankSkippedMove(@NotNull GameActionEvent event) {
            Tank tank = (Tank) event.getUnit();
            TankWidget tankWidget = _widgetFactory.getWidget(tank);
            tankWidget.repaint();
        }

        @Override
        public void tankShot(@NotNull GameActionEvent event) {

        }

        @Override
        public void gameOver(@NotNull GameActionEvent event) {

        }

        @Override
        public void bulletChangedCell(@NotNull GameActionEvent event) {
            Tank.Bullet bullet = (Tank.Bullet) event.getUnit();
            BulletWidget bulletWidget = _widgetFactory.create(bullet);
            if(event.getFromStorageUnit() == null && event.getFromCell() != null) {
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
            }
            else if(event.getFromStorageUnit() != null && event.getFromCell() == null) {
                BushWidget from = _widgetFactory.getWidget((Bush) event.getFromStorageUnit());
                from.removeBulletWidget();
            }
            if(event.getToStorageUnit() == null) {
                CellWidget to = _widgetFactory.getWidget(event.getToCell());
                to.addItem(bulletWidget);
            }
            else {
                BushWidget to = _widgetFactory.getWidget((Bush) event.getToStorageUnit());
                to.addBulletWidget(bulletWidget);
            }
            bulletWidget.revalidate();
            bulletWidget.repaint();
        }

        @Override
        public void tankChangedDirection(@NotNull GameActionEvent event) {
            Tank tank = (Tank) event.getUnit();
            TankWidget tankWidget = _widgetFactory.getWidget(tank);
            tankWidget.repaint();
        }

        @Override
        public void tankActivityChanged(@NotNull GameActionEvent event) {
            Tank tank = (Tank) event.getUnit();
            TankWidget tankWidget = _widgetFactory.getWidget(tank);
            if(tankWidget == null){
                return;
            }
            tankWidget.setActive(tank.isActive());
            if(tank.isActive()) {
                _infoAboutActiveTankWidget.setTank(tank);
            }
        }

        @Override
        public void damageCaused(@NotNull GameActionEvent event) {
            if(event.getUnit() instanceof BrickWall) {
                BrickWall brickWall = (BrickWall) event.getUnit();
                BrickWallWidget brickWallWidget = _widgetFactory.getWidget(brickWall);
                brickWallWidget.setState(State.DAMAGED);
            }
            else if(event.getUnit() instanceof Base) {
                Base base = (Base) event.getUnit();
                BaseWidget baseWidget = _widgetFactory.getWidget(base);
                baseWidget.setState(State.DAMAGED);
            }
            else if(event.getUnit() instanceof Tank) {
                Tank tank = (Tank) event.getUnit();
                TankWidget tankWidget = _widgetFactory.getWidget(tank);
                tankWidget.setState(State.DAMAGED);
                _tankWidget = tankWidget;
                _timer = new Timer();
                _timerTask = new TimerDamagedTank();
                _timer.schedule(_timerTask, 200);
                _infoAboutActiveTankWidget.repaint();
            }
            else if(event.getUnit() instanceof BarrelOfFuel) {
                BarrelOfFuel barrelOfFuel = (BarrelOfFuel) event.getUnit();
                BarrelOfFuelWidget barrelOfFuelWidget = _widgetFactory.getWidget(barrelOfFuel);
                barrelOfFuelWidget.setState(State.DAMAGED);
            }
        }

        @Override
        public void objectDestroyed(@NotNull GameActionEvent event) {
            if(event.getUnit() instanceof Tank.Bullet) {
                Tank.Bullet bullet = (Tank.Bullet) event.getUnit();
                if(event.getFromStorageUnit() == null) {
                    CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                    from.removeItem();
                }
                else {
                    BushWidget from = _widgetFactory.getWidget((Bush) event.getFromStorageUnit());
                    from.removeBulletWidget();
                }
                _widgetFactory.remove(bullet);
            }
            else if(event.getUnit() instanceof BrickWall) {
                BrickWall brickWall = (BrickWall) event.getUnit();
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
                _widgetFactory.remove(brickWall);
            }
            else if(event.getUnit() instanceof Tank) {
                Tank tank = (Tank) event.getUnit();
                if(event.getFromStorageUnit() == null) {
                    CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                    from.removeItem();
                }
                else {
                    BushWidget from = _widgetFactory.getWidget((Bush) event.getFromStorageUnit());
                    from.removeTankWidget();
                }
                _widgetFactory.remove(tank);
            }
            else if(event.getUnit() instanceof BarrelOfFuel) {
                BarrelOfFuel barrelOfFuel = (BarrelOfFuel) event.getUnit();
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
                _widgetFactory.remove(barrelOfFuel);
            }
            else if(event.getUnit() instanceof Base) {
                Base base = (Base) event.getUnit();
                BaseWidget baseWidget = _widgetFactory.getWidget(base);
                baseWidget.setState(State.DESTROYED);
            }
        }
    }
}
