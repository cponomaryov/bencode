package com.bittorrent.bencode.core;

import com.bittorrent.bencode.core.ast.BencodeNode;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.testng.Assert.assertEquals;

public class BencodeGeneratorTest {

    @Test(dataProviderClass = BencodeDataProvider.class, dataProvider = "testDataSuccess")
    public void testNextNode(String output, BencodeNode node) throws IOException {
        Writer writer = new StringWriter();
        BencodeGenerator generator = new BencodeFactory().createGenerator(writer);
        generator.writeNode(node);
        generator.close();
        assertEquals(writer.toString(), output);
    }

}
