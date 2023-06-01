package com.example.newproject.model;

import com.example.newproject.helper.Indices;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.util.List;

@Document(indexName = Indices.ALBUM_INDEX)
@Setting(settingPath = "static/es-settings.json")
public class ElasticArtist {
    @Override
    public String toString() {
        return "ElasticArtists{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", genre=" + genre +
                '}';
    }

    @Id
    @Field(type = FieldType.Keyword)
    String id;
    @Field(type = FieldType.Keyword)
    String name;

    @Field(type = FieldType.Keyword)
    String type;

    @Field(type = FieldType.Keyword)
    List<Object> genre;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getGenre() {
        return genre;
    }

    public void setGenre(List<Object> genre) {
        this.genre = genre;
    }
}
