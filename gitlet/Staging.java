package gitlet;

import java.io.File;
import java.io.Serializable;

/**Staging class.
 * @author Dhivyaa N Mailvaganam
 */

public class Staging implements Serializable {

    /** Method to add files to stage.
     * @param filename File to be added.
     * @param blob Contents of file.
     */
    public static void add(String filename, String blob) {
        File toAdd = Utils.join(".gitlet", "stagingarea", "toAdd", filename);
        Utils.writeContents(toAdd, blob);
    }

    /** Method to replace contents of a file.
     * @param filename File if of which contents will be replaced.
     * @param blob Message with which it will be replaced with.
     */
    public static void replace(String filename, String blob) {
        File toReplace = Utils.join(".gitlet", "stagingarea",
                "toAdd", filename);
        Utils.writeContents(toReplace, blob);
    }

    /** Method to remove a file.
     * @param filename Filename of file to be staged.
     */
    public static void remove(String filename) {
        File toDelete = Utils.join(".gitlet", "stagingarea", "toAdd", filename);
        toDelete.delete();
    }

    /** Method to remove all files on stage. */
    public static void clearStage() {
        File addDir = Utils.join(".gitlet", "stagingarea", "toAdd");
        File[] dirList = addDir.listFiles();
        for (File each : dirList) {
            File tempFile = Utils.join(addDir, each.getName());
            tempFile.delete();
        }
    }

    /** Method to add commit objects to commit folder.
     * @param comObj Commit object to be added to commit folder.
     * @param uId Id of the commit object.
     */
    public static void commitAdd(String uId, Commit comObj) {
        File toComm = Utils.join(".gitlet", "commits", uId);
        Utils.writeObject(toComm, comObj);
    }

    /** Method to rewrite contents of head file.
     * @param comObj Commit object to be made into new head.
     */
    public static void newHead(Commit comObj) {
        File nHead = Utils.join(".gitlet", "head");
        Utils.writeContents(nHead, Utils.serialize(comObj));
    }

    /**Method to remove a file.
     * @param filename Filename of file to be removed.
     */
    public static void rm(String filename) {
        File toRem = Utils.join(".gitlet", "stagingarea", "toRemove", filename);
        Utils.writeContents(toRem, filename);
        File toCheck = Utils.join(".gitlet", "stagingarea", "toAdd", filename);
        if (toCheck.exists()) {
            toCheck.delete();
        }
    }

}
