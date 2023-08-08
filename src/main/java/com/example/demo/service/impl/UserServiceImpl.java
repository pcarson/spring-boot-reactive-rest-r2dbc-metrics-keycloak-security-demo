package com.example.demo.service.impl;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserDetail;
import com.example.demo.exception.UserExistsException;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserServiceException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserMapperImpl;
import com.example.demo.repository.UserDetailRepository;
import com.example.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDetailRepository userRepository;
    private final UserMapper userMapper = new UserMapperImpl();

    public UserServiceImpl(UserDetailRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Flux<UserDetail> getUsers() {
        log.info("Getting all users");
        return userRepository.findAll();
    }

    @Override
    public Mono<UserDetail> getUser(String id) {
        log.info("Getting user by id {}", id);
        return userRepository.findById(id);
    }

    @Override
    public Flux<UserDetail> getUserByEmailAddress(final String emailAddress) {
        return userRepository.findByEmail(emailAddress.toLowerCase());
    }

    @Override
    /**
     * Note that this method blocks DB access in order to find out whether
     * a user exists already before adding it, potentially again.
     */
    public Mono<UserDTO> createUser(UserDTO userDTO) throws UserExistsException, UserServiceException {

        if (userDTO.getEmail() == null) {
            throw new UserServiceException("Email must not be null");
        }

        // check if it exists already, throw exception if it does
        var listOfUsers = userRepository.findByEmail(userDTO.getEmail()).collectList().blockOptional();
        if (listOfUsers.isPresent() && (!listOfUsers.get().isEmpty())) {
            throw new UserExistsException(userDTO.getEmail());
        }

        var user = createConfiguredUser(userDTO); // prepare
        return userRepository.save(user)
                .map(userMapper::mapToUserDto)
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Void> deleteUser(String id) throws UserNotFoundException {

        // block to confirm it exists before deleting it ....
        var user = userRepository.findById(id)
                .blockOptional()
                .orElseThrow(() -> new UserNotFoundException(id));

        return userRepository.delete(user);
    }

    private UserDetail createConfiguredUser(UserDTO userDTO) {
        var userToSave = userMapper.mapToUser(userDTO);

        // DO NOT provide the Id, otherwise r2dbc assumes it has to do an update instead of save ...
        userToSave.setEmail(userDTO.getEmail().toLowerCase());
        userToSave.setPassword(userDTO.getPassword());
        return userToSave;
    }

}
