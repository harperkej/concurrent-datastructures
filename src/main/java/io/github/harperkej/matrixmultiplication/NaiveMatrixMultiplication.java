package io.github.harperkej.matrixmultiplication;

public class NaiveMatrixMultiplication extends Thread {

    private int row;
    private int col;
    private double[][] a;
    private double[][] b;
    private double[][] c;

    public NaiveMatrixMultiplication(int row, int col, double[][] a, double[][] b, double[][] c) {
        this.row = row;
        this.col = col;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public void run() {
        double res = 0;
        for (int i = 0; i < a[row].length; i++)
            res += a[row][i] * b[i][col];
        c[row][col] = res;
    }

}
