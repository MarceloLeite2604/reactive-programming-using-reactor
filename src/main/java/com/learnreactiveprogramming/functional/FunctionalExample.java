package com.learnreactiveprogramming.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionalExample {

  public static void main(String[] args) {
    final var namesList = List.of("alex", "ben", "chloe", "adam");
    final var newNamesList = namesGreaterThanSize(namesList, 3);

    System.out.println("newNamesList: " + newNamesList);

    namesGreaterThanSize(namesList, 3);
  }

  private static List<String> namesGreaterThanSize(List<String> namesList, int size) {
    return namesList.stream()
      .filter(name -> name.length() > size)
      .collect(Collectors.toList());
  }
}
