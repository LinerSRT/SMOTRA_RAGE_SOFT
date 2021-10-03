package com.liner.ragebot.jna;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.util.LinkedList;

public class KeyboardHook {
    private static KeyboardHook keyboardHook;
    private final LinkedList<KeyCallback> keyCallbacks;

    public interface KeyCallback {
        void onKeyDown(int keyCode);

        void onKeyUp(int keyCode);
    }

    public static KeyboardHook getInstance() {
        if (keyboardHook == null)
            return keyboardHook = new KeyboardHook();
        return keyboardHook;
    }

    private KeyboardHook() {
        keyCallbacks = new LinkedList<>();
        new Thread(() -> {
            WinUser.LowLevelKeyboardProc hookProc = (nCode, wParam, lParam) -> {
                switch (wParam.intValue()) {
                    case WinUser.WM_KEYUP:
                        for (KeyCallback callback : keyCallbacks) {
                            callback.onKeyUp(lParam.vkCode);
                        }
                        break;
                    case WinUser.WM_KEYDOWN:
                        for (KeyCallback callback : keyCallbacks) {
                            callback.onKeyDown(lParam.vkCode);
                        }
                        break;
                }
                return new WinDef.LRESULT(0);
            };
            WinDef.HINSTANCE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
            User32.HHOOK hHook = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, hookProc, hMod, 0);
            User32.MSG msg = new User32.MSG();
            User32.INSTANCE.GetMessage(msg, null, 0, 0);
            User32.INSTANCE.UnhookWindowsHookEx(hHook);
        }).start();
    }

    public void subscribe(KeyCallback keyCallback) {
        if (!keyCallbacks.contains(keyCallback))
            keyCallbacks.add(keyCallback);
    }

    public void unsubscribe(KeyCallback keyCallback) {
        keyCallbacks.remove(keyCallback);
    }
}
