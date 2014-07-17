package com.bittorrent.bencode.io;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

public class BencodeReaderTest {

    BencodeReader reader;

    final String testString = "Test_All_Tests\nTest_java_io_BufferedInputStream\nTest_java_io_BufferedOutputStream\nTest_java_io_ByteArrayInputStream\nTest_java_io_ByteArrayOutputStream\nTest_java_io_DataInputStream\nTest_java_io_File\nTest_java_io_FileDescriptor\nTest_java_io_FileInputStream\nTest_java_io_FileNotFoundException\nTest_java_io_FileOutputStream\nTest_java_io_FilterInputStream\nTest_java_io_FilterOutputStream\nTest_java_io_InputStream\nTest_java_io_IOException\nTest_java_io_OutputStream\nTest_java_io_PrintStream\nTest_java_io_RandomAccessFile\nTest_java_io_SyncFailedException\nTest_java_lang_AbstractMethodError\nTest_java_lang_ArithmeticException\nTest_java_lang_ArrayIndexOutOfBoundsException\nTest_java_lang_ArrayStoreException\nTest_java_lang_Boolean\nTest_java_lang_Byte\nTest_java_lang_Character\nTest_java_lang_Class\nTest_java_lang_ClassCastException\nTest_java_lang_ClassCircularityError\nTest_java_lang_ClassFormatError\nTest_java_lang_ClassLoader\nTest_java_lang_ClassNotFoundException\nTest_java_lang_CloneNotSupportedException\nTest_java_lang_Double\nTest_java_lang_Error\nTest_java_lang_Exception\nTest_java_lang_ExceptionInInitializerError\nTest_java_lang_Float\nTest_java_lang_IllegalAccessError\nTest_java_lang_IllegalAccessException\nTest_java_lang_IllegalArgumentException\nTest_java_lang_IllegalMonitorStateException\nTest_java_lang_IllegalThreadStateException\nTest_java_lang_IncompatibleClassChangeError\nTest_java_lang_IndexOutOfBoundsException\nTest_java_lang_InstantiationError\nTest_java_lang_InstantiationException\nTest_java_lang_Integer\nTest_java_lang_InternalError\nTest_java_lang_InterruptedException\nTest_java_lang_LinkageError\nTest_java_lang_Long\nTest_java_lang_Math\nTest_java_lang_NegativeArraySizeException\nTest_java_lang_NoClassDefFoundError\nTest_java_lang_NoSuchFieldError\nTest_java_lang_NoSuchMethodError\nTest_java_lang_NullPointerException\nTest_java_lang_Number\nTest_java_lang_NumberFormatException\nTest_java_lang_Object\nTest_java_lang_OutOfMemoryError\nTest_java_lang_RuntimeException\nTest_java_lang_SecurityManager\nTest_java_lang_Short\nTest_java_lang_StackOverflowError\nTest_java_lang_String\nTest_java_lang_StringBuffer\nTest_java_lang_StringIndexOutOfBoundsException\nTest_java_lang_System\nTest_java_lang_Thread\nTest_java_lang_ThreadDeath\nTest_java_lang_ThreadGroup\nTest_java_lang_Throwable\nTest_java_lang_UnknownError\nTest_java_lang_UnsatisfiedLinkError\nTest_java_lang_VerifyError\nTest_java_lang_VirtualMachineError\nTest_java_lang_vm_Image\nTest_java_lang_vm_MemorySegment\nTest_java_lang_vm_ROMStoreException\nTest_java_lang_vm_VM\nTest_java_lang_Void\nTest_java_net_BindException\nTest_java_net_ConnectException\nTest_java_net_DatagramPacket\nTest_java_net_DatagramSocket\nTest_java_net_DatagramSocketImpl\nTest_java_net_InetAddress\nTest_java_net_NoRouteToHostException\nTest_java_net_PlainDatagramSocketImpl\nTest_java_net_PlainSocketImpl\nTest_java_net_Socket\nTest_java_net_SocketException\nTest_java_net_SocketImpl\nTest_java_net_SocketInputStream\nTest_java_net_SocketOutputStream\nTest_java_net_UnknownHostException\nTest_java_util_ArrayEnumerator\nTest_java_util_Date\nTest_java_util_EventObject\nTest_java_util_HashEnumerator\nTest_java_util_Hashtable\nTest_java_util_Properties\nTest_java_util_ResourceBundle\nTest_java_util_tm\nTest_java_util_Vector\n";

    @Test(expectedExceptions = IOException.class)
    public void testClose() throws IOException {
        reader = reader();
        reader.close();
        reader.read();
    }

    private BencodeReader reader() {
        return reader(testString);
    }

    private BencodeReader reader(String input) {
        return new BencodeReader(new StringReader(input));
    }

    private BencodeReader reader(String input, int bufferSize) {
        return new BencodeReader(new StringReader(input), bufferSize);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testConstructor() {
        reader("", -1);
    }

    @Test
    public void testMark1() throws IOException {
        reader = reader();
        reader.skip(500);
        reader.mark(1000);
        reader.skip(250);
        reader.reset();
        char[] buf = new char[testString.length()];
        reader.read(buf, 0, 500);
        assertEquals(new String(buf, 0, 500), testString.substring(500, 1000), "Failed to set mark properly");
    }

    @Test
    public void testMark2() throws IOException {
        char[] chars = new char[256];
        for (int i = 0; i < 256; i++)
            chars[i] = (char) i;
        reader = reader(new String(chars), 12);
        reader.skip(6);
        reader.mark(14);
        reader.read(new char[14], 0, 14);
        reader.reset();
        assertEquals(reader.read(), 6, "Wrong chars");
        assertEquals(reader.read(), 7, "Wrong chars");
    }

    @Test
    public void testMark3() throws IOException {
        char[] chars = new char[256];
        for (int i = 0; i < 256; i++)
            chars[i] = (char) i;
        reader = reader(new String(chars), 12);
        reader.skip(6);
        reader.mark(8);
        reader.skip(7);
        reader.reset();
        assertEquals(reader.read(), 6, "Wrong chars 2");
        assertEquals(reader.read(), 7, "Wrong chars 2");
    }

    @Test(expectedExceptions = IOException.class)
    public void testMarkInvalidate() throws IOException {
        reader = reader(testString, 800);
        char[] buf = new char[testString.length()];
        try {
            reader.skip(500);
            reader.mark(250);
            reader.read(buf, 0, 1000);
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        reader.reset();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testMarkNegativeReadAheadLimit() throws IOException {
        reader = reader();
        reader.mark(-1);
    }

    @Test
    public void testMarkSupported() {
        reader = reader();
        assertTrue(reader.markSupported(), "markSupported returned false");
    }

    @Test
    public void testRead1() throws IOException {
        reader = reader();
        assertEquals(reader.read(), testString.charAt(0), "Char read improperly");
    }

    @Test
    public void testRead2() throws IOException {
        reader = reader(new String(new char[]{'\u8765'}));
        assertEquals(reader.read(), '\u8765', "Wrong double byte character");
    }

    @Test
    public void testRead3() throws IOException {
        char[] chars = new char[256];
        for (int i = 0; i < 256; i++) {
            chars[i] = (char) i;
        }
        reader = reader(new String(chars), 12);
        assertEquals(reader.read(), 0, "Wrong initial char"); // Fill the
        // buffer
        char[] buf = new char[14];
        reader.read(buf, 0, 14); // Read greater than the buffer
        assertEquals(new String(buf), new String(chars, 1, 14), "Wrong block read data");
        assertEquals(reader.read(), 15, "Wrong chars"); // Check next byte
    }

    @Test
    public void testRead4() throws IOException {
        // regression test for HARMONY-841
        assertTrue(new BencodeReader(new CharArrayReader(new char[5], 1, 0), 2).read() == -1);
    }

    @Test(expectedExceptions = IOException.class)
    public void testReadBuf1() throws IOException {
        char[] ca = new char[2];
        reader = new BencodeReader(new InputStreamReader(new ByteArrayInputStream(new byte[0])));
        try {
            reader.close();
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
		/* Closed reader should throw IOException reading zero bytes */
        reader.read(ca, 0, 0);
    }

    @Test(expectedExceptions = IOException.class)
    public void testReadBuf2() throws IOException {
        char[] ca = new char[2];
        reader = new BencodeReader(new InputStreamReader(new ByteArrayInputStream(new byte[0])));
        try {
            reader.close();
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        /*
		 * Closed reader should throw IOException in preference to index out of
		 * bounds
		 */
        // Read should throw IOException before
        // ArrayIndexOutOfBoundException
        reader.read(ca, 1, 5);
    }

    @Test
    public void testReadBuf3() throws IOException {
        char[] ca = new char[2];
        // Test to ensure that a drained stream returns 0 at EOF
        reader = new BencodeReader(new InputStreamReader(new ByteArrayInputStream(new byte[2])));
        assertEquals(reader.read(ca, 0, 2), 2, "Emptying the reader should return two bytes");
        assertEquals(reader.read(ca, 0, 2), -1, "EOF on a reader should be -1");
        assertEquals(reader.read(ca, 0, 0), 0, "Reading zero bytes at EOF should work");
    }

    @Test
    public void testReadBuf4() throws IOException {
        char[] buf = new char[testString.length()];
        reader = reader();
        reader.read(buf, 50, 500);
        assertEquals(new String(buf, 50, 500), testString.substring(0, 500), "Chars read improperly");
    }

    @Test
    public void testReadBuf5() throws IOException {
        reader = new BencodeReader(new Reader() {
            int size = 2, pos = 0;
            char[] contents = new char[size];
            public int read() throws IOException {
                if (pos >= size)
                    throw new IOException("Read past end of data");
                return contents[pos++];
            }
            public int read(char[] buf, int off, int len) throws IOException {
                if (pos >= size)
                    throw new IOException("Read past end of data");
                int toRead = len;
                if (toRead > (size - pos))
                    toRead = size - pos;
                System.arraycopy(contents, pos, buf, off, toRead);
                pos += toRead;
                return toRead;
            }
            public boolean ready() throws IOException {
                return size - pos > 0;
            }
            public void close() throws IOException {
            }
        });
        reader.read();
        int result = reader.read(new char[2], 0, 2);
        assertEquals(result, 1, "Incorrect result: " + result);
    }

    @Test
    public void testReadBuf6() throws IOException {
        //regression for HARMONY-831
        try{
            new BencodeReader(new PipedReader(), 9).read(new char[] {}, 7, 0);
            fail("should throw IndexOutOfBoundsException");
        }catch(IndexOutOfBoundsException ignored){
        }
    }

    @DataProvider
    public Object[][] readBufIOBExceptionTestData() {
        return new Object[][]{
                {null, -1, -1},
                {null, -1, 0},
                {testString.toCharArray(), -1, -1},
                {testString.toCharArray(), -1, 0}
        };
    }

    @Test(dataProvider = "readBufIOBExceptionTestData", expectedExceptions = IndexOutOfBoundsException.class)
    public void testReadBufIOBException(char[] cbuf, int off, int len) throws IOException {
        reader = reader();
        reader.read(cbuf, off, len);
    }

    @DataProvider
    public Object[][] readBufNPETestData() {
        return new Object[][]{
                {0, -1},
                {0, 0},
                {0, 1}
        };
    }

    @Test(dataProvider = "readBufNPETestData", expectedExceptions = NullPointerException.class)
    public void testReadBufNPE(int off, int len) throws IOException {
        reader = reader();
        reader.read(null, off, len);
    }

    @DataProvider
    public Object[][] readBufIOExceptionTestData() {
        return new Object[][]{
                {null, -1, -1},
                {testString.toCharArray(), -1, 0},
                {testString.toCharArray(), 0, -1}
        };
    }

    @Test(dataProvider = "readBufIOExceptionTestData", expectedExceptions = IOException.class)
    public void testReadBufIOException(char[] cbuf, int off, int len) throws IOException {
        reader = reader();
        try {
            reader.close();
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        reader.read(cbuf, off, len);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testReadBufIOBException1() throws IOException {
        reader = reader();
        char[] charArray = testString.toCharArray();
        try {
            reader.read(charArray, 0, 0);
            reader.read(charArray, 0, charArray.length);
            reader.read(charArray, charArray.length, 0);
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        reader.read(charArray, charArray.length + 1, 0);
    }

    @Test(expectedExceptions = IndexOutOfBoundsException.class)
    public void testReadBufIOBException2() throws IOException {
        reader = reader();
        char[] charArray = testString.toCharArray();
        try {
            reader.read(charArray, 0, 0);
            reader.read(charArray, 0, charArray.length);
            reader.read(charArray, charArray.length, 0);
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        reader.read(charArray, charArray.length + 1, 1);
    }

    @Test
    public void testReady() throws IOException {
        reader = reader();
        assertTrue(reader.ready(), "ready returned false");
    }

    @Test
    public void testReset() throws IOException {
        reader = reader();
        reader.skip(500);
        reader.mark(900);
        reader.skip(500);
        reader.reset();
        char[] buf = new char[testString.length()];
        reader.read(buf, 0, 500);
        assertEquals(testString.substring(500, 1000), new String(buf, 0, 500), "Failed to reset properly");
    }

    @Test(expectedExceptions = IOException.class)
    public void testResetUnmarked() throws IOException {
        reader = reader();
        try {
            reader.skip(500);
        } catch (IOException e) {
            fail("unexpected exception", e);
        }
        reader.reset();
    }

    @Test
    public void testSkip() throws IOException {
        reader = reader();
        reader.skip(500);
        char[] buf = new char[testString.length()];
        reader.read(buf, 0, 500);
        assertEquals(testString.substring(500, 1000), new String(buf, 0, 500), "Failed to set skip properly");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testSkipNegativeCount() throws IOException {
        reader = reader();
        reader.skip(-1);
    }

    @DataProvider
    public Object[][] nextIntTestData() {
        return new Object[][]{
                {"i1e", 1},
                {"i2e", 2},
                {"i3e", 3},
                {"i4e", 4},
                {"i5e", 5},
                {"i6e", 6},
                {"i7e", 7},
                {"i8e", 8},
                {"i9e", 9},
                {"i10e", 10},
                {"i-1e", -1},
                {"i0e", 0},
                {"i-10e", -10},
                {"i1", 1},
                {"i-1", -1}
        };
    }

    @Test(dataProvider = "nextIntTestData")
    public void testNextInt(String input, int expected) throws IOException {
        reader = reader(input);
        reader.read();
        assertEquals(reader.readInt(), expected);
    }

    @Test
    public void testNextIntInString() throws IOException {
        reader = reader("4:spam");
        reader.mark(1);
        reader.read();
        reader.reset();
        assertEquals(reader.readInt(), 4);
    }

    @Test(expectedExceptions = NoSuchElementException.class)
    public void testNextIntEndOfStream() throws IOException {
        try {
            reader = reader("i");
            reader.read();
            reader.readInt();
        } catch (InputMismatchException ignored) {
            fail("NoSuchElementException expected but was InputMismatchException");
        }
    }

    @Test
    public void testNextIntInputMismatch() throws IOException {
        try {
            reader = reader("ai1e");
            reader.read();
            reader.readInt();
        } catch (InputMismatchException ignored) {
            return;
        } catch (NoSuchElementException e) {
            fail("InputMismatchException expected but was NoSuchElementException");
        }
        fail("InputMismatchException expected");
    }

    @DataProvider
    public Object[][] illegalNumbers() {
        return new Object[][]{{"i-0e"}, {"i01e"}, {"i-01e"}, {"ie"}};
    }

    @Test(dataProvider = "illegalNumbers")
    public void testNextIntIllegalNumber(String input) throws IOException {
        try {
            reader = reader(input);
            reader.read();
            reader.readInt();
        } catch (InputMismatchException ignored) {
        } catch (NoSuchElementException e) {
            fail("InputMismatchException expected but was NoSuchElementException");
        }
    }

    @DataProvider
    public Object[][] nextIntSmallBufferTestData() {
        return new Object[][]{
                {"1", 1, 0, 1},
                {"1", 2, 0, 1},
                {"10", 1, 0, 10},
                {"1e", 2, 0, 1},
                {"1ea", 2, 0, 1},
                {"1ea", 3, 0, 1},
                {"1eab", 3, 0, 1},
                {"i1", 1, 1, 1},
                {"i1", 2, 1, 1},
                {"i1e", 3, 1, 1},
                {"i1ea", 3, 1, 1},
                {"i1ea", 4, 1, 1},
                {"i1eab", 4, 1, 1},
                {"123e", 3, 0, 123},
                {"123ea", 3, 0, 123},
                {"123eab", 3, 0, 123},
                {"123eabc", 3, 0, 123},
                {"123e", 2, 0, 123},
                {"123ea", 2, 0, 123},
                {"i12e", 3, 1, 12},
                {"i12ea", 3, 1, 12},
                {"i12eab", 3, 1, 12},
                {"i12eabc", 3, 1, 12},
                {"i12e", 2, 1, 12},
                {"i12ea", 2, 1, 12},
                {"abci1e", 4, 4, 1},
                {"abci1ed", 4, 4, 1},
                {"abci1edf", 4, 4, 1},
                {"abci1edfg", 4, 4, 1},
                {"ai1e", 2, 2, 1},
                {"ai1eb", 2, 2, 1}
        };
    }

    @Test(dataProvider = "nextIntSmallBufferTestData")
    public void testNextIntSmallBuffer(String input, int bufferSize, int readCount, int expected) throws IOException {
        reader = reader(input, bufferSize);
        for (int i = 0; i < readCount; i++) {
            reader.read();
        }
        assertEquals(reader.readInt(), expected);
    }

    /**
     * Tears down the fixture, for example, close a network connection. This
     * method is called after a test is executed.
     */
    @AfterMethod
    protected void tearDown() {
        try {
            reader.close();
        } catch (Exception ignored) {
        }
    }

}
