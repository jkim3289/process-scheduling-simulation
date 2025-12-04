import java.io.File;
import java.util.*;

class MemoryBlock {
    int start;
    int size;
    boolean isFree;

    public MemoryBlock(int start, int size) {
        this.start = start;
        this.size = size;
        this.isFree = true;
    }

    @Override
    public String toString() {
        return String.format("[%d-%d] (%s)", start, start + size - 1, isFree ? "Free" : "Used");
    }
}

public class Check {

    // ---------------- Memory Allocation Methods ----------------
    static MemoryBlock firstFit(List<MemoryBlock> memory, int size) {
        for (MemoryBlock block : memory) {
            if (block.isFree && block.size >= size) {
                block.isFree = false;
                return block;
            }
        }
        return null;
    }

    static MemoryBlock bestFit(List<MemoryBlock> memory, int size) {
        MemoryBlock best = null;
        for (MemoryBlock block : memory) {
            if (block.isFree && block.size >= size) {
                if (best == null || block.size < best.size) best = block;
            }
        }
        if (best != null) best.isFree = false;
        return best;
    }

    static MemoryBlock worstFit(List<MemoryBlock> memory, int size) {
        MemoryBlock worst = null;
        for (MemoryBlock block : memory) {
            if (block.isFree && block.size >= size) {
                if (worst == null || block.size > worst.size) worst = block;
            }
        }
        if (worst != null) worst.isFree = false;
        return worst;
    }

    // ---------------- Main Program ----------------
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Usage: java Scheduler <input_file>");
            return;
        }

        // --- Read process data from file ---
        List<Process> processes = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(args[0]))) {
            fileScanner.nextLine(); // skip header
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                int processID = Integer.parseInt(parts[0]);
                int arrival = Integer.parseInt(parts[1]);
                int burst = Integer.parseInt(parts[2]);
                int priority = Integer.parseInt(parts[3]);
                int memory = Integer.parseInt(parts[4]);
                processes.add(new Process(processID, arrival, burst, priority, memory));
            }
        }

        Scanner input = new Scanner(System.in);
        System.out.println("Select CPU Algorithm: 1=FCFS, 2=SJF");
        int cpuChoice = input.nextInt();

        System.out.println("Select Memory Allocation: 1=First-Fit, 2=Best-Fit, 3=Worst-Fit");
        int memChoice = input.nextInt();

        // --- Initialize memory blocks ---
        List<MemoryBlock> memory = new ArrayList<>();
        memory.add(new MemoryBlock(0, 100));
        memory.add(new MemoryBlock(100, 200));
        memory.add(new MemoryBlock(300, 300));
        memory.add(new MemoryBlock(600, 400));

        // --- Scheduling ---
        if (cpuChoice == 1) {
            System.out.println("\n--- FCFS ---");
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            int currentTime = 0;

            for (Process p : processes) {
                if (currentTime < p.arrivalTime) currentTime = p.arrivalTime;

                // Allocate memory
                MemoryBlock allocated = allocateMemory(memory, p.memoryRequired, memChoice);
                if (allocated == null) {
                    System.out.printf("Process %d could not be allocated (Not enough memory)\n", p.processID);
                    continue;
                } else {
                    System.out.printf("Process %d allocated at block %s\n", p.processID, allocated);
                }

                currentTime += p.burstTime;
                int tat = currentTime - p.arrivalTime;
                int wt = tat - p.burstTime;
                System.out.printf("PID=%d, WT=%d, TAT=%d\n", p.processID, wt, tat);

                // Free memory after process finishes
                allocated.isFree = true;
            }
        } else if (cpuChoice == 2) {
            System.out.println("\n--- SJF ---");
            List<Process> completed = new ArrayList<>();
            int currentTime = 0;

            while (completed.size() < processes.size()) {
                List<Process> readyQueue = new ArrayList<>();
                for (Process p : processes) {
                    if (p.arrivalTime <= currentTime && !completed.contains(p)) readyQueue.add(p);
                }

                if (readyQueue.isEmpty()) {
                    currentTime++;
                    continue;
                }

                readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
                Process p = readyQueue.get(0);

                // Allocate memory
                MemoryBlock allocated = allocateMemory(memory, p.memoryRequired, memChoice);
                if (allocated == null) {
                    System.out.printf("Process %d could not be allocated (Not enough memory)\n", p.processID);
                    completed.add(p); // mark as done to avoid infinite loop
                    continue;
                } else {
                    System.out.printf("Process %d allocated at block %s\n", p.processID, allocated);
                }

                currentTime += p.burstTime;
                int tat = currentTime - p.arrivalTime;
                int wt = tat - p.burstTime;
                System.out.printf("PID=%d, WT=%d, TAT=%d\n", p.processID, wt, tat);

                completed.add(p);
                allocated.isFree = true;
            }
        }

        input.close();
    }

    // ---------------- Memory Allocation Dispatcher ----------------
    static MemoryBlock allocateMemory(List<MemoryBlock> memory, int size, int choice) {
        switch (choice) {
            case 1: return firstFit(memory, size);
            case 2: return bestFit(memory, size);
            case 3: return worstFit(memory, size);
            default: return null;
        }
    }
}
