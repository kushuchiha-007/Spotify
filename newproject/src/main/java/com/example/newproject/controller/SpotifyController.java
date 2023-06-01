package com.example.newproject.controller;

import com.example.newproject.model.ElasticArtist;
import com.example.newproject.model.MongoArtist;
import com.example.newproject.sevice.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.List;

@RestController
public class SpotifyController {

    @Autowired
    private SpotifyService spotifyService;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    public SpotifyController(SpotifyService SpotifyService) {
        this.spotifyService = SpotifyService;
        this.operations = operations;
    }

    @GetMapping("/mongo/artist/{id}")
    public MongoArtist getMongoArtists(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.getMongoArtists(id);
    }

    @GetMapping("/elastic/artist/{id}")
    public ElasticArtist getElasticArtists(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.getElasticArtists(id);
    }

    @GetMapping("/genre")
    public List<MongoArtist> searchByGenre() {
        return spotifyService.searchByGenre();
    }


    @GetMapping("/elastic/{id}")
    public List<ElasticArtist> searchByElastic(@PathVariable String id) {
        return spotifyService.searchByElastic(id);
    }
    @GetMapping("/elastic/users")
    public Iterable<ElasticArtist> findAll() {
        return spotifyService.findAll();
    }
}
