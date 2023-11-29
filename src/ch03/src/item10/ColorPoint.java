package src.ch03.src.item10;

import java.awt.*;

public class ColorPoint extends Point {
    private final Color color;

    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        if (!(o instanceof ColorPoint)) {
            return o.equals(this); // 동일 레벨의 Point상속 클래스가 존재하면, 재귀 호출 때문에 stackoverflow 발생!
        }
        return super.equals(o) && ((ColorPoint) o).color == color;
    }

}