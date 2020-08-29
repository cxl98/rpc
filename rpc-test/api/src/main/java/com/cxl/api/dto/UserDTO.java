package com.cxl.api.dto;

import com.cxl.rpc.util.Push;
import com.sun.org.apache.bcel.internal.generic.PUSH;

import java.io.Serializable;

public class UserDTO implements Serializable {
    private String name;
    private String word;

    public UserDTO() {
    }

    public UserDTO(String name, String word) {
        this.name = name;
        this.word = word;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "name='" + name + '\'' +
                ", word='" + word + '\'' +
                '}';
    }
}
