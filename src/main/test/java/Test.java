import java.util.Objects;

public class Test {

    public static void main(String[] args) {
        System.out.println(Objects.hash(3, -4));
        System.out.println(Objects.hash( -4, 3));
    }

}
