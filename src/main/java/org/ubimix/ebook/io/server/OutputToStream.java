/**
 * 
 */
package org.ubimix.ebook.io.server;

import java.io.IOException;
import java.io.OutputStream;

import org.ubimix.ebook.io.IOutput;

/**
 * @author kotelnikov
 */
public class OutputToStream extends OutputStream {

    private byte[] fBuf = { 0 };

    private IOutput fOutput;

    /**
     * 
     */
    public OutputToStream(IOutput output) {
        fOutput = output;
    }

    @Override
    public void close() throws IOException {
        fOutput.close();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        fOutput.write(b, off, len);
    }

    /**
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int b) throws IOException {
        fBuf[0] = (byte) (0xFF & b);
        write(fBuf, 0, fBuf.length);
    }

}
