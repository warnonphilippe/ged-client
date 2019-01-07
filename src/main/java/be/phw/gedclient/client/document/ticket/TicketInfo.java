package be.phw.gedclient.client.document.ticket;

public class TicketInfo {

    private Long ticketId;

    public TicketInfo() {
    }

    public TicketInfo(Long ticketConversionId) {
        this.ticketId = ticketConversionId;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
