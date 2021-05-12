import java.util.ArrayDeque;

public interface API {
    public Object xxx(String x);

    public static void main(String[] args) {
        String a="a";
        String b="b";
        String c="a";
        String a1=new String("a");
        String b1=new String("b");
        String c1=new String("a");
        System.out.println(a.equals(a1));
    }
}
