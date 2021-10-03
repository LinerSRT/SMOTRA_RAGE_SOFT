package com.liner.ragebot.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface PsapiEx extends StdCallLibrary {
    PsapiEx INSTANCE = Native.load("psapi", PsapiEx.class, W32APIOptions.DEFAULT_OPTIONS);

    int GetModuleBaseNameA(WinNT.HANDLE handle, WinDef.HMODULE module, char[] lpImageFileName, int nSize);

    int GetModuleBaseNameW(WinNT.HANDLE handle, WinNT.HANDLE handle2, char[] baseName, int size);

    int GetModuleFileNameExA(WinNT.HANDLE var1, WinNT.HANDLE var2, byte[] var3, int var4);

    int GetModuleFileNameExW(WinNT.HANDLE var1, WinNT.HANDLE var2, char[] var3, int var4);

    int GetModuleFileNameEx(WinNT.HANDLE var1, WinNT.HANDLE var2, Pointer var3, int var4);

    boolean EnumProcessModules(WinNT.HANDLE var1, WinDef.HMODULE[] var2, int var3, IntByReference var4);

    boolean GetModuleInformation(WinNT.HANDLE var1, WinDef.HMODULE var2, Psapi.MODULEINFO var3, int var4);

    int GetProcessImageFileName(WinNT.HANDLE var1, char[] var2, int var3);

    boolean GetPerformanceInfo(Psapi.PERFORMANCE_INFORMATION var1, int var2);

    boolean EnumProcesses(int[] var1, int var2, IntByReference var3);

    @Structure.FieldOrder({"cb", "CommitTotal", "CommitLimit", "CommitPeak", "PhysicalTotal", "PhysicalAvailable", "SystemCache", "KernelTotal", "KernelPaged", "KernelNonpaged", "PageSize", "HandleCount", "ProcessCount", "ThreadCount"})
    class PERFORMANCE_INFORMATION extends Structure {
        public WinDef.DWORD cb;
        public BaseTSD.SIZE_T CommitTotal;
        public BaseTSD.SIZE_T CommitLimit;
        public BaseTSD.SIZE_T CommitPeak;
        public BaseTSD.SIZE_T PhysicalTotal;
        public BaseTSD.SIZE_T PhysicalAvailable;
        public BaseTSD.SIZE_T SystemCache;
        public BaseTSD.SIZE_T KernelTotal;
        public BaseTSD.SIZE_T KernelPaged;
        public BaseTSD.SIZE_T KernelNonpaged;
        public BaseTSD.SIZE_T PageSize;
        public WinDef.DWORD HandleCount;
        public WinDef.DWORD ProcessCount;
        public WinDef.DWORD ThreadCount;

        public PERFORMANCE_INFORMATION() {
        }
    }

    @Structure.FieldOrder({"lpBaseOfDll", "SizeOfImage", "EntryPoint"})
    class MODULEINFO extends Structure {
        public Pointer EntryPoint;
        public Pointer lpBaseOfDll;
        public int SizeOfImage;

        public MODULEINFO() {
        }
    }
}