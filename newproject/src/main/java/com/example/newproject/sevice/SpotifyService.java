package com.example.newproject.sevice;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.HistogramBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
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
import java.util.*;


@Service
public class SpotifyService {
    @Autowired
    private MongoRepo mongoRepo;
    @Autowired
    private ElasticRepo elasticRepo;
    @Autowired
    public ElasticsearchOperations elasticsearchOperations;
    @Autowired
    ElasticsearchClient elasticsearchClient;
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
        int popularity = jsonObject.getInt("popularity");
        List<Object> genre = jsonObject.getJSONArray("genres").toList();

        System.out.println(popularity);
        ElasticArtist artist = new ElasticArtist();
        artist.setId(Id);
        artist.setName(name);
        artist.setType(type);
        artist.setPopularity(popularity);
        artist.setGenre(genre);
        return elasticRepo.save(artist);
    }
    public List<MongoArtist> aggregateByGenre() {

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
    public List<ElasticArtist> aggregateByPopularity() {
        try (ApiTypeHelper.DisabledChecksHandle h = ApiTypeHelper.DANGEROUS_disableRequiredPropertiesCheck(true)) {
            Query query = RangeQuery.of(r -> r
                    .field("Popularity")
                    .gte(JsonData.of(80))
                    .lte(JsonData.of(100)))
                    ._toQuery();

//            System.out.println(query);

            NativeQuery searchQuery2 = NativeQuery.builder()
                    .withSourceFilter(new FetchSourceFilterBuilder().withIncludes().build())
                    .withQuery(query)
                    .withAggregation("Popularity", Aggregation.of(a->a.terms(ta->ta.field("Popularity").size(1))))
                    .withSort(Sort.by(Sort.Direction.ASC, "Popularity"))
                    .build();

            System.out.println(searchQuery2);
            List<ElasticArtist> artists = new ArrayList<>();
            SearchHits<ElasticArtist> data = elasticsearchOperations.search(searchQuery2, ElasticArtist.class) ;
            data.forEach(searchHit -> artists.add(searchHit.getContent()));
            return artists;
        }
    }
    public  Map<String, Integer> filterWithHistogram() throws IOException {
        try (ApiTypeHelper.DisabledChecksHandle h = ApiTypeHelper.DANGEROUS_disableRequiredPropertiesCheck(true)) {
        Query query = MatchQuery.of(m -> m
                .field("genre")
                .query("pop")
        )._toQuery();

        SearchResponse<ElasticArtist> response = elasticsearchClient.search(b -> b
                        .index("elasticartists")
                        .query(query)
                        .aggregations("popularity-histogram", a -> a
                                .histogram(h1 -> h1
                                        .field("Popularity")
                                        .interval(1.0)
                                )
                        ).size(1),
                ElasticArtist.class
        );
        System.out.println(response);
        List<HistogramBucket> buckets = response.aggregations()
                .get("popularity-histogram")
                .histogram()
                .buckets().array();
        System.out.println("hi these are my buckets" + buckets);
            Map<String, Integer> popularityMap = new HashMap<>();

            for (HistogramBucket bucket: buckets) {
                String key = String.valueOf(bucket.key());
                int docCount = (int) bucket.docCount();
            System.out.println("There are " + bucket.docCount() +
                    " popularity under " + bucket.key());
                popularityMap.put(key, docCount);

            }
        return popularityMap;
    }}
    }



