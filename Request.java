public class Request implements Comparable<Request> {
    private int currFloor, destFloor;
    private boolean direction; //true = up, false = down

    /*
    purely a data class, just holds the data in a nice package.
     */

    public Request(int currFloor, int destFloor) {
        this.currFloor = currFloor;
        this.destFloor = destFloor;
        this.direction = currFloor < destFloor;
    }

    public int getCurrFloor() {
        return currFloor;
    }

    public int getDestFloor() {
        return destFloor;
    }

    public boolean getDirection() {
        return direction;
    }

    @Override
    public int compareTo(Request that) {
        return this.destFloor - that.destFloor;
    }
}
