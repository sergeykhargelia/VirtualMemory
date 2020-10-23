import java.io.PrintWriter
import java.util.Random

const val Small = 10
const val Big = 500

fun main() {
    val writer = PrintWriter("input.txt")
    val rnd = Random()
    val limit = if (rnd.nextBoolean()) Big else Small
    val cntPages = rnd.nextInt(limit) + 2
    val cntFrames = rnd.nextInt(cntPages - 1) + 1
    val cntQueries = rnd.nextInt(limit)
    writer.append("$cntFrames\n")
    for (i in 1..cntQueries) {
        writer.append("${rnd.nextInt(cntPages) + 1}")
        if (i < cntQueries) {
            writer.append(" ")
        }
    }
    writer.close()
}