package threadding;
class Myclass implements Runnable{
    @Override
    public void run(){
        System.out.println("current thread" + Thread.currentThread().getName());
    }
}
public class ThreadRunner {
    public static void main(){
        Myclass myclass = new Myclass();
        Thread thread = new Thread(myclass);
        thread.start();
    }
}
