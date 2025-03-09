package classes;

public class GUI {
    /**
     * Non user adjustable variables
     */



    public String elevatorAnimation(int currentFloor, boolean doorOpen, int maxFloor) {
        StringBuilder elevatorAnim = new StringBuilder();

        for (int i = 1; i <= maxFloor; i++) {
            if (i<10) {
                if (i==currentFloor) {
                    elevatorAnim.append(" [0").append(i).append("]");
                } else {
                    elevatorAnim.append(" 0").append(i);
                }

            } else {
                if (i==currentFloor) {
                    elevatorAnim.append(" [").append(i).append("]");
                } else {
                    elevatorAnim.append(" ").append(i);
                }
            }

        }


        return elevatorAnim.toString();
    }


}
