package tanks.UI;

import org.jetbrains.annotations.NotNull;
import tanks.BrickWall;
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
    private BaseWidget _baseWidget;

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
            _tankWidget = null;
            _timer.cancel();
            _timer = null;
        }
    }

    class TimerDamagedBase extends TimerTask {

        @Override
        public void run() {
            _baseWidget.setState(State.DESTROYED);
            _baseWidget = null;
            _timer.cancel();
            _timer = null;
        }
    }

    private class GameObserver implements GameActionListener {

        @Override
        public void tankMoved(@NotNull GameActionEvent event) {
            Tank tank = (Tank)event.getUnit();
            TankWidget tankWidget = _widgetFactory.getWidget(tank);
            CellWidget from = _widgetFactory.getWidget(event.getFromCell());
            CellWidget to = _widgetFactory.getWidget(event.getToCell());
            from.removeItem();
            to.addItem(tankWidget);
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
            CellWidget from = _widgetFactory.getWidget(event.getFromCell());
            CellWidget to = _widgetFactory.getWidget(event.getToCell());
            if(from != null) {
                from.removeItem();
            }
            to.addItem(bulletWidget);
            if(from == null) {
                bulletWidget.revalidate();
            }
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
                _baseWidget = baseWidget;
                _timer = new Timer();
                _timerTask = new TimerDamagedBase();
                _timer.schedule(_timerTask, 500);
            }
            else if(event.getUnit() instanceof Tank) {
                Tank tank = (Tank) event.getUnit();
                TankWidget tankWidget = _widgetFactory.getWidget(tank);
                tankWidget.setState(State.DAMAGED);
                _tankWidget = tankWidget;
                _timer = new Timer();
                _timerTask = new TimerDamagedTank();
                _timer.schedule(_timerTask, 200);
            }
        }

        @Override
        public void objectDestroyed(@NotNull GameActionEvent event) {
            if(event.getUnit() instanceof Tank.Bullet) {
                Tank.Bullet bullet = (Tank.Bullet) event.getUnit();
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
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
                CellWidget from = _widgetFactory.getWidget(event.getFromCell());
                from.removeItem();
                _widgetFactory.remove(tank);
            }
        }
    }
}
