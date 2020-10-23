import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FIFOTest {
    @Test
    fun singleRequest() {
        val frameCount = 6
        assertEquals(0, FIFO(frameCount).singleRequest(2))
    }
    @Test
    fun run() {
        val frameCount = 6
        val requests = listOf(1, 2, 3, 4, 5, 6, 1, 6, 3, 8, 7, 3, 4, 8, 10, 2)
        val correctResult = listOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 0, 0, 0, 3, 4)
        assertEquals(correctResult, FIFO(frameCount).run(requests))
    }
}