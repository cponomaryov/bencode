package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.*;
import com.bittorrent.bencode.io.BencodeWriter;

import java.io.IOException;
import java.util.*;

public class BencodeGeneratorImpl implements BencodeGenerator {

    private final BencodeWriter writer;

    public BencodeGeneratorImpl(BencodeWriter writer) {
        this.writer = writer;
    }

    @Override
    public void writeNode(BencodeNode node) throws IOException {
        if (node == null) {
            return;
        }
        Set<BencodeNode> visited = new HashSet<BencodeNode>();
        Stack<BencodeNode> stack = new Stack<BencodeNode>();
        stack.push(node);
        while (!stack.isEmpty()) {
            BencodeNode head = stack.peek();
            switch (head.getType()) {
                case STRING:
                    writer.writeString(((BencodeStringNode) head).getValue());
                    stack.pop();
                    break;
                case INTEGER:
                    writer.writeInteger(((BencodeIntegerNode) head).getValue());
                    stack.pop();
                    break;
                case LIST:
                    BencodeListNode list = (BencodeListNode) head;
                    if (visited.contains(list)) {
                        writer.writeEnd();
                        stack.pop();
                        break;
                    }
                    writer.writeStartList();
                    for (ListIterator<BencodeNode> i = list.listIterator(list.size()); i.hasPrevious();) {
                        stack.push(i.previous());
                    }
                    visited.add(list);
                    break;
                case DICTIONARY:
                    BencodeDictionaryNode dictionary = (BencodeDictionaryNode) head;
                    if (visited.contains(dictionary)) {
                        writer.writeEnd();
                        stack.pop();
                        break;
                    }
                    writer.writeStartDictionary();
                    List<Map.Entry<BencodeStringNode, BencodeNode>> entryList
                            = new ArrayList<Map.Entry<BencodeStringNode, BencodeNode>>(dictionary.entrySet());
                    for (ListIterator<Map.Entry<BencodeStringNode, BencodeNode>> i = entryList.listIterator(entryList.size()); i.hasPrevious();) {
                        Map.Entry<BencodeStringNode, BencodeNode> entry = i.previous();
                        stack.push(entry.getValue());
                        stack.push(entry.getKey());
                    }
                    visited.add(dictionary);
                    break;
                default: throw new RuntimeException("Unexpected error"); //this should never happen
            }
        }
    }

    @Override
    public void close() {
        writer.close();
    }

}
