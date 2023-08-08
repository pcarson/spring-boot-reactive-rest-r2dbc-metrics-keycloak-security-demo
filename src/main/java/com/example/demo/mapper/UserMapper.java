package com.example.demo.mapper;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.UserDetail;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    UserDetail mapToUser(UserDTO userDTO);

    UserDTO mapToUserDto(UserDetail user);

    List<UserDTO> mapToUserDtoList(List<UserDetail> users);

}
