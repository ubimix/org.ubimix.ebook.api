package org.webreformatter.ebook.io;

import java.io.IOException;

public interface IInput {

    void close() throws IOException;

    int read(byte[] buf, int offset, int len) throws IOException;
}