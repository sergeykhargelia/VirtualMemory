import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class LRUTest {
    @Test
    fun singleRequest() {
        val frameCount = 6
        assertEquals(0, LRU(frameCount).singleRequest(2))
    }
    @Test
    fun run() {
        val frameCount = 6
        val requests = listOf(1, 2, 3, 4, 5, 6, 1, 6, 3, 8, 7, 3, 4, 8, 10, 2)
        val correctResult = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 4, 0, 5, 0, 1, 6)
        assertEquals(correctResult, LRU(frameCount).run(requests))
    }
}