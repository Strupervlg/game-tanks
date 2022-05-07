package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.*;
import tanks.levels.Level;
import tanks.team.Tank;
import tanks.team.Team;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    private Field _field;

    public Game(@NotNull Level level) {
        this._field = level.buildField(new BrickWallObserver(), new BaseObserver());

        for(var team : _field.getTeams()) {
            team.getTank().addTankActionListener(new TankObserver());
        }
        setActiveTank();
    }

    public Field getField() {
        return _field;
    }

    // ------------------------------- Активный танк ---------------------------------
    private Tank _activeTank;

    public Tank activeTank() {
        return this._activeTank;
    }

    private void setActiveTank() {
        ArrayList<Team> teams = this._field.getTeams();

        Random random = new Random();
        int indexTeam = random.nextInt(teams.size());

        _activeTank = teams.get(indexTeam).getTank();
        _activeTank.setActive(true);
    }

    private void passMoveToNextTank() {
        ArrayList<Team> teams = this._field.getTeams();

        int indexCurrentTank = teams.indexOf(this._activeTank.getTeam());
        int indexNextTank = (indexCurrentTank + 1) % teams.size();
        while(!teams.get(indexNextTank).isAlive() || teams.get(indexNextTank).getTank().isActive()) {
            indexNextTank = (indexNextTank + 1) % teams.size();
        }

        _activeTank.setActive(false);
        _activeTank = teams.get(indexNextTank).getTank();
        _activeTank.setActive(true);
    }


    // ------------------------------- Команда победитель ---------------------------------
    private Team _winner;

    public Team winner() {
        return this._winner;
    }

    private void setWinner(@NotNull Team winnerTeam) {
        this._winner = winnerTeam;
    }

    private boolean determineWinner() {
        ArrayList<Team> teams = this._field.getTeams();

        Team winnerTeam = null;
        for (Team team: teams) {
            if(team.isAlive() && winnerTeam == null) {
                winnerTeam = team;
            }
            else if(team.isAlive() && winnerTeam != null) {
                return false;
            }
        }
        setWinner(winnerTeam);
        _activeTank.setActive(false);
        _activeTank = null;
        return true;
    }

    @Override
    public String toString() {
        return "Game{" +
                "_field=" + _field +
                ", _activeTank=" + _activeTank +
                ", _winner=" + _winner +
                ", gameActionListeners=" + gameActionListeners +
                '}';
    }

    // ------------------------------- События ---------------------------------
    public class TankObserver implements TankActionListener {
        @Override
        public void tankMoved(@NotNull TankActionEvent event) {
            fireTankMoved(event);
            passMoveToNextTank();
        }

        @Override
        public void tankSkippedMove(@NotNull TankActionEvent event) {
            fireTankSkippedMove(event.getTank());
            passMoveToNextTank();
        }

        @Override
        public void tankShot(@NotNull TankActionEvent event) {
            boolean isWinner = determineWinner();
            if(isWinner) {
                fireGameOver();
            }
            else {
                fireTankShot(event.getTank());
                passMoveToNextTank();
            }
        }

        @Override
        public void bulletChangedCell(@NotNull TankActionEvent event) {
            fireBulletChangeCell(event);
        }

        @Override
        public void tankChangedDirection(@NotNull TankActionEvent event) {
            fireTankChangedDirection(event.getTank());
        }

        @Override
        public void tankActivityChanged(@NotNull TankActionEvent event) {
            fireTankActivityChanged(event.getTank());
        }

        @Override
        public void damageCaused(@NotNull TankActionEvent event) {
            fireDamageCaused(event.getTank());
        }

        @Override
        public void objectDestroyed(@NotNull TankActionEvent event) {
            if(event.getTank() != null) {
                fireObjectDestroyed(event.getTank(), event.getFromCell());
            }
            else {
                fireObjectDestroyed(event.getBullet(), event.getFromCell());
            }
        }
    }

    private class BrickWallObserver implements BrickWallActionListener {

        @Override
        public void damageCaused(@NotNull BrickWallActionEvent event) {
            fireDamageCaused(event.getBrickWall());
        }

        @Override
        public void objectDestroyed(@NotNull BrickWallActionEvent event) {
            fireObjectDestroyed(event.getBrickWall(), event.getFromCell());
        }
    }

    private class BaseObserver implements BaseActionListener {

        @Override
        public void damageCaused(@NotNull BaseActionEvent event) {
            fireDamageCaused(event.getBase());
        }

    }

    private ArrayList<GameActionListener> gameActionListeners = new ArrayList<>();

    public void addGameActionListener(@NotNull GameActionListener listener) {
        gameActionListeners.add(listener);
    }

    public void removeGameActionListener(@NotNull GameActionListener listener) {
        gameActionListeners.remove(listener);
    }

    private void fireTankMoved(@NotNull TankActionEvent tankEvent) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(tankEvent.getTank());
            if(tankEvent.getFromStorageUnit() == null) {
                event.setFromCell(tankEvent.getFromCell());
            }
            else {
                event.setFromStorageUnit(tankEvent.getFromStorageUnit());
            }
            if(tankEvent.getToStorageUnit() == null) {
                event.setToCell(tankEvent.getToCell());
            }
            else {
                event.setToStorageUnit(tankEvent.getToStorageUnit());
            }
            listener.tankMoved(event);
        }
    }

    private void fireTankSkippedMove(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(tank);
            listener.tankSkippedMove(event);
        }
    }

    private void fireTankShot(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(tank);
            listener.tankShot(event);
        }
    }

    private void fireGameOver() {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            listener.gameOver(event);
        }
    }

    private void fireBulletChangeCell(@NotNull TankActionEvent bulletEvent) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(bulletEvent.getBullet());
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

    private void fireTankChangedDirection(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(tank);
            listener.tankChangedDirection(event);
        }
    }

    private void fireTankActivityChanged(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(tank);
            listener.tankActivityChanged(event);
        }
    }

    private void fireObjectDestroyed(@NotNull Unit unit, AbstractCell oldPosition) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(unit);
            event.setFromCell(oldPosition);
            listener.objectDestroyed(event);
        }
    }

    public void fireDamageCaused(@NotNull Unit unit) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setUnit(unit);
            listener.damageCaused(event);
        }
    }
}
