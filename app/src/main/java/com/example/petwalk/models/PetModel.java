package com.example.petwalk.models;

public class PetModel {

    private String pet_name, pet_age, pet_color, pet_raza, pet_size, pet_photo, last_walk;
    private String pet_id,Duenio;
    private float distance;

    public PetModel() {
        last_walk = "";
        distance = 0.0f;
    }

    public String getDuenio() {
        return Duenio;
    }

    public void setDuenio(String duenio) {
        Duenio = duenio;
    }

    public String getPet_id() {
        return pet_id;
    }

    public void setPet_id(String pet_id) {
        this.pet_id = pet_id;
    }

    public String getLast_walk() {
        return last_walk;
    }

    public void setLast_walk(String last_walk) {
        this.last_walk = last_walk;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getPet_name() {
        return pet_name;
    }

    public void setPet_name(String pet_name) {
        this.pet_name = pet_name;
    }

    public String getPet_age() {
        return pet_age;
    }

    public void setPet_age(String pet_age) {
        this.pet_age = pet_age;
    }

    public String getPet_color() {
        return pet_color;
    }

    public void setPet_color(String pet_color) {
        this.pet_color = pet_color;
    }

    public String getPet_raza() {
        return pet_raza;
    }

    public void setPet_raza(String pet_raza) {
        this.pet_raza = pet_raza;
    }

    public String getPet_size() {
        return pet_size;
    }

    public void setPet_size(String pet_size) {
        this.pet_size = pet_size;
    }

    public String getPet_photo() {
        return pet_photo;
    }

    public void setPet_photo(String pet_photo) {
        this.pet_photo = pet_photo;
    }
}
