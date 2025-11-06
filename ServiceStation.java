import java.util.*;

class Semaphore{
    private int value;

    Semaphore(int val){
        this.value = val;
    }

    public synchronized void acquire() {
        while (value <= 0) {
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }
        value--;
    }

    public synchronized void release() {
        value++;
        notify();
    }
}

class Car extends Thread{
    String carId;
    Queue<Car> waitingQueue;
    Semaphore empty;
    Semaphore full;
    Semaphore mutex;

    //Implement this constructor
    public Car(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
               Semaphore mutex, String carId){
        this.carId = carId;
        this.waitingQueue = waitingQueue;
        this.empty = empty;
        this.full = full;
        this.mutex = mutex;
    }

    //Implement the producer logic
    public void run(){
        empty.acquire();
        mutex.acquire();
        waitingQueue.add(this);
        System.out.println(carId + " arrived");
        mutex.release();
        full.release();
    }
}

class Pump extends Thread{
    private Queue<Car> waitingQueue;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore mutex;
    private int pumpId;
    //Implement this constructor
    public Pump(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
                Semaphore mutex, int pumpId){
        //Initialize all fields
        this.waitingQueue = waitingQueue;
        this.empty = empty;
        this.full = full;
        this.mutex = mutex;
        this.pumpId = pumpId;
    }
    //Implement the consumer logic
    public void run(){
        while (true){
            try {
                full.acquire();
                mutex.acquire();
                Car car = waitingQueue.poll();
                if (car != null){
                    System.out.println("Pump " + pumpId + ": " + car.carId + " login ");
                }
                mutex.release();
                empty.release();
                if (car != null){
                    System.out.println("Pump " + pumpId + ": " + car.carId + " begins service at Bay " + pumpId);
                    Thread.sleep(3000);
                    System.out.println("Pump " + pumpId + ": " + car.carId + " finishes service ");
                    System.out.println("Pump " + pumpId + ": Bay " + pumpId + " is now free ");
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
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
