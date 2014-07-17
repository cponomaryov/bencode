package com.bittorrent.bencode.io;

import java.io.IOException;

public class BencodeParseException extends IOException {

    public BencodeParseException(String message) {
        super(message);
    }

}
