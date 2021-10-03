package com.liner.ragebot.game.captcha;

import com.liner.ragebot.utils.Worker;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;

public class CaptchaThread extends Worker {
    private PcapNetworkInterface networkInterface;
    private PcapHandle pcapHandle;
    private final CaptchaCallback captchaCallback;

    public CaptchaThread(PcapNetworkInterface networkInterface, CaptchaCallback captchaCallback) {
        this.networkInterface = networkInterface;
        this.captchaCallback = captchaCallback;
    }

    public void setNetworkInterface(PcapNetworkInterface networkInterface) {
        if (isRunning()) {
            stop();
            this.networkInterface = networkInterface;
            start();
        } else {
            this.networkInterface = networkInterface;
        }
    }

    @Override
    public void start() {
        try {
            if (networkInterface != null) {
                pcapHandle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 16);
                if (pcapHandle.isOpen())
                    pcapHandle.setFilter("", BpfProgram.BpfCompileMode.OPTIMIZE);
            }
        } catch (PcapNativeException | NotOpenException e) {
            e.printStackTrace();
        }
        super.start();
    }

    @Override
    public void stop() {
        if (pcapHandle != null && pcapHandle.isOpen()) {
            pcapHandle.close();
        }
        super.stop();
    }

    @Override
    public void execute() {
        if (networkInterface != null && pcapHandle.isOpen()) {
            try {
                Packet packet = pcapHandle.getNextPacket();
                if (packet != null) {
                    if (packet.contains(IpV4Packet.class)) {
                        IpV4Packet ip4v = packet.get(IpV4Packet.class);
                        String destinationIP = ip4v.getHeader().getDstAddr().toString();
                        destinationIP = destinationIP.replace("/", "");
                        if (destinationIP.contains("54.37.128.196")) {
                            String p = new String(ip4v.getRawData());
                            if (p.contains("/captcha")) {
                                captchaCallback.onCaptcha(p);
                            }
                        }
                    }
                }
            } catch (NotOpenException ignored) {
            }
        }
    }

    @Override
    public long delay() {
        return 0;
    }

    public interface CaptchaCallback {
        void onCaptcha(String data);
    }
}
