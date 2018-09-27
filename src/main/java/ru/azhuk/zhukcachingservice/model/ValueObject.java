package ru.azhuk.zhukcachingservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by zhuk1 on 26.09.2018.
 */

@Entity
@Table(name = "value_object")
public class ValueObject implements Serializable {
    @Id
    @Column(name = "ID", nullable=false)
    @NotNull
    private String key;

    @Column(name = "VALUE")
    @NotNull
    private String value;

    public ValueObject() {
    }

    public ValueObject(String id) {
        this.key = id;
    }

    public ValueObject(String id, String value) {
        this.key = id;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueObject that = (ValueObject) o;
        return Objects.equals(key, that.key) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
