package org.webreformatter.ebook.io.server;

import java.io.IOException;
import java.io.InputStream;

import org.webreformatter.ebook.io.IInput;

/**
 * @author kotelnikov
 */
public class InputToStream extends InputStream {

    private byte[] fBuf = { 0 };

    private final IInput fInput;

    public InputToStream(IInput i) {
        fInput = i;
    }

    @Override
    public void close() throws IOException {
        fInput.close();
        super.close();
    }

    @Override
    public int read() throws IOException {
        if (read(fBuf, 0, fBuf.length) < 0) {
            return -1;
        }
        return fBuf[0] & 0xFF;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return fInput.read(b, off, len);
    }
}