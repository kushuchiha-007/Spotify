package com.example.newproject.controller;

import com.example.newproject.model.ElasticArtist;
import com.example.newproject.model.MongoArtist;
import com.example.newproject.sevice.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
@RestController
public class SpotifyController {
// All mongo operations starts with mongo in url and elastic with elastic
    private SpotifyService spotifyService;
    private ElasticsearchOperations elasticsearchOperation;
    @Autowired
    public SpotifyController(SpotifyService SpotifyService,ElasticsearchOperations elasticsearchOperation) {
        this.spotifyService = SpotifyService;
        this.elasticsearchOperation = elasticsearchOperation;
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleIOException(IOException exception) {
       String errorMessage = exception.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((ErrorResponse) Collections.singletonMap("error", errorMessage));
    }

    @ExceptionHandler(InterruptedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException exception) {
        String errorMessage = exception.getMessage();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body((ErrorResponse) Collections.singletonMap("error", errorMessage));
    }
    @GetMapping("/mongo/artist/{id}")
    public MongoArtist getMongoArtists(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.getMongoArtists(id);
    }
    @GetMapping("/mongo/genre")
    public List<MongoArtist> aggregateByGenre() throws IOException, InterruptedException {
        return spotifyService.aggregateByGenre();
    }
    @GetMapping("/elastic/artist/{id}")
    public ElasticArtist getElasticArtists(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.getElasticArtists(id);
    }
    @GetMapping("/elastic/{id}")
    public List<ElasticArtist> searchByElastic(@PathVariable String id) throws IOException, InterruptedException {
        return spotifyService.searchByElastic(id);
    }
    @GetMapping("/elastic/users")
    public Iterable<ElasticArtist> findAll() throws IOException, InterruptedException {
        return spotifyService.findAll();
    }
    @GetMapping("/elastic/popular")
    public List<ElasticArtist> aggregateByPopularity(@RequestParam int lower,@RequestParam int upper) throws IOException, InterruptedException {
        System.out.println("lower:" + lower + "upper:" + upper);
        return spotifyService.aggregateByPopularity(lower,upper);
    }
    @GetMapping("/elastic/histogram")
    public Map<String, Integer> filterWithHistogram(@RequestParam String genreType) throws IOException,InterruptedException {
        return spotifyService.filterWithHistogram(genreType);
    }
}
