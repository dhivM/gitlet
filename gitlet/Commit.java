package gitlet;

import java.io.Serializable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.io.File;

/**Class that creates a Commit object.
 * @author Dhivyaa N Mailvaganam
 */
public class Commit implements Serializable {

    /**Commit class constructor.
     * @param message message describing commit
     * @param file hashmap of files in commit
     * @param parent SHA-1 of this commit's parent
     */
    public Commit(String message, HashMap<String, String> file, String parent) {
        cMsg = message;
        timeStamp = makeDate("hello");
        if (timeStamp.equals("no")) {
            timeStamp = makeDate();
        }
        parentRef = parent;
        inCommit = file;
        uniqueId = makeId();
    }


    /** Creating unique ID for Commit object.
     * @return Gives SHA-1 for this Commit object.
     * */
    private String makeId() {
        String p;
        String inCom;
        if (parentRef == null && inCommit == null) {
            p = "";
            inCom = "";
        } else if (parentRef == null) {
            p = "";
            inCom = inCommit.toString();
        } else if (inCommit == null) {
            p = parentRef;
            inCom = "";
        } else {
            p = parentRef;
            inCom = inCommit.toString();
        }
        return Utils.sha1(timeStamp, cMsg, inCom, p);
    }

    /** Method that returns the SHA-1 or unique ID of commit. */
    public String getId() {
        return uniqueId;
    }

    /** Method that returns commit message. */
    public String getMsg() {
        return cMsg;
    }

    /** Method that returns commit parent SHA-1. */
    public String getParent() {
        return parentRef;
    }

    /** Method that returns timestamp of commit. */
    public String getDate() {
        return timeStamp;
    }

    /** Method that creates the timestamp for init.
     * @return returns different things depending on whether init or not.
     * @param forInit Random arg that generates date for init.
     */
    public String makeDate(String forInit) {
        File toCheck = new File(".gitlet", "commits");
        String[] toCount = toCheck.list();
        if (toCount.length == 0) {
            String initDate = "Wed Dec 31 16:00:00 1969 -0800";
            return initDate;
        } else {
            return "no";
        }
    }

    /** Method that creates time stamp for other commits.
     * @return gives date following required format.
     */
    public static String makeDate() {
        Date k = new Date();
        SimpleDateFormat newDate = new SimpleDateFormat("E MMM dd "
                + "HH:mm:ss yyyy Z");
        return newDate.format(k);
    }

    /**Method that returns files in a commit. */
    public HashMap<String, String> giveFiles() {
        return inCommit;
    }

    /** Commit message. */
    private String cMsg;

    /** Timestamp of a commit. */
    private String timeStamp;

    /** Parent String. */
    private String parentRef;

    /**HashMap of filenames in a commit to blobs. Val = sha1 of blob in file.*/
    private HashMap<String, String> inCommit;

    /**String of sha1. */
    private String uniqueId;

    /**String representing branchName of commit.
     * "" till branchName is no longer "master"
     */
    private String branch;


}
