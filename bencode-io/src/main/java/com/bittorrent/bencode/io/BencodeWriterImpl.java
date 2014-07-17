package com.bittorrent.bencode.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import static com.bittorrent.bencode.io.BencodeToken.END;
import static com.bittorrent.bencode.io.BencodeToken.START_DICTIONARY;
import static com.bittorrent.bencode.io.BencodeToken.START_LIST;
import static com.bittorrent.bencode.io.BencodeTokenConstants.END_INTEGER;
import static com.bittorrent.bencode.io.BencodeTokenConstants.START_INTEGER;
import static com.bittorrent.bencode.io.BencodeTokenConstants.STRING_DELIM;

public class BencodeWriterImpl implements BencodeWriter {

    private static final int BUFFER_SIZE = 4000;

    private final Writer writer;

    private final boolean manageWriter;

    public BencodeWriterImpl(Writer writer, boolean manageWriter) {
        this.writer = new BufferedWriter(writer, BUFFER_SIZE);
        this.manageWriter = manageWriter;
    }

    @Override
    public void writeStartList() throws IOException {
        writer.write(START_LIST.repr());
    }

    @Override
    public void writeStartDictionary() throws IOException {
        writer.write(START_DICTIONARY.repr());
    }

    @Override
    public void writeEnd() throws IOException {
        writer.write(END.repr());
    }

    private void writeInt(int integer) throws IOException {
        writer.write(String.valueOf(integer));
    }

    @Override
    public void writeString(String string) throws IOException {
        writeInt(string.length());
        writer.write(STRING_DELIM);
        writer.write(string);
    }

    @Override
    public void writeInteger(int integer) throws IOException {
        writer.write(START_INTEGER);
        writeInt(integer);
        writer.write(END_INTEGER);
    }

    @Override
    public void close() {
        try {
            writer.flush();
            if (manageWriter) {
                writer.close();
            }
        } catch (IOException ignored) {}
    }

}
