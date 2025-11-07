import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
// Semaphore class
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
// Car class
class Car extends Thread{
    String carId;
    Queue<Car> waitingQueue;
    Semaphore empty;
    Semaphore full;
    Semaphore mutex;
    CarWashGUI gui;

    //Implement this constructor
    public Car(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
               Semaphore mutex, String carId, CarWashGUI gui){
        this.carId = carId;
        this.waitingQueue = waitingQueue;
        this.empty = empty;
        this.full = full;
        this.mutex = mutex;
        this.gui = gui;
    }

    //Implement the producer logic
    public void run(){
        empty.acquire();
        mutex.acquire();
        waitingQueue.add(this);
        gui.addLog( carId + " arrived");
        gui.incrementArrived();
        gui.updateQueueDisplay();
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
    private CarWashGUI gui;
    private volatile boolean running = true;
    //Implement this constructor
    public Pump(Queue<Car> waitingQueue, Semaphore empty, Semaphore full,
                Semaphore mutex, int pumpId , CarWashGUI gui ){
        //Initialize all fields
        this.waitingQueue = waitingQueue;
        this.empty = empty;
        this.full = full;
        this.mutex = mutex;
        this.pumpId = pumpId;
        this.gui = gui;
    }
    public void stopPump() {
        running = false;
    }
    //Implement the consumer logic
    public void run(){
        while (true){
            try {
                full.acquire();
                mutex.acquire();
                Car car = waitingQueue.poll();
                if (car != null){
                    gui.addLog("Pump " + pumpId + ": " + car.carId + " login ");
                }
                mutex.release();
                empty.release();
                if (car != null){
                    gui.setPumpStatus(pumpId - 1, true, car.carId);
                    gui.addLog("Pump " + pumpId +" : " + car.carId + " begins service at Bay " + pumpId );
                    Thread.sleep(3000);
                    gui.setPumpStatus(pumpId - 1, false, car.carId);
                    gui.addLog(" Pump " + pumpId + " finished " + car.carId);
                    gui.addLog( "Pump "+ pumpId +" : Bay " + pumpId + " is now free ");
                    gui.updateQueueDisplay();
                    gui.incrementProcessed();
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}


// GUI
class CarWashGUI extends JFrame {
    private JTextArea logArea;
    private JPanel pumpsContainer;
    private JLabel[] pumpLabels = new JLabel[0];
    private JPanel[] pumpPanels = new JPanel[0];
    private JPanel queuePanel;
    private JLabel statsLabel;
    private JButton startBtn, resetBtn;
    private JTextField tfCapacity, tfPumps, tfCars;

    private Queue<Car> waitingQueue;
    private java.util.List<Pump> pumps = new ArrayList<>();
    private java.util.List<Car> cars = new ArrayList<>();
    private int processed = 0, arrived = 0;
    private int capacity = 5, numPumps = 3, numCars = 10;

    public CarWashGUI() {
        setTitle(" Raghwa - Your fav makhsala");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(33, 150, 243));
        JLabel title = new JLabel("Raghwa - Your Fav Makhsala ", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // Controls
        JPanel config = new JPanel(new FlowLayout());
        config.setBackground(new Color(240, 240, 240));
        config.add(new JLabel("Capacity:"));
        tfCapacity = new JTextField(String.valueOf(capacity), 3); config.add(tfCapacity);
        config.add(new JLabel("Pumps:"));
        tfPumps = new JTextField(String.valueOf(numPumps), 3); config.add(tfPumps);
        config.add(new JLabel("Cars:"));
        tfCars = new JTextField(String.valueOf(numCars), 3); config.add(tfCars);

        startBtn = new JButton("Start");
        startBtn.setBackground(new Color(76, 175, 80));
        startBtn.setForeground(Color.WHITE);
        startBtn.addActionListener(e -> startSimulation());

        resetBtn = new JButton("Reset");
        resetBtn.setBackground(new Color(244, 67, 54));
        resetBtn.setForeground(Color.WHITE);
        resetBtn.addActionListener(e -> resetSimulation());
        resetBtn.setEnabled(false);

        config.add(startBtn);
        config.add(resetBtn);
        add(config, BorderLayout.SOUTH);

        // Center
        JPanel center = new JPanel(new BorderLayout(8,8));
        add(center, BorderLayout.CENTER);

        // Pumps container
        pumpsContainer = new JPanel();
        pumpsContainer.setBorder(BorderFactory.createTitledBorder("Service Bays"));
        pumpsContainer.setBackground(Color.WHITE);
        center.add(pumpsContainer, BorderLayout.CENTER);

        // Waiting queue
        queuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        queuePanel.setBackground(new Color(255, 250, 205));
        queuePanel.setBorder(BorderFactory.createTitledBorder("Waiting Queue"));
        queuePanel.setPreferredSize(new Dimension(1000, 100));
        center.add(queuePanel, BorderLayout.SOUTH);

        // Log (separate, on the right)
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createTitledBorder("Activity Log"));
        scroll.setPreferredSize(new Dimension(300, 100));
        add(scroll, BorderLayout.EAST);

        // Stats
        statsLabel = new JLabel("Arrived: 0 | Processed: 0", SwingConstants.CENTER);
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statsLabel.setOpaque(true);
        statsLabel.setBackground(new Color(224, 247, 250));
        add(statsLabel, BorderLayout.PAGE_START);

        buildPumpsUI(numPumps);
        setVisible(true);
    }

    // Build pump panels dynamically
    private void buildPumpsUI(int count) {
        pumpsContainer.removeAll();
        pumpsContainer.setLayout(new GridLayout(1, Math.max(1, count), 10, 10));
        pumpPanels = new JPanel[count];
        pumpLabels = new JLabel[count];

        for (int i = 0; i < count; i++) {
            JPanel p = new JPanel(new BorderLayout());
            p.setPreferredSize(new Dimension(100, 100));
            p.setBackground(new Color(200, 230, 255));
            p.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 255), 3));

            JLabel lbl = new JLabel("Pump " + (i + 1) + ": FREE", SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
            p.add(lbl, BorderLayout.CENTER);

            pumpPanels[i] = p;
            pumpLabels[i] = lbl;
            pumpsContainer.add(p);
        }

        pumpsContainer.revalidate();
        pumpsContainer.repaint();
    }

    // Simulation start
    private void startSimulation() {
        try {
            capacity = Integer.parseInt(tfCapacity.getText().trim());
            numPumps = Integer.parseInt(tfPumps.getText().trim());
            numCars = Integer.parseInt(tfCars.getText().trim());
            if (capacity < 1 || numPumps < 1 || numCars < 1) throw new NumberFormatException();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            return;
        }

        buildPumpsUI(numPumps);
        waitingQueue = new LinkedList<>();
        Semaphore empty = new Semaphore(capacity);
        Semaphore full = new Semaphore(0);
        Semaphore mutex = new Semaphore(1);

        processed = arrived = 0;
        updateStats();
        addLog("=== Washing Started ===");

        pumps.clear();
        for (int i = 0; i < numPumps; i++) {
            Pump p = new Pump(waitingQueue, empty, full, mutex, i + 1, this);
            pumps.add(p);
            p.start();
        }

        new Thread(() -> {
            for (int i = 0; i < numCars; i++) {
                Car c = new Car(waitingQueue, empty, full, mutex, "C" + (i + 1), this);
                cars.add(c);
                c.start();
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
            }
        }).start();

        startBtn.setEnabled(false);
        resetBtn.setEnabled(true);
    }

    private void resetSimulation() {
        for (Pump p : pumps) { p.stopPump(); p.interrupt(); }
        pumps.clear();
        cars.clear();
        if (waitingQueue != null) waitingQueue.clear();
        queuePanel.removeAll();
        queuePanel.revalidate();
        queuePanel.repaint();
        logArea.setText("");
        processed = arrived = 0;
        updateStats();
        startBtn.setEnabled(true);
        resetBtn.setEnabled(false);
        addLog("=== Simulation Reset ===");
        buildPumpsUI(numPumps);
    }

    // Display waiting queue
    public synchronized void updateQueueDisplay() {
        SwingUtilities.invokeLater(() -> {
            queuePanel.removeAll();
            if (waitingQueue != null) {
                for (Car c : waitingQueue) {
                    JLabel carLbl = new JLabel(c.carId, SwingConstants.CENTER);
                    carLbl.setOpaque(true);
                    carLbl.setPreferredSize(new Dimension(60, 40));
                    carLbl.setBackground(new Color(255, 230, 180));
                    carLbl.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));
                    queuePanel.add(carLbl);
                }
            }
            queuePanel.revalidate();
            queuePanel.repaint();
        });
    }

    public synchronized void addLog(String msg) {
        SwingUtilities.invokeLater(() -> {
            String t = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + t + "] " + msg + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public synchronized void setPumpStatus(int i, boolean busy, String carId) {
        SwingUtilities.invokeLater(() -> {
            if (i < 0 || i >= pumpPanels.length) return;
            if (busy) {
                pumpPanels[i].setBackground(new Color(144, 238, 144));
                pumpLabels[i].setText("Pump " + (i + 1) + ": " + carId + " (Busy)");
            } else {
                pumpPanels[i].setBackground(new Color(200, 230, 255));
                pumpLabels[i].setText("Pump " + (i + 1) + ": Free");
            }
        });
    }

    public synchronized void incrementProcessed() { processed++; updateStats(); }
    public synchronized void incrementArrived() { arrived++; updateStats(); }

    private void updateStats() {
        SwingUtilities.invokeLater(() ->
                statsLabel.setText("Arrived: " + arrived + " | Processed: " + processed)
        );
    }
}

// Main class
public class ServiceStation {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(CarWashGUI::new);
    }
}
