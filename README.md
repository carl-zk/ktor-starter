# Ktor framework starter
> Ktor is a framework for building asynchronous servers and clients in connected systems using the powerful Kotlin programming language.

> Ktor + Jooq + MySQL
## prepare
```sql
CREATE SCHEMA `jooq_learn` DEFAULT CHARACTER SET utf8mb4;

CREATE TABLE `author` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `gender` char(10) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
```
## run
```bash
./gradlew run
```
## components
- ktor
- jooq
