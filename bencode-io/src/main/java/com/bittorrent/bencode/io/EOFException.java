package com.bittorrent.bencode.io;

public class EOFException extends BencodeParseException {

    private BencodeToken token;

    public EOFException(BencodeToken token) {
        super("Unexpected end of input while reading " + token + " token");
        this.token = token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EOFException that = (EOFException) o;

        if (token != that.token) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

}
