package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/** Driver class for Gitlet, the tiny version-control system.
 *  @author Dhivyaa N Mailvaganam
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */


    public static void main(String... args) {
        validArgs.add("init");
        validArgs.add("add");
        validArgs.add("commit");
        validArgs.add("rm");
        validArgs.add("log");
        validArgs.add("global-log");
        validArgs.add("find");
        validArgs.add("status");
        validArgs.add("checkout");
        validArgs.add("branch");
        validArgs.add("rm-branch");
        validArgs.add("reset");
        validArgs.add("merge");
        String[] arg = args;
        if (arg.length == 0) {
            Utils.message("Please enter a command.");
            System.exit(0);
        } else if (!validArgs.contains(arg[0])) {
            Utils.message("No command with that name exists.");
            System.exit(0);
        } else if (validArgs.contains(arg[0])) {
            if (arg[0].equals("init")) {
                if (initialized()) {
                    Utils.message("A Gitlet version-control system already "
                            + "exists in the current directory.");
                } else {
                    Repo.init();
                }
                System.exit(0);
            } else {
                if (!initialized()) {
                    Utils.message("Not in an initialized Gitlet directory.");
                } else {
                    String mainArg = args[0];
                    String[] rest = Arrays.copyOfRange(arg, 1, arg.length);
                    mainHelp(mainArg, rest);
                }
            }
        }
    }

    /**Command to help execute main.
     * @param args Arguments to main.
     * @param operands Operands to main.
     */
    public static void mainHelp(String args, String[] operands) {
        if (args.equals("add")) {
            if (opCheck(operands)) {
                Utils.message("Incorrect operands.");
                System.exit(0);
            } else {
                String fileName = operands[0];
                if (initialized()) {
                    Repo.add(fileName);
                }
            }
            System.exit(0);
        } else if (args.equals("commit")) {
            if (!(operands[0].trim().equals(""))) {
                Repo.commit(operands[0]);
            } else {
                Utils.message("Please enter a commit message.");
            }
            System.exit(0);
        } else if (args.equals("rm")) {
            Repo.rm(operands[0]);
            System.exit(0);
        } else if (args.equals("checkout")) {
            if (operands.length == 2) {
                Repo.checkoutF(operands[1]);
            } else if (operands.length == 3) {
                Repo.checkout(operands[0], operands[2]);
                System.exit(0);
            }
        } else if (args.equals("log")) {
            Repo.log();
        } else if (args.equals("global-log")) {
            Repo.globalLog();
        } else if (args.equals("find")) {
            if (opCheck(operands)) {
                Utils.message("Incorrect operands.");
            } else {
                Repo.find(operands[0]);
            }
        } else if (args.equals("branch")) {
            Repo.branch(operands[0]);
        } else if (args.equals("rm-branch")) {
            Repo.rmBranch(operands[0]);
        } else if (args.equals("status")) {
            Repo.status();
        }
    }

    /** Check if operand length is 0 for relevant commands.
     * @return Boolean to check if the operand is empty or not.
     * @param op Operand array.
     */
    public static boolean opCheck(String[] op) {
        return op.length == 0;
    }

    /**To check if .gitlet has been initialized.
     * @return Check if .gitlet has been initialized.
     */
    public static boolean initialized() {
        File mainFolder = new File(".gitlet");
        return mainFolder.exists();
    }

    /** ArrayList of valid arguments. */
    private static ArrayList<String> validArgs = new ArrayList<>();
}
