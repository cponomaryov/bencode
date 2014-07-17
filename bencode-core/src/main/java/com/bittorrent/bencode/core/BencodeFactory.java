package com.bittorrent.bencode.core;

import com.bittorrent.bencode.io.*;

import java.io.*;

public class BencodeFactory {

    private final BencodeIOFactory ioFactory = new BencodeIOFactory();

    public BencodeParser createParser(char[] content) {
        return new BencodeParserImpl(ioFactory.createScanner(content, 0, content.length));
    }

    public BencodeParser createParser(char[] content, int offset, int length) {
        return new BencodeParserImpl(ioFactory.createScanner(content, offset, length));
    }

    public BencodeParser createParser(File file) throws FileNotFoundException {
        return new BencodeParserImpl(ioFactory.createScanner(file));
    }

    public BencodeParser createParser(Reader reader) {
        return new BencodeParserImpl(ioFactory.createScanner(reader));
    }

    public BencodeParser createParser(String content) {
        return new BencodeParserImpl(ioFactory.createScanner(content));
    }

    public BencodeGenerator createGenerator(File file) throws IOException {
        return new BencodeGeneratorImpl(ioFactory.createWriter(file));
    }

    public BencodeGenerator createGenerator(OutputStream out) {
        return new BencodeGeneratorImpl(ioFactory.createWriter(out));
    }

    public BencodeGenerator createGenerator(Writer writer) {
        return new BencodeGeneratorImpl(ioFactory.createWriter(writer));
    }

}
