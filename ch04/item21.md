# 인터페이스는 구현하는 쪽을 생각해 설계하라

## 자바 8의 인터페이스와 디폴트 메서드
- 기존 구현테를 깨뜨리지 않고 인터페이스에 메서드 추가 가능
- 하지만, 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하 기란어려운법

## 디폴트 메서드의 단점
예. `Collection` 인터페이스에 추가된 `removeIf` 메서드
```java
    default boolean removeIf(Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
```
- 최대한 범용적이게 구현하였지만, 기존 구현체와의 호환이 불가
- SynchronizedCollection
  - org.apache.common 버전 : 컬렉션 대신 클라이언트가 제공한 객체로 락을 거는 능력을 추가 제공
  - java.util 버전
- 두가지 버전이 존재.
- 

- 디폴트 메서드는 기존 구현체에 런타임 오류를 일으킬 수 있다.
- 디폴트 메서드는 새로운 메서드를 추가하는데만 사용해야 한다.
- 디폴트 메서드는 `Object`에서 제공하는 `public` 메서드를 재정의할 수 없다.
- 인터페이스에 디폴트 메서드가 많으면 그 인터페이스를 구현한 모든 클래스가 디폴트 메서드를 재정의해야 할 수도 있다.
