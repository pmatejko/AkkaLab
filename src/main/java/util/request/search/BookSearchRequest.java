package util.request.search;

import util.request.Request;

public class BookSearchRequest implements Request {
    private final String bookTitle;

    public BookSearchRequest(String bookTitle) {
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
