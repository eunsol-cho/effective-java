# private 생성자나 열거 타입으로 싱글턴임을 보증하라

: 인스턴스를 오직 하나만 생성하는 클래스

사용 케이스
- 함수와 같은 무상태(stateless) 객체
- 설계상 유일해야하는 컴포넌트

다만, 싱글턴 객체의 클라이언트 코드의 테스트는 어렵다.
- 인터페이스를 가진다면, mock생성을 통해 보다 테스트 코드 작성이 용이.

## 생성방식

1. public static final 필드 방식
```java
class Elvis {
	public static final Elvis INSTANCE = new Elvis();
	private Elvis() {
        //...
    }
}
```
- api에 해당객체가 싱글턴임을 명확하게 드러낸다. - 반환타입이 final임
- 소스의 간결함

2. 정적 팩터리 메서드
```java
class Elvis {
	private static final Elvis INSTANCE = new Elvis();
	private Elvis() {
        //...
    }
	
	public static Elvis getInstance() {
		return INSTANCE;
    }
}
```
- 추후 클라이언트 코드 변경없이 싱글턴이 아니게 변경 가능
- 정적 팩터리를 제네릭 싱글턴 팩터리로 만들수 있음 
- 정적 팩터리의 메서드 참조를 공급자(supplier)로 사용할 수 있다.
  - 인자 없는 메서드를 호출해서 오브젝트를 리턴해줌 = supplier에 준하는 메서드 = 정적 팩터리 메서드
  - Elvis::getInstance - supplier를 파라미터로 받는 메서드에 넘겨줄 수 있음

<details>
<summary> [code] 제네릭 싱글턴 팩터리 </summary>
<div markdown="1">

```java
public class MetaElvis<T> {

    private static final MetaElvis<Object> INSTANCE = new MetaElvis<>();

    private MetaElvis() { }

    @SuppressWarnings("unchecked")
    public static <E> MetaElvis<E> getInstance() { return (MetaElvis<E>) INSTANCE; }
    //public static <T> MetaElvis<T> getInstance() { return (MetaElvis<T>) INSTANCE; }
    // ! 위와 같이 <T>를 리턴 앞에서 써줘서, scope이 다른 동일한 이름의 제네릭 문자를 사용가능. (클래스에 선언된 T와는 다른 것임)

    public void say(T t) {
        System.out.println(t);
    }

    public void leaveTheBuilding() {
        System.out.println("Whoa baby, I'm outta here!");
    }

    public static void main(String[] args) {
        MetaElvis<String> elvis1 = MetaElvis.getInstance();
        MetaElvis<Integer> elvis2 = MetaElvis.getInstance();
        System.out.println(elvis1);
        System.out.println(elvis2);
        System.out.println(elvis1.equals(elvis2)); // true이다(해시코드도 같음) == 로 비교하면, 타입이 달라서 false로 나옴
        elvis1.say("hello");
        elvis2.say(100);
    }

}
```

</div>
</details>


3. 열거타입방식 - 바람직함 😀
```java
public enum Elvis {
	INSTANCE;
	
	public void leaveTheBuilding() {
		// ...
    }
}
```
- 2번과 비슷하지만 보다 간결
- reflection api에도 자유로움 - enum은 절대 생성자에 접근X, 기존 열거된것만 쓰는거고 새로 만들어 쓸 수 없다.
- 직렬화에 용이
- 다만, enum이외의 상속이 필요한 경우 사용이 어렵다 - 열거타입이 다른 인터페이스를 구현하도록은 선언가능

## 1,2번 방식의 문제점
### 1) reflection api통한 접근시 싱글톤이 깨짐 
  ```java
  Constructor<Elvis> defaultConstrutor = Elvis.class.getDeclaredConstructor();
  defaultConstrutor.setAccessable(true);
  Elvis elvis = defaultConstrutor.newInstance();
  ```
  두번째 생성시 생성을 막는 코드를 추가해 방지 가능 


### 2) 싱글턴 클래스를 직렬화
: 역직렬화 작업시 새로운 인스턴스가 생길 수 있음
- Serializable 구현 선언으로는 부족
- 모든 인스턴스 필드를 transient로 선언
- readResolve 메서드를 제공

위와 같이 하지 않는다면, 직렬화된 인스턴스를 역직렬화 할때 마자 새로운 인스턴스가 만들어진다.

```java
private Object readResolve() {
	return INSTANCE; // 가짜 객체는 가비지 컬렉터에 맡김. 진짜를 반환.
}
```
- 오버라이딩 아님. 근데 위 메서드가 역직렬화시에 사용된다. - 동작은 오버라이딩이라고 생각하면됨.

---
## ETC

### 메소드 참조
: 메소드 하나만 호출하는 람다 표현식을 줄여쓰는 방법

- 스태틱 메소드 레퍼런스
- 인스턴스 메소드 레퍼런스
- 임의 객체의 인스턴스 메소드 레퍼런스 - 첫번째 인자는 자기자신
- 생성자 레퍼런스

### 함수형 인터페이스
: 선언부가 단 하나인 인터페이스 

- 기본 제공 : java.util.function
  - Function<T, R>
  - Supplier<R>
  - Consumer<T>
  - Predicate<T>
- @FunctionInterface

### 객체 직렬화
: 객체를 바이트스트림으로 상호 변환하는 기술

- 파일 저장 or 네트워크 전송시 용이 - 최근엔 json같은 포맷으로 변경해서 씀. 사실 jvm끼리 통신이 아니면, 역직렬화가 안되서 무의미하거든
- serializable 인터페이스 구현
- trasient 사용으로 직렬화 하지 않을 필드 선언
- serialVersionUID?
  - Serializable 구현 클래스에 JVM이 자동으로 선언해준다. (명시적으로 선언 가능)
  - 클래스가 변경되면, serialVersionUID가 변경됨
  - serialVersionUID변경되면, 이전버전에서 직렬화된 데이터를 역직렬화 할 수 없다.
    - 다만, 임의로 이를 동일하게 해서 클래스가 변경되도 역질렬화를 진행 할 수 있다.

<details>
<summary> [code] serializable </summary>
<div markdown="1">

```java
public class Book implements Serializable {

    private static final long serialVersionUID = 1L;
  
    private String isbn;
  
    private String title;
  
    private LocalDate published;
  
    private String name;
  
    private transient int numberOfSold;
	
}
```
```java
public class SerializationExample {
  
    private void serialize(Book book) {
        try (ObjectOutput out = new ObjectOutputStream(new FileOutputStream("book.obj"))) {
            out.writeObject(book);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
  
    private Book deserialize() {
        try (ObjectInput in = new ObjectInputStream(new FileInputStream("book.obj"))) {
            return (Book) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
  
    public static void main(String[] args) {
        SerializationExample example = new SerializationExample();
        Book deserializedBook = example.deserialize();
        System.out.println(deserializedBook);
    }
}
```

</div>
</details>
