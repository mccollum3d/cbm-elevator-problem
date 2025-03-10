package classes;

import java.util.ArrayList;
import java.util.Random;

public class Simulation {

    /************************
     * Simulation parameters
     * assumes user enters only valid values
     * Example Simulation Cases:
     * Default values: 4, 25,true, 4, 150, 20, true, false, 6, 18, 10
     * Going up: 10, 100, false, 8, 100, 80, false, false, 6, 18, 10
     * Busy Buidling: 4, 20, true, 5, 100, 15, true, true, 3, 8, 12
     */
    public int numberOfElevators = 4;   //Valid values: 1-26  --Reason is to label elevators A-Z.
    public int numberOfFloors = 25;     //Valid values: 1-n
    public boolean distributeElevatorsToRandomFloorsAtStart = true;    //Valid values: true/false
    public int elevatorCapacity = 4;    //Valid values: 1-n

    public int simulationRunTime = 150; //Valid values: 1-n, simulates n minutes of elevator usage

    public int startingPassengers = 20;     //Valid values: 1-n
    public boolean distributePassengersToRandomFloorsAtStart = true;

    //If you add too many new passengers/minute, the simulation may not complete in the run time selected.
    //It also often adds a new passenger at the last moment, so while this can be an interesting testing tool,
    //it often results in undelivered passengers.
    public boolean enableNewArrivals = false;
    public int minimumMinutesToNewPassenger = 8;  //Valid values: 1-n
    public int maximumMinutesToNewPassenger = 12;  //Valid values: 1-n
    private int nextPassengerArrives = 10;          //when the first passenger arrives (i.e. 10 minutes then one more every 6-18 minutes)

    /**
     * End of simulation parameters
     ***********************/

    /**
     * Sets the starting locations of each passenger, and their destination.
     * Sets the starting locations of each elevator.
     *
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */
    public void initializeSimulation(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        // creates the starting conditions of the simulation
        // random number between 1 (inclusive) and n (exclusive) therefore n+1 becomes boundary of rng.
        Random rand = new Random();

        //Initialize Passengers
        if (distributePassengersToRandomFloorsAtStart) {
            for (int p = 0; p < startingPassengers; p++) {
                //inclusive of 0, exclusive of highest floor, +1 fixes this so random floor = 1-n
                int start = rand.nextInt(numberOfFloors) + 1;
                int end = rand.nextInt(numberOfFloors) + 1;
                while (start == end) {
                    start = rand.nextInt(numberOfFloors) + 1;
                    end = rand.nextInt(numberOfFloors) + 1;
                }
                passengerList.add(new Passenger(start, end, p + 1));
            }
        } else {
            for (int p = 0; p < startingPassengers; p++) {
                int end = rand.nextInt(numberOfFloors) + 1;
                while (end == 1) {
                    end = rand.nextInt(numberOfFloors) + 1;
                }
                passengerList.add(new Passenger(1, end, p + 1));
            }
        }

        //Initialize Elevators
        if (distributeElevatorsToRandomFloorsAtStart) {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator(elevatorCapacity, rand.nextInt(numberOfFloors + 1), e));
            }
        } else {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator(elevatorCapacity, e));
            }
        }

        printStartingLocations(passengerList, elevatorList);
        System.out.println("Simulation Initialized! Now calculating...");
        runSimulation(passengerList, elevatorList);
    }

    /**
     * Executes a simulation of elevator(s) picking up passengers and delivers them
     *
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */
    public void runSimulation(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        //Runs the simulation and returns the results
        for (int minutes = 0; minutes <= simulationRunTime; minutes++) {
            // Deliver Passengers: if elevator is on target floor with passenger, remove passenger
            //  Assumption: passengers are polite and let people off before getting on, to free up capacity.
            deliverPassengers(elevatorList);

            // Pick up passengers: if elevator is on floor with passenger, add passenger
            pickUpPassengers(passengerList, elevatorList);

            // Call method to retrieve location info on each elevator, and assign target floor
            assignElevatorInstructions(passengerList, elevatorList);

            // Advance all elevators 1 floor
            advanceSimulationOneMinute(passengerList, elevatorList);

            //new passengers arrive
            if (enableNewArrivals) {
                Random rand = new Random();
                //spawns new passengers every x-y minutes, but stops near the end to give elevators a chance to finish all deliveries.
                if (minutes == nextPassengerArrives && minutes < simulationRunTime - 20) {
                    passengerList.add(new Passenger(rand.nextInt(1, numberOfFloors + 1), rand.nextInt(1, numberOfFloors + 1), passengerList.size() + 1));
                    nextPassengerArrives = rand.nextInt(minimumMinutesToNewPassenger, maximumMinutesToNewPassenger) + minutes;
                }
            }

            // Check if all passengers delivered
            if (isSimulationComplete(passengerList, elevatorList, minutes)) {
                break;
            }

            //Enable/Disable this to see ALL passenger and elevator actions
            // When logging, suggest elevators < 4 and Passengers < 12 for readability
             detailedLogging(passengerList, elevatorList);

            if (minutes == simulationRunTime) {
                System.out.println("Simulation reached maximum run time of " + simulationRunTime + " without delivering all passengers. Install more elevators.");
                System.out.println("Enable logging to see all elevator and passenger actions by uncommenting // detailedLogging(passengerList, elevatorList)");
            }

        }

    }

    /**
     * Helper Methods
     */

    /**
     * Handles the loading of passengers after any passengers on board the elevator that wanted to get off have done so.
     *
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */

    private void pickUpPassengers(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();


        for (Elevator elevator : elevatorList) {
            if (elevator.getRidingElevator().size() == elevator.getCapacity()) {
                continue;
            }
            for (Passenger passenger : passengerList) {
                //Passengers picked up, if elevator has capacity
                if (passenger.getCurrentFloor() == elevator.getCurrentFloor() && elevator.getRidingElevator().size() < elevator.getCapacity()) {
                    //Determine when the doors open if the passenger steps on. They step on if the elevator is going in the direction they want to go, or it is empty
                    if ((elevator.getElevatorDirection().equals(Elevator.direction.UP) && passenger.getTargetFloor() > elevator.getCurrentFloor()) ||
                            (elevator.getElevatorDirection().equals(Elevator.direction.DOWN) && passenger.getTargetFloor() < elevator.getCurrentFloor()) ||
                            (elevator.getRidingElevator().isEmpty())) {
                        elevator.getRidingElevator().add(passenger);
                        elevator.setTargetFloor(passenger.getTargetFloor());
                        passengersToRemove.add(passenger);
                        if (elevator.getTargetFloor() < elevator.getCurrentFloor()) {
                            elevator.setElevatorDirection(Elevator.direction.DOWN);
                        } else {
                            elevator.setElevatorDirection(Elevator.direction.UP);
                        }
                    }
                }
            }
            //Passengers removed outside of inner for loopto avoid concurrent modification exception
            if (!passengersToRemove.isEmpty()) {
                passengerList.removeAll(passengersToRemove);
                elevator.setDoorOpen(true);
            }
        }
    }

    /**
     * Unload passengers who have arrived on their target floor.
     *
     * @param elevatorList List of all elevators in service
     */

    private void deliverPassengers(ArrayList<Elevator> elevatorList) {
        for (Elevator elevator : elevatorList) {
            if (elevator.getRidingElevator().isEmpty()) {
                continue;
            }
            ArrayList<Passenger> passengersToRemove = new ArrayList<>();
            for (Passenger passenger : elevator.getRidingElevator()) {
                if (passenger.getTargetFloor() == elevator.getCurrentFloor()) {
                    passengersToRemove.add(passenger);
                    elevator.setPassengersDelivered(elevator.getPassengersDelivered() + 1);
                    elevator.setDoorOpen(true);
                }
                if (elevator.getRidingElevator().isEmpty()) {
                    elevator.setElevatorDirection(Elevator.direction.IDLE);
                }
            }
            if (!passengersToRemove.isEmpty()) {
                elevator.getRidingElevator().removeAll(passengersToRemove);
            }
        }
    }


    private void assignElevatorInstructions
            (ArrayList<Passenger> waitingPassengerList, ArrayList<Elevator> elevatorList) {

        for (Elevator elevator : elevatorList) {

            //Simulates the door open/close time by causing an elevator to skip 1 movement when picking up or delivering a passenger
            if (elevator.isDoorOpen()) {
                elevator.setDoorOpen(false);
                continue;
            }

            //ensures the elevator turns around and does not keep it's "UP" designation on the top floor.
            if (elevator.getCurrentFloor() >= numberOfFloors) {
                elevator.setElevatorDirection(Elevator.direction.DOWN);
            }
            if (elevator.getCurrentFloor() <= 1) {
                elevator.setElevatorDirection(Elevator.direction.UP);
            }

            if (elevator.getElevatorDirection().equals(Elevator.direction.UP)) {
                //Continue upward picking up/delivering until reaching highest requested floor
                for (Passenger passenger : elevator.getRidingElevator()) {
                    if (passenger.getTargetFloor() > elevator.getTargetFloor()) {
                        elevator.setTargetFloor(passenger.getTargetFloor());
                    }
                }
            }

            if (elevator.getElevatorDirection().equals(Elevator.direction.DOWN)) {
                //Continue downward picking up/delivering until reaching lowest requested floor
                for (Passenger passenger : elevator.getRidingElevator()) {
                    if (passenger.getTargetFloor() < elevator.getTargetFloor()) {
                        elevator.setTargetFloor(passenger.getTargetFloor());
                    }
                }
            }

            if (elevator.getElevatorDirection().equals(Elevator.direction.IDLE)) {
                //No passengers on board, so continuing a direction of travel is not an option. Locate nearest waiting passenger.
                if (!waitingPassengerList.isEmpty()) {
                    Passenger nearestPassenger = waitingPassengerList.getFirst();
                    for (Passenger passenger : waitingPassengerList) {
                        if (Math.abs(elevator.getCurrentFloor() - passenger.getCurrentFloor()) < Math.abs(elevator.getCurrentFloor() - nearestPassenger.getCurrentFloor())) {
                            nearestPassenger = passenger;
                        }
                    }
                    //Set new direction of travel to pick up nearest passenger & deliver them
                    if (nearestPassenger.getTargetFloor() > elevator.getCurrentFloor()) {
                        elevator.setElevatorDirection(Elevator.direction.UP);
                        elevator.setTargetFloor(nearestPassenger.getCurrentFloor());
                    } else if (nearestPassenger.getTargetFloor() < elevator.getCurrentFloor()) {
                        elevator.setElevatorDirection(Elevator.direction.DOWN);
                        elevator.setTargetFloor(nearestPassenger.getCurrentFloor());
                    }
                }
            }
        }


    }

    /**
     * Advances the elevators by one floor, using their current directional value which was set when picking up passengers.
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */

    private void advanceSimulationOneMinute(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        for (Elevator elevator : elevatorList) {
            if (elevator.getElevatorDirection().equals(Elevator.direction.UP)) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                elevator.setFloorsTraveled(elevator.getFloorsTraveled() + 1);
            } else if (elevator.getElevatorDirection().equals(Elevator.direction.DOWN)) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
                elevator.setFloorsTraveled(elevator.getFloorsTraveled() + 1);
            } else {
                elevator.setElevatorDirection(Elevator.direction.IDLE);
            }
        }
    }

    /**
     * Checks if the simulation is complete and all passengers delivered
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     * @param minutesElapsed how long the simulation has been running, where one iteration = one minute
     * @return
     */

    private boolean isSimulationComplete(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList, int minutesElapsed) {
        boolean allElevatorsEmpty = true;
        int totalFloorsTraveled = 0;
        int totalPassengersUndelivered = passengerList.size();

        for (Elevator elevator : elevatorList) {
            if (!elevator.getRidingElevator().isEmpty()) {
                allElevatorsEmpty = false;
            }

        }

        if (passengerList.isEmpty() && allElevatorsEmpty) {
            System.out.println("Simulation Complete! Posting results:");
            for (Elevator elevator : elevatorList) {
                System.out.println("Elevator [" + elevator.getElevatorName() + "] delivered " + elevator.getPassengersDelivered() +
                        " passengers and traversed " + elevator.getFloorsTraveled() + " floors.");
                totalFloorsTraveled += elevator.getFloorsTraveled();
            }
            //To simulate feedback given to a client on how to optimize their elevators in their building
            System.out.println("Suggested Actions: ");
            if (totalPassengersUndelivered > 10) {
                int suggestElevators = totalPassengersUndelivered / 4;
                System.out.println("Install more elevators to address the " + totalPassengersUndelivered + " undelivered passengers. Suggest " + suggestElevators + " new elevators");
            } else if (totalFloorsTraveled > 100) {
                int average = totalFloorsTraveled / elevatorList.size();
                System.out.println("Average elevator traveled " + average + " floors. Reduce wear and tear by installing more elevators");
            } else if (minutesElapsed < simulationRunTime / 2) {
                System.out.println("Elevators delivered all passengers in less than half allotted time. You can install fewer elevators.");
            } else {
                System.out.println("None. System met optimal transit speed for passengers");
            }

            return true;
        }
        return false;
    }

    /**
     * Detailed Logging Method: Used if the user wants to see step by step, every instruction's result on the current
     * floor of every elevator and location of every passenger
     *
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */
    private void detailedLogging(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        System.out.println();
        System.out.println("* Waiting Passenger List:");
        for (Passenger passenger : passengerList) {
            System.out.println("Passenger #" + passenger.getPassengerIdentifier() + " -> On floor " + passenger.getCurrentFloor() + " Waiting to go to floor: " + passenger.getTargetFloor());
        }

        System.out.println();
        System.out.println("*** Elevators");
        int elevatorCount = 1;
        for (Elevator elevator : elevatorList) {
            System.out.println("Elevator [" + elevator.getElevatorName() + "] -> On floor " + elevator.getCurrentFloor() + " Traveling to floor: " + elevator.getTargetFloor());
            for (Passenger passenger : elevator.getRidingElevator()) {
                System.out.println("--Passenger #" + passenger.getPassengerIdentifier() + " -> On elevator [" + elevator.getElevatorName() + "] Traveling to floor: " + passenger.getTargetFloor());
            }
            elevatorCount++;
        }
    }

    /**
     * Always printed at start of simulation to show where the passengers and elevators started,
     * mostly useful if using random starting floor assignments
     *
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList  List of all elevators in service
     */
    private void printStartingLocations(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        //Debugging Tool:
        if (distributePassengersToRandomFloorsAtStart) {
            System.out.print("Starting Passenger Locations: ");
            for (Passenger pass : passengerList) {
                System.out.print(pass.getCurrentFloor() + " ");
            }
        } else {
            System.out.println("All passengers starting on first floor. ");
        }
        System.out.println();

        if (distributeElevatorsToRandomFloorsAtStart) {
            System.out.print("Starting Elevator Locations: ");
            for (Elevator elev : elevatorList) {
                System.out.print(elev.getCurrentFloor() + " ");
            }
        } else {
            System.out.println("All elevators starting on first floor. ");
        }
        System.out.println();
    }
}


//Suggest optimizations? like floors per minute costs $100 but a new elevator costs $500, should you get another elevator or faster one?

