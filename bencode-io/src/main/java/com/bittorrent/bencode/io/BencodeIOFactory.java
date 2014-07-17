package com.bittorrent.bencode.io;

import java.io.*;

public class BencodeIOFactory {

    private BencodeScanner createScanner(Reader reader, boolean manageReader) {
        return new BencodeScannerImpl(reader, manageReader);
    }

    public BencodeScanner createScanner(char[] content) {
        return createScanner(content, 0, content.length);
    }

    public BencodeScanner createScanner(char[] content, int offset, int length) {
        return createScanner(new CharArrayReader(content, offset, length), true);
    }

    public BencodeScanner createScanner(File file) throws FileNotFoundException {
        return createScanner(new FileReader(file), true);
    }

    public BencodeScanner createScanner(Reader reader) {
        return createScanner(reader, false);
    }

    public BencodeScanner createScanner(String content) {
        return createScanner(new StringReader(content), true);
    }

    private BencodeWriter createWriter(Writer writer, boolean manageWriter) {
        return new BencodeWriterImpl(writer, manageWriter);
    }

    public BencodeWriter createWriter(File file) throws IOException {
        return createWriter(new FileWriter(file), true);
    }

    public BencodeWriter createWriter(OutputStream out) {
        return createWriter(new OutputStreamWriter(out), false);
    }

    public BencodeWriter createWriter(Writer writer) {
        return createWriter(writer, false);
    }

}
