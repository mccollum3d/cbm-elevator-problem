package classes;

import java.util.ArrayList;

public class Elevator {

    /**
     * Non user adjustable variables
     */
    private static final String[] elevatorNames = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    private String elevatorName;
    private int currentFloor;
    private int targetFloor;
    private int passengersDelivered = 0;
    private int floorsTraveled = 0;
    private ArrayList<Passenger> ridingElevator = new ArrayList<>();
    private direction elevatorDirection = direction.IDLE;
    private boolean isDoorOpen = false;

    public enum direction {
        UP,
        DOWN,
        IDLE
    }

    /**
     * User adjustable variables
     */
    public int capacity = 4;
    public int floorsPerMinute = 1;

    public Elevator(int elevatorName) {
        setElevatorName(elevatorNames[elevatorName]);
        currentFloor = 1;
    }

    public Elevator(int currentFloor, int elevatorName) {
        setElevatorName(elevatorNames[elevatorName]);
        setCurrentFloor(currentFloor);
    }

    public String getElevatorName() {
        return elevatorName;
    }

    public void setElevatorName(String elevatorName) {
        this.elevatorName = elevatorName;
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

    public int getPassengersDelivered() {
        return passengersDelivered;
    }

    public void setPassengersDelivered(int passengersDelivered) {
        this.passengersDelivered = passengersDelivered;
    }

    public int getFloorsTraveled() {
        return floorsTraveled;
    }

    public void setFloorsTraveled(int floorsTraveled) {
        this.floorsTraveled = floorsTraveled;
    }

    public ArrayList<Passenger> getRidingElevator() {
        return ridingElevator;
    }

    public void setRidingElevator(ArrayList<Passenger> ridingElevator) {
        this.ridingElevator = ridingElevator;
    }

    public direction getElevatorDirection() {
        return elevatorDirection;
    }

    public void setElevatorDirection(direction elevatorDirection) {
        this.elevatorDirection = elevatorDirection;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getFloorsPerMinute() {
        return floorsPerMinute;
    }

    public void setFloorsPerMinute(int floorsPerMinute) {
        this.floorsPerMinute = floorsPerMinute;
    }

    public boolean isDoorOpen() {
        return isDoorOpen;
    }

    public void setDoorOpen(boolean doorOpen) {
        this.isDoorOpen = doorOpen;
    }
}
