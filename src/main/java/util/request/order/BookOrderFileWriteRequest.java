package util.request.order;

import util.request.Request;

public class BookOrderFileWriteRequest implements Request {
    private final String line;

    public BookOrderFileWriteRequest(String line) {
        this.line = line.endsWith("\n") ? line : line + "\n";
    }

    public String getLine() {
        return line;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + line.replace("\n", "");
    }
}
