package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.event.GameActionEvent;
import tanks.event.GameActionListener;
import tanks.event.TankActionEvent;
import tanks.event.TankActionListener;
import tanks.levels.FirstLevel;
import tanks.levels.Level;
import tanks.team.Tank;
import tanks.team.Team;

import java.util.ArrayList;
import java.util.Random;

public class Game {

    private Field _field;

    public Game(@NotNull Level level) {
        this._field = level.buildField();

        for(var team : _field.getTeams()) {
            team.getTank().addTankActionListener(new TankObserver());
        }
        setActiveTank();
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
    private class TankObserver implements TankActionListener {
        @Override
        public void tankMoved(@NotNull TankActionEvent event) {
            fireTankMoved(event.getTank());
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
            fireBulletChangeCell();
        }
    }

    private ArrayList<GameActionListener> gameActionListeners = new ArrayList<>();

    public void addGameActionListener(@NotNull GameActionListener listener) {
        gameActionListeners.add(listener);
    }

    public void removeGameActionListener(@NotNull GameActionListener listener) {
        gameActionListeners.remove(listener);
    }

    private void fireTankMoved(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setTank(tank);
            listener.tankMoved(event);
        }
    }

    private void fireTankSkippedMove(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setTank(tank);
            listener.tankSkippedMove(event);
        }
    }

    private void fireTankShot(@NotNull Tank tank) {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            event.setTank(tank);
            listener.tankShot(event);
        }
    }

    private void fireGameOver() {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            listener.gameOver(event);
        }
    }

    private void fireBulletChangeCell() {
        for(GameActionListener listener: gameActionListeners) {
            GameActionEvent event = new GameActionEvent(listener);
            listener.bulletChangedCell(event);
        }
    }
}
