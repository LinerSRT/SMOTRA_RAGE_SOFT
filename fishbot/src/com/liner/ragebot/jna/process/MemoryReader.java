package com.liner.ragebot.jna.process;

import com.liner.ragebot.utils.Worker;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemoryReader {
    private Process process;
    private Pattern pattern;
    private String searchPattern;
    private UpdateCallback updateCallback;
    private String data;
    private long address;
    private int blockSize;
    private long updatePeriod;
    private boolean exists;
    private boolean isSearching;

    private Worker updateWorker;

    public MemoryReader(String searchPattern, Pattern pattern) {
        this.pattern = pattern;
        this.searchPattern = searchPattern;
        this.process = new Process("GTA5.exe");
        this.address = 0;
        this.blockSize = 1024*64;
        this.updatePeriod = 0;
        this.exists = false;
        new Thread(this::researchMemory).start();
        updateWorker = new Worker() {
            @Override
            public void execute() {
                if(exists){
                    String data = process.readString(address, blockSize);
                    Matcher matcher = pattern.matcher(data);
                    if(matcher.find()){
                        MemoryReader.this.data = matcher.group(1);
                        if(updateCallback != null)
                            updateCallback.onDataUpdate(MemoryReader.this.data);
                    }
                } else {
                    researchMemory();
                }
            }

            @Override
            public long delay() {
                return updatePeriod;
            }
        };
    }

    public void researchMemory(){
        isSearching = true;
        List<Long> addressList = process.findAll(searchPattern);
        exists = false;
        for(long address:addressList){
            String data = process.readString(address, 1024*64);
            Matcher matcher = pattern.matcher(data);
            if(matcher.find()){
                this.address = address;
                this.blockSize = matcher.group(0).length();
                this.exists = true;
                this.isSearching = false;
                break;
            }
        }
    }

    public void start(){
        if(updateWorker != null)
            updateWorker.start();
    }

    public void stop(){
        if(updateWorker != null)
            updateWorker.stop();
    }

    public String getData() {
        return data;
    }

    public boolean isExists() {
        return exists;
    }

    public long getAddress() {
        return address;
    }

    public void setUpdatePeriod(long updatePeriod) {
        this.updatePeriod = updatePeriod;
    }

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public interface UpdateCallback{
        void onDataUpdate(String data);
    }

    public static void main(String[] args) {
//        MemoryReader memoryReader = new MemoryReader(
//                "refreshTabPanel",
//                Pattern.compile("refreshTabPanel\\(\\'([a-zA-Zа-яА-Я0-9\\\"\\{\\:,\\-\\s.+\\[\\]}]*)\"fractions")
//                );
        MemoryReader memoryReader = new MemoryReader(
                "sendData('{\"id\"",
                Pattern.compile("sendData\\(\\'([a-zA-Zа-яА-Я0-9\\\"\\{\\:,\\-\\s.+\\[\\]}]*)\\'\\);")
                );
        memoryReader.setUpdatePeriod(TimeUnit.SECONDS.toMillis(1));
        memoryReader.setUpdateCallback(new UpdateCallback() {
            @Override
            public void onDataUpdate(String data) {
            }
        });
        memoryReader.start();
    }
}
