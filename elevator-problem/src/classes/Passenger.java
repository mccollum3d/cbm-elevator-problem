package classes;

public class Passenger {

    private boolean passengerDelivered = false;
    private boolean ascending = false;
    private boolean descending = false;
    private int currentFloor = 1;
    private int targetFloor = 0;
    private int passengerIdentifier = 0;

    public Passenger (int startingFloor, int targetFloor, int passengerIdentifier) {
        setPassengerIdentifier(passengerIdentifier);
        setCurrentFloor(startingFloor);
        setTargetFloor(targetFloor);
        if (startingFloor > targetFloor) {
            setDescending(true);
        } else if (startingFloor < targetFloor) {
            setAscending(true);
        } else {
            //Do nothing. Error handling for start=end handled in Simulation class.
        }
    }

    public boolean isPassengerDelivered() {
        return passengerDelivered;
    }

    public void setPassengerDelivered(boolean passengerDelivered) {
        this.passengerDelivered = passengerDelivered;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
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
