FROM jboss/keycloak:latest

COPY test-container-config/keycloak/provisioning/springboot-realm.json /opt/jboss/keycloak/imports/springboot-realm.json

ENV KEYCLOAK_ADMIN_USER admin
ENV KEYCLOAK_ADMIN_PASSWORD admin

ENV DB_VENDOR h2

ENV KEYCLOAK_IMPORT /opt/jboss/keycloak/imports/springboot-realm.json
