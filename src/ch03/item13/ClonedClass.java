package src.ch03.item13;

public class ClonedClass implements Cloneable{

    @Override
    public ClonedClass clone() {
        try {
            return (ClonedClass) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
