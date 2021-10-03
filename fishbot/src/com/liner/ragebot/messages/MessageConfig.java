package com.liner.ragebot.messages;

import com.liner.ragebot.Core;

import java.awt.image.BufferedImage;

public class MessageConfig {
    private MessagePosition messagePosition;
    private MessageType messageType;
    private String messageTitle;
    private String messageText;
    private BufferedImage messageIcon;
    private MessageActionListener cancelListener;
    private MessageActionListener confirmListener;

    private MessageConfig(Builder builder) {
        this.messagePosition = builder.messagePosition;
        this.messageType = builder.messageType;
        this.messageTitle = builder.messageTitle;
        this.messageText = builder.messageText;
        this.messageIcon = builder.messageIcon;
        this.confirmListener = builder.confirmListener;
        this.cancelListener = builder.cancelListener;
    }

    public MessageActionListener getCancelListener() {
        return cancelListener;
    }

    public MessageActionListener getConfirmListener() {
        return confirmListener;
    }

    public BufferedImage getMessageIcon() {
        return messageIcon;
    }

    public MessagePosition getMessagePosition() {
        return messagePosition;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageTitle() {
        return messageTitle;
    }

    public static class Builder{
        private MessagePosition messagePosition;
        private MessageType messageType;
        private String messageTitle;
        private String messageText;
        private BufferedImage messageIcon;
        private MessageActionListener cancelListener;
        private MessageActionListener confirmListener;

        public Builder() {
            messagePosition = MessagePosition.CENTER;
            messageType = MessageType.INFO;
            messageTitle = "Smotrafish";
            messageText = " ";
            messageIcon = Core.Icon.applicationIcon;
            cancelListener = null;
            confirmListener = null;
        }

        public Builder setConfirmListener(MessageActionListener confirmListener) {
            this.confirmListener = confirmListener;
            return this;
        }

        public Builder setCancelListener(MessageActionListener cancelListener) {
            this.cancelListener = cancelListener;
            return this;
        }

        public Builder setMessageIcon(BufferedImage messageIcon) {
            this.messageIcon = messageIcon;
            return this;
        }

        public Builder setMessagePosition(MessagePosition messagePosition) {
            this.messagePosition = messagePosition;
            return this;
        }

        public Builder setMessageType(MessageType messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder setMessageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public Builder setMessageTitle(String messageTitle) {
            this.messageTitle = messageTitle;
            return this;
        }

        public MessageConfig build(){
            return new MessageConfig(this);
        }
    }
}
