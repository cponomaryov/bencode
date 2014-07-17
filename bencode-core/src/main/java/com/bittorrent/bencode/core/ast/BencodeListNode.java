package com.bittorrent.bencode.core.ast;

import java.util.*;

public class BencodeListNode extends ArrayList<BencodeNode> implements BencodeNode {

    public BencodeListNode(int initialCapacity) {
        super(initialCapacity);
    }

    public BencodeListNode() {
    }

    public BencodeListNode(Collection<? extends BencodeNode> c) {
        super(c);
    }

    @Override
    public BencodeNodeType getType() {
        return BencodeNodeType.LIST;
    }

}
