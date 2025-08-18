package src.main;

import java.util.Random;

/**
 * Serves as the foundation of the simulation, initializing the
 * Logger, ProcessManager, Cron, and CLI classes and interacting
 * most closely with the ProcessManager.
 * 
 * @author Landon Reeder
 * @version %I%, %G%
 * 
 * @see Logger
 * @see ProcessManager
 * @see Cron
 * @see Job
 * @see CLI
 */
public class System {
    // Eager initialization
    private static final System SYSTEM = new System();

    private static final Logger LOGGER = new Logger();
    private static final ProcessManager PROCESSMANAGER = new ProcessManager();
    private static final Cron CRON = new Cron();
    private static final CommandLineInterface COMMANDLINEINTERFACE = new CommandLineInterface();
    private static final CpuManager CPUMANAGER = SYSTEM.new CpuManager();

    /**
     * Initializes the System and runs the CPU manager.
     */
    private System() {
        // log startup
    }

    /**
     * Nested CpuManager extending the Thread class, enabling
     * the cpuUsage member of the System class to stabilize
     * over time.
     */
    private class CpuManager extends Thread {
        /**
         * The double expressing the current CPU usage
         * by the System as a positive double less than 1.
         * @see #equilibrium
         */
        private double cpuUsage;

        /**
         * The double specifying the artifical, approximate cpuUsage
         * at idle.
         * @see #cpuUsage
         */
        private double equilibrium = 0.10;

        /**
         * Private constructor only to be accessed by the
         * System class. Sets cpuUsage to the equilibrium.
         */
        private CpuManager() {
            cpuUsage = equilibrium;
        }

        public synchronized double getCpuUsage() {
            return cpuUsage;
        }

        /**
         * Increments the cpuUsage by the addend argument.
         * 
         * @param addend the double specifying how much the cpuUsage
         * is to increase.
         */
        public synchronized void incrementCpuUsage(double addend) {
            cpuUsage += addend;
        }

        /**
         * Determines the bias (whether the current cpuUsage
         * is above or below the equilibrium value). The cpuUsage
         * then undergoes a random manipulation that is biased
         * toward equilibrium every 1000 milliseconds.
         */
        @Override
        public void run() {
            double bias = Math.signum(equilibrium - cpuUsage);

            Random rand = new Random();
            while (true) {
                incrementCpuUsage((rand.nextInt(3) + bias - 1) / 20.0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    /**
     * Provides access to the System singleton.
     * 
     * @return the System singleton.
     */
    public static System getInstance() {
        return SYSTEM;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public ProcessManager getProcessManager() {
        return PROCESSMANAGER;
    }

    public Cron getCron() {
        return CRON;
    }

    public CommandLineInterface getCLI() {
        return COMMANDLINEINTERFACE;
    }

    public double getCpuUsage() {
        return CPUMANAGER.getCpuUsage();
    }

    /**
     * Increments the cpuUsage by the addend argument.
     * 
     * @param addend the double specifying how much cpuUsage
     * is to be incremented.
     * @see CpuManager#incrementCpuUsage(double)
     */
    public void incrementCpuUsage(double addend) {
        incrementCpuUsage(addend);
    }

    /**
     * Calls the run method of the nested CPU manager class, which
     * causes the cpuUsage to take a biased random walk toward
     * equilibrium or idle usage.
     * 
     * @see CpuManager#run()
     * @see CpuManager#cpuUsage
     * @see CpuManager#equilibrium
     */
    public void startCpuManager() {
        CPUMANAGER.start();
    }

    /**
     * Logs the shutdown process, shuts down the logger and other
     * classes, and returns exit code 0.
     */
    public void shutdown() {
        // Log Shutdown
        CPUMANAGER.interrupt();
        // Shut down other classes
        java.lang.System.exit(0);
    }

    /**
     * Begins the simulation 
     * 
     * @param args the custom commands to be passed to the CLI,
     * namely crontab add, crontab remove, crontab clear/reset,
     * crontab list, systemctl status, journalctl, shutdown, and
     * help.
     */
    public static void main(String[] args) {
        System system = System.getInstance();
    }
}