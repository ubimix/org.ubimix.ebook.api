package org.ubimix.ebook.io.server;

import java.io.IOException;
import java.io.InputStream;

import org.ubimix.ebook.io.IInput;

public class StreamToInput implements IInput {
    private final InputStream fInput;

    public StreamToInput(InputStream input) {
        fInput = input;
    }

    public void close() throws IOException {
        fInput.close();
    }

    public int read(byte[] buf, int offset, int len) throws IOException {
        return fInput.read(buf, offset, len);
    }
}