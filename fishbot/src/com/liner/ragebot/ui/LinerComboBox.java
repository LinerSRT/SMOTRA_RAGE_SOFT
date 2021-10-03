package com.liner.ragebot.ui;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Vector;

public class LinerComboBox{
    public static void init(JComboBox comboBox){
        comboBox.setUI(new LinerComboBoxUI());
        comboBox.setRenderer(new RenderComboBox());
    }
    public static void setListener(JComboBox comboBox, Callback callback){
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent popupMenuEvent) {

            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent popupMenuEvent) {
                callback.onSelected(comboBox.getSelectedIndex());
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {

            }
        });
    }

    public interface Callback{
        void onSelected(int index);
    }
}
