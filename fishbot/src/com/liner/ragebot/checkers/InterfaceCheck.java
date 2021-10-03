package com.liner.ragebot.checkers;

import com.liner.ragebot.server.Server;
import com.liner.ragebot.utils.Check;
import org.pcap4j.core.*;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class InterfaceCheck extends Check {
    private final List<Model> modelList;

    public InterfaceCheck(Callback callback) {
        this.modelList = new ArrayList<>();
        FutureTask<PcapNetworkInterface> futureTask = new FutureTask<>(() -> {
            List<PcapNetworkInterface> networkInterfaces = Pcaps.findAllDevs();
            callback.onInterfaceCollected(networkInterfaces);
            if (networkInterfaces.size() == 1) return networkInterfaces.get(0);
            for (PcapNetworkInterface networkInterface : networkInterfaces) {
                Model model = new Model(networkInterface);
                model.compute(500);
                modelList.add(model);
            }
            while (isComputing(modelList)) {
                Thread.sleep(100);
            }
            modelList.sort(Comparator.comparingInt(Model::getPacketSize));
            return modelList.get(modelList.size() - 1).networkInterface;
        });
        new Thread(futureTask).start();
        try {
            callback.onInterfaceDetected(futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            callback.onFailed();
        }
    }

    @Override
    public boolean check() {
        return next();
    }

    private boolean isComputing(List<Model> models) {
        boolean result = false;
        for (Model model : models) {
            if (model.isComputing) {
                result = true;
                break;
            }
        }
        return result;
    }


    private static class Model {
        private final PcapNetworkInterface networkInterface;
        private final List<IpV4Packet> packets;
        private boolean isComputing;

        public Model(PcapNetworkInterface networkInterface) {
            this.networkInterface = networkInterface;
            this.packets = new ArrayList<>();
            this.isComputing = false;
        }

        public void addPacket(IpV4Packet packet) {
            if (!packets.contains(packet))
                packets.add(packet);
        }

        public int getPacketSize() {
            return packets.size();
        }

        public void compute(long timeout) throws PcapNativeException, NotOpenException {
            isComputing = true;
            PcapHandle handle = networkInterface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 8);
            if (handle.isOpen()) {
                handle.setFilter("", BpfProgram.BpfCompileMode.OPTIMIZE);
            }
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    isComputing = false;
                }
            }, timeout);
            new Thread(() -> {
                Server.sendRequest("http://80.87.199.200", response -> {

                });
                while (isComputing && handle.isOpen()) {
                    try {
                        Packet packet = handle.getNextPacket();
                        if (packet != null && packet.contains(IpV4Packet.class)) {
                            IpV4Packet ip4v = packet.get(IpV4Packet.class);
                            if (!packets.contains(ip4v)) {
                                String destinationIP = ip4v.getHeader().getDstAddr().toString();
                                destinationIP = destinationIP.replace("/", "");
                                if (destinationIP.contains("80.87.199.200")) {
                                    addPacket(ip4v);
                                }
                            }
                        }
                    } catch (NotOpenException e) {
                        e.printStackTrace();
                    }
                }
                if (handle.isOpen())
                    handle.close();
            }).start();
        }
    }


    public interface Callback {
        void onInterfaceDetected(PcapNetworkInterface networkInterface);
        void onInterfaceCollected(List<PcapNetworkInterface> networkInterfaces);
        void onFailed();
    }
}
