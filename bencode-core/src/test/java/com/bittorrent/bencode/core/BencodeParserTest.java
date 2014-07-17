package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.BencodeNode;
import org.testng.annotations.Test;

import java.io.IOException;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class BencodeParserTest {

    @Test(dataProviderClass = BencodeDataProvider.class, dataProvider = "testDataSuccess")
    public void testNextNodeSuccess(String input, BencodeNode node) throws IOException {
        BencodeParser parser = new BencodeFactory().createParser(input);
        assertEquals(parser.nextNode(), node);
        parser.close();
    }

    @Test(dataProviderClass = BencodeDataProvider.class, dataProvider = "testDataFailure")
    public void testNextNodeFailure(String input, Throwable throwable) throws IOException {
        BencodeParser parser = new BencodeFactory().createParser(input);
        try {
            parser.nextNode();
            fail(throwable + " is expected");
        } catch (Throwable t) {
            assertEquals(t, throwable);
        }
        parser.close();
    }

    @Test(dataProviderClass = BencodeDataProvider.class, dataProvider = "testDataSuccess")
    public void testParseSuccess(String input, BencodeNode node) throws IOException {
        BencodeParser parser = new BencodeFactory().createParser(input);
        assertEquals(parser.parse(), node == null ? emptyList() : singletonList(node));
        parser.close();
    }

    @Test(dataProviderClass = BencodeDataProvider.class, dataProvider = "testDataFailure")
    public void testParseFailure(String input, Throwable throwable) throws IOException {
        BencodeParser parser = new BencodeFactory().createParser(input);
        try {
            parser.parse();
            fail(throwable + " is expected");
        } catch (Throwable t) {
            assertEquals(t, throwable);
        }
        parser.close();
    }

}
