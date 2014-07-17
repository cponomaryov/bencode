package com.bittorrent.bencode.io;

import static com.bittorrent.bencode.io.BencodeTokenConstants.END_REPR;
import static com.bittorrent.bencode.io.BencodeTokenConstants.START_DICTIONARY_REPR;
import static com.bittorrent.bencode.io.BencodeTokenConstants.START_LIST_REPR;

public enum BencodeToken {

    STRING(null),
    INTEGER(null),
    START_LIST(START_LIST_REPR),
    START_DICTIONARY(START_DICTIONARY_REPR),
    END(END_REPR);

    private final Character repr;

    BencodeToken(Character repr) {
        this.repr = repr;
    }

    public Character repr() {
        return repr;
    }

}
