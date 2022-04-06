import static org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TestingScriptsTest {

    @Test
    @DisplayName("Test a bash script")
    void exampleTest() {
        Process process = "sh ./scripts/bash-script-that-does-something.sh".execute()
        assertEquals(0, process.exitValue(), "Exit value should be zero.")
    }
}
