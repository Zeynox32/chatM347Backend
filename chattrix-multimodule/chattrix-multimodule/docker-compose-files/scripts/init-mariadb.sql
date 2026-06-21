-- Wird beim ersten Start des MariaDB-Containers automatisch ausgeführt
-- Erstellt die zwei benötigten Datenbanken für Authentication- und User-Service

CREATE DATABASE IF NOT EXISTS authentication_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
