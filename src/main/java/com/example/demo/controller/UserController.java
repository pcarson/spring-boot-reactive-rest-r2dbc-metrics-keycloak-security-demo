package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Tag(name = "UserController", description = "Endpoint for user management")
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Returns user", description = "Returns user by id.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/user/{userId}")
    public Mono<ResponseEntity<UserDetail>> getUser(@PathVariable("userId") String userId) {
        return userService.getUser(userId)
                .map(acc -> new ResponseEntity<>(acc, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }


    @Operation(summary = "Returns all users", description = "Returns user by email address.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/users")
    public Flux<UserDetail> getAllUsers() {
        return userService.getUsers();
    }

    @Operation(summary = "Returns user", description = "Returns user by email address.", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/user")
    public Flux<ResponseEntity<UserDetail>> getUserByEmailAddress(@RequestParam(value = "emailAddress") String emailAddress) {
        return userService.getUserByEmailAddress(emailAddress)
                .map(acc -> new ResponseEntity<>(acc, HttpStatus.OK))
                .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Create user", description = "Creates a new user.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/users")
    public Mono<ResponseEntity<UserDTO>> createUser(@RequestBody UserDTO userDTO) {

        try {
            return userService.createUser(userDTO)
                    .map(acc -> new ResponseEntity<>(acc, HttpStatus.OK))
                    .switchIfEmpty(Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND)));
        } catch (UserExistsException | UserServiceException e) {
            return Mono.just(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
        }
    }

    @Operation(summary = "Delete user", description = "Delete a user for the specified Id.", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/user/{userId}")
    public Mono<ResponseEntity<Void>> deleteUser(@PathVariable("userId") String userId) {
        try {
            return userService.deleteUser(userId)
                    .map(acc -> new ResponseEntity<>(acc, HttpStatus.OK));
        } catch (UserNotFoundException e) {
            return Mono.just(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
        }
    }

}
