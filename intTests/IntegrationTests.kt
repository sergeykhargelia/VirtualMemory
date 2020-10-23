import org.junit.jupiter.api.Test
import java.io.File
import org.junit.jupiter.api.Assertions.*

internal class IntegrationTests {
    private fun runOnSingleCase(caseNumber: Int): Pair<List<String>, List<String>> {
        val inputFileName = "data/input$caseNumber.txt"
        val correctOutputFileName = "data/output$caseNumber.txt"
        main(arrayOf(inputFileName, "check.txt"))
        val programOutput = File("check.txt").readLines()
        val correctOutput = File(correctOutputFileName).readLines()
        return Pair(correctOutput, programOutput)
    }
    @Test
    fun runTests() {
        val testCount = 10
        for (currentTest in 1..testCount) {
            val (correctOutput, programOutput) = runOnSingleCase(currentTest)
            assertEquals(correctOutput, programOutput)
        }
    }
}