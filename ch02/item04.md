# 인스턴스화를 막으려거든 private 생성자를 사용하라
 
 - 정적 메서드와 정적 필드만을 담은 클래스
 - 인스턴스화를 막기 위해 private 생성자를 사용


 - 자바에서 사용되는 곳
   - java.lang.Math, java.util.Arrays : 기본 타입 값이나 배열 관련 메서드 모아둠
   - java.util.Collections : 특정 인터페이스 구현하는 객체 생성
   - final 클래스 관련 메서드. 
     - `public final class Math{ ... private Math() {} .. }` : 상속불가
 
 ```java
 public class UtilityClass {
     // 기본 생성자가 만들어지는 것을 막는다(인스턴스화 방지용).
     private UtilityClass() {
         throw new AssertionError(); // 예외는 필수는 아니지만, 코드의 직관성을 위해 작성
     }
     // 나머지 코드 생략
 }
 ```
 ## 인스턴스화 방지

추상 클래스로 만드는 것으로는 인스턴스화를 막을 수 없다.
- 하위 클래스를 만들어서 인스턴스화 하면 된다.

명시적으로 private 생성자를 만들어주면, 클래스 외부에서 생성자를 호출할 수 없다.
 - 생성자를 명시하지 않으면 컴파일러가 자동으로 기본 생성자를 만들어준다.
 - 이때, 생성자를 호출하면 AssertionError를 던지도록 만들어준다.

이는 상속을 불가능하게 만든다.
   - 모든 생성자는 상위 클래스의 생성자를 호출하게 되는데, private 생성자는 상위 클래스의 생성자를 호출할 수 없다.
 