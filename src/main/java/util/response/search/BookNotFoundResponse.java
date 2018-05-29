package util.response.search;

import util.response.Response;

public class BookNotFoundResponse implements Response {
    @Override
    public String toString() {
        return getClass().getName();
    }
}
