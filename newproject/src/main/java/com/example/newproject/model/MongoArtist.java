package com.example.newproject.model;

import com.example.newproject.repository.MongoRepo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;

@Document(collection = "artists")
public class MongoArtist {
    private String id;
    private String name;
    private String type;
    private List<Object> genre;

    public MongoArtist(){

    }
    public MongoArtist(String id, String name, String type, List<Object> genre) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.genre =genre;
    }
    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public List<Object> getGenre() {
        return genre;
    }

    // Setter methods
    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setGenre(List<Object> genre) {
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "MongoArtist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", genre=" + genre +
                '}';
    }

}
