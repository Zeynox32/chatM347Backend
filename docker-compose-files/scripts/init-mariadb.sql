CREATE DATABASE IF NOT EXISTS authentication_service;
CREATE DATABASE IF NOT EXISTS user_service;

CREATE USER IF NOT EXISTS 'auth_user'@'%' IDENTIFIED BY 'auth_password';
CREATE USER IF NOT EXISTS 'user_user'@'%' IDENTIFIED BY 'user_password';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER ON authentication_service.* TO 'auth_user'@'%';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER ON user_service.* TO 'user_user'@'%';

FLUSH PRIVILEGES;