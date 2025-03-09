import classes.Elevator;
import classes.Passenger;
import classes.Simulation;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        ArrayList<Passenger> passengerList = new ArrayList<>();
        ArrayList<Elevator> elevatorList = new ArrayList<>();

        Simulation simulation = new Simulation();
        simulation.initializeSimulation(passengerList, elevatorList);

    }
}
