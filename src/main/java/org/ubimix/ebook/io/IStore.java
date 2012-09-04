package org.ubimix.ebook.io;

import java.io.IOException;

public interface IStore {

    IInput getInput(String id) throws IOException;

    IOutput getOutput(String id) throws IOException;
}