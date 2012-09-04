package org.ubimix.ebook.io.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.ubimix.ebook.io.IInput;
import org.ubimix.ebook.io.IOutput;
import org.ubimix.ebook.io.IStore;

public class FileStore implements IStore {

    private File fDir;

    public FileStore(File dir) {
        fDir = dir;
    }

    public boolean exists() {
        return fDir.exists();
    }

    private File getFile(String id) {
        return new File(fDir, id);
    }

    public IInput getInput(String id) throws IOException {
        File file = getFile(id);
        if (!file.exists()) {
            return null;
        }
        final FileInputStream input = new FileInputStream(file);
        return new StreamToInput(input);
    }

    public IOutput getOutput(String id) throws IOException {
        File file = getFile(id);
        file.getParentFile().mkdirs();
        final OutputStream out = new FileOutputStream(file);
        return new StreamToOutput(out);
    }

}