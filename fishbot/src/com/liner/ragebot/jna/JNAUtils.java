package com.liner.ragebot.jna;

import com.sun.jna.platform.win32.*;
import com.sun.management.OperatingSystemMXBean;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static com.sun.jna.platform.win32.WinUser.*;

public class JNAUtils {
    public static final User32 IUSER32 = User32.INSTANCE;
    public static final Kernel32 KERNEL32 = Kernel32.INSTANCE;
    public static final GDI32 IGDI32 = GDI32.INSTANCE;


    public static void hideWindow(WinDef.HWND window) {
        IUSER32.SetForegroundWindow(window);
        IUSER32.ShowWindow(window, SW_SHOWMINIMIZED);
    }

    public static Rectangle getWindowRectangle(WinDef.HWND window) {
        WinDef.RECT windowRect = new WinDef.RECT();
        IUSER32.GetClientRect(window, windowRect);
        return windowRect.toRectangle();
    }

    public static void showWindow(WinDef.HWND window) {
        WinUser.WINDOWPLACEMENT place = new WinUser.WINDOWPLACEMENT();
        IUSER32.GetWindowPlacement(window, place);
        switch (place.showCmd) {
            case SW_SHOWMAXIMIZED:
                IUSER32.ShowWindow(window, SW_SHOWMAXIMIZED);
                break;
            case SW_SHOWMINIMIZED:
                IUSER32.ShowWindow(window, SW_RESTORE);
                break;
            default:
                IUSER32.ShowWindow(window, SW_NORMAL);
                break;
        }
        IUSER32.SetForegroundWindow(window);
        IUSER32.SetFocus(window);
    }

    public static BufferedImage getWindowBuffer(final HWND window) {
        return GDI32Util.getScreenshot(window);
    }
}
