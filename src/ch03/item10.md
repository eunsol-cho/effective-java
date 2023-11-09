# equals는 일반 규약을 지켜 재정의하라

## 아래의 조건인 경우 재정의 하지 마라
- 각 인스턴스가 본질적으로 고유하다.
  - ex. Thread : 값을 표현하는게 아닌, 동작하는 개체를 표현하는 클래스
  - 이는 Object의 equals만으로 충분
- 인스턴스의 논리적 동치성에 대한 검사가 필요없음
  - ex. Pattern : 인스턴스가 같은 정규표현식을 썻는지 검사할 일이 없음.
- 상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.
  - ex. Set, List, Map : 모두 AbstractCollection에서 재정의한 equals가 적절하다.
- 클래스가 private이거나 package-private이고, equals 메서드를 호출할 일이 없다.
  - 아래와 같이 이를 명시적으로 표현해주는게 좋다.
```java
@Override public boolean equals(Object o) {
    throw new AssertionError(); // 호출 금지!
}
```

## equals를 재정의해야 하는 경우
- 객체 식별성(object identity)이 아닌 논리적 동치성(logical equality)을 확인해야 하는 경우
  - (Integer, String) 값 클래스 -> 재정의시 Map의 키, Set의 원소로 사용가능.
  - (Enum) 값 클래스지만, 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장해야 하는 경우 재정의 하지 않아도 된다. (객체 식별성 = 논리적 동치성)

## equals를 재정의할 때 지켜야 할 일반 규약
- 반사성(reflexivity) : null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true
- *대칭성(symmetry) : null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)가 true면 y.equals(x)도 true
- *추이성(transitivity) : null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고 y.equals(z)도 true면 x.equals(z)도 true
- *일관성(consistency) : null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환
- null-아님 : null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false

## equals를 재정의할 때 지켜야 할 규약을 위반한 경우

### 규약을 위반한 경우, 해당 객체를 사용하는 다른 객체들이 어떻게 반응할지 알 수 없다.
- ex. HashSet, HashMap : equals 규약을 지키지 않은 객체를 넣으면, 정상적으로 동작하지 않는다.

### 구체 클래스를 확장해 새로운 값을 추가하면, equals 규약을 만족시킬 방법이 없다. (feat. 리스코프 치환 원칙 위배)
ex. Point : 2차원 좌표 / ColorPoint : 2차원 좌표 + 색상
```java
public class Point {
    private final int x;
    private final int y;
    
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    @Override public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        Point p = (Point) o;
        return p.x == x && p.y == y;
    }
}
```
```java
public class ColorPoint extends Point {
    private final Color color;
    
    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Point)) {
            return false;
        }
        if (!(o instanceof ColorPoint)) {
            return o.equals(this);
        }
            return super.equals(o) && ((ColorPoint) o).color == color;
    }
}
```
추이성에 위배
```java
ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
Point p2 = new Point(1, 2);
ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);

System.out.println(p1.equals(p2)); // true
System.out.println(p2.equals(p3)); // true
System.out.println(p1.equals(p3)); // false
```
instanceof 대신 getClass 사용
```java
@Override public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass()) {
        return false;
    }
    Point p = (Point) o;
    return p.x == x && p.y == y;
}

```

이는 리스코프 치환 원칙을 위반한다. (ColorPoint는 Point의 하위 타입이지만, Point를 대신 할수 없다.)
- 아래와 같은 코드에서 onUnitCircle 파라미터로 ColorPoint를 넘기는 경우, Set의 contains 메서드는 ColorPoint의 equals를 호출하지 않고, Point의 equals를 호출한다.
- 사실상 getClass 사용한 경우, ColorPoint는 Point의 하위 타입이 아니다.
```java
private static final Set<Point> unitCircle = Set.of(
    new Point( 1,  0), new Point( 0,  1),
    new Point(-1,  0), new Point( 0, -1));

public static boolean onUnitCircle(Point p) {
    return unitCircle.contains(p);
}
```
- 이를 해결하기 위해, ColorPoint의 상속을 제거하고, Point를 ColorPoint의 private 멤버로 만들어주면 된다.
- 이는 상속보다 컴포지션을 사용하라는 아이템18의 내용과 일맥상통한다.
- ex. Timestamp 클래스는 잘못설계된 사례
```java
리스코프의 치환원칙
        
"프로그램에서 어떤 클래스의 객체 대신에 그 서브클래스의 객체를 사용해도, 프로그램의 정확성을 해치지 않아야 한다."

즉, 어떤 클래스를 상속받는 서브클래스를 만들 때, 기존의 상위 클래스의 기능을 변경하지 않고 확장을 해야 한다는 것입니다. 
서브클래스는 상위 클래스의 모든 기능을 포함해야 하며, 이를 사용하는 코드를 변경하지 않고도 상위 클래스의 인스턴스 대신 서브클래스의 인스턴스로 대체될 수 있어야 합니다.

이 원칙은 다음과 같은 이점을 가집니다:

유연성: 상위 클래스 대신 서브클래스를 자유롭게 사용할 수 있으므로, 코드의 재사용성과 유연성이 증가합니다.
유지보수성: 기존 코드를 변경하지 않고도 새로운 기능을 추가하거나 변경할 수 있어, 시스템의 유지보수성이 향상됩니다.
확장성: 시스템이 확장되어도 기존 시스템에 영향을 미치지 않으므로 확장성이 좋습니다.
        
리스코프 치환 원칙을 위반하는 경우, 서브클래스를 사용하는 코드는 예기치 못한 동작을 하게 되거나 오류가 발생할 수 있습니다. 
예를 들어, 서브클래스가 상위 클래스의 메서드를 오버라이드(재정의)하여 원래의 동작을 변경하거나, 
상위 클래스에서는 예외를 발생시키지 않는 메서드가 서브클래스에서 예외를 발생시키는 경우 등이 이에 해당합니다.
```

## equals 메서드 구현 방법 단계
1. == 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.
2. instanceof 연산자로 입력이 올바른 타입인지 확인한다.
3. 입력을 올바른 타입으로 형변환한다.
4. 입력 객체와 자기 자신의 대응되는 '핵심'필드들이 모두 일치하는지 하나씩 검사한다.

- 기본타입 필드는 ==
- 참조타입은 equals
- float은 Float.compare, double은 Double.compare *유의 : 부동소수


- npe방지를 위해 Objects.equals(Object, Object) 사용
- 필드 비교 순서도 성능에 영향을 준다. ?

## equals 메서드 구현시 주의사항
- equals를 재정의할 때는 hashCode도 반드시 재정의해야 한다.
- 너무 복잡하게 해결하려 들지 말자.
- Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자. -> 이는 재정의가 아닌, 다중정의