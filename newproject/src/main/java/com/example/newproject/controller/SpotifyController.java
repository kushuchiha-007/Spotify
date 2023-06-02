package com.example.newproject.controller;

import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import com.example.newproject.model.ElasticArtist;
import com.example.newproject.model.MongoArtist;
import com.example.newproject.sevice.SpotifyService;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.util.List;
import java.util.Map;
@RestController
public class SpotifyController {
// All mongo operations starts with mongo in url and elastic with elastic
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
    @GetMapping("/mongo/genre")
    public List<MongoArtist> aggregateByGenre() {
        return spotifyService.aggregateByGenre();
    }

    @GetMapping("/elastic/artist/{id}")
    public ElasticArtist getElasticArtists(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.getElasticArtists(id);
    }
    @GetMapping("/elastic/{id}")
    public List<ElasticArtist> searchByElastic(@PathVariable String id) {
        return spotifyService.searchByElastic(id);
    }
    @GetMapping("/elastic/users")
    public Iterable<ElasticArtist> findAll() {
        return spotifyService.findAll();
    }

    @GetMapping("/elastic/popular")
    public List<ElasticArtist> aggregateByPopularity() {
        return spotifyService.aggregateByPopularity();
    }

    @GetMapping("/elastic/histogram")
    public Map<String, Integer> filterWithHistogram() throws IOException {
        return spotifyService.filterWithHistogram();
    }
}
