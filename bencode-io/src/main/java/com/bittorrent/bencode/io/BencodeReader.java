package com.bittorrent.bencode.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BencodeReader extends Reader {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("0|(-?[1-9]+\\d*)");

    private static Pattern DELIM_PATTERN = Pattern.compile("[^\\d\\-]");

    private Reader in;

    private char cb[];
    private int nChars, nextChar;

    private static final int INVALIDATED = -2;
    private static final int UNMARKED = -1;
    private int markedChar = UNMARKED;
    private int readAheadLimit = 0; /* Valid only when markedChar > 0 */

    private static int defaultCharBufferSize = 8192;

    // Internal matcher used for finding delimiters
    private Matcher matcher;

    // Boolean is true if source is done
    private boolean sourceClosed = false;

    // Boolean indicating more input is required
    private boolean needInput = false;

    private int offset = 0;

    /**
     * Creates a buffering character-input stream that uses an input buffer of
     * the specified size.
     *
     * @param  in   A Reader
     * @param  sz   Input-buffer size
     *
     * @exception  IllegalArgumentException  If {@code sz <= 0}
     */
    public BencodeReader(Reader in, int sz) {
        super(in);
        if (sz <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        this.in = in;
        cb = new char[sz];
        nextChar = nChars = 0;
        matcher = DELIM_PATTERN.matcher(CharBuffer.wrap(cb));
        matcher.useTransparentBounds(true);
        matcher.useAnchoringBounds(false);
    }

    /**
     * Creates a buffering character-input stream that uses a default-sized
     * input buffer.
     *
     * @param  in   A Reader
     */
    public BencodeReader(Reader in) {
        this(in, defaultCharBufferSize);
    }

    /** Checks to make sure that the stream has not been closed */
    private void ensureOpen() throws IOException {
        if (in == null)
            throw new IOException("Stream closed");
    }

    /**
     * Fills the input buffer, taking the mark into account if it is valid.
     */
    private void fill() throws IOException {
        int dst, delta;
        if (markedChar <= UNMARKED || nextChar - markedChar >= readAheadLimit) {
            if (markedChar > UNMARKED) {
                /* Gone past read-ahead limit: Invalidate mark */
                markedChar = INVALIDATED;
                readAheadLimit = 0;
            }
            delta = nChars - nextChar;
            if (nextChar > 0) {
                System.arraycopy(cb, nextChar, cb, 0, delta);
            } else if (nChars > 0) {
                char[] newBuf = new char[cb.length * 2];
                System.arraycopy(cb, 0, newBuf, 0, nChars);
                cb = newBuf;
                matcher.reset(CharBuffer.wrap(cb));
            }
            offset += nextChar;
            nextChar = 0;
        } else {
            delta = nChars - markedChar;
            if (readAheadLimit <= cb.length) {
                /* Shuffle in the current buffer */
                if (markedChar > 0) {
                    System.arraycopy(cb, markedChar, cb, 0, delta);
                } else if (nChars > 0) {
                    char[] newBuf = new char[cb.length * 2];
                    System.arraycopy(cb, 0, newBuf, 0, nChars);
                    cb = newBuf;
                    matcher.reset(CharBuffer.wrap(cb));
                }
            } else {
                /* Reallocate buffer to accommodate read-ahead limit */
                char ncb[] = new char[readAheadLimit];
                System.arraycopy(cb, markedChar, ncb, 0, delta);
                cb = ncb;
                matcher.reset(CharBuffer.wrap(cb));
            }
            offset += markedChar;
            nextChar = nextChar - markedChar;
            markedChar = 0;
        }
        nChars = dst = delta;

        int n;
        do {
            n = in.read(cb, dst, cb.length - dst);
        } while (n == 0);
        if (n > 0) {
            nChars = dst + n;
        } else {
            sourceClosed = true;
            needInput = false;
        }
    }

    /**
     * Reads a single character.
     *
     * @return The character read, as an integer in the range
     *         0 to 65535 (<tt>0x00-0xffff</tt>), or -1 if the
     *         end of the stream has been reached
     * @exception  IOException  If an I/O error occurs
     */
    public int read() throws IOException {
        synchronized (lock) {
            ensureOpen();
            for (;;) {
                if (nextChar >= nChars) {
                    fill();
                    if (nextChar >= nChars)
                        return -1;
                }
                return cb[nextChar++];
            }
        }
    }

    /**
     * Reads characters into a portion of an array, reading from the underlying
     * stream if necessary.
     */
    private int read1(char[] cbuf, int off, int len) throws IOException {
        if (nextChar >= nChars) {
            /* If the requested length is at least as large as the buffer, and
               if there is no mark/reset activity, do not bother to copy the characters into the
               local buffer.  In this way buffered streams will cascade
               harmlessly. */
            if (len >= cb.length && markedChar <= UNMARKED) {
                return in.read(cbuf, off, len);
            }
            fill();
        }
        if (nextChar >= nChars) return -1;
        int n = Math.min(len, nChars - nextChar);
        System.arraycopy(cb, nextChar, cbuf, off, n);
        nextChar += n;
        return n;
    }

    /**
     * Reads characters into a portion of an array.
     *
     * <p> This method implements the general contract of the corresponding
     * <code>{@link Reader#read(char[], int, int) read}</code> method of the
     * <code>{@link Reader}</code> class.  As an additional convenience, it
     * attempts to read as many characters as possible by repeatedly invoking
     * the <code>read</code> method of the underlying stream.  This iterated
     * <code>read</code> continues until one of the following conditions becomes
     * true: <ul>
     *
     *   <li> The specified number of characters have been read,
     *
     *   <li> The <code>read</code> method of the underlying stream returns
     *   <code>-1</code>, indicating end-of-file, or
     *
     *   <li> The <code>ready</code> method of the underlying stream
     *   returns <code>false</code>, indicating that further input requests
     *   would block.
     *
     * </ul> If the first <code>read</code> on the underlying stream returns
     * <code>-1</code> to indicate end-of-file then this method returns
     * <code>-1</code>.  Otherwise this method returns the number of characters
     * actually read.
     *
     * <p> Subclasses of this class are encouraged, but not required, to
     * attempt to read as many characters as possible in the same fashion.
     *
     * <p> Ordinarily this method takes characters from this stream's character
     * buffer, filling it from the underlying stream as necessary.  If,
     * however, the buffer is empty, the mark is not valid, and the requested
     * length is at least as large as the buffer, then this method will read
     * characters directly from the underlying stream into the given array.
     * Thus redundant <code>BufferedReader</code>s will not copy data
     * unnecessarily.
     *
     * @param      cbuf  Destination buffer
     * @param      off   Offset at which to start storing characters
     * @param      len   Maximum number of characters to read
     *
     * @return     The number of characters read, or -1 if the end of the
     *             stream has been reached
     *
     * @exception  IOException  If an I/O error occurs
     */
    public int read(char cbuf[], int off, int len) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                    ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return 0;
            }

            int n = read1(cbuf, off, len);
            if (n <= 0) return n;
            while ((n < len) && in.ready()) {
                int n1 = read1(cbuf, off + n, len - n);
                if (n1 <= 0) break;
                n += n1;
            }
            return n;
        }
    }

    /**
     * Skips characters.
     *
     * @param  n  The number of characters to skip
     *
     * @return    The number of characters actually skipped
     *
     * @exception  IllegalArgumentException  If <code>n</code> is negative.
     * @exception  IOException  If an I/O error occurs
     */
    public long skip(long n) throws IOException {
        if (n < 0L) {
            throw new IllegalArgumentException("skip value is negative");
        }
        synchronized (lock) {
            ensureOpen();
            long r = n;
            while (r > 0) {
                if (nextChar >= nChars)
                    fill();
                if (nextChar >= nChars) /* EOF */
                    break;
                long d = nChars - nextChar;
                if (r <= d) {
                    nextChar += r;
                    r = 0;
                    break;
                }
                else {
                    r -= d;
                    nextChar = nChars;
                }
            }
            return n - r;
        }
    }

    /**
     * Tells whether this stream is ready to be read.  A buffered character
     * stream is ready if the buffer is not empty, or if the underlying
     * character stream is ready.
     *
     * @exception  IOException  If an I/O error occurs
     */
    public boolean ready() throws IOException {
        synchronized (lock) {
            ensureOpen();

            return (nextChar < nChars) || in.ready();
        }
    }

    /**
     * Tells whether this stream supports the mark() operation, which it does.
     */
    public boolean markSupported() {
        return true;
    }

    /**
     * Marks the present position in the stream.  Subsequent calls to reset()
     * will attempt to reposition the stream to this point.
     *
     * @param readAheadLimit   Limit on the number of characters that may be
     *                         read while still preserving the mark. An attempt
     *                         to reset the stream after reading characters
     *                         up to this limit or beyond may fail.
     *                         A limit value larger than the size of the input
     *                         buffer will cause a new buffer to be allocated
     *                         whose size is no smaller than limit.
     *                         Therefore large values should be used with care.
     *
     * @exception  IllegalArgumentException  If {@code readAheadLimit < 0}
     * @exception  IOException  If an I/O error occurs
     */
    public void mark(int readAheadLimit) throws IOException {
        if (readAheadLimit < 0) {
            throw new IllegalArgumentException("Read-ahead limit < 0");
        }
        synchronized (lock) {
            ensureOpen();
            this.readAheadLimit = readAheadLimit;
            markedChar = nextChar;
        }
    }

    /**
     * Resets the stream to the most recent mark.
     *
     * @exception  IOException  If the stream has never been marked,
     *                          or if the mark has been invalidated
     */
    public void reset() throws IOException {
        synchronized (lock) {
            ensureOpen();
            if (markedChar < 0)
                throw new IOException((markedChar == INVALIDATED)
                        ? "Mark invalid"
                        : "Stream not marked");
            nextChar = markedChar;
        }
    }

    public void close() throws IOException {
        synchronized (lock) {
            if (in == null)
                return;
            try {
                in.close();
            } finally {
                in = null;
                cb = null;
            }
            sourceClosed = true;
        }
    }

    public int readInt() throws IOException {
        ensureOpen();
        String token;
        while (true) {
            token = getCompleteTokenInBuffer();
            if (token != null) {
                break;
            }
            if (needInput)
                fill();
            else
                throwFor();
        }
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException nfe) {
            nextChar = matcher.start(); // don't skip bad token
            throw new InputMismatchException(nfe.getMessage());
        }
    }

    private String getCompleteTokenInBuffer() {
        matcher.usePattern(DELIM_PATTERN);

        // If we are sitting at the end, no more tokens in buffer
        if (nextChar >= nChars) {
            if (sourceClosed)
                return null;
            needInput = true;
            return null;
        }

        // Must look for next delims. Simply attempting to match the
        // pattern at this point may find a match but it might not be
        // the first longest match because of missing input, or it might
        // match a partial token instead of the whole thing.

        // Then look for next delims
        matcher.region(nextChar, nChars);
        if (matcher.find()) {
            if (matcher.start() == nextChar) {
                return null;
            }
            // In the rare case that more input could cause the match
            // to be lost and there is more input coming we must wait
            // for more input. Note that hitting the end is okay as long
            // as the match cannot go away. It is the beginning of the
            // next delims we want to be sure about, we don't care if
            // they potentially extend further.
            if (matcher.requireEnd() && !sourceClosed) {
                needInput = true;
                return null;
            }
            int tokenEnd = matcher.start();
            //  Attempt to match against the desired pattern
            matcher.usePattern(INTEGER_PATTERN);
            matcher.region(nextChar, tokenEnd);
            if (matcher.matches()) {
                String s = matcher.group();
                nextChar = matcher.end();
                return s;
            } else { // Complete token but it does not match
                return null;
            }
        }

        // If we can't find the next delims but no more input is coming,
        // then we can treat the remainder as a whole token
        if (sourceClosed) {
            // Last token; Match the pattern here or throw
            matcher.usePattern(INTEGER_PATTERN);
            matcher.region(nextChar, nChars);
            if (matcher.matches()) {
                String s = matcher.group();
                nextChar = matcher.end();
                return s;
            }
            return null;
        }

        // There is a partial token in the buffer; must read more
        // to complete it
        needInput = true;
        return null;
    }

    // If we are at the end of input then NoSuchElement;
    // If there is still input left then InputMismatch
    private void throwFor() {
        if ((sourceClosed) && (nextChar >= nChars))
            throw new NoSuchElementException();
        else
            throw new InputMismatchException(String.valueOf(cb[nextChar]));
    }

    public int getPosition() {
        return nextChar + offset;
    }

}
