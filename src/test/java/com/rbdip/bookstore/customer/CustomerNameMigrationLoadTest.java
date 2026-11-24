package com.rbdip.bookstore.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class CustomerNameMigrationLoadTest {

    private static final int READER_COUNT = 4;

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bookstore_name_migration")
            .withUsername("bookstore")
            .withPassword("bookstore");

    @Test
    void keepsLegacyNameReadsAvailableThroughExpandAndBackfill() throws Exception {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(POSTGRES.getJdbcUrl());
        dataSource.setUser(POSTGRES.getUsername());
        dataSource.setPassword(POSTGRES.getPassword());
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        flyway(dataSource, "2").migrate();
        jdbc.update("INSERT INTO customers (full_name) VALUES ('Ivan Petrov')");

        AtomicBoolean keepReading = new AtomicBoolean(true);
        AtomicInteger successfulReads = new AtomicInteger();
        List<Exception> readErrors = new CopyOnWriteArrayList<>();
        CountDownLatch readersReady = new CountDownLatch(READER_COUNT);
        ExecutorService readers = Executors.newFixedThreadPool(READER_COUNT);
        for (int reader = 0; reader < READER_COUNT; reader++) {
            readers.submit(() -> readLegacyName(dataSource, keepReading, successfulReads, readErrors, readersReady));
        }

        try {
            assertThat(readersReady.await(5, TimeUnit.SECONDS)).isTrue();
            flyway(dataSource, "4").migrate();
        } finally {
            keepReading.set(false);
            readers.shutdown();
            assertThat(readers.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }

        assertThat(readErrors).isEmpty();
        assertThat(successfulReads).hasValueGreaterThan(0);
        assertThat(jdbc.queryForObject(
                "SELECT first_name || ' ' || last_name FROM customers WHERE full_name = 'Ivan Petrov'",
                String.class))
                .isEqualTo("Ivan Petrov");
        flyway(dataSource, null).migrate();
        assertThat(jdbc.queryForList(
                "SELECT column_name FROM information_schema.columns WHERE table_name = 'customers'",
                String.class))
                .contains("first_name", "last_name")
                .doesNotContain("full_name");
    }

    private void readLegacyName(
            PGSimpleDataSource dataSource,
            AtomicBoolean keepReading,
            AtomicInteger successfulReads,
            List<Exception> readErrors,
            CountDownLatch readersReady) {
        readersReady.countDown();
        while (keepReading.get()) {
            try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
                statement.executeQuery("SELECT full_name FROM customers LIMIT 1");
                successfulReads.incrementAndGet();
            } catch (Exception exception) {
                readErrors.add(exception);
            }
        }
    }

    private Flyway flyway(PGSimpleDataSource dataSource, String target) {
        var configuration = Flyway.configure().dataSource(dataSource).locations("classpath:db/migration");
        if (target != null) {
            configuration.target(target);
        }
        return configuration.load();
    }
}
