package actorSystem;

import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.*;

class Actor {
    public BlockingQueue<String> mailBox;
    public String address;
    public volatile boolean isActive = true;
    public Actor(String address, int mailBoxSize) {
        this.address = address;
        this.mailBox = new ArrayBlockingQueue<>(mailBoxSize);
    }
    public String getAddress(){
        return this.address;
    }
    public boolean isActive(){
        return isActive;
    }
    public void shutdown(){
        this.isActive = false;
    }
    public void receiveMesage(String message) throws Exception {
        if(this.isActive == false){
            throw new Exception("Actor is shut down");
        }
        // mailBox . queue = blocks queue if full until becomes free -> may wait indefinitely
        // mailBox.add = throws exception if full
        // offer => returns false if the queue is full
        if(!mailBox.offer(message)){
            throw new Exception("Mailbox is full");
        }
    }
    public String getMessage(){
        return mailBox.poll();
    }
    /*
     finite mailbox
     can be shut down
     unique address
    */
}

class ActorSystem{
    /*
    create actor ( finite number ) -> register actor
    send message to actor -> send message
    actors run concurrently, in thread safe manner
    can be shut down
    shut down all actors after processing message

    nfr
    throws exception
    */
    public Map<String, Actor> actors;
    public ExecutorService threadPool;
    public volatile boolean isShutDown = false;
    public ActorSystem(int threadPoolSize){
        this.actors = new ConcurrentHashMap<>();
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void registerActor(Actor actor) throws IllegalStateException {
        if(this.isShutDown){
            throw new IllegalStateException("Actor system is shut down");
        }
        actors.put(actor.getAddress(), actor);
    }
    public void sendMessage(String actorAddress, String message) throws Exception {
        if(this.isShutDown){
            throw new IllegalStateException("Actor system is shut down");
        }
        Actor actor = actors.get(actorAddress);
        if(actor == null){
            throw new Exception("Actor not found");
        }
        actor.receiveMesage(message);
        threadPool.submit(() -> {
            String msg;
            while((msg = actor.getMessage()) != null ){
                System.out.println("Actor : "+ actor.getAddress() + " processing: " + msg);
            }
        });
    }
    public void shudown(){
        isShutDown = true;
        actors.values().forEach(Actor::shutdown);
        threadPool.shutdown();
    }


}

public class ActorRunner {
    /*
    implement actor system
    run using command line switch case manner
     */
    public static void main() throws Exception {
        System.out.println("registering actors ...");
        Actor actor1 = new Actor("actor1", 3);
        Actor actor2 = new Actor("actor2", 3);
        ActorSystem actorSystem = new ActorSystem(3);
        actorSystem.registerActor(actor1);
        actorSystem.registerActor(actor2);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("enter command (send / shutdown / exit ): ");
            String command = scanner.next();
            if(Objects.equals(command, "send")) {
                System.out.println("enter actor name : ");
                String actorName = scanner.next();
                System.out.println("enter message : ");
                String message = scanner.next();
                actorSystem.sendMessage(actorName, message);
            }
            else if (Objects.equals(command, "shutdown")) {
                actorSystem.shudown();
            }
            else{
                break;
            }
        }

        scanner.close();
    }
}
