package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.BencodeNode;

import java.io.IOException;
import java.util.List;

public interface BencodeParser {

    BencodeNode nextNode() throws IOException;

    List<BencodeNode> parse() throws IOException;

    void close();

}
