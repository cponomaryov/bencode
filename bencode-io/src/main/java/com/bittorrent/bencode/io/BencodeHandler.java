package com.bittorrent.bencode.io;

public interface BencodeHandler {

    void startDictionary();

    void startList();

    void end();

    void string(String value);

    void valueInteger(int value);

}
