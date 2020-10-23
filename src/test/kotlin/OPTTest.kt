import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class OPTTest {
    val frameCount = 6
    val pageCount = 10
    val requests = listOf(1, 2, 3, 4, 5, 6, 1, 6, 3, 8, 7, 3, 4, 8, 10, 2)
    @Test
    fun singleRequest() {
        assertEquals(0, OPT(frameCount, pageCount, requests).singleRequest(2))
    }
    @Test
    fun run() {
        val correctResult = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 5, 0, 0, 0, 3, 0)
        assertEquals(correctResult, OPT(frameCount, pageCount, requests).run(requests))
    }
}