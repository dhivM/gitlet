package gitlet;

import java.io.Serializable;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;

/**The class that implements instructions from Main.
 * @author Dhivyaa N Mailvaganam
 */

public class Repo implements Serializable {

    /** Initialize a repo. */
    public static void init() {
        File mainFolder = Utils.join(".gitlet");
        mainFolder.mkdir();
        File stagingArea = Utils.join(mainFolder, "stagingarea");
        stagingArea.mkdir();
        File toAdd = Utils.join(stagingArea, "toAdd");
        toAdd.mkdir();
        File toRemove = Utils.join(stagingArea, "toRemove");
        toRemove.mkdir();
        File commits = Utils.join(mainFolder, "commits");
        commits.mkdir();
        File blobs = Utils.join(mainFolder, "blobs");
        blobs.mkdir();
        File branches = Utils.join(mainFolder, "branches");
        branches.mkdir();
        Commit initialCommit = new Commit("initial commit", null, null);
        String uId = initialCommit.getId();
        File firstCommit = Utils.join(".gitlet", "commits", uId);
        Utils.writeContents(firstCommit, Utils.serialize(initialCommit));
        File firstHead = Utils.join(".gitlet", "head");
        Utils.writeContents(firstHead, Utils.serialize(initialCommit));
        head = "master";
        File firstBranch = Utils.join(branches, Utils.sha1("master"));
        Utils.writeContents(firstBranch, Utils.serialize(initialCommit));
        File rBranch = Utils.join(".gitlet", "ref");
        Utils.writeContents(rBranch, "master");
    }

    /** Add a file.
     * @param file Filename to be added.
     */
    public static void add(String file) {
        File aFile = new File(file);
        if (!aFile.exists()) {
            Utils.message("File does not exist.");
        } else {
            File stageExist = Utils.join(".gitlet", "stagingarea",
                    "toAdd", file);
            String fileContent = Utils.sha1(Utils.readContentsAsString(aFile));
            String actualAdd = Utils.readContentsAsString(aFile);
            if (!stageExist.exists()) {
                Staging.add(file, actualAdd);
            } else {
                Staging.replace(file, actualAdd);
            }
            Commit headCommit = giveHead();
            HashMap commitFiles = headCommit.giveFiles();
            if (commitFiles != null) {
                if (commitFiles.containsKey(file)) {
                    if (commitFiles.get(file).equals(fileContent)) {
                        Staging.remove(file);
                    }
                }
            }
            File toRem = Utils.join(".gitlet", "stagingarea", "toRemove", file);
            if (toRem.exists()) {
                toRem.delete();
            }
        }
    }

    /** Commit a file.
     * @param message Message accompanying a commit.
     */
    public static void commit(String message) {
        Commit headCom = giveHead();
        HashMap<String, String> prevFiles = headCom.giveFiles();
        HashMap<String, String> catchFiles = new HashMap<>();
        boolean k = false;
        File addDir = Utils.join(".gitlet", "stagingarea", "toAdd");
        File rmDir = Utils.join(".gitlet", "stagingarea", "toRemove");
        File[] addFiles = addDir.listFiles();
        File[] removeFiles = rmDir.listFiles();
        if (removeFiles !=  null && removeFiles.length != 0) {
            remArr.addAll(Arrays.asList(removeFiles));
        }
        if (addFiles != null && addFiles.length != 0) {
            for (File each : addFiles) {
                if (prevFiles == null) {
                    catchFiles.put(each.getName(),
                            Utils.sha1(Utils.readContentsAsString(each)));
                    String sha = Utils.sha1(Utils.readContentsAsString(each));
                    File toAdd = Utils.join(".gitlet", "blobs", sha);
                    Utils.writeContents(toAdd, Utils.readContents(each));
                    k = true;
                } else {
                    prevFiles.put(each.getName(),
                            Utils.sha1(Utils.readContentsAsString(each)));
                    String sha = Utils.sha1(Utils.readContentsAsString(each));
                    File toAdd = Utils.join(".gitlet", "blobs", sha);
                    Utils.writeContents(toAdd, Utils.readContents(each));
                }
            }
            if (!(remArr.size() == 0)) {
                for (File eachR : remArr) {
                    if (k) {
                        catchFiles.remove(eachR.getName());
                    } else {
                        prevFiles.remove(eachR.getName());
                    }
                }
            }
            Commit next;
            if (k) {
                next = new Commit(message, catchFiles, headCom.getId());
            } else {
                next = new Commit(message, prevFiles, headCom.getId());
            }
            Staging.commitAdd(next.getId(), next);
            Staging.newHead(next);
            Staging.clearStage();
            remArr.clear();
        } else {
            Utils.message("No changes added to the commit.");
        }
    }

    /** Remove a file.
     * @param file File to be removed.
     */
    public static void rm(String file) {
        Commit comHead = giveHead();
        HashMap<String, String> files = comHead.giveFiles();
        File curFile = Utils.join(".gitlet", "stagingarea", file);
        if (files != null && !files.containsKey(file) && !curFile.exists()) {
            Utils.message("No reason to remove this file.");
        } else {
            Staging.rm(file);
            if (files != null && files.containsKey(file)) {
                File delFile = new File(file);
                Utils.restrictedDelete(delFile);
            }
        }
    }

    /** Checkout a file.
     * @param filename Filename to be checked out from most recent commit.
     */
    public static void checkoutF(String filename) {
        Commit headCom = giveHead();
        HashMap<String, String> files = headCom.giveFiles();
        String toWrite;
        if (!files.containsKey(filename)) {
            Utils.message("File does not exist in that commit.");
        } else {
            String shaBlob = files.get(filename);
            File accBlob = Utils.join(".gitlet", "blobs", shaBlob);
            toWrite = Utils.readContentsAsString(accBlob);
            File repFile = new File(filename);
            Utils.writeContents(repFile, toWrite);
        }
    }


    /** Status right now. */
    public static void status() {
        System.out.println("=== Branches ===");
        File branch = Utils.join(".gitlet", "branches");
        File bRef = Utils.join(".gitlet", "ref");
        String[] allB = branch.list();
        String curB = Utils.readContentsAsString(bRef);
        for (String each: allB) {
            if (each != curB) {
                System.out.println(each);
            } else {
                System.out.println("*");
                System.out.print(each);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        File stageF = Utils.join(".gitlet", "stagingarea", "toAdd");
        String[] allStage = stageF.list();
        if (allStage != null && allStage.length != 0) {
            for (String each: allStage) {
                System.out.println(each);
            }
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        File stageR = Utils.join(".gitlet", "stagingarea", "toRemove");
        String[] allStageR = stageR.list();
        if (allStageR != null && allStageR.length != 0) {
            for (String each: allStageR) {
                System.out.println(each);
            }
        }
        System.out.println();
    }

    /** Checkout a file from a commit.
     * @param comId Commit ID to be checked out from.
     * @param filename File to be checked out from the commit.
     */
    public static void checkout(String comId, String filename) {
        File idFile = Utils.join(".gitlet", "commits", comId);
        String toWrite;
        if (!idFile.exists()) {
            Utils.message("No commit with that id exists.");
        } else {
            Commit toCheck = Utils.readObject(idFile, Commit.class);
            HashMap<String, String> comFile = toCheck.giveFiles();
            if (!comFile.containsKey(filename)) {
                Utils.message("File does not exist in that commit.");
            } else {
                String shaBlob = comFile.get(filename);
                File accBlob = Utils.join(".gitlet", "blobs", shaBlob);
                toWrite = Utils.readContentsAsString(accBlob);
                File repFile = new File(filename);
                Utils.writeContents(repFile, toWrite);
            }
        }
    }

    /**Give log of commits. */
    public static void log() {
        Commit headCom = giveHead();
        String parent = headCom.getParent();
        boolean k = true;
        while (k) {
            printLog(headCom.getId(), headCom.getDate(), headCom.getMsg());
            File newHead = Utils.join(".gitlet", "commits", parent);
            headCom = Utils.readObject(newHead, Commit.class);
            parent = headCom.getParent();
            if (parent == null) {
                printLog(headCom.getId(), headCom.getDate(), headCom.getMsg());
                k = false;
            }
        }
    }

    /** Global log command. */
    public static void globalLog() {
        File comDir = Utils.join(".gitlet", "commits");
        File[] allCom = comDir.listFiles();
        for (File each: allCom) {
            Commit eachC = Utils.readObject(each, Commit.class);
            String eachM = eachC.getMsg();
            String eId = each.getName();
            String eDate = eachC.getDate();
            printLog(eId, eDate, eachM);
        }
    }



    /**Print logs.
     * @param date Date commit was created.
     * @param message Message accompanying a commit.
     * @param id Commit id.
     */
    public static void printLog(String id, String date, String message) {
        System.out.println("===");
        System.out.println("commit " + id);
        System.out.println("Date: " + date);
        System.out.println(message);
        System.out.println();
    }

    /**Runs the find command.
     * @param msg Commit message to be found.
     */
    public static void find(String msg) {
        File comFiles = Utils.join(".gitlet", "commits");
        File[] allFiles = comFiles.listFiles();
        boolean m = true;
        for (int i = 0; i < allFiles.length; i += 1) {
            Commit toCheck = Utils.readObject(allFiles[i], Commit.class);
            if (toCheck.getMsg().equals(msg)) {
                System.out.println(toCheck.getId());
                m = false;
            }
            if (m) {
                Utils.message("Found no commit with that message.");
                System.exit(0);
            }
        }
    }

    /**Runs branch command.
     * @param message Branch message.
     */
    public static void branch(String message) {
        File checkBranch = Utils.join(".gitlet", "branches");
        ArrayList<String> arrBranch = new ArrayList<>();
        String[] allBranch = checkBranch.list();
        if (allBranch != null && allBranch.length != 0) {
            for (String each: allBranch) {
                arrBranch.add(each);
            }
            if (!arrBranch.contains(message)) {
                File addBranch = Utils.join(checkBranch, message);
                Commit curCom = giveHead();
                Utils.writeContents(addBranch, Utils.serialize(curCom));
            } else {
                Utils.message("A branch with that name already exists.");
                System.exit(0);
            }
        }
    }

    /**Removes a branch.
     * @param message Message of branch to be removed.
     */
    public static void rmBranch(String message) {
        File refBranch = Utils.join(".gitlet", "ref");
        String curBranch = Utils.readContentsAsString(refBranch);
        File otherB = Utils.join("gitlet", "branches");
        String[] allB = otherB.list();
        ArrayList<String> listB = new ArrayList<>();
        for (String each: allB) {
            listB.add(each);
        }
        if (curBranch.equals(message)) {
            Utils.message("Cannot remove the current branch.");
            System.exit(0);
        } else if (!listB.contains(message)) {
            Utils.message("A branch with that name does not exist.");
            System.exit(0);
        } else {
            File toDelete = Utils.join(".gitlet", "branches", message);
            toDelete.delete();
        }
    }

    /** Some helper functions.*/

    /** Return current head. */
    public static Commit giveHead() {
        File headFile = Utils.join(".gitlet", "head");
        Commit headCom = Utils.readObject(headFile, Commit.class);
        return headCom;
    }


    /** All the variables we're using. */

    /** Array List of files in the toRemove folder. */
    private static ArrayList<File> remArr = new ArrayList<>();

    /** Head of commit. */
    private static String head;
}
