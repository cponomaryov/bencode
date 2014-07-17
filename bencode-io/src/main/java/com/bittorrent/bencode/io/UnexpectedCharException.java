package com.bittorrent.bencode.io;

public class UnexpectedCharException extends BencodeParseException {

    private int ch;

    private int location;

    public UnexpectedCharException(int ch, int location, String message) {
        super("Unexpected character (" + getCharDesc(ch) + ") at location " + location + ": " + message);
        this.ch = ch;
        this.location = location;
    }

    protected static String getCharDesc(int ch) {
        final char c = (char) ch;
        if (Character.isISOControl(c)) {
            return "(CTRL-CHAR, code " + ch + ")";
        }
        if (ch > 255) {
            return "'" + c + "' (code " + ch + " / 0x" + Integer.toHexString(ch) + ")";
        }
        return "'" + c + "' (code " + ch + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnexpectedCharException that = (UnexpectedCharException) o;

        if (ch != that.ch) return false;
        if (location != that.location) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ch;
        result = 31 * result + location;
        return result;
    }

}
