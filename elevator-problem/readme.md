# classes.Elevator Problem
Christopher McCollum
For Bluestaq

## Criteria:
Write code to simulate an elevator in Java. 
Upload to github. 
Document assumptions and unimplemented features.
Be creative.

## Goal:
To write a sample simulation that can tell the user how many passengers
the elevators can service given their capacity, number of elevators, and
allowing the user to alter the parameters to find the optimal elevator
rules for thier building.

## Assumptions:
1. Empty elevators will start at the first floor during simulation.
2. Elevators ascending with passengers will not descend to 
   pick up a passenger. (no u-turns)
3. The elevator controller will send the nearest elevator going in the same
   direction as the passenger (asc/desc), unless an empty elevator is closer.
4. Elevators will move one floor/minute (to account for travel time, floor selection, and
   the doors automatically opening/closing) The simulation will let the user specify in
   milliseconds how long a minute should be.

## Unimplemented features:
1. Spring Boot/Maven to set up a local web server. I think I'd get lost in the weeds trying
   to create a pretty website to show the elevators running in simulation, so I will stick
   to ascii in the console to avoid over-engineering the solution to this assessment.