# 클래스와 멤버의 접근 권한을 최소화 해라

> - 클래스 내부 데이터와 내부 구현 정보를 외부 컴포넌트로부터 얼마나 잘 숨겼느냐 <br>
> - 잘설계된 컴포넌트는 모든 내부구현을 완벽히 숨겨, <u>구현과 API를 깔끔히 분리</u>

### 구현과 API를 깔끔히 분리 = 캡슐화 & 정보은닉
- 오직 <u>API를 통해서만 다론 컴포넌트와 소통</u>하며 서로의 내부 동작 방식에는 전혀 개의치 않는다.
- 시스템을 구성하는 컴포넌트 들을 서로 독립시켜서 개발, 테스트, 최적화, 적용, 분석, 수정을 개별적으로 할 수 있게 해주는 것

## 정보은닉의 장점
- 시스템 개발 속도를 높임
  - 컴포넌트를 병렬로 개발 가능
- 시스템 관리 비용을 낮춤
  - 컴포넌트를 더 발리 파악하여, 디버깅
  - 다른 컴포넌트로 교체 부담이 적다.
- 성능 최적화
  - 정보은닉 != 성능향상
  - 성능 최적화에 도움
  - 다른 컴포넌트 영향 없이 해당 컴포넌트만 최적화 가능
- sw의 재사용성 향상
  - 외부의 의존도가 없는 컴포넌트라면, 낯선 환경에서도 활용 가능
- 큰 시스템 제작 난이도를 낮춤
  - 전체가 완성되기 전에 개별 컴포넌트 검증 가능.

## 접근 제어 메커니즘 for 정보은닉
> 모든 클래스와 멤버의 접근성을 가능한 한 좁혀야 한다.

### 톱레벨(가장바깥) 클래스와 인스턴스 : package-private & public
- public
  - 공개 API = 하위호환을 영원히 관리 해줘야함
- package-private
  - 내부구현 = 언제든지 수정 가능
  - 클라이언트에 영향 없이 수정,교체,제거 가능

### 톱레벨 접근 제한자 설정 방식
1. [한 클래스에서만 사용하는 package-private 의 경우](./item15/MainClass_v1.java) 
➡ [이를 사용하는 클래스 안에 private static으로 중첩](./item15/MainClass_v2.java)
2. public 일 필요가 없는 클래스의 접근 수준을 package-private 톱클래스로 좁혀라 ⭐️

## 멤버(필드, 메서드, 중첩클래스, 중첩인터페이스) 에 부여가능한 접근 수준
- private : 멤버를 선언한 톱클래스에서만 접근
- package-private : 멤버가 소속된 패키지안의 모든 클래스에서 접근 가능
  - 접근제한자 명시 하지 않았을때 적용
  - 단, 인터페이스의 멤버는 public
- protected : package-private 포함 + 상속한 하위 클래스에서 접근
- public : 모든 곳에서 접근

### 멤버 접근 제한자 설정 방식 
1. 공개 API 설계후, 그 외의 모든 멤버는 private으로 한다.
2. 오직 같은 패키지의 다른 클래스만 접근에 한하여, private제거 -> package-private 로 풀어주자
   1. 권한을 풀어주는 일이 잦다면, 컴포넌트 분해를 고려

### 주의사항
- private & package-private 멤버는 모두 해당 클래스의 구현, 보통은 공개 API에 영향을 주지 않음
- [Serializable 구현 클래스의 필드의 경우 의도치 않게 공개 API가 될 수 있다.](./chXXX/item86) TODO
- public 클래스 에서 멤버 접근 수준을 package-private -> protected 로 변경시, 접근대상이 너무 넓어진다.
- public 클래스의 protected 멤버는 공개 API이므로 영원히 지원돼야 한다. protected 멤버 의 수는 적을수록 좋다.

### 멤버 접근성을 좁히지 못하게 방해하는 제약
- 상위 클래 스의 메서드를 재정의할 때는 그 접근 수준을 상위 클래스에서보다 좁게 설정 할 수 없다.
- 제약은 상위 클래스의 인스턴스는 하위 클래스의 안스턴스로 대체해 사용할 수 있어야 한다는 규칙([리스코프 치환 원칙, 아이템 10](../ch03/item10/Main.java))을 지키기 위해 필요.
- 이 규칙을 어기면 하위 클래스를 컴파 일할 때 컴파일 오류 발생
- 클래스가 인터페이스를 구현하는 건 이 규칙의 특별한 예로 볼 수 있고, 이때 클래스는 인터페이스가 정의한 모든 메서드를 public으로 선언 (상속은 아니지만 상속과 유사하게 동작한다는 면에서 특별한 예라고 표현)

### 테스트를 목적으로 접근범위 넓힘
- 적당한 수준까지는 넓혀도 괜찮다
- 예를 들어, public클래스의 private 멤버를 package-private까지 풀어주는 것은 허용
- 그 이상은 안됨 (테스트 코드 를 테스트 대상과 같은 패키지에 두면 package-private 요소에 접근 가능)
- 즉, 테스트만을 위해 클래스, 인터페이스, 멤버를 공개 API로 만들어서는 안 된다. 

### public 클래스의 인스턴스 필드는 되도록 public이 아니어야 한다
- [public 가변 객체, 인스턴스 필드를 갖는 클래스는 일반적으로 스레드 안전하지 않다.](./item15/NonFinalFieldInPublicClass.java)
  - 불변을 보장하지 않음
  - 필드가 수정 될때 (락 획득 같은) 다른 작업을 할 수 없게 됨
- 필드가 final이면서 불변객체를 참조 
  - 불변은 보장하나
  - 필드 없애지도 못하고, 해당 필드가 API의 일부가 됨
- 예외로, public static final 필드로 공개 하는 상수 (단, 대문자 & _ 사용) -> 반드시 불변객체 or 기본값 타입
  - 가령, 배열의 길이가 0이 아니라면, 무조건 가변 취급 🎈
```java
// 방법1. 불변객체로 선언
private static final Thing[] PRIVATE_VALUE=S { ... }; 
public static final List <Thing> VALUES=
    Collections.unmodifiablelist(Arrays.aslist(PRIVATE_VALUES));

// 방법2. clone
private static final Thing[] PRIVATE_VALUE=S { ... };
public static final Thing[] values() {
    return PRIVATE_VALUES.clone() ;
}
```

### 자바9의 모듈 시스템
- 모듈 단위의 접근 수준 설정 (module—info.java 을 각 모듈에 정의)
- 모듈 > 패키지 > 클래스
- 모듈에 걸린 접근제한자가 우선 (클래스가 public이어도 모듈이 protected면, protected로 적용)
```java
module com.myapp {
    requires java.sql; // 모듈이 의존하는 다른 모듈
    exports com.myapp.api; // 공개 : 모듈 내에서 어떤 패키지가 다른 모듈에 의해 사용될 수 있는지를 결
}
```
- 모듈의 jar 파일을 다른 모듈의 calsspath에 둔다면? 모듈 접근 수준을 무시하고, public, protected 모두 접근
  - 이게 바로 JDK