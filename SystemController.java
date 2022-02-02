/*
Main Controller of the elevator system. Supports multiple elevators.
    - Starts InputParser as a thread, which it directly communicates with to get incoming user floor requests, and
    other input.
    - Creates elevator objects, depending on how many the user requests. Changed by changing TOTAL_ELEVATORS final value
    - Takes user floor requests, queries elevators to see what floor they are on, and then calculates the best elevator
    to send to requested floor.
    - The SystemControllerTest.java contains some JUnit methods to test common elevator problems. Does not test every individual method.
 */

public class SystemController {

    private static final int TOTAL_ELEVATORS = 2; //can be changed for more elevators. If changed from 2, the JUnit tests will break.

    private static Elevator[] elevators = new Elevator[TOTAL_ELEVATORS];
    public final static int MAX_FLOORS = 12; //can be changed for more floors
    private static RequestQueue queue;

    public static void main(String ... args) {
        for (int i = 0; i < TOTAL_ELEVATORS; i++) { //init elevators
            elevators[i] = new Elevator(1, i+1, true);
        }


        InputParser parser = new InputParser(); //Creates a parser object
        Thread thread = new Thread(parser); //passes that runnable parser object into a thread
        thread.start(); //.start() method will run the run() methon within the InputParser object.

        queue = new RequestQueue();
        Thread thread1 = new Thread(queue);  //same as above with inputparser, instead with RequestQueue object.
        thread1.start();
    }


    /*
    this is the method called by the InputParser when it creates a new Request object
    Method returns 1 for a successful parsing and add to an elevator.
    Method returns -1 for an unsuccessful parsing (adding to the queue instead)
     */
    public static int processRequest(Request request) {
        Elevator closest = null;
        int closestDist = -1;
        if (request == null)
            return -1;
        /*
        This if else block finds the closest elevator moving in the same direction of the request.
        Done by looping through each elevator, and subtracting request curr floor and elevator curr floor. Vice/Versa for down.
         */
        if (request.getDirection()) { //if moving up
            for (int i = 0; i < TOTAL_ELEVATORS; i++) {
                if (elevators[i].getDirection()) {
                    int tempDist = request.getCurrFloor() - elevators[i].getCurrFloor();
                    if (tempDist < closestDist && tempDist > 0 || closestDist == -1 && tempDist > 0) { //if the temporary dist is less (closer) than curr close then save it.
                        closest = elevators[i];
                        closestDist = tempDist;
                    }
                }
            }
        } else { //if moving down
            for (int i = 0; i < TOTAL_ELEVATORS; i++) {
                if (!elevators[i].getDirection()) {
                    int tempDist = elevators[i].getCurrFloor() - request.getCurrFloor();
                    if (tempDist < closestDist && tempDist > 0 || closestDist == -1 && tempDist > 0) {
                        closest = elevators[i];
                        closestDist = tempDist;
                    }
                }
            }
        }
        /*
        this block is entered to test if there is an available elevator
         */
        if (closest == null) { //if no elevator available
            boolean anyIdle = false;
            Elevator idle = null;
            for (int i = 0; i < TOTAL_ELEVATORS; i++) {
                if (elevators[i].isIdle()) {
                    anyIdle = true;
                    idle = elevators[i];
                    break;
                }
            }
            /*
            at this point, there are no elevators going down, and there are idle elevators.
            This is an edge case, where we need the elevator to go up to 1 floor, and back down to 1 floor.
            This warranted a creation of preparing and adding single stops, seen in the Elevator class.
             */
            if (anyIdle) {
                idle.prepareSingleStop(request.getCurrFloor());
                Thread thread = new Thread(idle);
                thread.start();
                idle.addSingleStop(request.getDestFloor());
                return 1;
            }
            /*
            Here this means a request wants to go a direction not available, and with no idle elevators.
            The request is then sent to the queue class to be handled there.
             */
            else {
                queue.addToQueue(request);
                return -1;
            }
        }
        /*
        Here we have exhausted every other option/edge case (further testing needed to be 100% sure), and an elevator is available for use.
         */
        else {
            //if the elevator is idle, prepare it, start the elevator in a thread and return 1.
            if (closest.isIdle()) {
                closest.prepareElevator(request);
                Thread thread = new Thread(closest);
                thread.start();
                return 1;
            }
            //if the elvator is not idle, then simple add a stop to the closest elevator and return 1.
            else {
                closest.addStop(request);
                return 1;
            }
        }
    }

    //simple getter for elevator
    public static Elevator[] getElevators() {
        return elevators;
    }
}
