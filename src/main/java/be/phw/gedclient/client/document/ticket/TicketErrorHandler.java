package be.phw.gedclient.client.document.ticket;

public interface TicketErrorHandler<T> {

    public void execute(T ticketDetail);

}
