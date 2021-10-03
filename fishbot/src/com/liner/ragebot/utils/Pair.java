package com.liner.ragebot.utils;

import java.util.List;

@SuppressWarnings("rawtypes")
public class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public boolean containValue(List<Pair<K, V>> pairList){
        for(Pair pair:pairList){
            if(pair.getValue().equals(getValue()))
                return true;
        }
        return false;
    }

    public boolean containKey(List<Pair<K, V>> pairList){
        for(Pair pair:pairList){
            if(pair.getKey().equals(getKey()))
                return true;
        }
        return false;
    }

    public int indexOfKey(List<Pair<K, V>> pairList){
        for (int i = 0; i < pairList.size(); i++) {
            if(pairList.get(i).getKey().equals(getKey()))
                return i;
        }
        return -1;
    }

    public int indexOfValue(List<Pair<K, V>> pairList){
        for (int i = 0; i < pairList.size(); i++) {
            if(pairList.get(i).getValue().equals(getValue()))
                return i;
        }
        return -1;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
