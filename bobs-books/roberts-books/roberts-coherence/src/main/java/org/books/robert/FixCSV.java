// Copyright (c) 2020, Oracle Corporation and/or its affiliates.

package org.books.robert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FixCSV {

    public static void main(String[] args) {

        try {
            BufferedReader in = new BufferedReader(new FileReader(new File("src/main/resources/oldbooks.csv")));

            String line = in.readLine();
            while (line != null) {

                boolean inString = false;

                main_loop:
                for (int i = 0; i < line.length(); i++) {
                    if (line.charAt(i) == '\"') {
                        inString = !inString;
                        continue main_loop;
                    }
                    if ((inString) && line.charAt(i) == ',') {
                        // ignore commas in strings
                        continue main_loop;
                    }
                    System.out.print(line.charAt(i));
                }
                System.out.print("\n");
                line = in.readLine();
            }


        } catch (Exception e) {
            System.exit(1);
        }
    }

}
