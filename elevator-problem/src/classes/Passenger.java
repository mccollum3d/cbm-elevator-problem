package classes;

public class Passenger {

    private int currentFloor = 1;
    private int targetFloor = 0;
    private int passengerIdentifier = 0; //used if viewing console logs to identify passengers for debugging

    public Passenger (int startingFloor, int targetFloor, int passengerIdentifier) {
        setPassengerIdentifier(passengerIdentifier);
        setCurrentFloor(startingFloor);
        setTargetFloor(targetFloor);
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public int getPassengerIdentifier() {
        return passengerIdentifier;
    }

    public void setPassengerIdentifier(int passengerIdentifier) {
        this.passengerIdentifier = passengerIdentifier;
    }
}
