package com.bittorrent.bencode.core.ast;

public class BencodeStringNode extends BencodeValueNode<String> {

    public BencodeStringNode(String value) {
        super(value);
    }

    @Override
    public BencodeNodeType getType() {
        return BencodeNodeType.STRING;
    }

}
