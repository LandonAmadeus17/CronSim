package src.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Logs operations by the System, ProcessManager, Cron, and
 * Logger classes using BSD syslog protocol (RFC 3164):
 * <p>
 * &lt;&hairsp;PRI&hairsp;&gt; Mnth DD HH:MM:SS hostname tag[PID]: message body.
 * <p>
 * The logger is called by the System, ProcessManager, or Cron
 * classes and does not operate asynchronously.
 * <p>
 * Supports the following facilities: kern (0), syslog (5), and
 * cron (9).
 * <p>
 * Supports the following severities: info (6), notice (5),
 * debug (7), and err(3).
 * <p>
 * Supports the following tags: kernel and cron.
 * 
 * @author Landon Reeder
 * @version %I%, %G%
 * 
 */
public class Logger {
    // Lazy Initialization
    private static Logger theLogger;

    // Identifiers to be used in self-referential logging.
    private static String logHostname;
    private static String logTag = "kernel";
    private static int logPid;

    /**
     * Package-private constructor only to be accessed by the System
     * class. Several convenient identifiers are endowed by to the
     * Logger by the System.
     * 
     * @param pid the int indicating the process indentifier of the logger.
     * @param hostname the String serving as the label assigned to the logger.
     * Identical to the hostname of the system.
     */
    private Logger(String hostname, int pid) {
        logHostname = hostname;
        logPid = pid;
        writeLog(5, 5, logHostname, "kernel", logPid, "Logger initiated.");
    }

    /**
     * getInstance method for the Logger singleton.
     */
    static Logger getInstance() {
        // Check for hostname and PID attribution
        if (logHostname == null) {
            java.lang.System.out.println("Logger singleton could not be instantiated. Hostname does not exist.");
        } else if (logPid == 0) {
            java.lang.System.out.println("Logger singleton could not be instantiated. PID does not exist.")
            return null;
        } else {
            // Instantiate / Return Logger Singleton
            if (theLogger == null) theLogger = new Logger(logHostname, logPid);
            return theLogger;
        }
    }

    /**
     * Sets the hostname of the Logger.
     * 
     * @param hostname the String serving as the label assigned to the logger.
     * Identical to the hostname of the system.
     */
    void setHostname(String hostname) {
        logHostname = hostname;
    }

    /**
     * Sets the PID of the Logger.
     * 
     * @param pid the int indicating the process indentifier of the logger.
     */
    void setPID(int pid) {
        logPid = pid;
    }

    /**
     * Writes a log entry to applications.log or error.log using BSD syslog
     * protocol (RFC 3164).
     * <p>
     * If an IOException is encountered, the failure is written to stdout.
     * 
     * @param facility the int indicating the type of source generating the
     * syslog entry.
     * @param severity the int indicating the urgency of the syslog entry.
     * @param hostname the String serving as the label assigned to the device
     * generating the syslog entry.
     * @param tag the String indicating the type of service or program
     * generating the syslog entry.
     * @param pid the int indicating the process identifier of the process
     * generating the syslog entry.
     * @param messageBody the String which contains the message body of the
     * syslog entry.
     */
    void writeLog(int facility, int severity, String hostname, String tag, int pid, String messageBody) {
        // Assertions TBW

        // Get Date
        LocalDate localDate = LocalDate.now();
        Integer day = localDate.getDayOfMonth();
        String dd;
        if (day < 10) {
            dd = String.join("0", day.toString());
        } else {
            dd = day.toString();
        }
        String month = localDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.US);

        // Get Time
        String time = LocalTime.now().toString().substring(0,8);

        // Format Message
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<%d>%s %s %s %s ", facility * 8 + severity, month, dd, time, hostname));
        if (tag =="kernel") {
            sb.append(String.format("%s: %s", tag, messageBody));
        } else {
            sb.append(String.format("%s[%d]: %s\n", tag, pid, messageBody));
        }
        String logEntry = sb.toString();

        // Determine which log file to write to
        File logFile;
        if (severity == 3) logFile = new File(".\\logs\\error.log");
        else logFile = new File(".\\logs\\application.log");

        // Check if log file exists
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // Log Failure to stdout
            }
        }

        // Write to the log file
        FileWriter logWriter;
        try {
            // FileWriter in Append Mode
            logWriter = new FileWriter(logFile, true);

            try {
                logWriter.write(logEntry);
            } catch (IOException e) {
                java.lang.System.out.println("Failed to write to the appropriate log file.");
            }
            // Close FileWriter
            logWriter.close();

        } catch (IOException e) {
            java.lang.System.out.println("The appropriate log file failed to be opened or created");
        }
    }

    /**
     * Erases all data from application.log and error.log.
     * <p>
     * If an IOException is encountered, the failure is written to stdout.
     */
    void wipeLogs() {
        // Wipe application.log
        File appLog = new File(".\\logs\\application.log");
        FileWriter appWriter;

        try {
            appWriter = new FileWriter(appLog);

            try {
                appWriter.write("");
            } catch (IOException e) {
                java.lang.System.out.println("Failed to write to application.log.");
            }
            // Close FileWriter
            appWriter.close();

        } catch (IOException e) {
            java.lang.System.out.println("application.log could not be opened or created.");
        }

        // Wipe error.log
        File errorLog = new File(".\\logs\\error.log");
        FileWriter errorWriter;

        try {
            errorWriter = new FileWriter(errorLog);

            try {
                errorWriter.write("");
            } catch (IOException e) {
                java.lang.System.out.println("Failed to write to error.log.");
            }
            // Close FileWriter
            errorWriter.close();

        } catch (IOException e) {
            java.lang.System.out.println("error.log could not be opened or created.");
        }
    }

    /**
     * Triggers a log entry indicating that the logger service is being
     * terminated.
     * <p>
     * If an IOException is encountered, the failure is written to stdout.
     * 
     * @see Logger#writeLog
     */
    void close() {
        writeLog(5, 5, logHostname, logTag, logPid, "Logger terminating...");
    }
}
