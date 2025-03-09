package classes;

public class Passenger {

    private boolean passengerDelivered = false;
    private boolean ascending = false;
    private boolean descending = false;
    private int currentFloor = 1;
    private int targetFloor = 0;

    public Passenger (int startingFloor, int targetFloor) {
        setCurrentFloor(startingFloor);
        setTargetFloor(targetFloor);
        if (startingFloor > targetFloor) {
            setDescending(true);
        } else if (startingFloor < targetFloor) {
            setAscending(true);
        } else {
            //If the passenger is randomly assigned to the same floor they started on, they will be considered delivered
            //until such time as I add error catching to ensure they can't start on the floor they want to go to.
            setPassengerDelivered(true);
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
}
