import java.util.*;

class Semaphore{
    Semaphore(int sem){

    }

}

class Car extends Thread{
    //Implement this constructor
    public Car(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
               Semaphore mutex, String carId){
        //Initialize all fields
    }

    //Implement the consumer logic
    public void run(){

    }
}

class Pump extends Thread{
    //Implement this constructor
    public Pump(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
                Semaphore mutex, int pumpId){
        //Initialize all fields
    }

    //Implement the producer logic
    public void run(){

    }
}

public class ServiceStation {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=====Welcome in our Car Wash Simulation=====");
        Scanner input = new Scanner(System.in);
        System.out.println("Waiting area capacity: ");
        int waitingAreaCapacity = input.nextInt();
        System.out.println("Number of service bays (pumps): ");
        int numberOfPumps = input.nextInt();
        System.out.println("Number of cars arriving: ");
        int totalCars = input.nextInt();

        //Initializing shared resources
        Queue<Car> waitingQueue = new LinkedList<>();
        Semaphore empty = new Semaphore(waitingAreaCapacity);
        Semaphore full = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        //Create and start Pump threads
        for(int i = 0; i < numberOfPumps; i++){
            new Pump(waitingQueue, empty, full, mutex, i+1).start();
        }

        //Create and start Car threads
        for(int i = 0; i < totalCars; i++){
            new Car(waitingQueue, empty, full, mutex, "C"+i+1).start();
            Thread.sleep(2000);
        }
    }
}
