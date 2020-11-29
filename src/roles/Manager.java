package roles;
import java.util.*;
import java.io.*;
import java.sql.*;
import database.*;

public class Manager {
  
  public static void service(Scanner keyboard) throws Exception {

    services: while (true) {
      System.out.println("Administrator, what would you like to do?");
      System.out.println("1. Find trips");
      System.out.println("2. Go back");
      System.out.println("Please enter [1-5]");
      
      int input = 0;
      try {
        input = keyboard.nextInt();
      } catch (Exception e) {
        keyboard.next();
      } finally {
        switch (input) {
          case 1: 
            findTrips();
            break;
          case 2: 
            break services;
          default: 
            System.out.println("Invalid input! Please try again.");
            break;
        }
      }
    }
  }

  private static void findTrips() throws Exception {

  }
}
