package util.response.search;

import util.response.Response;

public class BookFoundResponse implements Response {
    private final String bookTitle;
    private final int bookPrice;

    public BookFoundResponse(String bookTitle, int bookPrice) {
        this.bookTitle = bookTitle;
        this.bookPrice = bookPrice;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public int getBookPrice() {
        return bookPrice;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + bookTitle + ", " + bookPrice;
    }
}
