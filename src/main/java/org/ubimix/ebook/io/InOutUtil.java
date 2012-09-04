package org.ubimix.ebook.io;

import java.io.IOException;

public class InOutUtil {

    public static void copy(IInput input, IOutput out) throws IOException {
        try {
            try {
                doCopy(input, out);
            } finally {
                out.close();
            }
        } finally {
            input.close();
        }
    }

    public static void doCopy(IInput input, IOutput out) throws IOException {
        byte[] buf = new byte[1024 * 10];
        int len;
        while ((len = input.read(buf, 0, buf.length)) > 0) {
            out.write(buf, 0, len);
        }
    }

}
