import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SystemControllerTest {

    @BeforeEach
    void setUp() {
        SystemController.main();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void processRequestUpElevator() {
        assertEquals(1, SystemController.processRequest(new Request(2, 5)));
    }

    @Test
    void processRequestDownElevatorFromIdle() {
        assertEquals(1, SystemController.processRequest(new Request(6,2)));
    }

    @Test
    void processRequestDownElevatorNotIdle() throws InterruptedException {
        SystemController.processRequest(new Request(2, 4));
        Thread.sleep(18000);
        SystemController.processRequest(new Request(2,12));
        Thread.sleep(12000);
        assertEquals(1, SystemController.processRequest(new Request(3,2)));
    }

    @Test
    void processRequestNoAvailableElevator() throws InterruptedException {
        SystemController.processRequest(new Request(2, 4));
        Thread.sleep(18000);
        SystemController.processRequest(new Request(2,12));
        assertEquals(-1, SystemController.processRequest(new Request(3, 2)));
    }

    @Test
    void processRequestMultipleElevators() throws InterruptedException {
        SystemController.processRequest(new Request(2, 4));
        Thread.sleep(18000);
        SystemController.processRequest(new Request(2,12));
        assertFalse(SystemController.getElevators()[0].isIdle() && SystemController.getElevators()[1].isIdle());
    }

    @Test
    void elevatorPositionMovesUp() throws InterruptedException {
        SystemController.processRequest(new Request(2, 4));
        Thread.sleep(6000);
        assertEquals(2, SystemController.getElevators()[0].getCurrFloor());
    }

    @Test
    void elevatorPositionMovesDown() throws InterruptedException {
        SystemController.processRequest(new Request(3, 2));
        Thread.sleep(12000);
        assertEquals(3, SystemController.getElevators()[0].getCurrFloor());
        Thread.sleep(12000);
        assertEquals(2, SystemController.getElevators()[0].getCurrFloor());
    }

    @Test
    void addStop() {
        SystemController.getElevators()[0].addStop(new Request(2, 3));
        assertEquals(2, SystemController.getElevators()[0].getStops().get(0));
        assertEquals(3, SystemController.getElevators()[0].getStops().get(1));
    }

    @Test
    void addSingleStop() {
        SystemController.getElevators()[0].addSingleStop(2);
        assertEquals(2, SystemController.getElevators()[0].getStops().get(0));
    }

    @Test
    void multipleQueue() throws InterruptedException {
        SystemController.processRequest(new Request(3, 6));
        Thread.sleep(24000);
        SystemController.processRequest(new Request(2, 5));
        SystemController.processRequest(new Request(5, 3));
        SystemController.processRequest(new Request(4, 2));
        Thread.sleep(24000);
        assertEquals(2, SystemController.getElevators()[0].getStops().get(0));
        assertEquals(3, SystemController.getElevators()[0].getStops().get(1));
        assertEquals(4, SystemController.getElevators()[0].getStops().get(2));
        assertEquals(5, SystemController.getElevators()[0].getStops().get(3));
    }
}