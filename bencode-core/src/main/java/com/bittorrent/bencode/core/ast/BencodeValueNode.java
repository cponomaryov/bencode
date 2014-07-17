package com.bittorrent.bencode.core.ast;

public abstract class BencodeValueNode<T> implements BencodeNode {

    private final T value;

    public BencodeValueNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BencodeValueNode that = (BencodeValueNode) o;

        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
