package com.xpdustry.ansi;

import static arc.util.ColorCodes.*;
import static org.fusesource.jansi.internal.Kernel32.*;

import arc.struct.StringMap;
import arc.util.OS;


public class Ansi extends mindustry.mod.Plugin {
  // Enable colors asap
  { install(); }
  
  /** https://learn.microsoft.com/en-us/windows/console/setconsolemode */
  public static final int ENABLE_PROCESSED_OUTPUT = 0x0001,
                          ENABLE_VIRTUAL_TERMINAL_PROCESSING = 0x0004;
  
  /** 
   * Enables ANSI colors and resets escapes codes. <br>
   * Does nothing if it's not Windows or if it's the Windows Terminal, which already support colors. 
   */
  public static void install() {
    if (!OS.isWindows || OS.hasEnv("WT_SESSION")) return;
    if (enable()) resetColorCodes(); 
    // At this point we cannot use arc.util.Log
    else System.out.print("WARNING: Failed to enable ANSI colors: " + getLastError());
  }
  
  /** 
   * Enable ANSI escape codes (VT100 for windows) for all console handles ({@link STD_INPUT_HANDLE}, 
   * {@link STD_OUTPUT_HANDLE}, {@link STD_ERROR_HANDLE}).
   * @return whether the operation has been performed successfully.
   * @see #getLastError()
   */
  public static boolean enable() {
    return enable(STD_INPUT_HANDLE)
        && enable(STD_OUTPUT_HANDLE)
        && enable(STD_ERROR_HANDLE);
  }
  
  /** 
   * Enable ANSI escape codes (VT100 for windows).
   * @param hConsoleHandle the console handle ({@link STD_INPUT_HANDLE}, {@link STD_OUTPUT_HANDLE}, 
   *                       {@link STD_ERROR_HANDLE}).
   * @return whether the operation has been performed successfully.
   * @see #getLastError()
   */
  public static boolean enable(final int hConsoleHandle) {
    // https://github.com/fusesource/jansi/blob/master/src/main/java/org/fusesource/jansi/AnsiConsole.java#L280
    final long console = GetStdHandle(hConsoleHandle);
    final int[] mode = new int[1];
    return GetConsoleMode(console, mode) != 0
        && SetConsoleMode(console, mode[0] | ENABLE_PROCESSED_OUTPUT | ENABLE_VIRTUAL_TERMINAL_PROCESSING) != 0;
  }
  
  /** @return a formatted message of the last Windows error. */
  public static String getLastError() {
    final int errorCode = GetLastError();
    final int bufferSize = 160;
    final byte data[] = new byte[bufferSize]; 
    FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, 0, errorCode, 0, data, bufferSize, null);
    return new String(data);
  }
  
  /** Resets {@link arc.util.ColorCodes} fields. */
  public static void resetColorCodes() {
    flush = "\033[H\033[2J";
    reset = "\u001B[0m";
    bold = "\u001B[1m";
    italic = "\u001B[3m";
    underline = "\u001B[4m";
    black = "\u001B[30m";
    red = "\u001B[31m";
    green = "\u001B[32m";
    yellow = "\u001B[33m";
    blue = "\u001B[34m";
    purple = "\u001B[35m";
    cyan = "\u001B[36m";
    lightBlack = "\u001b[90m";
    lightRed = "\u001B[91m";
    lightGreen = "\u001B[92m";
    lightYellow = "\u001B[93m";
    lightBlue = "\u001B[94m";
    lightMagenta = "\u001B[95m";
    lightCyan = "\u001B[96m";
    lightWhite = "\u001b[97m";
    white = "\u001B[37m";
    backDefault = "\u001B[49m";
    backRed = "\u001B[41m";
    backGreen = "\u001B[42m";
    backYellow = "\u001B[43m";
    backBlue = "\u001B[44m";
    
    final StringMap map = StringMap.of(
      "ff", flush,
      "fr", reset,
      "fb", bold,
      "fi", italic,
      "fu", underline,
      "k", black,
      "lk", lightBlack,
      "lw", lightWhite,
      "r", red,
      "g", green,
      "y", yellow,
      "b", blue,
      "p", purple,
      "c", cyan,
      "lr", lightRed,
      "lg", lightGreen,
      "ly", lightYellow,
      "lm", lightMagenta,
      "lb", lightBlue,
      "lc", lightCyan,
      "w", white,
      "bd", backDefault,
      "br", backRed,
      "bg", backGreen,
      "by", backYellow,
      "bb", backBlue
    );
    
    for (int i=0; i<values.length; i++) 
      values[i] = map.get(codes[i], "");
  }
}
