import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Process Scheduling Simulator
 *
 * This program simulates CPU scheduling algorithms (FCFS and SJF) and displays
 * a Gantt chart along with performance metrics (Waiting Time and Turnaround Time).
 *
 * Supports:
 * - FCFS (First-Come, First-Served): Non-preemptive scheduling based on arrival time
 * - SJF (Shortest Job First): Non-preemptive scheduling based on burst time
 */
public class App {
    /**
     * Main entry point for the process scheduling simulator.
     * Prompts user to select scheduling algorithm and test file,
     * then executes the selected algorithm and displays results.
     *
     * @param args Command line arguments (not used)
     * @throws Exception If file I/O errors occur
     */
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Prompt user to select scheduling algorithm
        System.out.println("Select scheduling algorithm:");
        System.out.println("1. FCFS (First-Come, First-Served)");
        System.out.println("2. SJF (Shortest Job First)");
        System.out.print("Enter your choice (1 or 2): ");
        int algorithmChoice = scanner.nextInt();

        // Prompt user to select test file containing process data
        System.out.println("\nSelect test file:");
        System.out.println("1. processes.txt");
        System.out.println("2. test1_simple.txt");
        System.out.println("3. test2_same_arrival.txt");
        System.out.println("4. test3_staggered.txt");
        System.out.println("5. test4_gaps.txt");
        System.out.println("6. test5_edge_case.txt");
        System.out.print("Enter your choice (1-6): ");
        int fileChoice = scanner.nextInt();

        // Map user's file choice to actual file path
        String[] testFiles = {
            "../resources/processes.txt",
            "../resources/test1_simple.txt",
            "../resources/test2_same_arrival.txt",
            "../resources/test3_staggered.txt",
            "../resources/test4_gaps.txt",
            "../resources/test5_edge_case.txt"
        };

        // Validate user input
        if (fileChoice < 1 || fileChoice > 6) {
            System.out.println("Invalid file choice. Exiting.");
            scanner.close();
            return;
        }

        if (algorithmChoice < 1 || algorithmChoice > 2) {
            System.out.println("Invalid algorithm choice. Exiting.");
            scanner.close();
            return;
        }

        String selectedFile = testFiles[fileChoice - 1];

        // Read process data from selected file
        // File format: Each line contains space-separated values:
        // ProcessID ArrivalTime BurstTime MemoryRequired
        List<Process> processList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
        try {
            // Skip header line
            br.readLine();

            // Read each process line
            String line = br.readLine();
            while (line != null) {
                String[] w = (line.toString()).split(" ");
                Process newProcess;
                // Create process: processID, arrivalTime, burstTime, memoryRequired
                newProcess = new Process(Integer.parseInt(w[0]), Integer.parseInt(w[1]), Integer.parseInt(w[2]), Integer.parseInt(w[3]));
                processList.add(newProcess);
                line = br.readLine();
            }
        } finally {
            br.close();
            scanner.close();
        }

        // Execute selected scheduling algorithm and display results
        System.out.println("\n" + "=".repeat(60));
        if (algorithmChoice == 1) {
            System.out.println("Running FCFS Scheduling on " + selectedFile);
            System.out.println("=".repeat(60) + "\n");
            fcfs(processList);
        } else {
            System.out.println("Running SJF Scheduling on " + selectedFile);
            System.out.println("=".repeat(60) + "\n");
            sjf(processList);
        }
    }

    /**
     * First-Come, First-Served (FCFS) Scheduling Algorithm
     *
     * Non-preemptive scheduling where processes are executed in order of arrival.
     * The process that arrives first is executed first.
     *
     * Algorithm:
     * 1. Sort processes by arrival time
     * 2. Execute each process in order
     * 3. If CPU is idle when a process arrives, start immediately
     * 4. Otherwise, process waits until CPU is free
     *
     * @param processList List of processes to schedule
     */
    public static void fcfs(List<Process> processList) {
        // Create a copy to avoid modifying original list
        List<Process> temp = new ArrayList<>(processList);
        // Sort processes by arrival time (earliest first)
        temp.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
        List<Process> result = new ArrayList<>(temp);

        int currTime = 0;
        for (Process p : result) {
            // If CPU is idle (current time < arrival time), jump to arrival time
            if (p.arrivalTime > currTime) {
                currTime = p.arrivalTime;
            }
            // Set the start time for this process
            p.startTime = currTime;
            // Update current time after process completes
            currTime += p.burstTime;
        }

        // Display Gantt chart and performance metrics
        displayGanttChart(result);
    }

    /**
     * Shortest Job First (SJF) Scheduling Algorithm
     *
     * Non-preemptive scheduling where the process with the shortest burst time
     * is selected from the ready queue. Minimizes average waiting time.
     *
     * Algorithm:
     * 1. Sort processes by arrival time initially
     * 2. Use a priority queue (min-heap) to select shortest job from ready processes
     * 3. At each time point, add all newly arrived processes to the queue
     * 4. Execute the process with minimum burst time
     * 5. If no process is ready, advance time to next arrival
     *
     * @param processList List of processes to schedule
     */
    public static void sjf(List<Process> processList) {
        // Create a copy and sort by arrival time
        List<Process> temp = new ArrayList<>(processList);
        List<Process> result = new ArrayList<>();
        temp.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));

        // Priority queue to select process with shortest burst time
        PriorityQueue<Process> pq = new PriorityQueue<Process>((a,b) -> Integer.compare(a.burstTime,b.burstTime));
        int currTime = 0;

        // Continue until all processes are scheduled
        while (!temp.isEmpty() || !pq.isEmpty()) {
            // Add all processes that have arrived by current time to ready queue
            while (!temp.isEmpty() && temp.get(0).arrivalTime <= currTime) {
                pq.add(temp.remove(0));
            }

            if (!pq.isEmpty()) {
                // Select process with shortest burst time from ready queue
                Process nextProcess = pq.poll();
                nextProcess.startTime = currTime;
                result.add(nextProcess);
                // Update time after process completes
                currTime += nextProcess.burstTime;
            } else {
                // No process ready, advance time to next arrival
                currTime = temp.get(0).arrivalTime;
            }
        }

        // Display Gantt chart and performance metrics
        displayGanttChart(result);
    }

    // public static void printProcessList(List<Process> processList) {
    //     for (Process x : processList) {
    //         System.out.println(x.processID);
    //         System.out.println(x.arrivalTime);
    //         System.out.println(x.burstTime);
    //         System.out.println(x.memoryRequired);
    //         System.out.println();
    //     }
    //     System.out.println();
    // }

    /**
     * Display Gantt Chart and Performance Metrics
     *
     * Creates a visual Gantt chart showing process execution timeline,
     * and calculates performance metrics for each process.
     *
     * Displays:
     * - Gantt chart with process execution blocks and idle times
     * - Timeline with start/end times
     * - Per-process details (PID, arrival, burst, start, finish, WT, TAT)
     * - Average Waiting Time and Turnaround Time
     *
     * Performance Metrics:
     * - Waiting Time (WT) = Turnaround Time - Burst Time
     * - Turnaround Time (TAT) = Finish Time - Arrival Time
     *
     * @param processList List of scheduled processes with start times set
     */
    public static void displayGanttChart(List<Process> processList) {
        StringBuilder sbProcesses = new StringBuilder();
        List<Integer> barPositions = new ArrayList<>();
        List<Integer> times = new ArrayList<>();

        int currTime = 0;
        // Record initial bar position and time 0
        sbProcesses.append("|");
        barPositions.add(sbProcesses.length() - 1);
        times.add(currTime);

        // Calculate scale for visual width based on longest burst time
        int maxBurst = processList.stream().mapToInt(p -> p.burstTime).max().orElse(1);
        int maxWidth = 20;

        // Build Gantt chart process blocks
        for (Process p : processList) {
            // If there's a gap before this process starts, show idle time
            if (p.startTime > currTime) {
                int gapTime = p.startTime - currTime;
                int gapWidth = Math.max(5, gapTime * maxWidth / maxBurst);
                String gapLabel = "idle";
                int gapPadding = (gapWidth - gapLabel.length()) / 2;
                // Add left padding for idle block
                sbProcesses.append(" ".repeat(Math.max(0, gapPadding)));
                sbProcesses.append(gapLabel);
                // Add right padding for idle block
                sbProcesses.append(" ".repeat(Math.max(0, gapWidth - gapPadding - gapLabel.length())));
                sbProcesses.append("|");
                barPositions.add(sbProcesses.length() - 1);
                currTime = p.startTime;
                times.add(currTime);
            }

            // Calculate width for this process block
            int width = Math.max(5, p.burstTime * maxWidth / maxBurst);
            String label = "p" + p.processID;
            int padding = (width - label.length()) / 2;
            // Add left padding
            sbProcesses.append(" ".repeat(Math.max(0, padding)));
            // Add process label
            sbProcesses.append(label);
            // Add right padding
            sbProcesses.append(" ".repeat(Math.max(0, width - padding - label.length())));
            // Add closing bar for this process
            sbProcesses.append("|");
            barPositions.add(sbProcesses.length() - 1);

            // Update time after this process finishes
            currTime += p.burstTime;
            times.add(currTime);
        }

        // Create timeline string with same length as process line
        StringBuilder sbTimes = new StringBuilder(" ".repeat(sbProcesses.length()));

        // Place each numeric time value at its corresponding bar position
        for (int i = 0; i < barPositions.size(); i++) {
            String num = String.valueOf(times.get(i));
            int pos = barPositions.get(i);

            // If number would extend past current length, extend both strings
            int needed = pos + num.length();
            if (needed > sbTimes.length()) {
                int extra = needed - sbTimes.length();
                sbTimes.append(" ".repeat(extra));
                sbProcesses.append(" ".repeat(extra));
            }

            // Overwrite spaces at target position with the time value
            sbTimes.replace(pos, pos + num.length(), num);
        }

        // Print Gantt chart
        System.out.println(sbProcesses.toString());
        System.out.println(sbTimes.toString());
        System.out.println();

        // Calculate and display performance metrics for each process
        double totalWT = 0;
        double totalTAT = 0;

        System.out.println("Process Details:");
        System.out.println("PID\tArrival\tBurst\tStart\tFinish\tWT\tTAT");

        for (Process p : processList) {
            // Calculate metrics
            int finishTime = p.startTime + p.burstTime;
            int turnaroundTime = finishTime - p.arrivalTime;  // Total time in system
            int waitingTime = turnaroundTime - p.burstTime;    // Time spent waiting

            totalWT += waitingTime;
            totalTAT += turnaroundTime;

            // Print process details
            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                p.processID, p.arrivalTime, p.burstTime,
                p.startTime, finishTime, waitingTime, turnaroundTime);
        }

        // Print average metrics
        System.out.println();
        System.out.printf("Average Waiting Time (WT): %.2f\n", totalWT / processList.size());
        System.out.printf("Average Turnaround Time (TAT): %.2f\n", totalTAT / processList.size());
        System.out.println();
    }
    
}