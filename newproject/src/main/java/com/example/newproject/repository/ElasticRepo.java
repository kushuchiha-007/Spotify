package com.example.newproject.repository;

import com.example.newproject.model.ElasticArtist;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Primary
public interface ElasticRepo extends ElasticsearchRepository<ElasticArtist, String>{
}
