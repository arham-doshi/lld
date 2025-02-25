//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import actorSystem.ActorRunner;
import fimble.FimbleRunner;
import producerConsumer.PcRunner;
import threadding.ThreadRunner;

public class Main {
    public static void main(String[] args)  {
        System.out.println(Thread.currentThread().getName());

        try{
            FimbleRunner.main();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}