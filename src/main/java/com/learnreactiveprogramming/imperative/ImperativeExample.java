package com.learnreactiveprogramming.imperative;

import java.util.ArrayList;
import java.util.List;

public class ImperativeExample {

  public static void main(String[] args) {
    final var namesList = List.of("alex", "ben", "chloe", "adam");
    final var newNamesList = namesGreaterThanSize(namesList, 3);

    System.out.println("newNamesList: " + newNamesList);

    namesGreaterThanSize(namesList, 3);
  }

  private static List<String> namesGreaterThanSize(List<String> namesList, int size) {
    final var newNamesList = new ArrayList<String>();

    for (String name : namesList) {
      if (name.length() > size) {
        newNamesList.add(name);
      }
    }

    return newNamesList;
  }
}
