package com.liner.ragebot.bot;

import org.pcap4j.core.PcapNetworkInterface;

import java.util.List;

public interface InterfaceDetectCallback {
    void onDetected(List<PcapNetworkInterface> networkInterfaces);
    void onDetected(PcapNetworkInterface networkInterface);
    void onWindscribeDetected();
    void onError();
}
