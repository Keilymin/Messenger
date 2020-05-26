package com.example.messenger.entity;

public class User {
    private String email;
    private String id;
    private String image;
    private String name;
    private String status;


    public User() {
    }

    public User(String email, String id, String image, String name, String status) {
        this.email = email;
        this.id = id;
        this.image = image;
        this.name = name;
        this.status = status;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
