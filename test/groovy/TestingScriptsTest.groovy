import groovy.sql.Sql
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import org.testcontainers.containers.JdbcDatabaseContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

import static org.junit.jupiter.api.Assertions.assertEquals

@Testcontainers
class TestingScriptsTest {

    private static final String POSTGRES_DOCKER_IMAGE = "postgres:11.6-alpine"
    private static final String DB_NAME = "test_db"
    private static final String DB_USERNAME = "user"
    private static final String DB_PASSWORD = "pass"
    private static final String DB_HOST = "localhost"
    private static final int POSTGRES_PORT = 5432
    private static final String INIT_SCRIPT_PATH = "schema.sql"
    private static int mapperPort
    private static Sql sql

    @Container
    private static final JdbcDatabaseContainer POSTGRESQL_CONTAINER = new PostgreSQLContainer(
            DockerImageName.parse(POSTGRES_DOCKER_IMAGE))
            .withDatabaseName(DB_NAME)
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD)
            .withInitScript(INIT_SCRIPT_PATH)

    @BeforeAll
    static void beforeAll() {
        assert POSTGRESQL_CONTAINER.running

        // this is to acquire the random port that testcontainer uses
        mapperPort = POSTGRESQL_CONTAINER.getMappedPort(POSTGRES_PORT)

        // instantiate the groovy SQL object.
        sql = new Sql(
                new PGSimpleDataSource(
                        serverName: DB_HOST,
                        databaseName: DB_NAME,
                        user: DB_USERNAME,
                        password: DB_PASSWORD,
                        portNumber: mapperPort)
        )
    }

    @AfterEach
    void afterEach() {
        sql.execute('TRUNCATE items')
    }

    @Test
    @DisplayName("Assert bash script can be executed")
    void simpleTest() {
        Process process = "sh ./scripts/simple-bash-script.sh".execute()

        println(process.text) // prints standard output
        assertEquals(0, process.onExit().get().exitValue(), "Exit value should be zero.")
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    @Test
    @DisplayName("Assert script inserts data")
    void insertTest() {
        Process process = "sh ./scripts/bash-script-that-inserts-data.sh $DB_USERNAME $DB_HOST $mapperPort $DB_NAME $DB_PASSWORD".execute()

        println(process.text) //prints standard output
        assertEquals(0, process.onExit().get().exitValue(), "Exit value should be zero.")
        assertEquals(1, sql.firstRow('SELECT COUNT(id) as num FROM items').num)
    }

}
