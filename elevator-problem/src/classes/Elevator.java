package classes;

import java.util.ArrayList;

public class Elevator {

    /**
     * Non user adjustable variables
     */
    private boolean ascending;
    private boolean descending;
    private boolean doorOpen;
    private boolean idle;
    private int currentPassengers;
    private int currentFloor;
    private int targetFloor;
    private int passengersDelivered = 0;
    private int floorsTraveled = 0;
    private ArrayList<Passenger> ridingElevator = new ArrayList<>();

    /**
     * User adjustable variables
     */
    public int capacity = 4;
    public int floorsPerMinute = 1;

    public Elevator() {
        ascending = false;
        descending = false;
        doorOpen = false;
        idle = false;
        currentPassengers = 0;
        currentFloor = 1;
    }

    public Elevator(int currentFloor) {
        ascending = false;
        descending = false;
        doorOpen = false;
        idle = false;
        currentPassengers = 0;
        setCurrentFloor(currentFloor);
    }

    public void updateElevator(boolean ascending, boolean descending, boolean doorOpen, int currentPassengers, int currentFloor) {
        setAscending(ascending);
        setDescending(descending);
        setDoorOpen(doorOpen);
        setCurrentPassengers(currentPassengers);
        setCurrentFloor(currentFloor);
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

    public boolean isDoorOpen() {
        return doorOpen;
    }

    public void setDoorOpen(boolean doorOpen) {
        this.doorOpen = doorOpen;
    }

    public int getCurrentPassengers() {
        return currentPassengers;
    }

    public void setCurrentPassengers(int currentPassengers) {
        this.currentPassengers = currentPassengers;
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

    public int getCurrentFloor() {
        return currentFloor;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public ArrayList<Passenger> getRidingElevator() {
        return ridingElevator;
    }

    public void setRidingElevator(ArrayList<Passenger> ridingElevator) {
        this.ridingElevator = ridingElevator;
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
}
