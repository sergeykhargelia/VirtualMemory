import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class mainTest {

    /**tests for function parse input**/
    @Test
    fun parseInput() {
        val data : List<String> = listOf("6", "1 6 3 8 7 3 4 8 10 2")
        val result = parseInput(data)
        assertEquals(6, result.frameCount)
        assertEquals(10, result.pageCount)
        assertEquals(listOf(1, 6, 3, 8, 7, 3, 4, 8, 10, 2), result.queries)
    }
    @Test
    fun `parse input with one query`() {
        val data : List<String> = listOf("6", "1")
        val result = parseInput(data)
        assertEquals(6, result.frameCount)
        assertEquals(1, result.pageCount)
        assertEquals(listOf(1), result.queries)
    }

    /**tests for function List<String>.getFiles()**/
    @Test
    fun `get files when input and output files exist`() {
        assertEquals(Pair("in.txt", "out.txt"), listOf("in.txt", "out.txt").getFiles())
    }
    @Test
    fun `get files when only input file exists`() {
        assertEquals(Pair("in.txt", "output.txt"), listOf("in.txt").getFiles())
    }
}