package com.liner.ragebot.server.models;

public class PromoCode {
    private final String promoCode;
    private final int percent;
    private boolean activated;

    public PromoCode(String promoCode, int percent) {
        this.promoCode = promoCode;
        this.percent = percent;
        this.activated = false;
    }

    public int getPercent() {
        return percent;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public String toString() {
        return "PromoCode{" +
                "\n\tpromoCode='" + promoCode + '\'' +
                "\n\tpercent=" + percent +
                "\n\tactivated=" + activated +
                "\n}";
    }
}
