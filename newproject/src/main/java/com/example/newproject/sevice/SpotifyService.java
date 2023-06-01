package com.example.newproject.sevice;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.ApiTypeHelper;
import com.example.newproject.repository.MongoRepo;
import com.example.newproject.repository.ElasticRepo;
import com.example.newproject.model.MongoArtist;
import com.example.newproject.model.ElasticArtist;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.mongodb.core.convert.MongoConverter;

import org.springframework.stereotype.Service;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SpotifyService {
    @Autowired
    private MongoRepo mongoRepo;
    @Autowired
    private ElasticRepo elasticRepo;

    @Autowired
    public ElasticsearchOperations elasticsearchOperations;

    @Autowired
    MongoClient client;
    @Autowired
    MongoConverter mongoConverter;
    @Value("${spotify.bearerToken}")
    public String bearerToken;

    @Value("${spotify.database}")
    public String databaseName;
    @Value("${spotify.collection}")
    public String collectionName;
    @Value("${spotify.BASE_URL}")
    public String BASE_URL ;

    public SpotifyService(MongoRepo mongoRepo, ElasticRepo elasticRepo,ElasticsearchOperations elasticsearchOperations) {
        this.mongoRepo = mongoRepo;
        this.elasticRepo = elasticRepo;
        this.elasticsearchOperations = elasticsearchOperations;

    }

    private static HttpRequest createGetRequest(String url, String bearerToken) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .setHeader("Authorization", "Bearer " + bearerToken)
                .setHeader("Content-Type", "application/json")
                .build();
    }
    public MongoArtist getMongoArtists(String id) throws IOException, InterruptedException {

        String artistId = id;
        String artistProfileEndpoint = BASE_URL + "/artists/" + artistId;
        HttpRequest request = createGetRequest(artistProfileEndpoint, bearerToken);
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        System.out.println(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);

        String Id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");
        List<Object> genre = jsonObject.getJSONArray("genres").toList();


        MongoArtist artist = new MongoArtist();
        artist.setId(Id);
        artist.setName(name);
        artist.setType(type);
        artist.setGenre(genre);
        return mongoRepo.save(artist);
    }
    public ElasticArtist getElasticArtists(String id) throws IOException, InterruptedException {

        String artistId = id;
        String artistProfileEndpoint = BASE_URL + "/artists/" + artistId;
        HttpRequest request = createGetRequest(artistProfileEndpoint, bearerToken);
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String jsonString = response.body();
        System.out.println(jsonString);

        JSONObject jsonObject = new JSONObject(jsonString);

        String Id = jsonObject.getString("id");
        String name = jsonObject.getString("name");
        String type = jsonObject.getString("type");
        List<Object> genre = jsonObject.getJSONArray("genres").toList();


        ElasticArtist artist = new ElasticArtist();
        artist.setId(Id);
        artist.setName(name);
        artist.setType(type);
        artist.setGenre(genre);
        return elasticRepo.save(artist);
    }
    public List<MongoArtist> searchByGenre() {

        System.out.println(databaseName);
        final List<MongoArtist> ans = new ArrayList<>();
        MongoDatabase database = client.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection(collectionName);
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$search",
                        new Document("index", "default").append("text", new Document("query", "pop").append("path", "genre"))),
                new Document("$sort", new Document("name", 1L)),
                new Document("$limit", 2L)));
        for (Document document : result) {
            ans.add(mongoConverter.read(MongoArtist.class, document));
        }
        return ans;
    }
    public List<ElasticArtist> searchByElastic(String Id) {
        System.out.println(Id);
            try (ApiTypeHelper.DisabledChecksHandle h = ApiTypeHelper.DANGEROUS_disableRequiredPropertiesCheck(true)) {
                Query query = MatchQuery.of(m -> m
                        .field("genre")
                        .query(Id))._toQuery() ;

                NativeQuery searchQuery2 = NativeQuery.builder()
                        .withSourceFilter(new FetchSourceFilterBuilder().withIncludes().build())
                        .withQuery(query)
                        .withSort(Sort.by(Sort.Direction.ASC, "name"))
                        .withMaxResults(5)
                        .build();

                List<ElasticArtist> artists = new ArrayList<>();
                SearchHits<ElasticArtist> data = elasticsearchOperations.search(searchQuery2, ElasticArtist.class) ;
                data.forEach(searchHit -> artists.add(searchHit.getContent()));
                return artists;
            }
    }
    public Iterable<ElasticArtist> findAll() {
        return elasticRepo.findAll();
    }
}



