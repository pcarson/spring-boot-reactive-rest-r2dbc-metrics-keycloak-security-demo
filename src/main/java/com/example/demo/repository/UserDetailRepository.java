package com.example.demo.repository;

import com.example.demo.entity.UserDetail;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface UserDetailRepository extends ReactiveCrudRepository<UserDetail, String> {

    Flux<UserDetail> findAll();

    Flux<UserDetail> findByEmail(String email);

}
