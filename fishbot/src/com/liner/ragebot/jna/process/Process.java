package com.liner.ragebot.jna.process;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.liner.ragebot.jna.JNAUtils.IUSER32;
import static com.liner.ragebot.jna.JNAUtils.KERNEL32;
import static com.sun.jna.platform.win32.WinNT.MEM_COMMIT;
import static com.sun.jna.platform.win32.WinNT.PAGE_READWRITE;

@SuppressWarnings("unused")
public class Process {
    public static final int PROCESS_QUERY_INFORMATION = 0x0400;
    public static final int PROCESS_VM_READ = 0x0010;
    public static final int PROCESS_VM_WRITE = 0x0020;
    public static final int PROCESS_VM_OPERATION = 0x0008;
    public static final int PROCESS_ALL_ACCESS = 0x001F0FFF;
    private WinNT.HANDLE process;
    private int processID;
    private String processExe;
    private long processThreads;
    private long processPriority;

    public Process(WinDef.HWND hwnd) {
        IntByReference intByReference = new IntByReference(0);
        IUSER32.GetWindowThreadProcessId(hwnd, intByReference);
        Tlhelp32.PROCESSENTRY32 processEntry32 = findProcessByPID(intByReference.getValue());
        if (processEntry32 == null)
            processEntry32 = findProcessByName(getExecutorPath(hwnd));
        this.processID = processEntry32.th32ProcessID.intValue();
        this.process = KERNEL32.OpenProcess(PROCESS_ALL_ACCESS, false, processID);
        this.processExe = Native.toString(processEntry32.szExeFile);
        this.processPriority = processEntry32.pcPriClassBase.longValue();
        this.processThreads = processEntry32.cntThreads.longValue();
    }

    public Process(String processName) {
        Tlhelp32.PROCESSENTRY32 processEntry32 = findProcessByName(processName);
        this.processID = processEntry32.th32ProcessID.intValue();
        this.process = KERNEL32.OpenProcess(PROCESS_ALL_ACCESS, false, processID);
        this.processExe = Native.toString(processEntry32.szExeFile);
        this.processPriority = processEntry32.pcPriClassBase.longValue();
        this.processThreads = processEntry32.cntThreads.longValue();
    }

    public Process(int processID) {
        Tlhelp32.PROCESSENTRY32 processEntry32 = findProcessByPID(processID);
        this.processID = processEntry32.th32ProcessID.intValue();
        this.process = KERNEL32.OpenProcess(PROCESS_ALL_ACCESS, false, processID);
        this.processExe = Native.toString(processEntry32.szExeFile);
        this.processPriority = processEntry32.pcPriClassBase.longValue();
        this.processThreads = processEntry32.cntThreads.longValue();
    }

    public Process(String processName, int processID) {
        Tlhelp32.PROCESSENTRY32 processEntry32 = null;
        if (processName != null) processEntry32 = findProcessByName(processName);
        if (processID != -1) processEntry32 = findProcessByPID(processID);
        if (processEntry32 == null) return;
        this.processID = processEntry32.th32ProcessID.intValue();
        this.process = KERNEL32.OpenProcess(PROCESS_ALL_ACCESS, false, processID);
        this.processExe = Native.toString(processEntry32.szExeFile);
        this.processPriority = processEntry32.pcPriClassBase.longValue();
        this.processThreads = processEntry32.cntThreads.longValue();
    }

    public Memory read(long address, int bytesToRead) {
        if (process == null) {
            return new Memory(0);
        }
        IntByReference read = new IntByReference(0);
        Memory output = new Memory(bytesToRead);
        KERNEL32.ReadProcessMemory(process, new Pointer(address), output, bytesToRead, read);
        return output;
    }

    public String readString(long address, int bytesToRead) {
        if (process == null) {
            return "none";
        }
        return new String(read(address, bytesToRead).getByteArray(0, bytesToRead));
    }

    public byte[] readBytes(long address, int bytesToRead) {
        if (process == null) {
            return new byte[]{};
        }
        return read(address, bytesToRead).getByteArray(0, bytesToRead);
    }

    public int readInt(long address, int bytesToRead) {
        if (process == null) {
            return 0;
        }
        return read(address, bytesToRead).getInt(0);
    }

    public long readLong(long address, int bytesToRead) {
        if (process == null) {
            return 0;
        }
        return read(address, bytesToRead).getLong(0);
    }

    public double readDouble(long address, int bytesToRead) {
        if (process == null) {
            return 0;
        }
        return read(address, bytesToRead).getDouble(0);
    }

    public float readFloat(long address, int bytesToRead) {
        if (process == null) {
            return 0;
        }
        return read(address, bytesToRead).getFloat(0);
    }

    public void write(long address, byte[] data) {
        if (process == null) {
            return;
        }
        int size = data.length;
        Memory memory = new Memory(size);
        for (int i = 0; i < size; i++)
            memory.setByte(i, data[i]);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, size, null);
    }

    public void writeString(long address, String string) {
        if (process == null) {
            return;
        }
        Memory memory = new Memory(string.length());
        memory.setString(0, string);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, (int) memory.size(), null);
    }

    public void writeInt(long address, int value) {
        if (process == null) {
            return;
        }
        Memory memory = new Memory(String.valueOf(value).length());
        memory.setInt(0, value);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, (int) memory.size(), null);
    }


    public void writeLong(long address, long value) {
        if (process == null) {
            return;
        }
        Memory memory = new Memory(String.valueOf(value).length());
        memory.setLong(0, value);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, (int) memory.size(), null);
    }

    public void writeDouble(long address, double value) {
        if (process == null) {
            return;
        }
        Memory memory = new Memory(String.valueOf(value).length());
        memory.setDouble(0, value);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, (int) memory.size(), null);
    }

    public void writeFloat(long address, float value) {
        if (process == null) {
            return;
        }
        Memory memory = new Memory(String.valueOf(value).length());
        memory.setFloat(0, value);
        KERNEL32.WriteProcessMemory(process, new Pointer(address), memory, (int) memory.size(), null);
    }

    public List<Long> findAll(String string, int chunkCount, int threadCount) {
        return findAll(string.getBytes(), chunkCount, threadCount, false, true);
    }

    public List<Long> findAll(String string) {
        return findAll(string.getBytes(), 16, 8, false, true);
    }


    public long find(String string, int chunkCount, int threadCount) {
        return findAll(string.getBytes(), chunkCount, threadCount, true, true).get(0);
    }

    public long find(byte[] pattern, int chunkCount, int threadCount) {
        return findAll(pattern, chunkCount, threadCount, true, true).get(0);
    }


    public long find(String string) {
        return findAll(string.getBytes(), 16, 8, true, true).get(0);
    }

    public long find(byte[] pattern) {
        List<Long> addresses = findAll(pattern, 16, 8, true, true);
        if (addresses.isEmpty())
            return -1;
        return addresses.get(0);
    }

    public List<Long> findAll(byte[] pattern, int chunkCount, int threadCount, boolean limitToOne, boolean logOut) {
        long startSearchTime = System.currentTimeMillis();
        long totalSize = getMemoryUsage();
        long processedSize = 0;
        List<Long> resultList = new ArrayList<>();
        if (process == null) {
            return resultList;
        }
        WinNT.MEMORY_BASIC_INFORMATION mbi;
        WinBase.SYSTEM_INFO si = new WinBase.SYSTEM_INFO();
        KERNEL32.GetSystemInfo(si);
        Pointer lpMem = si.lpMinimumApplicationAddress;
        long maxAddress = pointerToAddress(si.lpMaximumApplicationAddress);
        AtomicBoolean stop = new AtomicBoolean(false);
        while (pointerToAddress(lpMem) < maxAddress && !stop.get()) {
            mbi = new WinNT.MEMORY_BASIC_INFORMATION();
            BaseTSD.SIZE_T t = KERNEL32.VirtualQueryEx(process, lpMem, mbi, new BaseTSD.SIZE_T(mbi.size()));
            if (t.longValue() == 0) {
                break;
            }
            if (mbi.protect.intValue() == PAGE_READWRITE && mbi.state.intValue() == MEM_COMMIT) {
                long regionSize = mbi.regionSize.longValue();
                long blockSize = regionSize / chunkCount;
                ExecutorService executor = Executors.newFixedThreadPool(chunkCount);
                List<byte[]> regionData = new ArrayList<>();
                for (int i = 0; i < threadCount; i++) {
                    WinNT.MEMORY_BASIC_INFORMATION finalMbi = mbi;
                    int finalI = i;
                    executor.execute(() -> {
                        long readAddress = pointerToAddress(finalMbi.baseAddress) + (blockSize * finalI);
                        Memory output = new Memory(blockSize);
                        KERNEL32.ReadProcessMemory(process, new Pointer(readAddress), output, (int) output.size(), new IntByReference(0));
                        byte[] data = output.getByteArray(0, (int) output.size());
                        output.clear();
                        if (exists(data, pattern)) {
                            long foundAddress = readAddress + indexOf(data, pattern);
                            resultList.add(foundAddress);
                            if (limitToOne) {
                                stop.set(true);
                                executor.shutdownNow();
                            }
                        }
                    });
                }
                executor.shutdown();
                try {
                    executor.awaitTermination(10, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (!executor.isTerminated()) {
                }
                processedSize += mbi.regionSize.longValue();
            }
            int percent = Math.round((float) processedSize / (float) totalSize * 100f);
            lpMem = new Pointer(pointerToAddress(mbi.baseAddress) + mbi.regionSize.longValue());
        }

        if (logOut) {
            long tookMillis = System.currentTimeMillis() - startSearchTime;
            StringBuilder stringBuilder = new StringBuilder();
            for (long address : resultList) {
                stringBuilder.append("\t\t [0x").append(Long.toHexString(address)).append("]\n");
            }
        }
        Memory.purge();
        return resultList;
    }

    public boolean exists(byte[] search, byte[] pattern) {
        return indexOf(search, pattern) != -1;
    }

    public int indexOf(byte[] search, byte[] pattern) {
        for (int i = 0; i < search.length - pattern.length + 1; ++i) {
            boolean found = true;
            for (int j = 0; j < pattern.length; ++j) {
                if (search[i + j] != pattern[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return i;
        }
        return -1;
    }

    private static long pointerToAddress(Pointer p) {
        String s = p.toString();
        s = s.replace("native@0x", "");
        return Long.parseLong(s, 16);
    }

    public static String formatSize(long bytesSize) {
        if (bytesSize < 1024) return bytesSize + " B";
        int z = (63 - Long.numberOfLeadingZeros(bytesSize)) / 10;
        return String.format("%.1f %sB", (double) bytesSize / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    private long getMemoryUsage() {
        long totalSize = 0;
        if (process == null) {
            return totalSize;
        }
        WinNT.MEMORY_BASIC_INFORMATION mbi;
        WinBase.SYSTEM_INFO si = new WinBase.SYSTEM_INFO();
        KERNEL32.GetSystemInfo(si);
        Pointer lpMem = si.lpMinimumApplicationAddress;
        long maxAddress = pointerToAddress(si.lpMaximumApplicationAddress);
        while (pointerToAddress(lpMem) < maxAddress) {
            mbi = new WinNT.MEMORY_BASIC_INFORMATION();
            BaseTSD.SIZE_T t = KERNEL32.VirtualQueryEx(process, lpMem, mbi, new BaseTSD.SIZE_T(mbi.size()));
            if (t.longValue() == 0) {
                break;
            }
            if (mbi.protect.intValue() == PAGE_READWRITE && mbi.state.intValue() == MEM_COMMIT) {
                totalSize += mbi.regionSize.longValue();
            }
            lpMem = new Pointer(pointerToAddress(mbi.baseAddress) + mbi.regionSize.longValue());
        }
        return totalSize;
    }

    public static Tlhelp32.PROCESSENTRY32 findProcessByName(String processName) {
        Tlhelp32.PROCESSENTRY32 result = null;
        WinNT.HANDLE handle = null;
        try {
            handle = KERNEL32.CreateToolhelp32Snapshot(
                    Tlhelp32.TH32CS_SNAPPROCESS,
                    new WinDef.DWORD(0)
            );
            final Tlhelp32.PROCESSENTRY32 processEntry32 = new Tlhelp32.PROCESSENTRY32();
            KERNEL32.Process32First(handle, processEntry32);
            do {
                if (processName.contains(Native.toString(processEntry32.szExeFile))) {
                    result = processEntry32;
                    break;
                }
            } while (KERNEL32.Process32Next(handle, processEntry32));

        } finally {
            KERNEL32.CloseHandle(handle);
        }
        return result;
    }

    public static Tlhelp32.PROCESSENTRY32 findProcessByPID(int processID) {
        Tlhelp32.PROCESSENTRY32 result = null;
        WinNT.HANDLE handle = null;
        try {
            handle = KERNEL32.CreateToolhelp32Snapshot(
                    Tlhelp32.TH32CS_SNAPPROCESS,
                    new WinDef.DWORD(0)
            );
            final Tlhelp32.PROCESSENTRY32 processEntry32 = new Tlhelp32.PROCESSENTRY32();
            KERNEL32.Process32First(handle, processEntry32);
            do {
                if (processEntry32.th32ProcessID.intValue() == processID) {
                    result = processEntry32;
                    break;
                }
            } while (KERNEL32.Process32Next(handle, processEntry32));

        } finally {
            KERNEL32.CloseHandle(handle);
        }
        return result;
    }

    public static String getExecutorPath(WinDef.HWND window) {
        String executor = "None";
        IntByReference processId = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(window, processId);
        WinNT.HANDLE process = KERNEL32.OpenProcess(PROCESS_VM_READ | PROCESS_QUERY_INFORMATION, false, processId.getValue());
        if (process != null) {
            try {
                Memory memory = new Memory(512);
                Psapi.INSTANCE.GetModuleFileNameEx(process, null, memory, 256);
                executor = Native.toString(memory.getCharArray(0, 256));
            } finally {
                KERNEL32.CloseHandle(process);
            }
        }
        return executor;
    }

    public int getProcessID() {
        return processID;
    }

    public String getProcessExe() {
        return processExe;
    }

    public long getProcessPriority() {
        return processPriority;
    }

    public long getProcessThreads() {
        return processThreads;
    }

    public void releaseResources(){
        System.gc();
        Memory.disposeAll();
    }

    @Override
    public String toString() {
        return "Process {" +
                "processID=" + processID +
                ", processExe='" + processExe + '\'' +
                ", processThreads=" + processThreads +
                ", processPriority=" + processPriority +
                '}';
    }
}
