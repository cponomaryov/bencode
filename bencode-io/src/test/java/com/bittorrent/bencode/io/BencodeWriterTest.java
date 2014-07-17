package com.bittorrent.bencode.io;

import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class BencodeWriterTest {

    @Test(dataProviderClass = BencodeIODataProvider.class, dataProvider = "testDataSuccess")
    public void testWrite(Object... args) throws IOException {
        Writer writer = new StringWriter();
        BencodeWriter bencodeWriter = new BencodeIOFactory().createWriter(writer);
        for (int i = 1; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof BencodeToken) {
                BencodeToken token = (BencodeToken) arg;
                switch (token) {
                    case START_LIST: bencodeWriter.writeStartList(); break;
                    case START_DICTIONARY: bencodeWriter.writeStartDictionary(); break;
                    case END: bencodeWriter.writeEnd(); break;
                    default: fail("Illegal token: " + token);
                }
            } else if (arg instanceof String) {
                bencodeWriter.writeString((String) arg);
            } else if (arg instanceof Integer) {
                bencodeWriter.writeInteger((Integer) arg);
            } else {
                fail("Unknown arg: " + arg);
            }
        }
        bencodeWriter.close();
        assertEquals(writer.toString(), (String) args[0]);
    }

}
