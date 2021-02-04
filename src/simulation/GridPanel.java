package simulation;

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

    public GridPanel(){
        this.setLayout(new FlowLayout(FlowLayout.LEFT, GAP, GAP));
        this.setPreferredSize(new Dimension(
                COL * SQUARE_SIZE + (COL + 1) * GAP,
                ROW * SQUARE_SIZE + (ROW + 1) * GAP
        ));

        this.setBackground(new java.awt.Color(48, 95, 114));

        this.fillGrid();
    }


    public void fillGrid() {
        grid = new GridSquare[ROW][COL];

        toggleObstaclesListener handler = new toggleObstaclesListener();

        for (int i = ROW - 1; i >= 0; i--) {
            for (int j = 0; j < COL; j++) {
                grid[i][j] = new GridSquare(i, j);

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
            square.toggleBackground();
        }
    }
}


