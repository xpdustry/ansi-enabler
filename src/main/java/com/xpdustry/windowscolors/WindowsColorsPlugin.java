package com.xpdustry.windowscolors;

import arc.util.OS;
import mindustry.mod.Plugin;

/**
 * Plugin that enables ANSI colors on Windows consoles by loading jansi-native.
 * This plugin implements the functionality from Arc commit eed8cd2c55605fec2dd02c3eeeb216b825bee6e0
 * which adds support for jansi-native library to enable colored console output on Windows.
 */
public class WindowsColorsPlugin extends Plugin {

    @Override
    public void init() {
        // Only initialize on Windows systems
        if (OS.isWindows) {
            try {
                // Force jansi-native to load by accessing the JNI library
                // This enables ANSI escape code processing on Windows consoles
                System.loadLibrary("jansi");
                arc.util.Log.info("[@] Windows colors enabled successfully.", "windows-colors");
            } catch (UnsatisfiedLinkError e) {
                arc.util.Log.warn("[@] Failed to enable Windows colors: @", "windows-colors", e.getMessage());
            }
        } else {
            arc.util.Log.info("[@] Not running on Windows, colors already supported.", "windows-colors");
        }
    }
}
