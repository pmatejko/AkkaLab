package util.request.stream;

import util.request.Request;

public class BookStreamRequest implements Request {
    private final String bookTitle;

    public BookStreamRequest(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + bookTitle;
    }
}
