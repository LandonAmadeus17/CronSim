package src.main;

public class System {
    private static final System SYSTEM = new System();
    private Logger logger;
    private ProcessManager pm;
    private Cron cron;
    private CLI cli;

    private System() {
        logger = new Logger();
        pm = new ProcessManager();
        cron = new Cron();
        cli = new CLI();
    }
    public static System getInstance() {
        return SYSTEM;
    }

    public static void main(String[] args) {
        System.getInstance();
    }
}