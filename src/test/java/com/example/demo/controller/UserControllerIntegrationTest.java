package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    @Test
    void createUserSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDTO();
        when(userService.createUser(any())).thenReturn(Mono.just(dto));

        //WHEN
        webTestClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/users").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new ObjectMapper().writeValueAsString(dto)))
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void createUserWhereUserAlreadyExists() throws Exception {
        //GIVEN

        var dto = getDummyUserDetail();
        when(userService.createUser(any())).thenThrow(new UserExistsException(""));
        webTestClient
                .post()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/users").build())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(new ObjectMapper().writeValueAsString(dto)))
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @Test
    void findUserByEmailAddressSucceeds() throws Exception {
        //GIVEN

        var dto = getDummyUserDetail();
        when(userService.getUserByEmailAddress(any())).thenReturn(Flux.just(getDummyUserDetail()));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user")
                                .queryParam("emailAddress", "test@gmail.com").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(ArrayList.class)
                .consumeWith(serverResponse ->
                        assertNotNull(serverResponse.getResponseBody()))
        ;

    }

    @Test
    void findUserByEmailAddressNotFound() {
        //GIVEN

        when(userService.getUserByEmailAddress(any())).thenReturn(Flux.empty());

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/user")
                                .queryParam("emailAddress", "test@gmail.com").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
        ;
    }

    @Test
    void findUserByIdSucceeds() {
        //GIVEN

        var dto = getDummyUserDetail();
        var uuidValue = UUID.randomUUID().toString();
        dto.setId(uuidValue);
        when(userService.getUser(uuidValue)).thenReturn(Mono.just(dto));

        //WHEN
        webTestClient
                .get()
                .uri("/user/{id}", uuidValue)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(UserDetail.class)
                .consumeWith(serverResponse ->
                        assertNotNull(serverResponse.getResponseBody()))
        ;
    }

    @Test
    void findUserByIdNotFound() {
        var uuidValue = UUID.randomUUID().toString();
        when(userService.getUser(uuidValue)).thenReturn(Mono.justOrEmpty(Optional.empty()));

        webTestClient
                .get()
                .uri("/user/{id}", uuidValue)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
        ;
    }

    @Test
    void deleteUserByIdSucceeds() throws UserNotFoundException {
        //GIVEN
        var uuidValue = UUID.randomUUID().toString();
        when(userService.deleteUser(any())).thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri("/user/{id}", uuidValue)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
        ;
    }

    @Test
    void deleteUserByIdNotFound() throws UserNotFoundException {
        //GIVEN

        var uuidValue = UUID.randomUUID().toString();
        doThrow(new UserNotFoundException("")).when(userService).deleteUser(any());

        //WHEN
        webTestClient
                .delete()
                .uri("/user/{id}", uuidValue)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound()
        ;
    }

    private UserDetail getDummyUserDetail() {
        var user = new UserDetail();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("x@y.com");
        user.setPassword("ssshhhh");
        return user;
    }

    private UserDTO getDummyUserDTO() {
        var user = new UserDTO();
        user.setId(UUID.randomUUID().toString());
        user.setEmail("x@y.com");
        user.setPassword("ssshhhh");
        return user;
    }

}