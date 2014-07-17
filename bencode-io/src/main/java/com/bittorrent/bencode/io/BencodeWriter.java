package com.bittorrent.bencode.io;

import java.io.IOException;

public interface BencodeWriter {

    void writeStartList() throws IOException;

    void writeStartDictionary() throws IOException;

    void writeEnd() throws IOException;

    void writeString(String string) throws IOException;

    void writeInteger(int integer) throws IOException;

    void close();

}
