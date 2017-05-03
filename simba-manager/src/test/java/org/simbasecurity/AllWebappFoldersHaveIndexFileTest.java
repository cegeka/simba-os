/*
 * Copyright 2013-2017 Simba Open Source
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
