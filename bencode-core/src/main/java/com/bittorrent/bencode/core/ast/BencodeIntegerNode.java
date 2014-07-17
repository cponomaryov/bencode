package com.bittorrent.bencode.core.ast;

public class BencodeIntegerNode extends BencodeValueNode<Integer> {

    public BencodeIntegerNode(Integer value) {
        super(value);
    }

    @Override
    public BencodeNodeType getType() {
        return BencodeNodeType.INTEGER;
    }

}
