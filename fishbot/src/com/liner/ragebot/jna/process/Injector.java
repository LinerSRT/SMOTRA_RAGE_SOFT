package com.liner.ragebot.jna.process;

import com.sun.jna.Function;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.sun.jna.platform.win32.WinNT.*;

public class Injector implements Runnable {
    private int processId = -1;
    private Path processPath;
    private Path dllPath;

    public Injector(int processId, String dllPathString) {
        this(processId, Paths.get(dllPathString));
    }

    public Injector(int processId, Path dllPath) {
        this.processId = processId;
        this.dllPath = dllPath;
    }

    public Injector(String processPathString, String dllPathString) {
        this(Paths.get(processPathString), Paths.get(dllPathString));
    }

    public Injector(String processPathString, Path dllPath) {
        this(Paths.get(processPathString), dllPath);
    }

    public Injector(Path processPath, Path dllPath) {
        this.processPath = processPath;
        this.dllPath = dllPath;
    }

    @Override
    public void run() {
        final String DllPath = dllPath.toAbsolutePath().toString() + '\0';
        WinNT.HANDLE hProcess;
        if (processId > -1) {
            hProcess = Kernel32.INSTANCE.OpenProcess(WinNT.PROCESS_ALL_ACCESS, false, processId);
        } else {
            WinBase.STARTUPINFO startupInfo = new WinBase.STARTUPINFO();
            WinBase.PROCESS_INFORMATION.ByReference processInformation = new WinBase.PROCESS_INFORMATION.ByReference();
            Kernel32.INSTANCE.CreateProcess(
                    processPath.toAbsolutePath().toString(),
                    null,
                    null,
                    null,
                    false,
                    new WinDef.DWORD(WinBase.CREATE_DEFAULT_ERROR_MODE),
                    Pointer.NULL,
                    null,
                    startupInfo,
                    processInformation);
            hProcess = processInformation.hProcess;
        }
        Pointer pDllPath = Kernel32.INSTANCE.VirtualAllocEx(
                hProcess,
                null,
                new BaseTSD.SIZE_T(DllPath.length()),
                MEM_COMMIT | MEM_RESERVE, PAGE_EXECUTE_READWRITE
        );
        ByteBuffer bufSrc = ByteBuffer.allocateDirect(DllPath.length());
        bufSrc.put(DllPath.getBytes());
        Pointer ptrSrc = Native.getDirectBufferPointer(bufSrc);
        IntByReference bytesWritten = new IntByReference();
        Kernel32.INSTANCE.WriteProcessMemory(hProcess, pDllPath, ptrSrc, DllPath.length(), bytesWritten);
        NativeLibrary kernel32Library = NativeLibrary.getInstance("kernel32");
        Function LoadLibraryAFunction = kernel32Library.getFunction("LoadLibraryA");
        Kernel32.INSTANCE.WaitForSingleObject(
                Kernel32.INSTANCE.CreateRemoteThread(
                hProcess,
                null,
                0,
                LoadLibraryAFunction,
                pDllPath,
                0,
                        new DWORDByReference()
                ),
                20 * 1000
        );
        Kernel32.INSTANCE.VirtualFreeEx(hProcess, pDllPath, new SIZE_T(0), WinNT.MEM_RELEASE);
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public Path getProcessPath() {
        return processPath;
    }

    public void setProcessPath(Path processPath) {
        this.processPath = processPath;
    }

    public Path getDllPath() {
        return dllPath;
    }

    public void setDllPath(Path dllPath) {
        this.dllPath = dllPath;
    }
}