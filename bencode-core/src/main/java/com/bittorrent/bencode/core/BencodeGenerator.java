package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.BencodeNode;

import java.io.IOException;

public interface BencodeGenerator {

    void writeNode(BencodeNode node) throws IOException;

    void close();

}
