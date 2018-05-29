package util;

import akka.actor.ActorRef;

public class SearchStatus {
    private boolean found;
    private int completeSearchesAmount;
    private final ActorRef customer;

    public SearchStatus(ActorRef customer) {
        this.customer = customer;
        this.found = false;
        this.completeSearchesAmount = 0;
    }

    public boolean isFound() {
        return found;
    }

    public int getCompleteSearchesAmount() {
        return completeSearchesAmount;
    }

    public ActorRef getCustomer() {
        return customer;
    }

    public void incrementCompleteSearchesAmount() {
        ++completeSearchesAmount;
    }

    public void setFound() {
        found = true;
    }
}
