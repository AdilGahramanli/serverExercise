package org.example.bddconcurrentaccess;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CounterRepository extends MongoRepository<Counter, String> {
}
