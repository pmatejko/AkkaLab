package util.response.search;

import util.response.Response;

public abstract class BookSlaveResponse implements Response {
    private final long searchNumber;

    public BookSlaveResponse(long searchNumber) {
        this.searchNumber = searchNumber;
    }

    public long getSearchNumber() {
        return searchNumber;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + searchNumber;
    }
}
