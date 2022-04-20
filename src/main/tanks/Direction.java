package tanks;

public class Direction {

    private int _angle;

    public Direction(int angle) {
        // Приводим заданный угол к допустимому диапазону
        angle = angle%360;
        if(angle < 0) {
            angle += 360;
        }

        this._angle = angle;
    }

    public int getAngle() {
        return _angle;
    }

    // ------------------ Возможные направления ---------------------

    public static Direction north()
    { return new Direction(0); }

    public static Direction south()
    { return new Direction(180); }

    public static Direction east()
    { return new Direction(90); }

    public static Direction west()
    { return new Direction(270); }


    // ------------------ Новые направления ---------------------

    @Override
    public Direction clone(){
        return new Direction(this._angle);
    }

    public Direction clockwise() {
        return new Direction(this._angle+90);
    }

    public Direction anticlockwise() {
        return new Direction(this._angle-90);
    }

    public Direction opposite() {
        return new Direction(this._angle+180);
    }

    // ------------------ Сравнить направления ---------------------

    @Override
    public boolean equals(Object other) {

        if(other instanceof Direction) {
            Direction otherDirect = (Direction)other;
            return  _angle == otherDirect._angle;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this._angle;
    }

    public boolean isOpposite(Direction other) {
        return this.opposite().equals(other);
    }

    @Override
    public String toString() {
        return "Direction{" +
                "_angle=" + _angle +
                '}';
    }
}
