# 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라
- 자원에 의존하는 클래스는 테스트하기 어렵다.

- 정적인 유틸리티 (아이템4)
- 싱글턴 (아이템3)

```java
class SpellChecker {
    private static final Lexicon dictionary = ...;
    
    private SpellChecker() {} // 객체 생성 방지
    
    public static boolean isValid(String word) { ... }
    public static List<String> suggestions(String typo) { ... }
}
```

- 위의 소스는 하나의 사전에만 의존.
- 여러 사전을 사용하기 위해 다음과 같이 다른 사전으로 교체 가능하게 할 수 있지만,
- 사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.

## 의존 객체 주입 패턴
SpellChecker 가 여러 자원 인스턴스를 지원 + 클라이언트가 원하는 dictionary를 사용하게 하는 방법
- 인스턴스를 생성할때 생성자에 필요한 자원을 준다.
```java
private SpellChecker(Lexicon dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary);
}
```
장점
- 유연성과 테스트 용이성을 개선
- 다만 소스의 복잡도에 따라, 코드가 어려워짐 -> 그래서 이를 간단하게 하기 위해 나온게 Spring

변형
- 생성자에 자원 팩터리를 넘겨주는 방식
```java 
private SpellChecker(Supplier<Lexicon> dictionary) {
    this.dictionary = Objects.requireNonNull(dictionary.get());
}
```

팩터리? 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체
= 팩터리 메서드 패턴
- ex. `Supplier<T>` : 클라이언트가 명시한 하위타입을 모든 넘길수 있다. (Supplier<? extends Title>)
- ex. `Provider<T>` : 클라이언트가 명시한 하위타입을 모든 넘길수 있다. (Supplier와 비슷하지만, get()을 호출할 때마다 새로운 인스턴스를 생성한다.)


## 요약
- 의존성이 많은 클래스는 싱글턴, 정적 유틸리티 클래스 지양
- 자원을 직접 만들지 않고, 생성자(or 빌더)에 자원을(or 자원을 만들어주는 팩터리를) 넘겨주자
- 이는, 의존성 객체 주입 기법이라 하며, 클래스의 유연성, 재사용성, 테스트 용이성을 개선해
