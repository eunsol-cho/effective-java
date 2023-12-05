# 생성자에 매개변수가 많다면 빌더를 고려하라

정적 팩터리와 생성자는 매개변수가 많은 경우 대응이 어렵다.

## 매개변수가 많은 경우 대응방안

### 점층적 생성자 패턴(telescoping constructor pattern)    
 : 필요에 따라 생성자에 매개변수를 늘려가는 방식
``ex. 생성자(매개변수1), 생성자(매개변수1, 매개변수2)``   

단점  
- 매개변수가 많은 경우 클라이언트 코드를 작성/읽기가 어려움
- 순서가 바껴도 컴파일 시점이 아닌 런타임 시점에 알아챌 수 있음

### 자바빈즈 패턴(JavaBeans pattern)   
 : 매개변수 없는 생성자로 객체 생성후, setter로 매개변수 설정

단점      
- 객체 하나를 만들기 위해 **여러 메서드**를 호출 필요
- 객체가 완전히 생성되기 전까지 **일관성(consistency)** 이 무너진 상태임
- 클래스를 불변으로 만들 수 없음 - 스레드 안전성을 가지기 위한 추가 작업이 필요함
  - freeze : 객체를 생성후 수동으로 불변하게 처리. 다만 freeze 처리에 대한 컴파일러의 보장이 어려워 런타임 오류에 취약함

### 빌터 패턴(Builder pattern)
 : 빌더 객체를 통해 필요 매개변수만으로 객체를 만든다. - 위 두가지 방식의 장점만을 취함

- 빌더는 생성할 클래스 안에 정적 맴버 클래스로 만들어 둔다.
- 메서드 체이닝을 통해 가독성을 높힌다. (fluent API or method chaining)
- 불변 객체 생성 가능
- 각 매개변수에 따른 예외 처리도 가능 (아래 소스에서는 가독성을 위해 생략됨)

<details>
<summary> [code] </summary>
<div markdown="1">

선언
```java
class NutritionFacts {
	private final int calories;
	private final int fat;
    // ... 멤버변수들
  
    // 클래스 안에 정적 멤버 클래스로 만든다.
	public static class Builder {
		// 필수
        private final int calories;
        // 선택 - 기본값 초기화
        private final int fat = 0;
		
		public Builder(int calories) {
			this.calories = calories;
        }

        public Builder fat(int fat) {
          this.fat = fat; 
		  return this;
        }
    }
	
    // NutritionFacts 클래스는 불변이다.
	public NutritionFacts (Builder builder) {
		this.calories = builder.calories;
		this.fat = builder.fat;
    } 
}
```
사용
```java
// fluent API or method chaining
NutritionFacts cocaCola = new NutritionFacts.builder(240)
        .fat(10)
        .build();
```
</div>
</details>

장점

#### 계층적으로 설계된 클래스와 함께 쓰기 좋다.

<details>
<summary> [code] 20p </summary>
<div markdown="1">

선언
```java
import java.util.EnumSet;
import java.util.Objects;

abstract class Pizza {
  public enum Topping {HAM, ONION}

  final Set<Topping> toppings;

  abstract static class Builder<T extends Builder<T>> {
      EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
  
      public T addTopping(Topping topping) {
        toppings.add(Objects.requireNonNull(topping));
        return self(); // !
      }
      
      abstract Pizza build();
      
      // 하위 클래스는 이 메서드를 재정의하여 this를 반환하도록 한다.
      protected abstract T self();
  }

  Pizza (Builder<?> builder) {
    this.toppings = builder.toppings.clone();
  }
}
```

```java
import java.util.EnumSet;
import java.util.Objects;

class NyPizza extends Pizza {
  public enum Size {SMALL, LARGE}
  private final Size size;

  public static class Builder extends Pizza.Builder<Builder> {
	  private final Size size;
  
      public Builder(Size size) {
        this.size = Objects.requireNonNull(size);
      }
      
	  @Override
      public NyPizza build() {
		  return new NyPizza(this);
      }

      @Override
      protected Builder self() {
		  return this;
      }
  }

  private NyPizza (Builder builder) {
	  super(builder);
	  size = builder.size;
  }
}
```

사용
```java
// fluent API or method chaining
NyPizza pizza = new NyPizza.builder(SMALL)
        .addTopping(HAM)
        .addTopping(ONION)
        .build();
```

</div>
</details>

- 여러객체를 순회하면서 만들수 있음
- 매개변수에 따라 다른 객체 생성 가능
- 객체마다 일련번호가 부여된다면, 빌더가 알아서 채우게 처리 가능

단점
- 빌더의 생성비용 고려 - 코드가 비교적 길다 (매개변수가 4개는 이상이어야 값어치를 한다.)
- 하지만, 매개변수는 항상 늘어간다는 것을 고려해 빌더로 초기에 해두는 것 권장
