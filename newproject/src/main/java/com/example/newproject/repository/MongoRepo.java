package com.example.newproject.repository;

import com.example.newproject.model.MongoArtist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoRepo extends MongoRepository<MongoArtist, String> {
}
