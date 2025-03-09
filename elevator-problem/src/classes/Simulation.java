package classes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class Simulation {

    /************************
     * Simulation parameters
     */
    public int numberOfElevators = 1;
    //    public int capacityOfElevators = 4;
    public int numberOfFloors = 30;
    public boolean distributeElevatorsToRandomFloorsAtStart = false;

    //        int simulationSpeed = 500; //in milliseconds. Suggest 1,000ms -> 1 simulated minute of elevator travel.
    public int simulationRunTime = 100; //Simulates this many minutes worth of continuous elevator usage

    public int minPassengersPerMinute = 1;
    public int maxPassengersPerMinute = 6;
    public int startingPassengers = 12;
    public boolean distributePassengersToRandomFloorsAtStart = true;

    /************************
     * End of simulation parameters
     */

    public void initializeSimulation(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        // creates the starting conditions of the simulation
        // random number between 1 (inclusive) and n (exclusive) therefore n+1 becomes boundary of rng.
        Random rand = new Random();

        //Initialize Passengers
        if (distributePassengersToRandomFloorsAtStart) {
            for (int p = 0; p < startingPassengers; p++) {
                passengerList.add(new Passenger(rand.nextInt(numberOfFloors), rand.nextInt(numberOfFloors + 1)));
            }
        } else {
            for (int p = 0; p < startingPassengers; p++) {
                passengerList.add(new Passenger(1, rand.nextInt(2, numberOfFloors)));
            }
        }

        //Initialize Elevators
        if (distributeElevatorsToRandomFloorsAtStart) {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator(rand.nextInt(numberOfFloors)));
            }
        } else {
            for (int e = 0; e < numberOfElevators; e++) {
                elevatorList.add(new Elevator());
            }
        }

        printStartingLocations(passengerList, elevatorList);
        System.out.println("Simulation Initialized! Now calculating...");
        runSimulation(passengerList, elevatorList);
    }

    public void runSimulation(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        //Runs the simulation and returns the results
        for (int minutes = 0; minutes <= simulationRunTime; minutes++) {
            // Pick up passengers: if elevator is on floor with passenger, add passenger
            pickUpPassengers(passengerList, elevatorList);

            // Deliver Passengers: if elevator is on target floor with passenger, remove passenger
            deliverPassengers(elevatorList);

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

    private void pickUpPassengers(ArrayList<Passenger> passengerList, ArrayList<Elevator> elevatorList) {
        //use iterator to avoid concurrentmodificationexception of double for loop.
        //stream api would be ideal here to optimize, once I confirm the logic works correctly.
        ArrayList<Passenger> passengersToRemove = new ArrayList<>();

        for (Elevator elevator : elevatorList) {
            for (Passenger passenger : passengerList) {
                if (passenger.getCurrentFloor() == elevator.getCurrentFloor()) {
                    elevator.getRidingElevator().add(passenger);
                    elevator.setTargetFloor(passenger.getTargetFloor());
                    passengersToRemove.add(passenger);
                }
            }
            if (!passengersToRemove.isEmpty()) {
                passengerList.removeAll(passengersToRemove);
            }
        }
    }

    private void deliverPassengers(ArrayList<Elevator> elevatorList) {
        for (Elevator elevator : elevatorList) {
            ArrayList<Passenger> passengersToRemove = new ArrayList<>();
            for (Passenger passenger : elevator.getRidingElevator()) {
                if (passenger.getTargetFloor() == elevator.getCurrentFloor()) {
                    passengersToRemove.add(passenger);
                    elevator.setPassengersDelivered(elevator.getPassengersDelivered() + 1);
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
            int currentLocation = elevator.getCurrentFloor();
            if (elevator.isAscending()) {
                //ascending, go to nearest passenger traveling up

                int nearestPassengers = numberOfFloors;
                for (Passenger passenger : elevator.getRidingElevator()) {
                    if (passenger.getCurrentFloor() - elevator.getCurrentFloor() < nearestPassengers) {
                        nearestPassengers = passenger.getCurrentFloor() - elevator.getCurrentFloor();
                    }
                }
                elevator.setTargetFloor(elevator.getCurrentFloor() + nearestPassengers);

            } else if (elevator.isDescending()) {
                //descending, go to nearest passenger traveling down
                elevator.setAscending(false);
                elevator.setDescending(true);
                elevator.setIdle(false);

                int nearestPassengers = numberOfFloors;
                for (Passenger passenger : elevator.getRidingElevator()) {
                    if (elevator.getCurrentFloor() - passenger.getCurrentFloor() < nearestPassengers) {
                        nearestPassengers = passenger.getCurrentFloor() - elevator.getCurrentFloor();
                    }
                }
                elevator.setTargetFloor(elevator.getCurrentFloor() - nearestPassengers);

            } else {
                elevator.setAscending(false);
                elevator.setDescending(false);
                elevator.setIdle(true);

                //idle, go to nearest passenger in either direction
                if (passengerList.isEmpty()) {
                    break;
                }
                int nearestPassengers = numberOfFloors;
                boolean goingUp = false;
                for (Passenger passenger : passengerList) {
                    boolean goingUpTemp = false;
                    int distanceUpOrDown = numberOfFloors;
                    if (passenger.getCurrentFloor() - elevator.getCurrentFloor() < 0) {
                        goingUpTemp = false;
                        nearestPassengers = passenger.getCurrentFloor() - elevator.getCurrentFloor();
                        distanceUpOrDown = Math.abs(nearestPassengers);
                    } else if (passenger.getCurrentFloor() - elevator.getCurrentFloor() > 0) {
                        goingUpTemp = true;
                        distanceUpOrDown = passenger.getCurrentFloor() - elevator.getCurrentFloor();
                    }
                    if (distanceUpOrDown < nearestPassengers) {
                        nearestPassengers = distanceUpOrDown;
                        goingUp = goingUpTemp;
                    }
                }

                //having determined distance and direction, set new target floor
                if (goingUp) {
                    elevator.setAscending(true);
                    elevator.setIdle(false);
                    elevator.setTargetFloor(elevator.getCurrentFloor() + nearestPassengers);
                } else {
                    elevator.setDescending(false);
                    elevator.setTargetFloor(elevator.getCurrentFloor() - nearestPassengers);
                }
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
        boolean activeElevators = false;
        for (Elevator elevator : elevatorList) {
            if (elevator.isIdle()) {
                //do nothing
            } else {
                activeElevators = true;
            }
        }

        boolean allElevatorsEmpty = true;
        for (Elevator elevator : elevatorList) {
            if (!elevator.getRidingElevator().isEmpty()) {
                allElevatorsEmpty = false;
            }

        }

        if (passengerList.isEmpty() && allElevatorsEmpty) {
            System.out.println("Simulation Complete! Posting results:");
            int elevatorNumber = 1;
            for (Elevator elevator : elevatorList) {
                System.out.println("Elevator #1 delivered " + elevator.getPassengersDelivered() +
                        " passengers and traversed " + elevator.getFloorsTraveled() + " floors.");
                elevatorNumber++;
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

