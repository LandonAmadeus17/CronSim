# Cron Simulation
This educational project simulates a user's interaction with a job scheduler via a command-line interface. This particular project was chosen to practice automatic manipulation of text files, programming keywords, unit testing, and designing a logger while improving understanding of how an operating sytem functions at a low-level. After some progress was made, it was realized that the program would need to leverage multithreading in order to properly manage CPU usage and run scheduled jobs at the specified time.
This project has also served as an exercise in writing a formal README.md file as well as composing consistent and professional Javadoc comments.

## How to Run
The System class must be started by running the main method contained in that class. This will initialize the Logger, ProcessManager, Cron, and CommandLineInterface classes. From this point on, the CommandLineInterface class will communicate with the user via the terminal.

## Available Commands
The command-line interface supports a myriad of custom but inspired commands: crontab add, crontab remove, crontab clear/reset, crontab list, systemctl status, journalctl, shutdown, and help.

## Class Structure
The CommandLineInterface class writes to a crontab text file, which is inspected by the Cron class at regular intervals. The Cron instantiates the ready Job classes, sending them to the ProcessManager class, which monitors CPU usage by communicating with the System class. The ProcessManager runs processes when the resources are available, which increments the System class's CPU usage, which passively stabilizes. All the while, the Logger class maintains a set of log files that record actions taken by the System, ProcessManager, Cron, and CommandLineInterface classes. The Logger, ProcessManager, Cron, and CommandLineInterface classes are all instantiated in the System class via composition.

## License
This project is licensed under the GNU General Public License Version 3. See the LICENSE file for more details.