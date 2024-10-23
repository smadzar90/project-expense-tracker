package org.example.db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Seeder {

    public static void executeDefaultQueries(Connection connection) {
        Path path = Paths.get("./src/main/resources/files/default_queries.txt");
        List<String> queries = new ArrayList<>();

        try(Stream<String> lines = Files.lines(path)) {
            StringBuilder sb = new StringBuilder();
            lines.forEach(line -> {
               sb.append(line.strip());

               if(line.contains(";")) {
                   queries.add(sb.toString());
                   sb.setLength(0);
               }
            });
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while reading the file." + e.getMessage());
        }
        executeQueries(queries, connection);
    }

    private static void executeQueries(List<String> queries, Connection connection) {
        queries.forEach(q -> {
            try {
                Statement statement = connection.createStatement();
                statement.execute(q);
                connection.commit();
            } catch (SQLException e) {
                throw new RuntimeException("An error occurred while executing default queries."  + e.getMessage());
            }
        });
    }
}
