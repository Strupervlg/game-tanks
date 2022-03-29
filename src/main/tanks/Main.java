package tanks;

import tanks.levels.FirstLevel;
import tanks.levels.Level;
import tanks.team.Tank;
import tanks.team.Team;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // write your code here
        System.out.println("Hello 123");
        Level lev = new FirstLevel();
        Field f = lev.buildField();
        Team team1 = f.getTeams().get(0);
        System.out.println(team1.toString());
//        CellPosition c = new CellPosition(1, 2);
//        Map<CellPosition, AbstractCell> y = new HashMap<>();
//
//        Field f = new Field(1, 1, y);
//        Tank t = new Tank(new Team(c, c, f));
    }
}
