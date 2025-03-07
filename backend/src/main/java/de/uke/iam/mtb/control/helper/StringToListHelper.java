package de.uke.iam.mtb.control.helper;

import java.util.ArrayList;
import java.util.Arrays;

public class StringToListHelper {
  public static ArrayList<String> getListOfString(String string) {
    String[] convertedStringArray = string.split(",");
    return new ArrayList<String>(Arrays.asList(convertedStringArray));
  }
}
