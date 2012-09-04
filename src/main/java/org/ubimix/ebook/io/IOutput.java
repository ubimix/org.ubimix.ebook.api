package org.ubimix.ebook.io;

import java.io.IOException;

/**
 * An output stream used to write book content.
 * 
 * @author kotelnikov
 */
public interface IOutput {

    void close() throws IOException;

    void write(byte[] buf, int offset, int len) throws IOException;
}