package roles;
import java.util.*;
import java.io.*;
import java.sql.*;
import database.*;

public class Passenger {

  public static void service() throws Exception {

    Scanner keyboard = new Scanner(System.in);

    services: while (true) {
      System.out.println("Administrator, what would you like to do?");
      System.out.println("1. Request a ride");
      System.out.println("2. Check trip records");
      System.out.println("3. Go back");
      System.out.println("Please enter [1-3]");
      
      int input = 0;
      try {
        input = keyboard.nextInt();
      } catch (Exception e) {
        System.out.println("Invalid input! Please try again.");
      } finally {
        switch (input) {
          case 1: 
            requestRide();
            break;
          case 2: 
            checkTripRecords();
            break;
          case 3: 
            break services;
          default: 
            System.out.println("Invalid input! Please try again.");
            break;
        }
      }
    }
    keyboard.close();
  }

  private static void requestRide() throws Exception {

  }

  private static void checkTripRecords() throws Exception {

  }
}
