# clone 재정의는 주의해서 진행해라.

- Cloneable 인터페이스는 복제해도 되는 클래스임을 명시하는 용도의 믹스인 인터페이스이다.


### clone 메서드는 Cloneable이 아닌 Object에 선언되어 있다.
```java
public interface Cloneable {
    // 아무것도 없다.
}
```
```java
public class Object {
    // ...
    @IntrinsicCandidate
    protected native Object clone() throws CloneNotSupportedException;
    // ...
}

```
- Cloneable을 구현하는 것만으로는 외부 객체에서 clone을 호출할 수 없다.

### Cloneable은 protected 메서드 clone 동작방식을 결정
- Cloneable을 구현한 클래스는 super.clone을 호출할 수 있다. (=필드 하나하나를 복사한 객체를 반환)
- Cloneable을 구현하지 않은 클래스는 super.clone을 호출하면 CloneNotSupportedException을 던진다.

+ [ ! ] 이는 이례적인 인터페이스 사용 방식으로 권장하지 않는다.

### 우리는 완벽한 복제를 기대하지만, 사실 깨지기 쉽고 모순적인 메커니즘이 발생함.
- 생성자를 호출하지 않고 객체를 생성하게 된다.
- clone 메서드의 규약은 허술하다.


