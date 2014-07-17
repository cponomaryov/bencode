package com.bittorrent.bencode.io;

import java.io.IOException;

public interface BencodeScanner {

    BencodeToken nextToken() throws IOException;

    String getStringValue();

    Integer getIntegerValue();

    void scan(BencodeHandler handler) throws IOException;

    void close();

}
