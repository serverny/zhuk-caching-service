package ru.azhuk.zhukcachingservice.model;

import java.io.Serializable;

/**
 * Created by zhuk1 on 26.09.2018.
 * This class exists only to conform with output requirements given in task description :)
 */
public class ValueObjectImpostor implements Serializable {
    private String key;

    public ValueObjectImpostor(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
