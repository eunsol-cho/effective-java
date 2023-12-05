package ch04.src.item15;

public class MainClass_v1 {
    public void doSomething() {
        HelperClass helper = new HelperClass();
        helper.help();
    }
}

class HelperClass {
    void help() {
        System.out.println("도움을 제공합니다.");
    }
}

