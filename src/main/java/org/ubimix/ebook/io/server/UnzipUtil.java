package org.ubimix.ebook.io.server;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.ubimix.ebook.io.IInput;
import org.ubimix.ebook.io.IOutput;
import org.ubimix.ebook.io.IStore;
import org.ubimix.ebook.io.InOutUtil;

public class UnzipUtil {

    public interface IProgressListener {

        void begin();

        void end();

        boolean onUpdate(String name, long size, long time, String comment);
    }

    public static void unzip(
        IInput input,
        IStore store,
        IProgressListener progress) throws IOException {
        progress.begin();
        try {
            try {
                ZipInputStream zip = new ZipInputStream(new InputToStream(input));
                StreamToInput zipWrapper = new StreamToInput(zip);
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();
                    long size = entry.getSize();
                    long time = entry.getTime();
                    String comment = entry.getComment();
                    if (!progress.onUpdate(name, size, time, comment)) {
                        break;
                    }
                    if (!name.endsWith("/")) {
                        IOutput output = store.getOutput(name);
                        try {
                            InOutUtil.doCopy(zipWrapper, output);
                        } finally {
                            output.close();
                        }
                    }
                }
            } finally {
                input.close();
            }
        } finally {
            progress.end();
        }
    }

}