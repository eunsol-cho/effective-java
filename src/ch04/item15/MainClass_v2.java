package src.ch04.item15;

public class MainClass_v2 {

    String field1;

    // MainClass 에서만 사용가능
    private static class HelperClass {

        String field2;

        void help() {
            // new MainClass_v2().field1 = ""; -> 가능 => 다만, 여기서 생성된 MainClass_v2는 현 HelperClass가 속한 인스턴스가 아니다
            // field1 = ""; -> 불가능
            System.out.println("도움을 제공합니다.");
        }
    }

    /**
     * Inner class 'HelperClass' may be 'static'
     * - static 으로 안해도 정상 동작
     * - 외부클래스(MainClass_v2)의 멤버 접근시, 반드시 static 제거
     * - 즉, 외부클래스와의 결합을 줄이기 위해 static 을 쓰는것임!!
     *
     * * * static 사용에 대한 장점
     * 1) 메모리 효율성 : 외부 클래스 인스턴스 참조가 없으니, GC가 외부 인스턴스 회수시 문제없음. = 메모리 누수 위험 감소
     * 2) 명확성과 유지보수성 : 외부클래스와의 결합 감소 = 코드의 명확성 = 유지보수 용이 = 재사용성 용이
     * 3) 명확하게 외부 클래스에 대한 참조를 줄여 = 실수 방지 = 오류 방지
     * */

    public void doSomething() {
        HelperClass helper = new HelperClass();
        helper.help();
        helper.field2 = "";
    }
}


class Performer {
    public static void main(String[] args) {
        MainClass_v2 m = new MainClass_v2();
        m.doSomething();
    }

}