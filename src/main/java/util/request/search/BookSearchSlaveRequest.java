package util.request.search;

public class BookSearchSlaveRequest extends BookSearchRequest {
    private final long searchNumber;

    public BookSearchSlaveRequest(long searchNumber, String bookTitle) {
        super(bookTitle);
        this.searchNumber = searchNumber;
    }

    public long getSearchNumber() {
        return searchNumber;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + searchNumber + ", " + getBookTitle();
    }
}
