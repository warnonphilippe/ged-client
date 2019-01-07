package be.phw.gedclient.client.document.ticket;

import be.phw.gedclient.client.document.DocumentMainClient;

public abstract class PollingTicket<T> implements Runnable {

    private DocumentMainClient client;
    private Long ticketId;
    private TicketSuccesHandler<T> succesHandler;
    private TicketErrorHandler<T> errorHandler;

    public PollingTicket(DocumentMainClient client, Long ticketId, TicketSuccesHandler<T> succesHandler, TicketErrorHandler<T> errorHandler) {
        this.client = client;
        this.ticketId = ticketId;
        this.succesHandler = succesHandler;
        this.errorHandler = errorHandler;
    }

    public DocumentMainClient getClient() {
        return client;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public TicketSuccesHandler<T> getSuccesHandler() {
        return succesHandler;
    }

    public TicketErrorHandler<T> getErrorHandler() {
        return errorHandler;
    }

}
