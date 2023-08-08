package com.example.demo.service.impl;

import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserMapperImpl;
import com.example.demo.repository.UserDetailRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserDetailRepository userRepository;

    @InjectMocks
    UserServiceImpl service;

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void testGetUsers() {
        var howMany = 1;
        when(userRepository.findAll()).thenReturn(getNUsers(howMany));

        var dtos = service.getUsers();
        assertNotNull(dtos);
        assertEquals(howMany, dtos.collectList().blockOptional().get().size());
    }

    @Test
    void testGetUserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(returnEmptyMono());
        assertEquals(Mono.justOrEmpty(Optional.empty()), service.getUser(UUID.randomUUID().toString()));
    }

    @Test
    void testGetUserFound() throws UserNotFoundException {
        when(userRepository.findById(anyString())).thenReturn(Mono.just(getDummyUser()));
        var dto = service.getUser(UUID.randomUUID().toString());
        assertNotNull(dto);
    }

    @Test
    void testFindUserByEmailFound() throws UserNotFoundException, UserServiceException {
        when(userRepository.findByEmail(any())).thenReturn(Flux.just(getDummyUser()));
        var dtos = service.getUserByEmailAddress("x@y.com");
        assertNotNull(dtos);
    }

    @Test
    void testFindUserByEmailNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(returnEmptyFlux());
        assertEquals(Flux.empty(), service.getUserByEmailAddress("x@y.com"));
    }

    @Test
    void testCreateUsers() throws UserExistsException, UserServiceException {

        when(userRepository.findByEmail(any())).thenReturn(returnEmptyFlux());
        var dto = mapper.mapToUserDto(getDummyUser());
        when(userRepository.save(any())).thenReturn(Mono.just(getDummyUser()));
        var user = service.createUser(dto).blockOptional().get();
        assertNotNull(user);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testCreateUserEmailNotProvided() {

        var user = getDummyUser();
        user.setEmail(null);
        assertThrows(UserServiceException.class, () -> service.createUser(mapper.mapToUserDto(user)));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testCreateUserEmailAlreadyExists() {

        when(userRepository.findByEmail(any())).thenReturn(Flux.just(getDummyUser()));
        assertThrows(UserExistsException.class, () -> service.createUser(mapper.mapToUserDto(getDummyUser())));
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.findById(anyString())).thenReturn(returnEmptyMono());
        assertThrows(UserNotFoundException.class, () -> service.deleteUser(UUID.randomUUID().toString()));
        verify(userRepository, times(0)).delete(any());
    }

    @Test
    void testDeleteUserFound() throws UserNotFoundException {
        when(userRepository.findById(anyString())).thenReturn(Mono.just(getDummyUser()));
        service.deleteUser(UUID.randomUUID().toString());
        verify(userRepository, times(1)).delete(any());
    }

    private Flux<UserDetail> getNUsers(int howMany) {
        var listu = new ArrayList<UserDetail>();
        for (int i = 0; i < howMany; i++) {
            listu.add(getDummyUser());
        }
        return Flux.just(listu.get(0));
    }

    private UserDetail getDummyUser() {
        var user = new UserDetail();
        user.setEmail("x@y.com");
        user.setPassword("ssshhhh");
        return user;
    }

    private Mono<UserDetail> returnEmptyMono() {
        return Mono.justOrEmpty(Optional.empty());
    }

    private Flux<UserDetail> returnEmptyFlux() {
        return Flux.empty();
    }
}