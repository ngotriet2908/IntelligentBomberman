package sample;

import java.util.Arrays;
import java.util.Random;

public class Board {
    private int[][] matrix;
    private int size;

    public Board(int size) {
        this.size = size;
        this.matrix = new int[size][size];
        Random random = new Random();

        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++) {
                this.matrix[i][j] = 0; //unbreakableWall
            }

        for(int i = 1; i < size - 1; i++) {
            for(int j = 1; j < size - 1; j++) {
                this.matrix[i][j] = random.nextInt(3) + 1;
            }
        }
        this.matrix[1][1] = 4; //agent
    }

    public void resetBoard() {
        Random random = new Random();

        for(int i = 0; i < size; i++)
            for(int j = 0; j < size; j++) {
                this.matrix[i][j] = 0; //unbreakableWall
            }

        for(int i = 1; i < size - 1; i++) {
            for(int j = 1; j < size - 1; j++) {
                this.matrix[i][j] = random.nextInt(3) + 1;
            }
        }
        this.matrix[1][1] = 4; //agent
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public void setMatrix(int[][] matrix) {
        this.matrix = matrix;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(int[] x: matrix) {
            result.append(Arrays.toString(x)).append("\n");
        }
        return result.toString();
    }
}
