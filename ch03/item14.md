# Comparable을 구현할지 고려하라

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```
- Objectd의 equals와 동일
- Comparable를 구현 = 인스턴스에 순서가 있다.
- Comparable인터페이스를 활용하는 수많은 제네릭 알고리즘과 컬렉션의 힘을 누릴 수 있다.
- 대부분의 자바 플랫폼 라이브러리의 모든 값 클래스와 열거 타입이 Comparable을 구현했다.
- ex. TreeSet, TreeMap, Collections.sort

### compareTo 메서드의 일반 규약
- 이 객체 < 주어진 객체 : 음의 정수 반환
- 이 객체 == 주어진 객체 : 0 반환
- 이 객체 > 주어진 객체 : 양의 정수 반환
- 이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.


### Comparable 구현 클래스 확장을 원하면, 컴포지션을 사용하라.
### Comparable은 equals와 일관되어야 한다.
- 예로 BigDecimal 클래스가 있다.
- 1.0 != 1.00 이지만, compareTo는 0을 반환한다.
- HashSet은 두개의 값으로, TreeSet은 하나의 값으로 인식

### compareTo의 작성요령
- 타입을 인수로 받는 제네릭 인터페이스
- 인수 타입은 컴파일타임에 정해짐 = 입력인수의 타입을 확인하거나 형변환 할 필요 없다.
- 인수의 타입이 잘못되면 컴파일 오류
- null이 들어오면 npe
- compareTo 메서드에서 필드의 값을 비교할 때 <, > 연산자를 사용하면 안된다.
- 기본 타입 클래스들은 박싱된 타입 클래스들의 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교 생성자 메서드를 사용하자
- 핵심적인 필드부터 비교하자.
