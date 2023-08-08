package com.example.demo.security;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.HashMap;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KeycloakJwtAuthenticationConverterTest {

    @InjectMocks
    KeycloakJwtAuthenticationConverter converter;

    @Test
    @Disabled
    void convert() {

        // needs work
        var jwt = Jwt.withTokenValue("eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI3RDI0ZEVEdTB4QzdfVzdRWDZkTTd1UTZhcGQ2RWlBNDZ4R0Q5NkdyMTRVIn0.eyJleHAiOjE2OTE1MDk3MDYsImlhdCI6MTY5MTUwOTEwNiwianRpIjoiMmEwY2U1YjYtZDM0Yy00MGM0LWExMzYtMmZjOGZkMWQxMmY1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo3MDcwL2F1dGgvcmVhbG1zL3NwcmluZ2Jvb3QiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZTdkMmE2MmEtZDcwOS00MDY5LTk4ODItMzk3YjAyM2M5YmQ1IiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic3ByaW5nYm9vdC1jbGllbnQiLCJzZXNzaW9uX3N0YXRlIjoiNDhkMTIxMjktZTBhZi00YjQ5LTgxMzAtMjY0YWRhOGQ1YzcxIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJzcHJpbmdib290LXVzZXIiLCJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIiwiZGVmYXVsdC1yb2xlcy1zcHJpbmdib290Il19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiNDhkMTIxMjktZTBhZi00YjQ5LTgxMzAtMjY0YWRhOGQ1YzcxIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJuYW1lIjoidGVzdCB1c2VyIiwicHJlZmVycmVkX3VzZXJuYW1lIjoidGVzdC11c2VyIiwiZ2l2ZW5fbmFtZSI6InRlc3QiLCJmYW1pbHlfbmFtZSI6InVzZXIiLCJlbWFpbCI6InRlc3QtdXNlckB0ZXN0LmNvbSJ9.pc7MoGL_ToFbz2TtNh1iKxnmySP3PPybSed7TXK4WQQrzWWhGeocsU0jSdM4GP3v3QhMFoRkYy_51L4EKU0KrQIwoHCFEQbdKZDKhk5OFQHobRywGi2rDxJPB2JM-DxNwvG_slhv3pW9qmywNUhIT0bWhxmM7ARplSeGtSkpZ2Ql5E8OvfV5BSd7cDE3lA8AWU_gWS93iOGQVD1NOqgX-7TdCexfQtWzdWM7iMerY8S1Wz8t-ovkyvW6SFa3qJWncMnb1uH62KminvmICq6NQpxa_tV1gRN7KlM6T1Cb-6iN5eFhKzZVUYapIt0SgOr3rYnZ1VQJMUn6ACgsB0_dkw");
        jwt.header("A", "32");
        jwt.claim("b", "sezyou");
        var token = converter.convert(jwt.build());
        assertTrue(token.getAuthorities().contains("ROLE_springboot-user"));
    }
}