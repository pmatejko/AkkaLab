package util.response.search;

public class BookNotFoundSlaveResponse extends BookSlaveResponse {
    public BookNotFoundSlaveResponse(long searchNumber) {
        super(searchNumber);
    }

    @Override
    public String toString() {
        return super.toString() + "; " + getClass().getName();
    }
}
