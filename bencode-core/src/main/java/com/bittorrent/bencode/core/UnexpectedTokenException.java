package com.bittorrent.bencode.core;

import com.bittorrent.bencode.io.BencodeParseException;
import com.bittorrent.bencode.io.BencodeToken;

public class UnexpectedTokenException extends BencodeParseException {

    private BencodeToken token;

    public UnexpectedTokenException(BencodeToken token) {
        super("Unexpected token " + token);
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnexpectedTokenException that = (UnexpectedTokenException) o;

        if (token != that.token) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

}
