package util.response.stream;

import util.response.Response;

public class BookStreamResponse implements Response {
    private final String bookLine;

    public BookStreamResponse(String bookLine) {
        this.bookLine = bookLine;
    }

    public String getBookLine() {
        return bookLine;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + bookLine;
    }
}
