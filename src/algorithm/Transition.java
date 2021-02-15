package algorithm;

import robot.RobotAction;

public class Transition {
    public State from;
    public State to;
    public int cost;
    public RobotAction action;

    public Transition(State from, State to, int cost, RobotAction action)
    {
        this.from = from;
        this.to = to;
        this.cost = cost;
        this.action = action;
    }

}
