import java.util.InputMismatchException;
import java.util.Scanner;

public class InputParser implements Runnable {

    public InputParser() {

    }

    public void run() {
        while (true) {
            getFloorRequest(SystemController.MAX_FLOORS);
        }
    }

    /*
    This is the method that requests the data from the user. Saves the data and then sends it to the processRequest method.
     */
    public static void getFloorRequest(int maxFloor) {
        System.out.println("What is your current floor number?");
        int currFloor = getFloor(maxFloor);
        System.out.println("What is your destination floor number?");
        int destFloor = getFloor(maxFloor);
        SystemController.processRequest(new Request(currFloor, destFloor));
    }

    /*
    This method simply processes the user input, and validates the incoming data.
    Returns the user integer.
     */
    private static int getFloor(int maxFloor) {
        int floor = -1;
        Scanner in = new Scanner(System.in);
        while (floor < 1 && floor <= maxFloor) {
            try {
                floor = in.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid floor number! Please try again.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return floor;
    }
}
