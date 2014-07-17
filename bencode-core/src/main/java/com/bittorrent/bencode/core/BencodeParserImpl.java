package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.*;
import com.bittorrent.bencode.io.BencodeScanner;
import com.bittorrent.bencode.io.BencodeToken;
import com.bittorrent.bencode.io.EOFException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BencodeParserImpl implements BencodeParser {

    private final BencodeScanner scanner;

    public BencodeParserImpl(BencodeScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public BencodeNode nextNode() throws IOException {
        Stack<BencodeNode> stack = new Stack<BencodeNode>();
        while (true) {
            BencodeToken token = scanner.nextToken();
            if (token == null) {
                if (stack.isEmpty()) {
                    return null;
                }
                throw new EOFException(null);
            }
            switch (token) {
                case STRING:
                    BencodeStringNode stringNode = new BencodeStringNode(scanner.getStringValue());
                    if (stack.isEmpty()) {
                        return stringNode;
                    }
                    BencodeNode head = stack.peek();
                    switch (head.getType()) {
                        case STRING:
                            BencodeStringNode key = (BencodeStringNode) stack.pop();
                            ((BencodeDictionaryNode) stack.peek()).put(key, stringNode);
                            continue;
                        case LIST: ((BencodeListNode) head).add(stringNode); continue;
                        case DICTIONARY: stack.push(stringNode); continue;
                        default: throw new RuntimeException("Unexpected error"); //this should never happen
                    }
                case INTEGER:
                    BencodeIntegerNode integerNode = new BencodeIntegerNode(scanner.getIntegerValue());
                    if (stack.isEmpty()) {
                        return integerNode;
                    }
                    head = stack.peek();
                    switch (head.getType()) {
                        case STRING:
                            BencodeStringNode key = (BencodeStringNode) stack.pop();
                            ((BencodeDictionaryNode) stack.peek()).put(key, integerNode);
                            continue;
                        case LIST: ((BencodeListNode) head).add(integerNode); continue;
                        case DICTIONARY: throw new UnexpectedTokenException(token);
                        default: throw new RuntimeException("Unexpected error"); //this should never happen
                    }
                case START_LIST:
                    if (!stack.isEmpty() && stack.peek().getType() == BencodeNodeType.DICTIONARY) {
                        throw new UnexpectedTokenException(token);
                    }
                    stack.push(new BencodeListNode());
                    break;
                case START_DICTIONARY:
                    if (!stack.isEmpty() && stack.peek().getType() == BencodeNodeType.DICTIONARY) {
                        throw new UnexpectedTokenException(token);
                    }
                    stack.push(new BencodeDictionaryNode());
                    break;
                case END:
                    if (stack.isEmpty()) {
                        throw new UnexpectedTokenException(token);
                    }
                    BencodeNode node = stack.pop();
                    if (stack.isEmpty()) {
                        return node;
                    }
                    if (node.getType() == BencodeNodeType.STRING) {
                        throw new UnexpectedTokenException(token);
                    }
                    head = stack.peek();
                    switch (head.getType()) {
                        case STRING:
                            BencodeStringNode key = (BencodeStringNode) stack.pop();
                            ((BencodeDictionaryNode) stack.peek()).put(key, node);
                            continue;
                        case LIST: ((BencodeListNode) head).add(node); continue;
                        default: throw new RuntimeException("Unexpected error"); //this should never happen
                    }
                default: throw new RuntimeException("Unknown token " + token);
            }
        }
    }

    @Override
    public List<BencodeNode> parse() throws IOException {
        List<BencodeNode> nodes = new ArrayList<BencodeNode>();
        BencodeNode node;
        while ((node = nextNode()) != null) {
            nodes.add(node);
        }
        return nodes;
    }

    @Override
    public void close() {
        scanner.close();
    }

}
