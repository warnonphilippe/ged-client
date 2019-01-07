package be.phw.gedclient.client.document.ticket;

import java.io.IOException;

public interface TicketSuccesHandler<T> {

    public void execute(T ticketDetail) throws IOException;
}
