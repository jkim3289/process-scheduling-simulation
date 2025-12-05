import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        // Prompt for scheduling algorithm
        System.out.println("Select scheduling algorithm:");
        System.out.println("1. FCFS (First-Come, First-Served)");
        System.out.println("2. SJF (Shortest Job First)");
        System.out.print("Enter your choice (1 or 2): ");
        int algorithmChoice = scanner.nextInt();

        // Prompt for test file
        System.out.println("\nSelect test file:");
        System.out.println("1. processes.txt");
        System.out.println("2. test1_simple.txt");
        System.out.println("3. test2_same_arrival.txt");
        System.out.println("4. test3_staggered.txt");
        System.out.println("5. test4_gaps.txt");
        System.out.println("6. test5_edge_case.txt");
        System.out.print("Enter your choice (1-6): ");
        int fileChoice = scanner.nextInt();

        // Map file choice to filename
        String[] testFiles = {
            "../resources/processes.txt",
            "../resources/test1_simple.txt",
            "../resources/test2_same_arrival.txt",
            "../resources/test3_staggered.txt",
            "../resources/test4_gaps.txt",
            "../resources/test5_edge_case.txt"
        };

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

        // Read processes from selected file
        List<Process> processList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(selectedFile));
        try {
            // Skip first line
            br.readLine();

            String line = br.readLine();
            while (line != null) {
                String[] w = (line.toString()).split(" ");
                Process newProcess;
                newProcess = new Process(Integer.parseInt(w[0]), Integer.parseInt(w[1]), Integer.parseInt(w[2]), Integer.parseInt(w[3]));
                processList.add(newProcess);
                line = br.readLine();
            }
        } finally {
            br.close();
            scanner.close();
        }

        // Run selected algorithm
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

    // First-Come, First-Served (FCFS)
    public static void fcfs(List<Process> processList) {
        List<Process> temp = new ArrayList<>(processList);
        temp.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
        List<Process> result = new ArrayList<>(temp);

        int currTime = 0;
        for (Process p : result) {
            if (p.arrivalTime > currTime) {
                currTime = p.arrivalTime;
            }
            p.startTime = currTime;
            currTime += p.burstTime;
        }

        displayGanttChart(result);
    }

    // Shortest Job First (SJF)
    public static void sjf(List<Process> processList) {
        List<Process> temp = new ArrayList<>(processList);
        List<Process> result = new ArrayList<>();
        temp.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
        PriorityQueue<Process> pq = new PriorityQueue<Process>((a,b) -> Integer.compare(a.burstTime,b.burstTime));
        int currTime = 0;
        while (!temp.isEmpty() || !pq.isEmpty()) {
            while (!temp.isEmpty() && temp.get(0).arrivalTime <= currTime) {
                pq.add(temp.remove(0));
            }
            if (!pq.isEmpty()) {
                Process nextProcess = pq.poll();
                nextProcess.startTime = currTime;
                result.add(nextProcess);
                currTime += nextProcess.burstTime;
            } else {
                currTime = temp.get(0).arrivalTime;
            }
        }
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

    public static void displayGanttChart(List<Process> processList) {
        StringBuilder sbProcesses = new StringBuilder();
        List<Integer> barPositions = new ArrayList<>();
        List<Integer> times = new ArrayList<>();

        int currTime = 0;
        // record initial bar and time 0
        sbProcesses.append("|");
        barPositions.add(sbProcesses.length() - 1);
        times.add(currTime);

        // scale for visual width
        int maxBurst = processList.stream().mapToInt(p -> p.burstTime).max().orElse(1);
        int maxWidth = 20;

        for (Process p : processList) {
            // If there's a gap before this process starts, show idle time
            if (p.startTime > currTime) {
                int gapTime = p.startTime - currTime;
                int gapWidth = Math.max(5, gapTime * maxWidth / maxBurst);
                String gapLabel = "idle";
                int gapPadding = (gapWidth - gapLabel.length()) / 2;
                sbProcesses.append(" ".repeat(Math.max(0, gapPadding)));
                sbProcesses.append(gapLabel);
                sbProcesses.append(" ".repeat(Math.max(0, gapWidth - gapPadding - gapLabel.length())));
                sbProcesses.append("|");
                barPositions.add(sbProcesses.length() - 1);
                currTime = p.startTime;
                times.add(currTime);
            }

            int width = Math.max(5, p.burstTime * maxWidth / maxBurst);
            String label = "p" + p.processID;
            int padding = (width - label.length()) / 2;
            // left padding
            sbProcesses.append(" ".repeat(Math.max(0, padding)));
            // the label
            sbProcesses.append(label);
            // right padding
            sbProcesses.append(" ".repeat(Math.max(0, width - padding - label.length())));
            // closing bar for this process
            sbProcesses.append("|");
            barPositions.add(sbProcesses.length() - 1);

            // update time after this process finishes
            currTime += p.burstTime;
            times.add(currTime);
        }

        // Make times line same length as process line (fill with spaces)
        StringBuilder sbTimes = new StringBuilder(" ".repeat(sbProcesses.length()));

        // Place each numeric time so it starts exactly under its bar index
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

            // overwrite the spaces at the target position with the number
            sbTimes.replace(pos, pos + num.length(), num);
        }

        System.out.println(sbProcesses.toString());
        System.out.println(sbTimes.toString());
        System.out.println();

        // Calculate and display WT, TAT for each process
        double totalWT = 0;
        double totalTAT = 0;

        System.out.println("Process Details:");
        System.out.println("PID\tArrival\tBurst\tStart\tFinish\tWT\tTAT");

        for (Process p : processList) {
            int finishTime = p.startTime + p.burstTime;
            int turnaroundTime = finishTime - p.arrivalTime;
            int waitingTime = turnaroundTime - p.burstTime;

            totalWT += waitingTime;
            totalTAT += turnaroundTime;

            System.out.printf("%d\t%d\t%d\t%d\t%d\t%d\t%d\n",
                p.processID, p.arrivalTime, p.burstTime,
                p.startTime, finishTime, waitingTime, turnaroundTime);
        }

        System.out.println();
        System.out.printf("Average Waiting Time (WT): %.2f\n", totalWT / processList.size());
        System.out.printf("Average Turnaround Time (TAT): %.2f\n", totalTAT / processList.size());
        System.out.println();
    }
    
}