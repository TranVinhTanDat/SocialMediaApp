package com.team8.socialmedia.models;

public class ModelChatlist {
    String id; // we'll need this id to get chat list sender/receiver uid

    public ModelChatlist(String id) {
        this.id = id;
    }

    public ModelChatlist(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
