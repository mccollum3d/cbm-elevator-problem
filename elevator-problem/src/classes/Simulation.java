package classes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation {

    /************************
     * Simulation parameters
     * assumes user enters only valid values
     */
    public int numberOfElevators = 1;   //Valid values: 1-26
    public int numberOfFloors = 30;     //Valid values: 1-n
    public boolean distributeElevatorsToRandomFloorsAtStart = false;    //Valid values: true/false

    public int simulationRunTime = 100; //Valid values: 1-n, simulates n minutes of elevator usage

    public int minPassengersPerMinute = 1;  //Valid values: 1-n
    public int maxPassengersPerMinute = 6;  //Valid values: 1-n
    public int startingPassengers = 6;     //Valid values: 1-n
    public boolean distributePassengersToRandomFloorsAtStart = true;    //Valid values: true/false

    /**
     * End of simulation parameters
     ***********************/

    public void initializeSimulation(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        // creates the starting conditions of the simulation
        // random number between 1 (inclusive) and n (exclusive) therefore n+1 becomes boundary of rng.
        Random rand = new Random();

        //Initialize Passengers
        if (distributePassengersToRandomFloorsAtStart) {
            for (int p = 0; p < startingPassengers; p++) {
                int start = rand.nextInt(numberOfFloors);
                int end = rand.nextInt(numberOfFloors + 1); //exclusive, so if top floor = 10, we want nextInt(11)
                while (start == end) {
                    start = rand.nextInt(numberOfFloors);
                    end = rand.nextInt(numberOfFloors + 1);
                }
                passengerList.add(new Passenger(start, end, p));
            }
        } else {
            for (int p = 0; p < startingPassengers; p++) {
                int end = rand.nextInt(numberOfFloors + 1);
                while (end == 1) {
                    end = rand.nextInt(numberOfFloors + 1);
                }
                passengerList.add(new Passenger(1, end, p));
            }
        }

        //Initialize Elevators
        if (distributeElevatorsToRandomFloorsAtStart) {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator(rand.nextInt(numberOfFloors + 1), e));
            }
        } else {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator(e));
            }
        }

        printStartingLocations(passengerList, elevatorList);
        System.out.println("Simulation Initialized! Now calculating...");
        runSimulation(passengerList, elevatorList);
    }

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

            // Check if all passengers delivered
            if (isSimulationComplete(passengerList, elevatorList)) {
                break;
            };

            detailedLogging(passengerList, elevatorList);

        }

    }

    /**
     * Helper Methods
     */

    /**
     * Handles the loading of passengers after any passengers on board the elevator that wanted to get off have done so.
     * @param passengerList List of all passengers in the building awaiting an elevator
     * @param elevatorList List of all elevators in service
     */

    private void pickUpPassengers(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();


        for (Elevator elevator : elevatorList) {
            for (Passenger passenger : passengerList) {
                //Passengers picked up, if elevator has capacity
                if (passenger.getCurrentFloor() == elevator.getCurrentFloor() && elevator.getCurrentPassengers() <= elevator.getCapacity()) {
                    elevator.setCurrentPassengers(elevator.getCurrentPassengers()+1);
                    elevator.getRidingElevator().add(passenger);
                    elevator.setTargetFloor(passenger.getTargetFloor());
                    passengersToRemove.add(passenger);
                }
            }
            //Passengers removed outside of inner for loopto avoid concurrent modification exception
            if (!passengersToRemove.isEmpty()) {
                passengerList.removeAll(passengersToRemove);
            }
        }
    }

    /**
     * Unload passengers who have arrived on their target floor.
     * @param elevatorList List of all elevators in service
     */

    private void deliverPassengers(ArrayList<Elevator> elevatorList) {
        for (Elevator elevator : elevatorList) {
            ArrayList<Passenger> passengersToRemove = new ArrayList<>();
            for (Passenger passenger : elevator.getRidingElevator()) {
                if (passenger.getTargetFloor() == elevator.getCurrentFloor()) {
                    elevator.setCurrentPassengers(elevator.getCurrentPassengers()-1);
                    passengersToRemove.add(passenger);
                    elevator.setPassengersDelivered(elevator.getPassengersDelivered() + 1);
                }
                if (elevator.getCurrentPassengers() == 0) {
                    elevator.setIdle(true);
                }
            }
            if (!passengersToRemove.isEmpty()) {
                elevator.getRidingElevator().removeAll(passengersToRemove);
            }
        }
    }


    private void assignElevatorInstructions
            (ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {

        for (Elevator elevator : elevatorList) {
            Passenger nearestPassenger = null;
            if (elevator.getCurrentPassengers() == 0) {
                elevator.setAscending(false);
                elevator.setDescending(false);
                elevator.setIdle(true);
            }

            if (elevator.isIdle()) {
                //Elevator not in use, pick up nearest passenger above or below it.
                for (Passenger passenger : passengerList) {
                    if (nearestPassenger==null) {
                        nearestPassenger = passenger;
                        continue;
                    }
                    if (Math.abs(elevator.getCurrentFloor() - passenger.getCurrentFloor()) < Math.abs(elevator.getCurrentFloor() - nearestPassenger.getCurrentFloor())) {
                        nearestPassenger = passenger;
                        if (elevator.getCurrentFloor() - passenger.getCurrentFloor() < 0 ) {
                            elevator.setAscending(true);
                            elevator.setDescending(false);
                            elevator.setIdle(false);
                        } else {
                            elevator.setAscending(false);
                            elevator.setDescending(true);
                            elevator.setIdle(false);
                        }
                    }
                }
            }
            else if (elevator.isAscending()) {
                //Elevator ascending, pick up nearest passenger along current path up.
                for (Passenger passenger : passengerList) {
                    if (nearestPassenger == null) {
                        nearestPassenger = passenger;
                        continue;
                    }
                    if ((passenger.getCurrentFloor() - elevator.getCurrentFloor() < (nearestPassenger.getCurrentFloor() - elevator.getCurrentFloor()))) {
                        nearestPassenger = passenger;
                        elevator.setAscending(true);
                        elevator.setDescending(false);
                        elevator.setIdle(false);
                    }
                }
            }
            else if (elevator.isDescending()) {
                //Elevator ascending, pick up nearest passenger along current path up.
                for (Passenger passenger : passengerList) {
                    if (nearestPassenger == null) {
                        nearestPassenger = passenger;
                        continue;
                    }
                    if ((elevator.getCurrentFloor() - passenger.getCurrentFloor()) < (elevator.getCurrentFloor() - nearestPassenger.getCurrentFloor())) {
                        nearestPassenger = passenger;
                        elevator.setAscending(false);
                        elevator.setDescending(true);
                        elevator.setIdle(false);
                    }
                }
            }

            //The Elevator has located its next passenger to pick up based on if it was idle, ascending, or descending
            if (nearestPassenger==null) {
                //No more passengers to pick up in building, let elevator continue any current instructions.
            } else {
                //Give elevator new instruction to proceed to pick up nearest passenger.
                elevator.setTargetFloor(nearestPassenger.getCurrentFloor());
            }

        }

    }

    private void advanceSimulationOneMinute (ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        for (Elevator elevator : elevatorList) {
            if (elevator.getCurrentFloor() < elevator.getTargetFloor()) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
                elevator.setIdle(false);
                elevator.setFloorsTraveled(elevator.getFloorsTraveled() + 1);
            } else if (elevator.getCurrentFloor() > elevator.getTargetFloor()) {
                elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
                elevator.setIdle(false);
                elevator.setFloorsTraveled(elevator.getFloorsTraveled() + 1);
            } else {
                elevator.setIdle(true);
            }
        }
    }

    private boolean isSimulationComplete(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        boolean allElevatorsEmpty = true;
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
            }
            return true;
        }
        return false;
    }

    // DEBUGGING
    private void detailedLogging(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        System.out.println();
        System.out.println("*** Passengers");
        int passengerCount = 1;
        for(Passenger passenger : passengerList) {
            System.out.println("Passenger #" + passengerCount + " -> On " + passenger.getCurrentFloor() + " Waiting to go to floor: " + passenger.getTargetFloor());
            passengerCount++;
        }

        System.out.println();
        System.out.println("*** Elevators");
        int elevatorCount = 1;
        for(Elevator elevator : elevatorList) {
            System.out.println("Elevator #" + elevatorCount + " -> On " + elevator.getCurrentFloor() + " Traveling to floor: " + elevator.getTargetFloor());
            int ridingElevator = 1;
            for (Passenger passenger : elevator.getRidingElevator()) {
                System.out.println("--Passenger #" + passengerCount + " -> On elevator #" + elevatorCount + " Traveling to floor: " + passenger.getTargetFloor());
                passengerCount++;
            }
            elevatorCount++;
        }
    }
    private void printStartingLocations(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        //Debugging Tool:
        if (distributePassengersToRandomFloorsAtStart) {
            System.out.print("Starting Passenger Locations: ");
            for (Passenger pass : passengerList) {
                System.out.print(pass.getCurrentFloor() + ", ");
            }
        } else {
            System.out.println("All passengers starting on first floor. ");
        }
        System.out.println();

        if (distributeElevatorsToRandomFloorsAtStart) {
            System.out.print("Starting Elevator Locations: ");
            for (Elevator elev : elevatorList) {
                System.out.print(elev.getCurrentFloor() + ", ");
            }
        } else {
            System.out.println("All elevators starting on first floor. ");
        }
        System.out.println();
    }
}


//Suggest optimizations? like floors per minute costs $100 but a new elevator costs $500, should you get another elevator or faster one?

