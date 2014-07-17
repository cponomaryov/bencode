package com.bittorrent.bencode.io;

import org.mockito.InOrder;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.bittorrent.bencode.io.BencodeToken.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

public class BencodeScannerTest {

    @SuppressWarnings("ConstantConditions")
    @Test(dataProviderClass = BencodeIODataProvider.class, dataProvider = "testData")
    public void testNextToken(Object... args) throws IOException {
        BencodeScanner scanner = new BencodeIOFactory().createScanner((String) args[0]);
        for (int i = 1; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Throwable) {
                try {
                    scanner.nextToken();
                    fail(arg + " is expected");
                } catch (Throwable t) {
                    assertEquals(t, arg);
                    scanner.close();
                    return;
                }
            } else {
                BencodeToken expected = null;
                if (arg instanceof BencodeToken) {
                    expected = (BencodeToken) arg;
                } else if (arg instanceof String) {
                    expected = STRING;
                } else if (arg instanceof Integer) {
                    expected = INTEGER;
                } else {
                    fail("Unknown arg: " + arg);
                }
                assertEquals(scanner.nextToken(), expected);
                if (expected == STRING) {
                    assertEquals(scanner.getStringValue(), (String) arg);
                } else if (expected == INTEGER) {
                    assertEquals(scanner.getIntegerValue(), arg);
                }
            }
        }
        assertEquals(scanner.nextToken(), null);
        scanner.close();
    }

    @Test(dataProviderClass = BencodeIODataProvider.class, dataProvider = "testData")
    public void testScan(Object... args) throws IOException {
        BencodeScanner scanner = new BencodeIOFactory().createScanner((String) args[0]);
        BencodeHandler handler = Mockito.mock(BencodeHandler.class);
        Throwable throwable = null;
        try {
            scanner.scan(handler);
        } catch (Throwable t) {
            throwable = t;
        }
        scanner.close();
        InOrder inOrder = Mockito.inOrder(handler);
        for (int i = 1; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof Throwable) {
                if (throwable == null) {
                    fail(arg + " is expected");
                } else {
                    assertEquals(throwable, arg);
                }
                return;
            }
            if (arg instanceof BencodeToken) {
                BencodeToken token = (BencodeToken) arg;
                switch (token) {
                    case START_DICTIONARY: inOrder.verify(handler).startDictionary(); break;
                    case START_LIST: inOrder.verify(handler).startList(); break;
                    case END: inOrder.verify(handler).end(); break;
                    default: fail("Unexpected token: " + token);
                }
            } else if (arg instanceof String) {
                inOrder.verify(handler).string((String) arg);
            } else if (arg instanceof Integer) {
                inOrder.verify(handler).valueInteger((Integer) arg);
            } else {
                fail("Unknown arg: " + arg);
            }
        }
        assertNull(throwable);
    }

}
