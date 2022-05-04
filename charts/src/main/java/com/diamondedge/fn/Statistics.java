/**
 * Copyright 2004 Reed Ellsworth. All rights reserved.
 *
 * @author Reed Ellsworth
 */
package com.diamondedge.fn;

public class Statistics {
    public static double sum(double[] args) {
        if (args.length == 0)
            return 0;
        double sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i];
        return sum;
    }

    public static double sumSquares(double[] args) {
        if (args.length == 0)
            return 0;
        double sum = 0;
        for (int i = 0; i < args.length; i++)
            sum += args[i] * args[i];
        return sum;
    }

    public static double product(double[] args) {
        if (args.length == 0)
            return 0;
        double product = 1;
        for (int i = 0; i < args.length; i++)
            product *= args[i];
        return product;
    }

    /**
     * Multiplies the corresponding elements of each vector and returns the sum of those products.
     * Each vector should be the same size and loaded in the same row.
     */
    public static double sumProduct(double[][] args) {
        if (args.length == 0)
            return 0;
        int nvect = args.length;
        int nvals = args[0].length;
        double sum = 0;
        for (int j = 0; j < nvals; j++) {
            double product = 0;
            for (int i = 0; i < nvect; i++) {
                if (i == 0)
                    product = args[i][j];
                else
                    product *= args[i][j];
            }
            sum += product;
        }
        return sum;
    }

    public static double min(double[] args) {
        if (args.length == 0)
            return 0;
        double min = Double.MAX_VALUE;
        for (int i = 0; i < args.length; i++)
            if (args[i] < min)
                min = args[i];
        return min;
    }

    public static double max(double[] args) {
        if (args.length == 0)
            return 0;
        double max = Double.MIN_VALUE;
        for (int i = 0; i < args.length; i++)
            if (args[i] > max)
                max = args[i];
        return max;
    }

    public static double average(double[] args) {
        return sum(args) / args.length;
    }

    public static double median(double[] args) {
        if (args.length == 0)
            return 0;
        java.util.Arrays.sort(args);
        if ((args.length % 2) == 0) {
            // average the 2 middle values
            return (args[args.length / 2 - 1] + args[args.length / 2]) / 2;
        } else {
            // odd number so return the middle value
            return args[args.length / 2];
        }
    }

    public static int rank(double num, double[] args, int ascending) {
        if (args.length == 0)
            return 0;
        java.util.Arrays.sort(args);
        if (ascending > 0) {
            for (int i = 0; i < args.length; i++) {
                if (num == args[i])
                    return i + 1;
            }
        } else {
            for (int i = args.length - 1; i >= 0; i--) {
                if (num == args[i])
                    return i + 1;
            }
        }
        return 0;
    }

    public static double stdev(double[] vals) {
        return Math.sqrt(var(vals));
    }

    public static double stdevp(double[] vals) {
        return Math.sqrt(varp(vals));
    }

    public static double var(double[] vals) {
        double sum = sum(vals);
        double sumsq = sumSquares(vals);
        int n = vals.length;

        return (n * sumsq - sum * sum) / (n * (n - 1));
    }

    public static double varp(double[] vals) {
        double sum = sum(vals);
        double sumsq = sumSquares(vals);
        int n = vals.length;

        return (n * sumsq - sum * sum) / (n * n);
    }

}

