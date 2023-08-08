package com.example.demo.service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<UserDetail> getUsers();

    Mono<UserDetail> getUser(String id);

    Flux<UserDetail> getUserByEmailAddress(String emailAddress);

    Mono<UserDTO> createUser(UserDTO userDTO) throws UserExistsException, UserServiceException;

    Mono<Void> deleteUser(String id) throws UserNotFoundException;

}
