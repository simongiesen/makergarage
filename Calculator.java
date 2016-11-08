package com.simongiesen.makerapp;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by simongiesen on 30.10.16.
 */

public class Calculator {


    public double createMath(String expression) {
        double solution = 0;

        try (Scanner scanner = new Scanner(expression)) {

            double n1 = scanner.nextDouble();
            String operation = scanner.next();
            double n2 = scanner.nextDouble();

            switch (operation) {
                case "+":
                    solution = (n1 + n2);
                    break;

                case "-":
                    solution = (n1 - n2);
                    break;

                case "/":
                    solution = (n1 / n2);
                    break;

                case "*":
                    solution = (n1 * n2);
                    break;

                default:
                    return solution;

            }
        }

        return solution;


    }


    public int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }


    public boolean isInteger(double d) {

        return (d == Math.floor(d)) && !Double.isInfinite(d);
    }


}
