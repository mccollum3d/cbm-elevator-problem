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

## How to Run Simulation
In the Simulation class, you can choose the number of floors, elevators, people arriving, and their
start locations. This provides a way to test and see my elevator code in action. It also would simulate
building software for a client, who would pay to have their architectural blueprints tested and determine
if they meet fire codes and deliver passengers effectively in a new building. 

### Run and Log results
After inputting your custom values, run Main.java() to see the results. Uncomment 
Simulation.detailedLogging() to have the debugging tool print all passenger and elevator actions.

## Design Thought Process
1. I need to build an elevator controller that would work for any number of elevators.
2. I need the elevators to be autonomous in carrying out basic commands from passengers: call button up,
   call button down, floor button.
3. I need a way to show the Elevator working and passengers interacting with the elevator. This means I
   should create a simulation as a means to test and demonstrate my design.

## Assumptions:
1. Human Nature: Elevators will not go up to pick up a passenger if it already has a passenger
   that is heading down. In example: If passengers on floor 5 and 6 are both going down and 5 pressed
   down first, then the elevator will pick up 5 and return downward. While it'd be more optimal
   to pick up 6 and then 5, elevators work on a first in first out system. This is likely due to
   the fact a person would be upset if they pressed down and saw the elevator go up, even if it
   results in a net benefit.

2. The elevator controller will send the nearest elevator going in the same
   direction as the passenger (asc/desc), unless an empty elevator is closer.
   This results in an elevator going up to usually continue upward deliveries 
   until out of passengers, then returning down.

3. Elevators will move one floor/minute (to account for travel time, floor selection, and
   the doors automatically opening/closing) The simulation will let the user specify in
   milliseconds how long a minute should be.

4. The user running the simulation won't deliberately break it. I didn't put in checks for non-sense values
   to save time. In a real world project, it would be necessary to do additional checks for valid inputs
   and null values.

## Unimplemented features:
1. Spring Boot/Maven to set up a local web server. I think building a front end to make it look
   nice is not as important as writing a solid controller for the elevator, and simulator to show
   that the elevator works properly.
2. Fire Marshal Override / Override keys. Elevators require an On/Off switch usually keyed for fire
   department use. Some may have penthouse suites where only authorized individuals can benefit. I
   decided not to implement logic to control on/off, authorized access, or emergency call buttons to
   the elevator. If I had, it would allow a chosen elevator to ignore stops to pick up passengers and
   allow firefighters direct access to any chosen floor. (Or allow a penthouse owner to their home)
3. Passenger impatience: if passengers wait too long for an elevator, they might choose to take the stairs
   or leave all-together. Passenger wait time is an important metric that could impact choices on effeciency
   but I decided not to go down that rabbit hole, and instead focus on the elevator algorithm and creating
   a way to customize a simulation that tests that algorithm.
4. Front End. It would have looked nicer with a front end, but I knew once I started down the path to building
   a Vue front end I would easily lose focus on the algorithm in a desire to make it look as pretty as possible.
   Therefore I kept it simple and used the console to print the necessary information.
5. Open/Close door button. I'm not sure how handling this would improve the simulation, it would mean simulating
   impatient users wanting to close the doors on someone just to save a few seconds travel time. Furthermore,
   most elevators disconnect the open/close button so that they exist only to make the user feel like they
   are going faster. This eliminates people being rude.
6. No 13th floor. I'm not superstitious, but it might have been humorous to have the simulation deliberately
   remove the 13th floors of the buildings to match current American architectural practices.

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