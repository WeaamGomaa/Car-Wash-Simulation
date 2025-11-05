import java.io.*;
import java.util.*;

class Semaphore{

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
    private Queue<Car> waitingQueue;
    private Semaphore empty;
    private Semaphore full;
    private Semaphore mutex;

    public static void main(String[] args){

    }
}
