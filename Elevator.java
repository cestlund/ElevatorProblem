/*
Thread used from an individual elevator class to move its location.
 */

import com.sun.source.tree.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class Elevator implements Runnable {
    private int currFloor, elevNum;
    private Request destFloor;

    private boolean direction;

    public boolean isIdle() {
        return idle;
    }

    private boolean idle;

    private ArrayList<Integer> stops = new ArrayList<Integer>(); //Using TreeSet to have an auto sorted list. And prevents duplicates

    public Elevator(int currFloor, int elevNum, boolean direction) {
        this.currFloor = currFloor;
        this.elevNum = elevNum;
        this.direction = direction;
        this.idle = true;
    }

    /*
    This method prepares the elevator by adding the initial two stops, and setting the elevator to not idle.
     */
    public void prepareElevator(Request destFloor) {
        this.stops.add(destFloor.getCurrFloor());
        this.stops.add(destFloor.getDestFloor());
        this.idle = false;
        System.out.println("Elevator " + this.elevNum + " is active.");
    }


    /*
    The run method is the methon ran when the thread is started.
    This method will run a loop until there are no more stops in the list.
    If there is a stop, it will move up one floor at a time, via moveUp, and moveDown methods until it reaches that floor.
    If it reaches a floor with a stop, it will open/close doors then remove the stop from the list.
    Once there are no more floors, it will loop back to ground floor. If a stop is added while moving to ground floor it will stop and handle those stops.
     */
    public void run() {
        while (!stops.isEmpty()) { //while there are stops.
            if (stops.size() == 1) { //this handles the edge case of going down with idle elevator. ensures the direction is changed if the stop requires it.
                if (stops.get(0) > this.currFloor)
                    this.direction = true;
                else
                    this.direction = false;
            }
            int nextFloor;
            //this gets the location of the next floor, based on the direction.
            if (direction)
                nextFloor = stops.get(0);
            else
                nextFloor = stops.get(stops.size() - 1);
            //this ensures the elevator doesn't move opposite the current direction boolean. if so, it kills the elevator.
            if (direction && this.currFloor > nextFloor || !direction && this.currFloor < nextFloor) {
                System.out.println("Error! Elevator " + this.elevNum + " tried to move the wrong direction.");
                this.idle = true;
                Thread.currentThread().interrupt();
            }
            //this opens/closes the doors and removes stop when reached.
            if (this.currFloor == nextFloor) {
                doors();
                if (direction)
                    stops.remove(stops.get(0));
                else
                    stops.remove(stops.get(stops.size() - 1));
            }
            //if not a stop, then moves in the given direction.
            else {
                if (nextFloor > this.currFloor) {//needs to go up
                    moveUp();
                }
                else { //needs to go down
                    moveDown();
                }
            }
            //this handles the returning to ground floor. This is within the first loop, so if a new stop is added, it will exit the loop and move the elevator.
            if (stops.isEmpty())
                System.out.println("Elevator " + this.elevNum + " is returning to ground level (floor 1)."); //return to ground floor
            while (stops.isEmpty() && this.currFloor > 1) {
                if (direction) this.direction = false; //sets the direction to down if going up.
                moveDown();
            }
        }
        //once the elevator reaches ground floor, it becomes idle.
        if (this.currFloor == 1) {
            System.out.println("Elevator " + this.elevNum + " is now idle.");
            this.idle = true;
            this.direction = true;
        }
    }

    //this method opens the doors, and is purely "visual"
    private void doors() {
        System.out.println("Doors now opening on floor " + this.currFloor);
        try { Thread.sleep(5000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
        System.out.println("Doors now closing on floor " + this.currFloor);
        try { Thread.sleep(1000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
    }

    //this method moves the elvator up, printing and increasing currfloor by 1.
    private void moveUp() {
        //System.out.println("Elevator " + this.elevNum + " is moving up to floor " + (this.currFloor + 1));
        try { Thread.sleep(5000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
        System.out.println("Elevator " + this.elevNum + " has arrived at floor " + ++this.currFloor); //lets the user know where the elevator is, and increments currFloor.
        try { Thread.sleep(1000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
    }

    //this method moves the elvator down, printing and decreasing currfloor by 1.
    private void moveDown() {
        //System.out.println("Elevator " + this.elevNum + " is moving down to floor " + (this.currFloor - 1)); //this was commented as it became too convoluted in the command window
        try { Thread.sleep(5000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
        System.out.println("Elevator " + this.elevNum + " has arrived at floor " + --this.currFloor);
        try { Thread.sleep(1000); } catch (InterruptedException e){ Thread.currentThread().interrupt(); }
    }

    /*
    this method adds two stops from a new request.
    there is an edge case to handle the going down from idle elevator.
    The method checks to see if the last element is less than the element before it. If so it stores it, and removes the last element.
    The method then as normal adds the two new stops, and sorts them.
    If the temp stores element is not null it is then readded to the end.
     */
    public void addStop(Request newStop) {
        Integer tempStore = null;
        if (stops.size() > 1 && stops.get(stops.size() - 1) < stops.get(stops.size() - 2) && this.direction) {
            tempStore = stops.get(stops.size() - 1);
            stops.remove(tempStore);
        }
        if (!this.stops.contains(newStop.getCurrFloor())) //if not already in the list (avoids duplicates)
            this.stops.add(newStop.getCurrFloor());
        if (!this.stops.contains(newStop.getDestFloor())) //if not already in the list (avoids duplicates)
            this.stops.add(newStop.getDestFloor());
        Collections.sort(this.stops);
        if (tempStore != null)
            stops.add(tempStore);

    }

    /*
    similar to prepareElevator, except this only prepares a single stop rather than 2.
     */
    public void prepareSingleStop(Integer stop) {
        this.stops.add(stop);
        this.idle = false;
        System.out.println("Elevator " + this.elevNum + " is active.");
    }
    public void addSingleStop(Integer stop) {
        this.stops.add(stop);
    }

    public int getCurrFloor() {
        return this.currFloor;
    }

    public boolean getDirection() {
        return direction;
    }

    public ArrayList<Integer> getStops() {
        return stops;
    }

    public String toString() {
        return "This is elevator " + this.elevNum;
    }
}
