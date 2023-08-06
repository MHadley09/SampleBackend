## About

This is a simple sample project leveraging database controlled roles to manage access to REST endpoints through JWT tokens.   The project is built on Java 18 using Spring + Jooq + Flyway as a simple example of some basic features of each framework.

## To Run
- Install Intellij Community Edition or another Java IDE
- Ensure you have Java 18 installed and it as your default Java language
- Import sample-backend into Intellij or other IDE
- Ensure your project is set to use Java 18
- Ensure target/generated-sources is included in your package build path
- Install pgAdmin 4 and create a local database instance using PostgresSQL 14 with a root user with username: postgres and password: root
- This local db should be an empty pg server on port 5432
- You can optionally create a specific database sample-db
- Ensure you have maven installed and configured to use Java 18.
- Open the terminal and navigate to your sample-backend root folder
- Run `mvn flyway:migrate`
- Run `mvn jooq-codegen:generate`
- These two commands will create your database and auto generate the Java bindings with the database
- You can now run your sample-backend
- Run sample backend using either the play button in your IDE or in the terminal run `mvn spring-boot:run -Dspring-boot.run.profiles=local`
- This will start your backend on port 8080.
