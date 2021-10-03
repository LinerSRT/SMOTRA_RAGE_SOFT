package com.liner.ragebot.utils;

import com.liner.ragebot.Core;
import com.liner.ragebot.jna.JNAUtils;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;

import static com.liner.ragebot.jna.JNAUtils.IUSER32;

public class InputManager {
    public static boolean useDXApi = false;
    public static int WM_LBUTTONDOWN = 513;
    public static int WM_LBUTTONUP = 514;
    public static int WM_RBUTTONDOWN = 516;
    public static int WM_RBUTTONUP = 517;
    public static int WM_MOUSEMOVE = 512;
    public static int WM_KEYDOWN = 256;
    public static int WM_KEYUP = 257;

    public static void pressKey(WinDef.HWND window, int keyCode) {
        WinDef.HWND activeWindow = IUSER32.GetForegroundWindow();
        boolean windowActive = activeWindow == null || activeWindow.equals(window);
        if (!windowActive)
            JNAUtils.showWindow(window);
        if(useDXApi){
            holdKey(keyCode);
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
            releaseKey(keyCode);
        } else {
            IUSER32.PostMessage(window, WM_KEYDOWN, new WinDef.WPARAM(keyCode), null);
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
            IUSER32.PostMessage(window, WM_KEYUP, new WinDef.WPARAM(keyCode), null);
        }
        if (!windowActive)
            JNAUtils.showWindow(activeWindow);
    }

    public static void leftClick(WinDef.HWND window, int x, int y) {
        WinDef.HWND activeWindow = IUSER32.GetForegroundWindow();
        boolean windowActive = activeWindow == null || activeWindow.equals(window);
        if (!windowActive)
            JNAUtils.showWindow(window);
        if (useDXApi) {
            Rectangle rectangle = JNAUtils.getWindowRectangle(window);
            mouseInput(new WinDef.DWORD(0x0001L | 0x4000L | 0x8000L | 0x0002L | 0x0004L), rectangle.x+x, rectangle.y+y, true);
        } else {
            IUSER32.PostMessage(window, WM_LBUTTONDOWN, new WinDef.WPARAM(1), new WinDef.LPARAM(x + y * 65536));
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
            IUSER32.PostMessage(window, WM_LBUTTONUP, new WinDef.WPARAM(0), new WinDef.LPARAM(x + y * 65536));
        }
        if (!windowActive)
            JNAUtils.showWindow(activeWindow);
    }

    public static void rightClick(WinDef.HWND window, int x, int y) {
        WinDef.HWND activeWindow = IUSER32.GetForegroundWindow();
        boolean windowActive = activeWindow == null || activeWindow.equals(window);
        if (!windowActive)
            JNAUtils.showWindow(window);
        if (useDXApi) {
            Rectangle rectangle = JNAUtils.getWindowRectangle(window);
            mouseInput(new WinDef.DWORD(0x0001L | 0x4000L | 0x8000L | 0x0008L | 0x0010L), rectangle.x+x, rectangle.y+y, true);
        } else {
            IUSER32.PostMessage(window, WM_RBUTTONDOWN, new WinDef.WPARAM(1), new WinDef.LPARAM(x + y * 65536));
            try {
                Thread.sleep(16);
            } catch (InterruptedException ignored) {
            }
            IUSER32.PostMessage(window, WM_RBUTTONUP, new WinDef.WPARAM(0), new WinDef.LPARAM(x + y * 65536));
        }
        if (!windowActive)
            JNAUtils.showWindow(activeWindow);
    }

    public static void leftDrag(WinDef.HWND window, int fromX, int fromY, int toX, int toY) {
        WinDef.HWND activeWindow = IUSER32.GetForegroundWindow();
        boolean windowActive = activeWindow == null || activeWindow.equals(window);
        if (!windowActive)
            JNAUtils.showWindow(window);
        if(useDXApi) {
            Rectangle rectangle = JNAUtils.getWindowRectangle(window);
            try {
                Thread.sleep(120);
            } catch (InterruptedException ignored) {
            }
            mouseInput(new WinDef.DWORD(0x0001L | 0x4000L | 0x8000L), (rectangle.x+fromX), (rectangle.y+fromY), true);
            try {
                Thread.sleep(32);
            } catch (InterruptedException ignored) {
            }
            mouseInput(new WinUser.DWORD(0x0002L));
            try {
                Thread.sleep(32);
            } catch (InterruptedException ignored) {
            }
            mouseInput(new WinUser.DWORD(0x0001L | 0x4000L | 0x8000L), (rectangle.x+toX), (rectangle.y+toY), true);
            try {
                Thread.sleep(32);
            } catch (InterruptedException ignored) {
            }
            mouseInput(new WinUser.DWORD(0x0004L));
            try {
                Thread.sleep(220);
            } catch (InterruptedException ignored) {
            }
        } else {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            IUSER32.PostMessage(
                    window,
                    WM_LBUTTONDOWN,
                    new WinDef.WPARAM(1),
                    new WinDef.LPARAM(fromX + fromY * 65536)
            );
            IUSER32.PostMessage(
                    window,
                    WM_MOUSEMOVE,
                    new WinDef.WPARAM(1),
                    new WinDef.LPARAM(toX + toY * 65536)
            );
            IUSER32.PostMessage(
                    window,
                    WM_LBUTTONUP,
                    new WinDef.WPARAM(1),
                    new WinDef.LPARAM(toX + toY * 65536)
            );
        }
        if (!windowActive)
            JNAUtils.showWindow(activeWindow);
    }

    public static void holdKey(int keyCode) {
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
        input.input.setType("ki");
        input.input.ki.wScan = new WinDef.WORD(0);
        input.input.ki.time = new WinDef.DWORD(0);
        input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
        input.input.ki.wVk = new WinDef.WORD(keyCode);
        input.input.ki.dwFlags = new WinDef.DWORD(0);
        IUSER32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) input.toArray(1), input.size());
    }

    public static void releaseKey(int keyCode) {
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinDef.DWORD(WinUser.INPUT.INPUT_KEYBOARD);
        input.input.setType("ki");
        input.input.ki.wScan = new WinDef.WORD(0);
        input.input.ki.time = new WinDef.DWORD(0);
        input.input.ki.dwExtraInfo = new BaseTSD.ULONG_PTR(0);
        input.input.ki.wVk = new WinDef.WORD(keyCode);
        input.input.ki.dwFlags = new WinDef.DWORD(WinUser.KEYBDINPUT.KEYEVENTF_KEYUP);
        IUSER32.SendInput(new WinDef.DWORD(1), (WinUser.INPUT[]) input.toArray(1), input.size());
    }

    public static void mouseInput(WinUser.DWORD flag, int x, int y, boolean absolute) {
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinUser.DWORD(WinUser.INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        if (absolute) {
            input.input.mi.dx = new WinUser.LONG(x * 65536 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
            input.input.mi.dy = new WinUser.LONG(y * 65536 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
        } else {
            input.input.mi.dx = new WinUser.LONG(x);
            input.input.mi.dy = new WinUser.LONG(y);
        }
        input.input.mi.mouseData = new WinUser.DWORD(0);
        input.input.mi.dwFlags = flag;
        input.input.mi.time = new WinUser.DWORD(0);
        WinUser.INPUT[] inArray = {input};
        int cbSize = input.size();
        WinUser.DWORD nInputs = new WinUser.DWORD(1);
        IUSER32.SendInput(nInputs, inArray, cbSize);
    }

    public static void mouseInput(WinUser.DWORD flag) {
        WinUser.INPUT input = new WinUser.INPUT();
        input.type = new WinUser.DWORD(WinUser.INPUT.INPUT_MOUSE);
        input.input.setType("mi");
        input.input.mi.mouseData = new WinUser.DWORD(0);
        input.input.mi.dwFlags = flag;
        input.input.mi.time = new WinUser.DWORD(0);
        WinUser.INPUT[] inArray = {input};
        int cbSize = input.size();
        WinUser.DWORD nInputs = new WinUser.DWORD(1);
        IUSER32.SendInput(nInputs, inArray, cbSize);
    }
}
