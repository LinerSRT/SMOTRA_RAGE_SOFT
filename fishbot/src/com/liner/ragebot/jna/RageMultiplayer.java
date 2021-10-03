package com.liner.ragebot.jna;

import com.liner.ragebot.Core;
import com.liner.ragebot.jna.process.Process;
import com.liner.ragebot.messages.*;
import com.liner.ragebot.utils.InputManager;
import com.sun.jna.Memory;
import com.sun.jna.platform.DesktopWindow;
import com.sun.jna.platform.WindowUtils;
import com.sun.jna.platform.win32.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.liner.ragebot.jna.JNAUtils.IGDI32;
import static com.liner.ragebot.jna.JNAUtils.IUSER32;

@SuppressWarnings("unused")
public class RageMultiplayer {
    private volatile Process process;
    public static class MEM {
        public static byte[] FISHING_WARNS_DATA = new byte[]{0x70, 0x00, 0x6F, 0x00, 0x73, 0x00, 0x46, 0x00, 0x69, 0x00, 0x73, 0x00, 0x68, 0x00, 0x69, 0x00, 0x6E, 0x00, 0x67, 0x00, 0x57, 0x00, 0x61, 0x00, 0x72, 0x00, 0x6E, 0x00, 0x73, 0x00, 0x2B, 0x00, 0x2B, 0x00, 0x3B};
        public static byte[] FISHING_WARNS_MOD_DATA = new byte[]{0x70, 0x00, 0x6F, 0x00, 0x73, 0x00, 0x46, 0x00, 0x69, 0x00, 0x73, 0x00, 0x68, 0x00, 0x69, 0x00, 0x6E, 0x00, 0x67, 0x00, 0x57, 0x00, 0x61, 0x00, 0x72, 0x00, 0x6E, 0x00, 0x73, 0x00, 0x3D, 0x00, 0x30, 0x00, 0x3B};
    }
    private volatile DesktopWindow desktopWindow;
    private volatile WinDef.HWND hwnd;
    private volatile BufferedImage buffer;
    private boolean isLaunched;
    private int width;
    private int height;
    private int positionX;
    private int positionY;

    public RageMultiplayer() {
        this.desktopWindow = getRageWindow();
        if (desktopWindow == null) {
            isLaunched = false;
        } else {
            isLaunched = true;
            this.hwnd = desktopWindow.getHWND();
            this.process = new Process("GTA5.exe");
            Rectangle rectangle = desktopWindow.getLocAndSize();
            this.width = rectangle.width;
            this.height = rectangle.height;
            this.positionX = rectangle.x;
            this.positionY = rectangle.y;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.gc();
                }
            },1000, 5000);
        }
    }

    public Rectangle updateLocation() {
        WinDef.RECT windowRect = new WinDef.RECT();
        IUSER32.GetClientRect(hwnd, windowRect);
        Rectangle rectangle = windowRect.toRectangle();
        this.width = rectangle.width;
        this.height = rectangle.height;
        this.positionX = rectangle.x;
        this.positionY = rectangle.y;
        return rectangle;
    }

    public BufferedImage getBuffer() {
        Rectangle windowDimension = updateLocation();
        final int width = windowDimension.width;
        final int height = windowDimension.height;
        if(width <= 0 || height <= 0)
            return new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        final int bufferSize = width * height;
        buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WinDef.HDC hdcWindow = IUSER32.GetDC(hwnd);
        WinDef.HDC hdcMemory = IGDI32.CreateCompatibleDC(hdcWindow);
        WinDef.HBITMAP bitmap = IGDI32.CreateCompatibleBitmap(hdcWindow, width, height);
        WinNT.HANDLE handle = IGDI32.SelectObject(hdcMemory, bitmap);
        IGDI32.BitBlt(hdcMemory, 0, 0, width, height, hdcWindow, 0, 0, GDI32.SRCCOPY);
        IGDI32.SelectObject(hdcMemory, handle);
        IGDI32.DeleteDC(hdcMemory);
        WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();
        bitmapInfo.bmiHeader.biWidth = width;
        bitmapInfo.bmiHeader.biHeight = -height;
        bitmapInfo.bmiHeader.biPlanes = 1;
        bitmapInfo.bmiHeader.biBitCount = 32;
        bitmapInfo.bmiHeader.biCompression = WinGDI.BI_RGB;
        Memory memory = new Memory(bufferSize * 4);
        IGDI32.GetDIBits(hdcWindow, bitmap, 0, height, memory, bitmapInfo, WinGDI.DIB_RGB_COLORS);
        buffer.setRGB(
                0,
                0,
                width,
                height,
                memory.getIntArray(
                        0,
                        bufferSize
                ),
                0,
                width
        );
        memory.clear();
        IGDI32.DeleteObject(bitmap);
        IUSER32.ReleaseDC(hwnd, hdcWindow);
        return buffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getPositionY() {
        return positionY;
    }

    public int getPositionX() {
        return positionX;
    }

    public WinDef.HWND getHwnd() {
        return hwnd;
    }

    public boolean isLaunched() {
        return isLaunched;
    }

    public void pressKey(int keyCode) {
        InputManager.pressKey(hwnd, keyCode);
    }

    public void leftClick(int x, int y) {
        InputManager.leftClick(hwnd, x, y);
    }

    public void leftDrag(int startX, int startY, int endX, int endY) {
        InputManager.leftDrag(
                hwnd,
                startX,
                startY,
                endX,
                endY);
    }

    public void waitLaunch() {
        do {
            isLaunched = false;
        } while ((desktopWindow = getRageWindow()) == null);
        isLaunched = true;
        this.hwnd = desktopWindow.getHWND();
        this.process = new Process("GTA5.exe");
        Rectangle rectangle = desktopWindow.getLocAndSize();
        this.width = rectangle.width;
        this.height = rectangle.height;
        this.positionX = rectangle.x;
        this.positionY = rectangle.y;
    }

    public void fishingPosHook() {
        MessageForm messageForm = new MessageForm(
                new MessageConfig.Builder()
                        .setMessageText("Пожалуйста, подождите выполенения операции!")
                        .setMessageTitle("Внедрение в игру")
                        .setMessageIcon(Core.Icon.hackIcon)
                        .setMessageType(MessageType.PROGRESS_INDETERMINATE)
                        .setMessagePosition(MessagePosition.CENTER)
                        .build()
        );
        messageForm.show();
        messageForm.playSound(Core.Sound.notification);
        messageForm.closeAfter(5000);
        process.write(process.find(MEM.FISHING_WARNS_DATA), MEM.FISHING_WARNS_MOD_DATA);

        MessageForm messageNext = new MessageForm(
                new MessageConfig.Builder()
                        .setMessageText("Процесс завершился, если результата нет, попробуйте еще раз!")
                        .setMessageTitle("Внедрение завершено")
                        .setMessageIcon(Core.Icon.hackCheckIcon)
                        .setMessageType(MessageType.FINISH)
                        .setConfirmListener(new MessageActionListener() {
                            @Override
                            public String getName() {
                                return "Понятно";
                            }

                            @Override
                            public BufferedImage getIcon() {
                                return Core.Icon.doneIcon;
                            }

                            @Override
                            public void onClicked(MessageForm message) {
                                message.close();
                            }
                        })
                        .setMessagePosition(MessagePosition.CENTER)
                        .build()
        );
        messageNext.show();
        messageNext.playSound(Core.Sound.notification);
    }

    public boolean isFishingHocked() {
        return process.find(MEM.FISHING_WARNS_DATA) == -1;
    }

    public void releaseResource(){
        process.releaseResources();
    }

    private static DesktopWindow getRageWindow() {
        try {

            final List<DesktopWindow> desktopWindowList = WindowUtils.getAllWindows(false);
            for (final DesktopWindow desktopWindow : desktopWindowList) {
                if (desktopWindow.getFilePath().toLowerCase().endsWith("GTA5.exe".toLowerCase())) {
                    return desktopWindow;
                }
            }
            return null;
        } catch (Win32Exception e){
            return null;
        }
    }
}
