package com.bittorrent.bencode.io;

import java.io.IOException;
import java.io.Reader;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import static com.bittorrent.bencode.io.BencodeToken.*;
import static com.bittorrent.bencode.io.BencodeTokenConstants.*;

public class BencodeScannerImpl implements BencodeScanner {

    private static final int BUFFER_SIZE = 4000;

    private static final int END_OF_STREAM = -1;

    private final BencodeReader reader;

    private final boolean manageReader;

    private String stringValue;

    private Integer integerValue;

    public BencodeScannerImpl(Reader reader, boolean manageReader) {
        this.reader = new BencodeReader(reader, BUFFER_SIZE);
        this.manageReader = manageReader;
    }

    @Override
    public BencodeToken nextToken() throws IOException {
        reader.mark(1);
        final int ch = reader.read();
        if (ch == END_OF_STREAM) {
            return null;
        }
        switch (ch) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                stringValue = readString();
                return STRING;
            case START_INTEGER:
                integerValue = readInteger();
                return INTEGER;
            case START_DICTIONARY_REPR:
                return START_DICTIONARY;
            case START_LIST_REPR:
                return START_LIST;
            case END_REPR:
                return END;
            default:
                throw unexpectedChar(ch, 1, "expected a valid value (String, Integer, List or Dictionary)");
        }
    }

    @Override
    public String getStringValue() {
        return stringValue;
    }

    @Override
    public Integer getIntegerValue() {
        return integerValue;
    }

    private int read(BencodeToken token) throws IOException {
        final int ch = reader.read();
        if (ch == END_OF_STREAM) {
            throw new EOFException(token);
        }
        return ch;
    }

    private String readString() throws IOException {
        reader.reset();
        final int length = reader.readInt();
        final int ch = read(STRING);
        if (ch != STRING_DELIM) {
            throw unexpectedChar(ch, 1, "was expecting a colon to separate string length and data");
        }
        final char[] buf = new char[length];
        final int read = reader.read(buf);
        if (read < length) {
            throw new EOFException(STRING);
        }
        return new String(buf);
    }

    private int readInteger() throws IOException {
        int value;
        try {
            value = reader.readInt();
        } catch (InputMismatchException e1) {
            throw unexpectedChar(e1.getMessage().charAt(0), 0, "was expecting a valid integer");
        } catch (NoSuchElementException e2) {
            throw new EOFException(INTEGER);
        }
        final int ch = read(INTEGER);
        if (ch != END_INTEGER) {
            throw unexpectedChar(ch, 1, "was expecting a trailing 'e' as integer ending delimiter");
        }
        return value;
    }

    private UnexpectedCharException unexpectedChar(int ch, int diff, String message) {
        return new UnexpectedCharException(ch, reader.getPosition() - diff, message);
    }

    @Override
    public void scan(BencodeHandler handler) throws IOException {
        BencodeToken token;
        while ((token = nextToken()) != null) {
            switch (token) {
                case START_DICTIONARY: handler.startDictionary(); break;
                case START_LIST: handler.startList(); break;
                case END: handler.end(); break;
                case STRING: handler.string(getStringValue()); break;
                case INTEGER: handler.valueInteger(getIntegerValue());
            }
        }
    }

    @Override
    public void close() {
        if (manageReader) {
            try {
                reader.close();
            } catch (IOException ignored) {}
        }
    }

}
