package util.response.search;

public class BookFoundSlaveResponse extends BookSlaveResponse {
    private final String bookTitle;
    private final int bookPrice;

    public BookFoundSlaveResponse(long searchNumber, String bookTitle, int bookPrice) {
        super(searchNumber);
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
        return super.toString() + "; " + getClass().getName() + ": " + bookTitle + ", " + bookPrice;
    }
}
