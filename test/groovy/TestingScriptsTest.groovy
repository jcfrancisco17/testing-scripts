import groovy.sql.Sql
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@Testcontainers
class TestingScriptsTest {

    private static String databaseName
    private static String username
    private static String password
    private static String host
    private static int port
    private static Sql sql

    @Container
    private static final JdbcDatabaseContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse('postgres:11.6-alpine'))
            .withDatabaseName('test_db')
            .withUsername('user')
            .withPassword('pass')
            .withInitScript('schema.sql')

    @BeforeAll
    static void beforeAll() {
        assert POSTGRESQL_CONTAINER.running

        databaseName = POSTGRESQL_CONTAINER.databaseName
        username = POSTGRESQL_CONTAINER.username
        password = POSTGRESQL_CONTAINER.password
        host = POSTGRESQL_CONTAINER.host
        port = POSTGRESQL_CONTAINER.getMappedPort(5432) // this is to acquire the random port that testcontainer uses

        sql = new Sql(
                new PGSimpleDataSource(
                        serverName: host,
                        portNumber: port,
                        databaseName: databaseName,
                        user: username,
                        password: password)
        )
    }

    @AfterAll
    static void afterAll() {
        sql.execute('TRUNCATE items')
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    @Test
    @DisplayName("Assert script inserts data")
    void insertTest() {
        Process process = "sh ./scripts/bash-script-that-inserts-data.sh $username $host $port $databaseName $password".execute()
        process.consumeProcessErrorStream(new StringBuffer()) //need this here or else process will not exit

        assertEquals(0, process.exitValue(), "Exit value should be zero.")
        assertEquals(1, sql.firstRow('SELECT COUNT(id) as num FROM items').num)
    }
}
