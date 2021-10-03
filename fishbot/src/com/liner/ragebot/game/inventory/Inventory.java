package com.liner.ragebot.game.inventory;

import com.liner.ragebot.Core;
import com.liner.ragebot.bot.BotContext;
import com.liner.ragebot.game.ImageSearch;
import com.liner.ragebot.jna.RageMultiplayer;
import com.liner.ragebot.utils.ImageUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final RageMultiplayer rageMultiplayer;
    private final BotContext context;
    private static final Color slotBackground = new Color(184, 184, 184);
    private static final Color counterBackground = new Color(69, 69, 69);
    private static final Color fishBackground = new Color(55, 72, 233);
    private static final Color baitBackground = new Color(153, 112, 63);
    private static final Color rodsBackground = new Color(93, 142, 207);
    private static final Color weaponBackground = new Color(153, 57, 57);
    private static final Color s0e15StatusColor = new Color(43, 140, 47);
    private static final Color s15e35StatusColor = new Color(120, 143, 55);
    private static final Color s35e55StatusColor = new Color(212, 209, 78);
    private static final Color s55e75StatusColor = new Color(250, 153, 82);
    private static final Color s75e100StatusColor = new Color(224, 76, 71);

    private List<Slot> slots;
    private Instrument instrumentSlot;
    private int fallbackCount = 0;

    public Inventory(BotContext context) {
        this.context = context;
        this.rageMultiplayer = context.getRageMultiplayer();
        this.slots = new ArrayList<>();
        this.instrumentSlot = null;
    }

    public Instrument getInstrumentSlot() {
        return instrumentSlot;
    }

    public List<Slot> getUpdatedSlots() {
        slots.clear();
        BufferedImage screenBuffer = rageMultiplayer.getBuffer();
        while (!isInventoryOpen(screenBuffer)) {
            if(!context.isBotRunning())
                return new ArrayList<>();
            fallbackCount ++;
            if(fallbackCount > 10){
                context.stopBot();
                return new ArrayList<>();
            }
            rageMultiplayer.pressKey(KeyEvent.VK_I);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            screenBuffer = rageMultiplayer.getBuffer();
        }
        if(screenBuffer.getWidth() < 1200 || screenBuffer.getHeight() < 700){
            return getUpdatedSlots();
        }
        BufferedImage instrumentBuffer = screenBuffer.getSubimage(149, 506, 77, 75);
        boolean empty = true;
        int y = instrumentBuffer.getHeight() / 2;
        for (int x = 6; x < instrumentBuffer.getWidth() - 6; x++) {
            Color currentColor = new Color(instrumentBuffer.getRGB(x, y));
            if (!currentColor.equals(slotBackground)) {
                empty = false;
                break;
            }
        }
        instrumentSlot = new Instrument(
                instrumentBuffer,
                empty,
                0,
                149 + (instrumentBuffer.getWidth() / 2),
                506 + (instrumentBuffer.getHeight() / 2)
        );
        for (int index = 0; index < 30; index++) {
            int colID = (index % 6);
            int rowID = (index / 6);
            int slotStartX = (415 + (colID * 116) + (colID * 5)) - (colID == 0 ? 0 : 1);
            int slotStartY = (79 + (rowID * 110) + (rowID * 5));
            int slotEndX = (slotStartX + 116) - (colID == 0 ? 1 : 0);
            int slotEndY = slotStartY + 110;
            int slotCountStartX = slotStartX + 2;
            int slotCountStartY = slotStartY + 2;
            for (int x = slotStartX; x < slotEndX; x++) {
                Color currentColor = new Color(screenBuffer.getRGB(x, slotStartY + 6));
                if (currentColor.equals(counterBackground)) {
                    slotCountStartX = x;
                    break;
                }
            }
            int slotCountEndX = slotEndX - 2;
            int slotCountEndY = slotCountStartY + 21;
            int statusStartX = slotStartX + 2;
            int statusStartY = slotEndY - 38;
            int statusEndX = statusStartX + 70;
            int statusEndY = statusStartY + 17;
            BufferedImage buffer = screenBuffer.getSubimage(slotStartX, slotStartY, (slotEndX - slotStartX), (slotEndY - slotStartY));
            BufferedImage counterBuffer = null;
            BufferedImage statusBuffer = screenBuffer.getSubimage(statusStartX, statusStartY, (statusEndX - statusStartX), (statusEndY - statusStartY));
            if ((slotCountEndX - slotCountStartX) < 35) {
                counterBuffer = screenBuffer.getSubimage(slotCountStartX, slotCountStartY, slotCountEndX - slotCountStartX, slotCountEndY - slotCountStartY);
            }
            Slot slot = new Slot(buffer, counterBuffer, statusBuffer);
            slot.setIndex(index);
            slot.setCenterX(slotStartX + ((slotEndX - slotStartX) / 2));
            slot.setCenterY(slotStartY + ((slotEndY - slotStartY) / 2));
            slot.setHasCounter(counterBuffer != null);
            ImageSearch imageSearch = new ImageSearch(buffer);
            Color typeColor = new Color(screenBuffer.getRGB(slotStartX + 3, slotEndY - 3));
            if (typeColor.equals(fishBackground)) {
                slot.setSlotType(SlotType.FISH);
            } else if (typeColor.equals(baitBackground)) {
                slot.setSlotType(SlotType.BAIT);
                for (int baits = 0; baits < Core.Bait.baits.length; baits++) {
                    if (imageSearch.exists(Core.Bait.baits[baits])) {
                        slot.setMetaData(baits);
                        break;
                    }
                }
            } else if (typeColor.equals(rodsBackground)) {
                int metaData = -1;
                for (int rods = 0; rods < Core.Rod.rods.length; rods++) {
                    if (imageSearch.exists(Core.Rod.rods[rods])) {
                        metaData = rods;
                        break;
                    }
                }
                if (metaData == -1) {
                    slot.setSlotType(SlotType.WEAR);
                } else {
                    slot.setSlotType(SlotType.ROD);
                    slot.setMetaData(metaData);
                    Color statusColor = new Color(statusBuffer.getRGB(3, 3));
                    if (statusColor.equals(s0e15StatusColor)) {
                        slot.setHealth(0); // 0-15;
                    } else if (statusColor.equals(s15e35StatusColor)) {
                        slot.setHealth(15); // 15-35;
                    } else if (statusColor.equals(s35e55StatusColor)) {
                        slot.setHealth(35); // 35-55;
                    } else if (statusColor.equals(s55e75StatusColor)) {
                        slot.setHealth(55); // 55-75;
                    } else if (statusColor.equals(s75e100StatusColor)) {
                        slot.setHealth(75); // 75-100;
                        if (new Color(statusBuffer.getRGB(statusBuffer.getWidth() - 3, 3)).equals(s75e100StatusColor)) {
                            slot.setHealth(100);
                        }
                    }
                }
            } else if (typeColor.equals(weaponBackground)) {
                slot.setSlotType(SlotType.WEAPON);
            }
            slots.add(slot);
        }
        boolean allEmpty = true;
        for (Slot slot : slots)
            if (slot.getSlotType() != SlotType.EMPTY) {
                allEmpty = false;
                break;
            }
        if (allEmpty) {
            return getUpdatedSlots();
        } else {
            fallbackCount = 0;
            return slots;
        }
    }


    public boolean isInventoryOpen(BufferedImage bufferedImage) {
        return ImageUtils.isColorPresent(
                bufferedImage,
                1100, 42,
                new Color(217, 219, 221)
        ) || ImageUtils.isColorPresent(
                bufferedImage,
                1100, 675,
                new Color(67, 74, 84)
        ) || ImageUtils.isColorPresent(
                bufferedImage,
                185, 130,
                new Color(184, 184, 184)
        ) || ImageUtils.isColorPresent(
                bufferedImage,
                265, 130,
                new Color(184, 184, 184)
        ) || ImageUtils.isColorPresent(
                bufferedImage,
                342, 130,
                new Color(184, 184, 184)
        );
    }

    private boolean containSlot(List<Slot> slots, SlotType slotType) {
        for (Slot slot : slots) {
            if (slot.getSlotType() == slotType)
                return true;
        }
        return false;
    }

    private boolean containSlot(List<Slot> slots, SlotType slotType, int metadata) {
        for (Slot slot : slots) {
            if (slot.getSlotType() == slotType && slot.getMetaData() == metadata)
                return true;
        }
        return false;
    }

    private boolean containBrokenRods(List<Slot> slots) {
        for (Slot slot : slots) {
            if (slot.getSlotType() == SlotType.ROD && slot.getHealth() == 100)
                return true;
        }
        return false;
    }

    public Slot getFirstSlot(List<Slot> slots, SlotType slotType) {
        for (Slot slot : slots) {
            if (slot.getSlotType() == slotType)
                return slot;
        }
        return slots.get(0);
    }


    public interface InventoryCallback {
        void onFinish();
        void onNoRods();

        void onNoSelectedRods();

        void onNoBaits();

        void onNoSelectedBaits();

        void onNoHaveEmptySlots();

        void failedChooseRod();
    }

    public void checkInventory(InventoryCallback callback) {
        if(!context.isBotRunning())
            return;
        List<Slot> slotList = getUpdatedSlots();
        boolean haveRods = containSlot(slotList, SlotType.ROD);
        boolean haveBaits = containSlot(slotList, SlotType.BAIT);
        boolean haveSelectedRods = containSlot(slotList, SlotType.ROD, context.getSettings().getRodIndex());
        boolean haveSelectedBaits = containSlot(slotList, SlotType.BAIT, context.getSettings().getBaitIndex());
        boolean haveBrokenRods = containBrokenRods(slotList);
        if(!context.isBotRunning())
            return;
        if (!instrumentSlot.isEmpty()) {
            Slot emptySlot = getFirstSlot(slotList, SlotType.EMPTY);
            rageMultiplayer.leftDrag(
                    instrumentSlot.getCenterX(), instrumentSlot.getCenterY(),
                    emptySlot.getCenterX(), emptySlot.getCenterY()
            );
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            slotList = getUpdatedSlots();
            haveRods = containSlot(slotList, SlotType.ROD);
            haveBaits = containSlot(slotList, SlotType.BAIT);
            haveSelectedRods = containSlot(slotList, SlotType.ROD, context.getSettings().getRodIndex());
            haveSelectedBaits = containSlot(slotList, SlotType.BAIT, context.getSettings().getBaitIndex());
            haveBrokenRods = containBrokenRods(slotList);
        }
        if (haveBrokenRods) {
            for (Slot slot : slotList) {
                if (Slot.isRod(slot) && Slot.isBrokenRod(slot)) {
                    if (context.getSettings().isDropBrokenRods()) {
                        rageMultiplayer.leftDrag(
                                slot.getCenterX(), slot.getCenterY(),
                                10, 10
                        );
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        rageMultiplayer.pressKey(KeyEvent.VK_I);
                        try {
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    slot.setSlotType(SlotType.EMPTY);
                }
            }
        }
        if (!haveRods && instrumentSlot.isEmpty()) {
            System.out.println("!haveRods && instrumentSlot.isEmpty()");
            callback.onNoRods();
            return;
        }
        if (!haveBaits) {
            System.out.println("!haveBaits");
            callback.onNoBaits();
            return;
        }
        if (!haveSelectedRods && !context.getSettings().isUseAnyRods()) {
            System.out.println("!haveSelectedRods && !context.getSettings().isUseAnyRods()");
            callback.onNoSelectedRods();
            return;
        }
        if (!haveSelectedBaits && !context.getSettings().isUseAntBaits()) {
            System.out.println("!haveSelectedBaits && !context.getSettings().isUseAntBaits()");
            callback.onNoSelectedBaits();
            return;
        }
        if (!haveSelectedBaits && context.getSettings().isUseAntBaits()) {
            for (int i = 0; i < 5; i++) {
                if (containSlot(slotList, SlotType.BAIT, i)) {
                    context.getSettings().setBaitIndex(i);
                    context.updateUI();
                    break;
                }
            }
        }
        if (!haveSelectedRods && context.getSettings().isUseAnyRods()) {
            for (int i = 0; i < 3; i++) {
                if (containSlot(slotList, SlotType.ROD, i)) {
                    context.getSettings().setBaitIndex(i);
                    context.updateUI();
                    break;
                }
            }
        }
        Slot rodSlot = null;
        boolean useAnyRods = context.getSettings().isUseAnyRods();
        for(Slot slot:slotList){
            if(Slot.isRod(slot)){
                if(useAnyRods){
                    if(rodSlot == null || (!Slot.isBrokenRod(slot) && slot.getHealth() > rodSlot.getHealth()))
                        rodSlot = slot;
                } else {
                    if(slot.getMetaData() == context.getSettings().getRodIndex()){
                        if(rodSlot == null || (!Slot.isBrokenRod(slot) && slot.getHealth() > rodSlot.getHealth()))
                            rodSlot = slot;
                    }
                }
            }
        }
        if(rodSlot == null){
            callback.failedChooseRod();
            return;
        }
        rageMultiplayer.leftDrag(
                rodSlot.getCenterX(), rodSlot.getCenterY(),
                instrumentSlot.getCenterX(), instrumentSlot.getCenterY()
        );
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {
        }
        callback.onFinish();

//
//
//        if (!instrumentSlot.isEmpty())
//            System.out.println("Instrument slot not empty, moving to inventory");
//            for (Slot slot : slotList) {
//                if (slot.getSlotType() == SlotType.EMPTY && !instrumentSlot.isEmpty()) {
//                    rageMultiplayer.leftDrag(
//                            instrumentSlot.getCenterX(), instrumentSlot.getCenterY(),
//                            slot.getCenterX(), slot.getCenterY()
//                    );
//                    instrumentMoved = true;
//                    break;
//                }
//            }
//        if (instrumentMoved && !instrumentSlot.isEmpty()) {
//            dropBrokenRods(callback);
//        } else if (instrumentSlot.isEmpty()) {
//            System.out.println("Drop broken rods: "+context.getSettings().isDropBrokenRods());
//            if (context.getSettings().isDropBrokenRods())
//                for (Slot slot : slotList) {
//                    if (Slot.isRod(slot) && Slot.isBrokenRod(slot)) {
//                        System.out.println("Found broken rod, dropping");
//                        rageMultiplayer.leftDrag(
//                                slot.getCenterX(), slot.getCenterY(),
//                                10, 10
//                        );
//                        dropped = true;
//                    }
//                }
//            if (dropped && context.getSettings().isDropBrokenRods()) {
//                dropBrokenRods(callback);
//            } else {
//                boolean haveSelectedBait = false;
//                boolean haveBaits = false;
//                boolean haveSelectedRod = false;
//                boolean haveRods = false;
//                for (Slot slot : slotList) {
//                    if (slot.getSlotType() == SlotType.BAIT) {
//                        haveBaits = true;
//                        if (slot.getMetaData() == context.getSettings().getBaitIndex())
//                            haveSelectedBait = true;
//                    } else if (slot.getSlotType() == SlotType.ROD) {
//                        haveRods = true;
//                        if (slot.getMetaData() == context.getSettings().getRodIndex())
//                            haveSelectedRod = true;
//                    }
//                }
//
//                System.out.println("Have rods: "+haveRods);
//                System.out.println("Have baits: "+haveBaits);
//                System.out.println("Have selected rod: "+haveSelectedRod);
//                System.out.println("Have selected bait: "+haveSelectedBait);
//                if (haveRods) {
//                    if (haveBaits) {
//                        {
//                            if (!haveSelectedBait && !context.getSettings().isUseAntBaits()) {
//                                callback.onFail("dont_have_selected_bait");
//                            } else {
//                                if (!haveSelectedBait && context.getSettings().isUseAntBaits()) {
//                                    int currentIndex = context.getSettings().getBaitIndex();
//                                    context.getSettings().setBaitIndex(Other.randomInt(0, 4, currentIndex));
//                                    context.updateUI();
//                                    try {
//                                        Thread.sleep(300);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (!haveSelectedRod && !context.getSettings().isUseAnyRods()) {
//                                    callback.onFail("dont_have_selected_rods");
//                                } else if (!haveSelectedRod && context.getSettings().isUseAnyRods()) {
//                                    int currentIndex = context.getSettings().getRodIndex();
//                                    context.getSettings().setRodIndex(Other.randomInt(0, 2, currentIndex));
//                                    context.updateUI();
//                                    try {
//                                        Thread.sleep(300);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                    Slot anyRod = null;
//                                    for (Slot slot : slotList) {
//                                        if (Slot.isRod(slot)) {
//                                            if (anyRod == null || (slot.getHealth() < 100 && slot.getHealth() > anyRod.getHealth()))
//                                                anyRod = slot;
//                                        }
//                                    }
//                                    if (anyRod != null) {
//                                        rageMultiplayer.leftDrag(
//                                                anyRod.getCenterX(), anyRod.getCenterY(),
//                                                instrumentSlot.getCenterX(), instrumentSlot.getCenterY()
//                                        );
//                                        try {
//                                            Thread.sleep(500);
//                                        } catch (InterruptedException ignored) {
//                                        }
//                                        callback.onFinish();
//                                    } else {
//                                        callback.onFail("rods_broken");
//                                    }
//                                } else {
//                                    Slot rod = null;
//                                    for (Slot slot : slotList) {
//                                        if (Slot.isRod(slot)) {
//                                            if (slot.getMetaData() == context.getSettings().getRodIndex() && (rod == null || (slot.getHealth() < 100 && slot.getHealth() < rod.getHealth())))
//                                                rod = slot;
//                                        }
//                                    }
//                                    if (rod != null) {
//                                        rageMultiplayer.leftDrag(
//                                                rod.getCenterX(), rod.getCenterY(),
//                                                instrumentSlot.getCenterX(), instrumentSlot.getCenterY()
//                                        );
//                                        try {
//                                            Thread.sleep(500);
//                                        } catch (InterruptedException ignored) {
//                                        }
//                                        callback.onFinish();
//                                    } else {
//                                        callback.onFail("rods_broken");
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        callback.onFail("no_baits");
//                    }
//                } else {
//                    callback.onFail("no_rods");
//                }
//            }
//        }
    }
}
