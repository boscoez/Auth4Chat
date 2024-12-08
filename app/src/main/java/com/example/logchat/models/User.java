package com.example.logchat.models;

import java.io.Serializable;

public class User extends ChatMessage implements Serializable {

    public String name, image, email, token, id;
}
