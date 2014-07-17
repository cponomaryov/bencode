package com.bittorrent.bencode.core.ast;

import java.util.LinkedHashMap;
import java.util.Map;

public class BencodeDictionaryNode extends LinkedHashMap<BencodeStringNode, BencodeNode> implements BencodeNode {

    public BencodeDictionaryNode(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public BencodeDictionaryNode(int initialCapacity) {
        super(initialCapacity);
    }

    public BencodeDictionaryNode() {
    }

    public BencodeDictionaryNode(Map<? extends BencodeStringNode, ? extends BencodeNode> m) {
        super(m);
    }

    @Override
    public BencodeNodeType getType() {
        return BencodeNodeType.DICTIONARY;
    }

}
