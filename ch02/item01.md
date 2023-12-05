# 생성자 대신 정적 팩터리 메서드를 고려하라

일반적으로 클래스의 인스턴스를 얻는 public 생성자 방식 이외 ``정적 팩터리 메서드(static factory method)`` 방식이 있다.

```java
public static Boolean valueOf(boolean b) {
    return (b ? TRUE : FALSE);
}
```
## 정적 팩터리 메서드의 장점

### 1. 이름을 가질 수 있다

> - 생성자 : BigInteger(int, int, Random)
> - 정적 팩터리 메서드 : BigInteger.probablePrime(int bitLength, Random rnd)

메서드명을 통해 반환될 객체의 특성을 쉽게 묘사 할 수 있다.

생성자는 모두 같은 시그니처를 가진다. 매개변수의 순서로 여러 생성자 생성이 가능하지만, 뭐가 뭔지 파악하는게 더 어렵다.

### 2. 호출될 때 마다 인스턴스를 새로 생성하지 않아도 된다.

``immutable(불변) class`` 생성을 통해, 인스턴스를 미리 만들거나 캐싱하여 재활용 할 수 있다.

언제 어느 인스턴스가 살아있게 할지 통제하는것을 통해 ``sigleton``, ``noninstantiable(인스턴스 불가)``로 만들 수 있다.
이는 인스턴스가 단 하나임<b>(a == b 일 때만, a.equals(b))</b>을 보장하며, Flyweight pattern의 근간이 된다.

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

반환할 객체 타입을 선택할 수 있는 <b>유연성</b>을 제공한다.

이는 구현 클래스를 제공하지 않는것을 통해 API를 작게 유지 할 수 있다.

자바 컬렉션 프레임워크는 ``Collections의 정적 팩터리 메서드``를 통해, 45개의 유틸리티 구현체(핵심 인터페이스에 수정불가나 동기화 기능을 더한)를 제공한다.
```java
List<String> emptyList = Collections.emptyList();
```
클라이언트는 인터페이스에 명시한 객체를 얻을 것임을 알고, 구현 클래스를 알아보지 않아도 된다. 실제로 클라이언트는 얻은 객체를 인터페이스로만 다루게된다.

### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

정적 팩터리 메서드만 제공하는 ``EnumSet`` 경우 원소 수에 따라 RegularEnumSet(64개이하의 원소), JumboEnumSet을 반환하지만 클라이언트는 이를 모른다.
또한, RegularEnumSet의 이점이 없는 경우 이를 제거 또는 다른 클래스를 추가해도 무관하다. 

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

``JDBC(서비스 제공자 프레임워크)`` 와 같이 제공자가 서비스의 구현체를 관리, 클라이언트를 구현체로 부터 분리해준다.

서비스 제공자 프레임워크 구성요소
> - 서비스 인터페이스 : 구현체의 동작을 정의 ``ex. Connection``
> - 제공자 등록 API : 제공자가 구현체를 등록 ``ex. DriverManager.registerDriver``
> - 서비스 접근 API : 클라이언트가 서비스의 인스턴스를 얻을때 사용 ``ex. DriverManager.getConnection``
> - 서비스 제공자 API : 팩터리 객체를 설명하는 서비스를 제공 (제공하지 않는경우 리플렉션을 통해 파악해야함) ``ex. Driver``

클라이언트는 인스턴스를 얻을때, 조건을 명시할 수 있다. ``= 유연한 정적 팩터리``

- Bridge Pattern : 공급자가 제공하는 것 보다 풍부한 서비스 인터페이스를 클라이언트에 반환
- Dependency Injection : 강력한 서비스 제공자 
- 자바5 이후, Service Loader라는 범용 서비스 제공자 프레임 워크 제공

```java
public class HelloServiceFactory {

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        
        // 방법1.
        ServiceLoader<HelloService> loader = ServiceLoader.load(HelloService.class);
        Optional<HelloService> helloServiceOptional = loader.findFirst();
        helloServiceOptional.ifPresent(h -> {
            System.out.println(h.hello()); // 사용시점에 클래스 존재 하지 않음 - 어떤 임의의 구현체가 올지 아예 몰라. 인터페이스 기반으로만 코딩!
        });
        
        
        /* 구현체를 가지는 서비스를 의존으로 가지고잇음 - like 우린 어떤 DB를 사용할지 모르지만, JDBC에 대한 의존가지고만 특정 DB에 종속되지 않는 어플리케이션을 만들수 있음
         <dependency>
            <groupId>me.whiteship.hello</groupId>
            <artifactId>chinese-hello-service</artifactId>
            <version>0.0.1-SNAPSHOT</version>
         </dependency>
         * */
        
        
        // 방법2. 이거랑 다른점은?
        HelloService helloService = new ChineseHelloService(); // 의존성이 생김! - import해야한다.
        System.out.println(helloService.hello());
        
    }

}
```

## 정적 팩터리 메서드의 단점

### 1. 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다. 
- 상속시 public/protected 생성자가 필요
- 정적 팩터리 메서드는 private 생성자를 가져야한다.
- 물론 상속을 하지않고, deligation(멤버변수와 같이 다른 클래스로 감싸서 참조) 하여, 기존코드에 기능을 추가 할 수 있다.
```java
interface Printer {
    void print(String message);
}
// 상속
class RealPrinter implements Printer {
    @Override
    public void print(String message) {
        System.out.println(message);
    }
}
// deligation
class PrinterDelegate {
    private Printer printer = new RealPrinter();

    void print(String message) {
        printer.print(message); // Delegation: 위임한다.
    }
}
```

컬렉션 프레임워크의 유틸리티 구현 클래스들은 상속할 수 없다.

### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.
- 자바 docs만 해도 생성자는 한눈에 보기 쉽게 정리해서 보여주지만, 정적 팩터리 메서드는 한눈에 파악이 어렵다. (static method tavb)

명명 방법
- form : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
```java
  Date d = Date.from(instant);
```
- of : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
```java
  Set<Rank> faceCards = EnumSet.of(JACK, QUEEN, KING);
```
- valueOf : from과 of의 더 자세한 버전
```java
  BigInteger prime = BigInteger.valueOf(Integer.MAX_VALUE);
```
- instance 혹은 getInstance : 매개변수가 있다면, 같은 인스턴스를 반환하나 이를 보장하진 않음
- create 혹은 newInstance : 매번 새로운 인스턴스 생성을 보장
- getType 혹은 newType 혹은 type : Type은 해당 팩터리 메서드가 반환할 인스턴스 타입이다.

---
## ETC

### 플라이웨이트 패턴
: 자주사용되는 객체들을 캐싱해두고 사용하는것
