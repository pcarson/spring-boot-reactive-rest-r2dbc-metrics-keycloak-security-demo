package com.example.demo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
/**
 * Non-JPA R2jdbc entity definition - no index definition (etc.) possible.
 */
public class UserDetail implements Serializable {

    @Id
    private String id;

    private String email;

    private String password;
}
