-- DROP TABLE user_detail IF EXISTS;
CREATE TABLE IF NOT EXISTS
    user_detail (
        id VARCHAR(36) default random_uuid() PRIMARY KEY,
        email VARCHAR(255),
        password VARCHAR(255)
    );
CREATE INDEX IF NOT EXISTS email_index ON user_detail(email);