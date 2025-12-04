import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.ArrayList;

public class App {
    public static void main(String[] args) throws Exception {
        List<Process> processList = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader("../resources/processes.txt"));
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
        }
        fcfs(processList);
        displayGanttChart(processList);
    }

    // First-Come, First-Served (FCFS) 
    public static void fcfs(List<Process> processList) {
        processList.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
    }

    // Shortest Job First (SJF)
    
    
    public static void sjf(List<Process> processList) {
        processList.sort((p1, p2) -> Integer.compare(p1.arrivalTime, p2.arrivalTime));
        // if duplicate arrival time -> start with a process with short burst-time
        //      sort arrival time duplicates - burst-time
        //      compare (burst time and next start time) - process with shorter one
        // keep track of curr time as you move
        // update start time/burst-time when you meet next start-time
    }

    public static void printProcessList(List<Process> processList) {
        for (Process x : processList) {
            System.out.println(x.processID);
            System.out.println(x.arrivalTime);
            System.out.println(x.burstTime);
            System.out.println(x.memoryRequired);
            System.out.println();
        }
        System.out.println();
    }

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
    }
    
}