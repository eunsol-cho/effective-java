# 변경 가능성을 최소화 하라

## 불변 클래스
- 그 인스턴스의 내부 값을 수정할 수 없는 클래스
- 불변 인스턴스에 간직된 정보는 고정되어 객체가 파괴되는 순간까지 절대 달라지지 않는다.
- 해당하는 자바 클래스 : St r i ng, 기본 타입의 박싱된 클래스들, Bigi nt eger, BigDecimal

### 불변으로 설계하는 이유
- 불변 클래스는 가변 클래스보다 설계하고 구현하고 사용하기 쉬움
- 오류가 생길 여지도 적고 안전

## 불변 클래스로 만드는 규칙
1. 객체의 상태를 변경하는 메서드(변경자)를 제공하지 않는다.
2. 클래스를 확장할 수 없도록 한다. (final - 상속한 클래스에서 변경을 막음)
3. 모든 필드를 fmal로 선언한다. (설계자의 의도를 명확히 드러내는 방법)
4. 모든 필드를 private으로 선언한다.
   - public final로만 선언해도 불변 객체가 되지만, 다음 릴리스에서 내부 표현을 바꾸지 못함
5. 자신 외에는 내부의 가변 컴포넌트에 접근할 수 없도록 한다.
   - 생성자, 접근자, readObject 메서드(아이템 88) 모두에서 방어적 복사를 수행

### 예시
```java
public final class Complex { 
    
    private final double re;
    private final double im;
    
    public Complex(double re, double im) { 
        this.re = re;
        this.im = im;
    }
    
    public double realPart() { return re; } 
    public double imaginaryPart( ) { return im; }
    
    /** 사칙연산 **/
    public Complex plus(Complex c) {  // 피 연산자의 변경 없이
        return newComplex(re+c.re, im+c.im); // 새로운 객체를 생성해서 리턴
    }
    // ...
}
```
- 사칙연산메서드들이 인스턴스자신은수정하지 않고새로운 Complex 인스턴스를 만들어 반환 <br>
- 피 연산자에 함수를 적용해 그 결과를 반환하지만, 피 연산자 자체는 그대로인 프로그래밍 패턴 = 함수형 프로그래밍

## 불변 클래스 장점

### 불변 객체는 근본적으로 스레드 안전하여 따로 동기화할 필요 없다.
- 여러 스 레드가 동시에 사용해도 절대 훼손되지 않는다.
- 불변 객체는 안심하고 공유할 수 있다.

### 불변 클래스라면 한번 만든 인스턴스를 최대한 재활용하기를 권장
- 상수(public static final)로 제공
- `public static final Complex ZERO= new Complex(0, 0);`
- 자주 사용되는 인스턴스를 캐싱하여 같은 인스턴스를 중복 생성하지 않게 해주는 정적 팩터리(아이템 1)를 제공
- 여러 클라이언트가 인스턴스를 공유하여 메모리 사용량과 가비지 컬렉션 비용이 줄어듦
- 클래스 설계시, public 생성자대신 정적 팩터리를만들어두면,클라이언트를수정하지 않고도 필요에 따라 캐시 기능을 나중에 덧붙일 수 있다.

### 불변객체는 방어적 복사도 필요없음
- clone 메서드나 복사 생성 자(아이템 13)를 제공하지 않는 게 좋다
- String 클래스의 복사 생성자도 사용 지양


- 객체를 만들 때 다론 불변 객체들을 구성요소로 사용하면 이점 - Collection내부 객체가 불변 객체라면, 안전
- 불변 객체는 그 자체로 실패 원자성올 제공 (절대 안변함)

## 불변 클래스 단점
### 값이 다르면 반드시 독립된 객체로 만들어야 한다
- 이때문에, 시간과 공간을 비효율적으로 사용하게 될 수있다. ex. BigInteger

### 해결방안
- 다단계 연산?
    - BigInteger는 모듈러 지수 같은 다단계 연산 속도를 높여주는 가변 동반 클래스(companion class)를 package-private으로 두고 있다. ???
    - String의 가변 동반 클래스는? 바로 StringBuilder(와 구닥다리 전임자 StringBuffer)

- 111~113 다시보기