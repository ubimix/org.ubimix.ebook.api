package org.webreformatter.ebook.io.server;

import java.io.IOException;
import java.io.OutputStream;

import org.webreformatter.ebook.io.IOutput;

public class StreamToOutput implements IOutput {

    private final OutputStream fOut;

    public StreamToOutput(OutputStream out) {
        fOut = out;
    }

    public void close() throws IOException {
        fOut.close();
    }

    public void write(byte[] buf, int offset, int len) throws IOException {
        fOut.write(buf, offset, len);
    }
}