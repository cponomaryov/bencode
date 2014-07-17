package com.bittorrent.bencode.core;

import com.bittorrent.bencode.io.EOFException;
import org.testng.annotations.DataProvider;

import static com.bittorrent.bencode.core.BencodeDSL.*;
import static com.bittorrent.bencode.io.BencodeToken.*;

public class BencodeDataProvider {

    @DataProvider
    public static Object[][] testDataSuccess() {
        return new Object[][] {
                {"", null},
                {"1:a", s("a")},
                {"i1e", i(1)},
                {"le", l()},
                {"l1:ae", l(s("a"))},
                {"li1ee", l(i(1))},
                {"l1:ai1ee", l(s("a"), i(1))},
                {"li1e1:ae", l(i(1), s("a"))},
                {"l1:a1:be", l(s("a"), s("b"))},
                {"li1ei2ee", l(i(1), i(2))},
                {"de", d()},
                {"d1:k1:ae", d("k", s("a"))},
                {"d1:ki1ee", d("k", i(1))},
                {"d1:k1:a1:li1ee", d(e("k", s("a")), e("l", i(1)))},
                {"d1:ki1e1:l1:ae", d(e("k", i(1)), e("l", s("a")))},
                {"d1:k1:a1:l1:be", d(e("k", s("a")), e("l", s("b")))},
                {"d1:ki1e1:li2ee", d(e("k", i(1)), e("l", i(2)))},
                {"ll1:aee", l(l(s("a")))},
                {"ld1:k1:aee", l(d("k", s("a")))},
                {"d1:kl1:aee", d("k", l(s("a")))},
                {"d1:kd1:l1:aee", d("k", d("l", s("a")))}
        };
    }

    @DataProvider
    public static Object[][] testDataFailure() {
        return new Object[][] {
                {"l", new EOFException(null)},
                {"di1e", new UnexpectedTokenException(INTEGER)},
                {"dl", new UnexpectedTokenException(START_LIST)},
                {"dd", new UnexpectedTokenException(START_DICTIONARY)},
                {"e", new UnexpectedTokenException(END)},
                {"d1:ae", new UnexpectedTokenException(END)}
        };
    }

}
