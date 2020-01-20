package gitlet;

import ucb.junit.textui;
import org.junit.Test;
import java.io.File;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author Dhivyaa  N Mailvaganam
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class);
        createFiles();
    }

    public static void createFiles() {
        File testF = new File("testfolder");
        testF.mkdir();
    }


   /** Test for init.*/
    @Test
    public void initTest() {
        createFiles();
        Main.main("init");
        File newFile = Utils.join("testfolder", ".gitlet");
        assertTrue(newFile.exists());
    }
    /**Test for add. */
    @Test
    public void addTest() {


    }

    File actual = new File("testfolder");
}


