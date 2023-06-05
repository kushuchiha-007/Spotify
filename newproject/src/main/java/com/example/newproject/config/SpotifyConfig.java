package com.example.newproject.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
@Configuration
@EnableElasticsearchRepositories(basePackages = {"com.example.newproject.Repository"})
@ComponentScan(basePackages = {"com.example.newproject"})
public class SpotifyConfig {
    private RestClient restClient = RestClient.builder(
            new HttpHost("localhost", 9200)).build();
    private ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper());
    private ElasticsearchClient client = new ElasticsearchClient(transport);

}
