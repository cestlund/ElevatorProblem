/*
    Very rudimentary system to handle queues.
 */

import java.util.LinkedList;
import java.util.Queue;

public class RequestQueue implements Runnable {
    Queue<Request> queue;

    public RequestQueue() {
        queue = new LinkedList<Request>();
    }



    /*
    This thread handles the retrying of requests until an elevator is available.
    This only runs every 3 seconds to reduce strain on the system.
     */
    public void run() {
        while (true) {
            if (SystemController.processRequest(queue.peek()) == 1) {
                queue.remove();
            }
            else {
                try { Thread.sleep(3000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
            }
        }
    }

    //simply adds to the queue.
    public void addToQueue(Request r) {
        if (!queue.contains(r))
            queue.add(r);
    }
}
