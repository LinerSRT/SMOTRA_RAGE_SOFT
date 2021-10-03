package com.liner.ragebot.bot;

import com.liner.ragebot.Settings;
import com.liner.ragebot.jna.RageMultiplayer;
import org.pcap4j.core.PcapNetworkInterface;

public interface BotContext {
    BotForm getContext();
    RageMultiplayer getRageMultiplayer();
    Settings getSettings();
    PcapNetworkInterface getSelectedInterface();
    void updateUI();
    boolean isBotRunning();
    void startBot();
    void stopBot();
}
