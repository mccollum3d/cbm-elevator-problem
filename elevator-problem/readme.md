# Elevator Problem
Christopher McCollum, For Bluestaq

## Criteria:
Write code to simulate an elevator in Java. 
Upload to github. 
Document assumptions and unimplemented features.
Be creative.

## Goal:
To write an elevator controller to simulate an elevator. Next I will 
write a sample simulation that can tell the user how many passengers
the elevators can service given their capacity, number of elevators, and
allowing the user to alter the parameters to find the optimal elevator
rules for their building.

## Assumptions:
1. Human Nature: Elevators will not go up to pick up a passenger if it already has a passenger
   that is heading down. While it might be more optimal for an elevator on floor 5
   to pick up someone going down on floor 6 before picking up a person on floor 5, since the
   person on floor 5 pressed the 'down' button they will be confused/upset if they get on and the
   elevator then goes up to floor 6. I am assuming elevators should work similar to real life 
   where they put the user input (up/down) as a higher priority than optimizing routes.

2. The elevator controller will send the nearest elevator going in the same
   direction as the passenger (asc/desc), unless an empty elevator is closer.

3. Elevators will move one floor/minute (to account for travel time, floor selection, and
   the doors automatically opening/closing) The simulation will let the user specify in
   milliseconds how long a minute should be.

## Unimplemented features:
1. Spring Boot/Maven to set up a local web server. I think building a front end to make it look
   nice is not as important as writing a solid controller for the elevator, and simulator to show
   that the elevator works properly.

## Example of elevator expected behavior
### Ascending

Passengers waiting on floor 1, 4, 7, 8, and 9 in a ten-floor building:
The elevator on floor 1 stops to pick up the passengers on floor 1.
It then sets its target to 9 (the passengers it just picked up are going up
and floor 9 is the greatest target floor in the continued direction of travel upward). 
Therefore, it can deliver the passengers on the way to pick up floor 9.

### Descending
If passengers are waiting on floor 5, 6, 7, and 8 of a ten-floor building:
The elevator on floor 1 ascends to floor 5. It then picks up this passenger heading down.
It goes down to deliver them, ignoring passengers on 6-8. If the elevator didn't go down
the passenger on floor 5 would be quite upset that the elevator 'went up' when they chose
down, even though it'd be far more effecient to start on floor 8. This is the assumption
of human behavior. (We could optimize by having it pick up on floor 8 first so the people
waiting on floor 5-7 never know they got 'skipped'. With multiple elevators running, this
situation occurring is less likely and might not be worth optimizing for.)

### Idle

If an elevator is empty and idle on floor 5, and there is a passenger on floor 3 and 7,
then it will choose to go to whichever passenger it checked first in its list of waiting passengers.
This assumes another elevator will be closer to the one not chosen, so that the order they are 
picked up is not of critical importance.