# equals를 재정의하려거든 hashCode도 재정의하라

- HashSet, HashMap, HashTable, ConcurrentHashMap 등의 해시를 사용하는 컬렉션은 객체의 동등성을 비교할 때 hashCode 메서드를 사용한다.

### Object 명세
- equals가 변경되지 않는다면, hashCode는 언제나 같은 값을 반환해야한다.
- equals가 같다면 hashCode는 같은 값을 반환해야한다.
- equals가 다르더라도 hashCode가 같을 수 있다. 하지만, 달라야지 성능이 좋다.

즉, 논리적으로 같은 객체는 같은 해시코드를 반환해야한다.

### hashCode 재정의 방법
... 복잡하다