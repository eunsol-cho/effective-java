package src.ch04.item15;

public class NonFinalFieldInPublicClass {

    // final이 아닌 public 필드 -> 캡슐화 & 데이터무결성 & 스레드 안정성 문제 야기
    public int counter = 0;

    // 락 획득
    // public synchronized void incrementCounter() {
    public void incrementCounter() {
        counter++;
    }

}
