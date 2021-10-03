package com.liner.keygen.generator.ui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class LinerList<T> extends RoundedPanel {
    private final DefaultListModel<T> selectionModel;
    private LinerScroll scrollPane;
    private JList<T> list;

    public LinerList() {
        selectionModel = new DefaultListModel<>();
        list = new JList<>(selectionModel);
        list.setBackground(new Color(0, 0, 0, 0));
        scrollPane = new LinerScroll(list);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setLayout(new GridLayout());
        add(scrollPane);
    }

    public void add(T data) {
        selectionModel.addElement(data);
        validate();
    }

    public void add(List<T> data) {
        for (T t : data)
            selectionModel.addElement(t);
        validate();
    }

    public void remove(int index) {
        selectionModel.remove(index);
        validate();
    }

    public void remove(T data) {
        selectionModel.remove(indexOf(data));
        validate();
    }

    public int indexOf(T data) {
        return selectionModel.indexOf(data);
    }

    public T get(int index) {
        return selectionModel.get(index);
    }

    public int getDataSize() {
        return selectionModel.size();
    }

    public List<T> getData() {
        List<T> data = new ArrayList<>();
        for (int i = 0; i < getDataSize(); i++)
            data.add(get(i));
        return data;
    }

    public void clearData(){
        selectionModel.clear();
        validate();
    }

    public void setData(List<T> data) {
        selectionModel.clear();
        validate();
    }

}
