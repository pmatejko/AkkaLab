package util.request.order;

import util.request.Request;

public class BookOrderRequest implements Request {
    private final String bookTitle;

    public BookOrderRequest(String bookTitle) {
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
