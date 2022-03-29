package tanks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tanks.team.Base;
import tanks.team.Team;

import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    public void test_create_greaterThan360Degrees() {
        assertEquals(new Direction(90), new Direction(450));
    }

    @Test
    public void test_create_negativeAngle() {
        assertEquals(new Direction(-90), new Direction(270));
    }

    @Test
    public void test_north() {
        assertEquals(Direction.north(), new Direction(0));
    }

    @Test
    public void test_south() {
        assertEquals(Direction.south(), new Direction(180));
    }

    @Test
    public void test_east() {
        assertEquals(Direction.east(), new Direction(270));
    }

    @Test
    public void test_west() {
        assertEquals(Direction.west(), new Direction(90));
    }

    @Test
    public void test_clockwise() {
        assertEquals(Direction.north().clockwise(), Direction.west());
    }

    @Test
    public void test_anticlockwise() {
        assertEquals(Direction.north().anticlockwise(), Direction.east());
    }

    @Test
    public void test_opposite() {
        assertEquals(Direction.north().opposite(), Direction.south());
    }

    @Test
    public void test_clone_doesNotChange() {
        assertEquals(Direction.north().clone(), Direction.north());
    }

    @Test
    public void test_clone_changes() {
        Direction direction = Direction.north();
        Direction cpyDirection = direction.clone();

        direction.clockwise();

        assertEquals(direction, cpyDirection);
    }

    @Test
    public void test_equals_equal() {
        assertTrue(Direction.north().equals(Direction.north()));
    }

    @Test
    public void test_equals_notEqual() {
        assertFalse(Direction.north().equals(Direction.east()));
    }

    @Test
    public void test_equals_otherClass() {
        assertFalse(Direction.north().equals(new CellPosition(0,0)));
    }

    @Test
    public void test_isOpposite_True() {
        assertTrue(Direction.north().isOpposite(Direction.south()));
    }

    @Test
    public void test_isOpposite_False() {
        assertFalse(Direction.north().isOpposite(Direction.west()));
    }
}