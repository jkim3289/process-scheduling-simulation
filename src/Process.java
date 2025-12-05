public class Process {
    int processID;
    int arrivalTime;
    int burstTime;
    int memoryRequired;
    int startTime;

    public Process(int processID, int arrivalTime, int burstTime, int memoryRequired) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.memoryRequired = memoryRequired;
        this.startTime = 0;
    }
}
