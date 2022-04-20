package tanks;

import org.jetbrains.annotations.NotNull;
import tanks.team.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Field {

    private int _width;
    private int _height;


    public Field(int width, int height, @NotNull Map<CellPosition, AbstractCell> cells) {
        if(width <= 0) {
            throw new IllegalArgumentException("Width is less than or equal to zero");
        }
        if(height <= 0) {
            throw new IllegalArgumentException("Height is less than or equal to zero");
        }
        Set<CellPosition> positions = cells.keySet();
        for(int i = 0; i < height; ++i) {
            for(int j = 0; j < width; ++j) {
                if(!positions.contains(new CellPosition(i, j))) {
                    throw new IllegalArgumentException("Number of cells does not match the size of the field");
                }
            }
        }

        this._width = width;
        this._height = height;
        this._cells.putAll(cells);
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

    // ------------------------------- Клетки ---------------------------------
    private Map<CellPosition, AbstractCell> _cells = new HashMap<>();

    public AbstractCell getCell(@NotNull CellPosition position) {
        return _cells.get(position);
    }

    public CellPosition getPosition(@NotNull AbstractCell cell) {
        for (Entry entry : _cells.entrySet()) {
            if (entry.getValue().equals(cell)) {
                return (CellPosition) entry.getKey();
            }
        }
        return null;
    }

    // ------------------------------- Команды ---------------------------------
    private ArrayList<Team> _teams = new ArrayList();

    public ArrayList<Team> getTeams() {
        return (ArrayList<Team>) this._teams.clone();
    }

    public boolean isTeamExist(Team team) {
        return _teams.contains(team);
    }

    public boolean setTeam(Team team) {
        if(!isTeamExist(team)) {
            this._teams.add(team);
            team.replaceField(this);
            return true;
        }
        return false;
    }

    public boolean removeTeam(Team team) {
        if(!isTeamExist(team)) {
            return false;
        }

        int indexTeam = _teams.indexOf(team);
        Team removedTeam = _teams.get(indexTeam);
        _teams.remove(team);
        removedTeam.removeField();
        return true;
    }

    @Override
    public String toString() {
        return "Field{" +
                "_width=" + _width +
                ", _height=" + _height +
                ", _cells=" + _cells +
                ", _teams=" + _teams +
                '}';
    }
}
