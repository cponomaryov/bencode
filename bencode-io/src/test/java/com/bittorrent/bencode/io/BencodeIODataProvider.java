package com.bittorrent.bencode.io;

import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.bittorrent.bencode.io.BencodeToken.*;

public class BencodeIODataProvider {

    @DataProvider
    public static Object[][] testDataSuccess() {
        return new Object[][] {
                {""},
                {"l", START_LIST},
                {"d", START_DICTIONARY},
                {"e", END},
                {"1:a", "a"},
                {"1:al", "a", START_LIST},
                {"i1e", 1},
                {"i123e", 123},
                {"i0e", 0},
                {"i-1e", -1},
        };
    }

    @DataProvider
    public static Object[][] testData() {
        Object[][] testDataFailure = new Object[][] {
                {"1", new EOFException(STRING)},
                {"123", new EOFException(STRING)},
                {"1a", new UnexpectedCharException('a', 1, null)},
                {"123a", new UnexpectedCharException('a', 3, null)},
                {"1:", new EOFException(STRING)},
                {"123:", new EOFException(STRING)},
                {"2:a", new EOFException(STRING)},
                {"i", new EOFException(INTEGER)},
                {"ia", new UnexpectedCharException('a', 1, null)},

                {"i1", new EOFException(INTEGER)},
                {"i123", new EOFException(INTEGER)},
                {"i0", new EOFException(INTEGER)},
                {"i-1", new EOFException(INTEGER)},
                {"i01", new UnexpectedCharException('0', 1, null)},
                {"i-", new UnexpectedCharException('-', 1, null)},

                {"i1a", new UnexpectedCharException('a', 2, null)},
                {"i123a", new UnexpectedCharException('a', 4, null)},
                {"i0a", new UnexpectedCharException('a', 2, null)},
                {"i-1a", new UnexpectedCharException('a', 3, null)},
                {"i01a", new UnexpectedCharException('0', 1, null)},
                {"i-a", new UnexpectedCharException('-', 1, null)},

                {"i01e", new UnexpectedCharException('0', 1, null)},
                {"i-e", new UnexpectedCharException('-', 1, null)},

                {"a", new UnexpectedCharException('a', 0, null)},
                {"-", new UnexpectedCharException('-', 0, null)}
        };
        List<Object[]> result = new ArrayList<Object[]>();
        result.addAll(Arrays.asList(testDataSuccess()));
        result.addAll(Arrays.asList(testDataFailure));
        return result.toArray(new Object[result.size()][]);
    }

}
