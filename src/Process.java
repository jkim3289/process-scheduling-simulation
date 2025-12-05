/**
 * Process Class
 *
 * Represents a process in the scheduling simulation.
 * Contains all necessary information for CPU scheduling algorithms.
 */
public class Process {
    // Unique identifier for the process
    int processID;

    // Time when the process arrives in the ready queue
    int arrivalTime;

    // Total CPU time required for process completion
    int burstTime;

    // Amount of memory required by the process (not used in current scheduling)
    int memoryRequired;

    // Time when the process starts execution (set by scheduling algorithm)
    int startTime;

    /**
     * Constructor for Process
     *
     *
     * @param processID Unique identifier for the process
     * @param arrivalTime Time when process arrives in the system
     * @param burstTime CPU time required for execution
     * @param memoryRequired Memory required by the process
     */
    public Process(int processID, int arrivalTime, int burstTime, int memoryRequired) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.memoryRequired = memoryRequired;
        this.startTime = 0;  // Will be set by scheduling algorithm
    }
}
