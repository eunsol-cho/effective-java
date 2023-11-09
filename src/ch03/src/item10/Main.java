package src.ch03.src.item10;

import java.awt.*;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        추이성_위반_테스트();
        리스코프_원칙_테스트();
    }

    private static void 추이성_위반_테스트() {
        ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

        System.out.println(p1.equals(p2)); // true
        System.out.println(p2.equals(p3)); // true
        System.out.println(p1.equals(p3)); // false
    }

    private static void 리스코프_원칙_테스트() {
        ColorPoint point = new ColorPoint(1,  0, Color.BLUE);
        boolean result = onUnitCircle(point);
        System.out.println("result = " + result);
    }

    private static final Set<Point> unitCircle = Set.of(
            new Point( 1,  0), new Point( 0,  1),
            new Point(-1,  0), new Point( 0, -1));

    public static boolean onUnitCircle(Point p) {
        return unitCircle.contains(p);
    }


}
