package org.simbasecurity;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

public class AllWebappFoldersHaveIndexFileTest {
    private boolean allFoldersHaveIndexFile = true;

    @Test
    public void testAllResourceFolders() throws Exception {
        testFolder(getWebappFolder());
        Assert.assertTrue(allFoldersHaveIndexFile);
    }

    private File getWebappFolder() {
        File folder = new File("src/main/webapp");
        if(folder.exists()) {
            return folder;
        }
        return new File("simba-manager/src/main/webapp");
    }

    private void testFolder(File folder) throws Exception {
        File[] files = folder.listFiles();
        boolean indexFilePresent = false;
        for (File file : files) {
            if(file.isDirectory()) {
                testFolder(file);
            }
            if(file.getName().equals("index.html")) {
                indexFilePresent = true;
            }
        }
        if(!indexFilePresent) {
            System.out.println("No index.html in " + folder.getAbsoluteFile());
            allFoldersHaveIndexFile = false;
        }
    }
}
