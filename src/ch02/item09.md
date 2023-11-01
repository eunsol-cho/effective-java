# try-finally보다는 try-with-resources를 사용하라

- InputStream, OutputStream, java.sql.Connection 등의 자원을 사용하는 클래스는 close 메서드를 호출하여 직접 닫아줘야 한다.
- 자원 닫기를 하지 않으면 심각한 성능 이슈를 가져올 수 있다.
- 이를 자동으로 해주는 try-with-resources를 사용하자.

### try-finally
- 자원이 둘이상인 경우 중첩으로 사용 -> 지저분. 또한 중첩으로 인한 디버깅의 어려움을 겪을 수 있다.
- 숙련된 개발자도 close를 선언하는것을 놓친다.

### try-with-resources
- 자바7 부터 등장
- 해당 자원이 AutoCloseable 인터페이스를 구현해야한다.
```java
public interface AutoCloseable {
    void close() throws Exception; // 닫기 메서드만 있는 인터페이스
}
```
- try-with-resources를 사용하면, try 블록을 벗어날 때 자동으로 close 메서드가 호출된다.
- 소스가 간결
- 중첩된 로직으로 인해 오류가 덮어써져도, 해당 오류가 스택 추적 내역에 suppressed로 남아있다.
    - gerSuppressed 메서드를 통해 확인 가능
- catch 블록 사용가능
- 