import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileSyncLogger {

    // Define absolute paths (update these paths as needed)
    private static final String SYNC_FOLDER = "/home/ubuntu/proj1/sync";  // e.g., "/home/ubuntu/sync"
    private static final String DATE_TIME_FILE = SYNC_FOLDER + "/date_time.dat";
    private static final String MEM_NAMES_FILE = SYNC_FOLDER + "/mem_names.dat";
    private static final String LOG_FILE = SYNC_FOLDER + "/log.dat";

    // Group member names (adjust or add names as needed)
    private static final String[] MEMBER_NAMES = {"Sampath", "Sridatta", "Tarun"};

    // Random number generator
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        // Create sync folder if it does not exist
        File folder = new File(SYNC_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // Log the start of the Java program
        logMessage("Start Java program");

        // Create the files (only once) if they do not exist
        createFileIfNotExists(DATE_TIME_FILE, "Create date_time.dat");
        createFileIfNotExists(MEM_NAMES_FILE, "Create mem_names.dat");
        // Log file (log.dat) is created implicitly when logging messages

        // Create an ExecutorService with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Thread to update date_time.dat
        executor.submit(() -> {
            while (true) {
                try {
                    // Append the current date and time
                    String currentTime = getCurrentTime();
                    appendToFile(DATE_TIME_FILE, currentTime);
                    logMessage("Append to date_time.dat: " + currentTime);

                    // Wait for a random interval (1, 3, or 5 minutes)
                    int waitMinutes = getRandomInterval();
                    logMessage("Waiting " + waitMinutes + " minutes before next date_time.dat update.");
                    Thread.sleep(waitMinutes * 60 * 1000); // Convert minutes to milliseconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        // Thread to update mem_names.dat
        executor.submit(() -> {
            while (true) {
                try {
                    // Choose a random member name and append it
                    String name = MEMBER_NAMES[RANDOM.nextInt(MEMBER_NAMES.length)];
                    appendToFile(MEM_NAMES_FILE, name);
                    logMessage("Append to mem_names.dat: " + name);

                    // Wait for a random interval (1, 3, or 5 minutes)
                    int waitMinutes = getRandomInterval();
                    logMessage("Waiting " + waitMinutes + " minutes before next mem_names.dat update.");
                    Thread.sleep(waitMinutes * 60 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });

        // The program runs indefinitely. In a real project, you might add shutdown logic.
    }

    // Helper method to create a file if it does not exist, and log its creation
    private static void createFileIfNotExists(String filePath, String creationLogMessage) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
                logMessage(creationLogMessage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Helper method to append content to a file
    private static void appendToFile(String filePath, String content) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(content);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to log a message (appends to log.dat and prints to console)
    private static void logMessage(String message) {
        String timeStampedMessage = getCurrentTime() + " " + message;
        System.out.println(timeStampedMessage); // Console output
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            bw.write(timeStampedMessage);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Returns the current date and time as a formatted string
    private static String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    // Returns a random interval: 1, 3, or 5 minutes
    private static int getRandomInterval() {
        int[] intervals = {1, 3, 5};
        return intervals[RANDOM.nextInt(intervals.length)];
    }
}

