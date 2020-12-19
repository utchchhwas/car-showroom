public class Debug {

    public static void debug(String msg) {
        System.out.println(Thread.currentThread().getName() + " >>> " + msg);
    }
}
