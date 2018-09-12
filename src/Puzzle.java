import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import sac.graph.AStar;
import sac.graph.GraphSearchAlgorithm;
import sac.graph.GraphSearchConfigurator;
import sac.graph.GraphState;
import sac.graph.GraphStateImpl;

public class Puzzle extends GraphStateImpl {

    public byte[][] board = null;
    public static final Random r = new Random();
    public static final int n = 3;
    boolean shuffle = false;

    public Puzzle() {
        board = new byte[n][n];
        byte liczba = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = liczba++;
            }
        }
    }

    public Puzzle(Puzzle parent) {
        board = new byte[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                board[i][j] = parent.board[i][j];
    }

    public byte[][] getTab(){

        return board;

    }

    public String toString() {

        StringBuilder txt = new StringBuilder();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {

                txt.append(board[i][j] + " ");
            }
            txt.append("\n");
        }
        return txt.toString();
    }

    public boolean makeMove(int move)
    {
        byte temp = 0;
        int i = 0;
        int j = 0;
        int x = 0;
        int y = 0;

        zero:
        for (i = 0; i < n; i++)
            for (j = 0; j < n; j++)
                if (board[i][j] == 0)
                {
                    x=i;
                    y=j;
                    break zero;
                }

        switch (move)
        {
            case 0:
                temp = board[x - 1][y];
                board[x - 1][y] = 0;
                board[x][y] = temp;
                break;

            case 2:
                temp = board[x + 1][y];
                board[x + 1][y] = 0;
                board[x][y] = temp;
                break;

            case 3:
                temp = board[x][y - 1];
                board[x][y - 1] = 0;
                board[x][y] = temp;
                break;

            case 1:
                temp = board[x][y + 1];
                board[x][y + 1] = 0;
                board[x][y] = temp;
                break;
        }

        return true;
    }

    public boolean isLegal(int move) {
        int x=0,y=0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if(board[i][j]==0){
                    x=i;
                    y=j;
                }
        int check;
        if (move == 0) {
            check = x - 1;
            if (check < 0) {
                return false;
            }
        } else if (move == 1) {
            check = y + 1;
            if (check > 2) {
                return false;
            }
        } else if (move == 2) {
            check = x + 1;
            if (check > 2) {
                return false;
            }
        } else {
            check = y - 1;
            if (check < 0) {
                return false;
            }
        }

        return true;

    }

    public void shuffleBoard(int n) {

        shuffle = true;
        for (int i = 0; i < n; i++) {
            boolean legal = false;

            while (!legal) {
                int move = r.nextInt() % 4;
                if (isLegal(move)) {
                    legal = true;
                    makeMove(move);
                }
            }
        }
    }

    @Override
    public List<GraphState> generateChildren() {
        List<GraphState> children = new ArrayList<GraphState>();
        shuffle = false;
        boolean solved = true;
        byte licznik = 0;
        unsolved: for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (board[i][j] != licznik) {
                    solved = false;
                    break unsolved;
                }
        licznik++;
        if (solved) {
            return children;
        }

        for (int i = 0; i < 4; i++)
            if (isLegal(i)) {
                Puzzle child = new Puzzle(this);
                child.makeMove(i);
                children.add(child);
            }

        return children;

    }

    @Override
    public boolean isSolution() {
        byte licznik = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (board[i][j] != licznik++) {
                    return false;
                }
        //licznik++;
        return true;
    }

    @Override
    public int hashCode() {
        byte[] copy = new byte[n * n];
        int k = 0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                copy[k++] = board[i][j];

        return Arrays.hashCode(copy);
    }

    public static void main(String[] args) {
        Puzzle p = new Puzzle();
        System.out.println(p);
        p.shuffleBoard(100);
        System.out.println(p);
        Puzzle.setHFunction(new ManhattanFunction());
        GraphSearchConfigurator conf = new GraphSearchConfigurator();
        conf.setWantedNumberOfSolutions(Integer.MAX_VALUE);
        GraphSearchAlgorithm a = new AStar(p);
        a.execute();
        List<GraphState> solutions = a.getSolutions();
        for (GraphState sol : solutions) {
            System.out.println(solutions);
            System.out.println("------------------");

        }
        System.out.println("Time: " + a.getDurationTime());
        System.out.println("Closed: " + a.getClosedStatesCount());
        System.out.println("Open: " + a.getOpenSet().size());
        System.out.println("Solutions: " + a.getSolutions().size());
    }

}