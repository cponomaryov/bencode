package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.*;

import static java.util.Arrays.asList;

public class BencodeDSL {

    public static BencodeStringNode s(String value) {
        return new BencodeStringNode(value);
    }

    public static BencodeIntegerNode i(int value) {
        return new BencodeIntegerNode(value);
    }

    public static BencodeListNode l(BencodeNode... elements) {
        return new BencodeListNode(asList(elements));
    }

    public static DictionaryEntry e(String key, BencodeNode value) {
        return new DictionaryEntry(s(key), value);
    }

    public static BencodeDictionaryNode d(DictionaryEntry... entries) {
        BencodeDictionaryNode dictonary = new BencodeDictionaryNode(entries.length);
        for (DictionaryEntry entry : entries) {
            dictonary.put(entry.key, entry.value);
        }
        return dictonary;
    }

    public static BencodeDictionaryNode d(String key, BencodeNode value) {
        return d(e(key, value));
    }

    public static class DictionaryEntry {

        private final BencodeStringNode key;

        private final BencodeNode value;

        private DictionaryEntry(BencodeStringNode key, BencodeNode value) {
            this.key = key;
            this.value = value;
        }

    }

}
