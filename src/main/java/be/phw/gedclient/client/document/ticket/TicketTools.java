package be.phw.gedclient.client.document.ticket;

import org.springframework.http.ResponseEntity;

import be.phw.gedclient.client.document.DocumentMainClient;

public class TicketTools {

    private static final int THREAD_SLEEP = 3000;

    public static Thread whenTicketConversionHandled(DocumentMainClient client, Long ticketId, TicketSuccesHandler<TicketConversion> successHandler, TicketErrorHandler<TicketConversion> errorHandler){

        PollingTicket polling = new PollingTicket<TicketConversion>(client, ticketId, successHandler, errorHandler) {

            @Override
            public void run() {

                TicketConversion ticket = null;

                try{
                    //attendre fin exécution de la conversion
                    boolean ended = false;
                    while (!ended){
                        ResponseEntity<TicketConversion> resp = this.getClient().getTicketConversion(this.getTicketId());
                        if (resp.getBody() != null){
                            ticket = resp.getBody();
                            if (ticket.getOk() != null){
                                ended = true;
                            }
                        }
                        if (!ended){
                            Thread.sleep(THREAD_SLEEP);
                        }
                    }

                    if (ticket != null && Boolean.TRUE.equals(ticket.getOk())){
                        this.getSuccesHandler().execute(ticket);
                    } else {
                        this.getErrorHandler().execute(ticket);
                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                    if (ticket == null) ticket = new TicketConversion();
                    ticket.setErrorMsg(ex.getMessage());
                    this.getErrorHandler().execute(ticket);
                }

            }
        };

        Thread thread = new Thread(polling);
        thread.start();
        return thread;
    }

    public static Thread whenTicketMergeHandled(DocumentMainClient client, Long ticketId, TicketSuccesHandler<TicketMerge> successHandler, TicketErrorHandler<TicketMerge> errorHandler){

        PollingTicket polling = new PollingTicket<TicketMerge>(client, ticketId, successHandler, errorHandler) {

            @Override
            public void run() {

                TicketMerge ticket = null;

                try{
                    //attendre fin exécution du merge
                    boolean ended = false;
                    while (!ended){
                        ResponseEntity<TicketMerge> resp = this.getClient().getTicketMerge(this.getTicketId());
                        if (resp.getBody() != null){
                            ticket = resp.getBody();
                            if (ticket.getOk() != null){
                                ended = true;
                            }
                        }
                        if (!ended){
                            Thread.sleep(THREAD_SLEEP);
                        }
                    }

                    if (ticket != null && Boolean.TRUE.equals(ticket.getOk())){
                        this.getSuccesHandler().execute(ticket);
                    } else {
                        this.getErrorHandler().execute(ticket);
                    }

                } catch (Exception ex){
                    ex.printStackTrace();
                    if (ticket == null) ticket = new TicketMerge();
                    ticket.setErrorMsg(ex.getMessage());
                    this.getErrorHandler().execute(ticket);
                }

            }
        };

        Thread thread = new Thread(polling);
        thread.start();
        return thread;
    }

    public static void wait(Thread t, int delayInSeconds) throws InterruptedException {
        if (t.isAlive()){
            t.join(delayInSeconds * 1000);
        }
    }

}
