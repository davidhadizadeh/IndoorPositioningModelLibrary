package test.de.hadizadeh.positioning.roommodel;

import de.hadizadeh.positioning.roommodel.FileManager;
import junit.framework.TestCase;

import java.io.File;


public class FileManagerTest extends TestCase {

    public void testGetTmpName() throws Exception {
        assertNotNull(FileManager.getTmpName("subfolder"));
    }

    public void testReadTextFile() throws Exception {
        assertEquals("", FileManager.readTextFile(""));
    }

    public void testCalculateHash() throws Exception {
        assertEquals("2973d150c0dc1fefe998f834810d68f278ea58ec", FileManager.calculateHash(new File("lib/junit-4.12.jar")));
    }

    public void testRemoveDirectory() throws Exception {
        File dir = new File("testdata");
        dir.mkdir();
        FileManager.removeDirectory(dir);
    }

    public void testCompress() throws Exception {
        File dir = new File("testdata");
        dir.mkdir();
        File zipFile = new File("testdata/test.zip");
        File folder = new File("lib");
        assertEquals(folder, FileManager.compress(zipFile, folder));
        FileManager.removeDirectory(dir);
    }

    public void testDecompress() throws Exception {
        File dir = new File("testdata");
        dir.mkdir();
        File zipFile = new File("testdata/test.zip");
        File folder = new File("lib");
        FileManager.compress(zipFile, folder);
        assertEquals(new File("testdata"), FileManager.decompress(zipFile, new File("testdata")));
    }
}