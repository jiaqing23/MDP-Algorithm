package simulation.views;

import map.*;
import robot.Robot;
import utils.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GridPanel extends JPanel {

    private static final int ROW = 20;
    private static final int COL = 15;
    private static final int SQUARE_SIZE = 30;
    private static final int GAP = 1;

    private GridSquare[][] grid;
    private Robot robot;
    private Map map;

    public GridPanel(){
        this.setLayout(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        this.setPreferredSize(new Dimension(
                COL * SQUARE_SIZE + (COL + 1) * GAP,
                ROW * SQUARE_SIZE + (ROW + 1) * GAP
        ));

        this.setBackground(new java.awt.Color(48, 95, 114));

        this.fillGrid();
    }

    public void setRobotAndMap(Robot robot, Map map){
        this.robot = robot;
        this.map = map;

        for (int i = 0; i < ROW; i++) {
            for (int j = 0; j < COL; j++) {
                grid[i][j].setWayPoint(map.getMap()[i][j]);
            }
        }
    }

    public void fillGrid() {
        grid = new GridSquare[ROW][COL];

        toggleObstaclesListener handler = new toggleObstaclesListener();

        for (int i = ROW - 1; i >= 0; i--) {
            for (int j = 0; j < COL; j++) {
                grid[i][j] = new GridSquare(new Position(i, j));

                grid[i][j].setPreferredSize(new Dimension(SQUARE_SIZE, SQUARE_SIZE));
                grid[i][j].addMouseListener(handler);
                this.add(grid[i][j]);
            }
        }

    }

    public class toggleObstaclesListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            GridSquare square = (GridSquare)e.getComponent();
            square.toggleObstacle();
        }
    }

    public void updateGrid(){
        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                switch(grid[i][j].getWayPoint().getState()){
                    case isObstacle -> grid[i][j].setBackground(Color.black);
                    case isEmpty -> grid[i][j].setBackground(Color.white);
                }
            }
        }

        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                grid[map.getStart().x() + i][map.getStart().y() + j].setBackground(Color.LIGHT_GRAY);
                grid[map.getGoal().x() + i][map.getGoal().y() + j].setBackground(Color.LIGHT_GRAY);
            }
        }

        for(int i = 0; i < ROW; i++){
            for(int j = 0; j < COL; j++){
                switch (grid[i][j].getWayPoint().getSpecialState()){
                    case isFastestPath -> grid[i][j].setBackground(Color.green);
                }
            }
        }
        grid[map.getFPW().x()][map.getFPW().y()].setBackground(Color.YELLOW);


        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                grid[robot.getPosition().x() + i][robot.getPosition().y() + j].setBackground(Color.red);
            }
        }
        grid[robot.getHeadPosition().x()][robot.getHeadPosition().y()].setBackground(Color.pink);

    }
}


