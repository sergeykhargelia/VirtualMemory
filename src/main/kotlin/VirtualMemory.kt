@file: JvmName("main")
import java.io.File
import java.io.FileNotFoundException
import java.io.PrintWriter
import java.util.*

typealias Request = Int
data class Input(val queries: List<Request>, val frameCount: Int, val pageCount: Int)

data class Frame(val page: Int, val frameID: Int)

typealias Result = List<Int>

const val noChange = 0

interface VirtualMemoryAlgorithm {
    val maxMemorySize: Int
    val memory: MutableCollection<Frame>
    fun updateWithoutReplacement(positionInMemory: Int)
    fun updateWithReplacement(newPage: Request): Int

    /**this function process single request to the memory
     * it takes the page number to add
     * if there is free space in memory, this page will be added in it
     * after that if the memory contain given page,
     * some information in data structures will be updated
     * and 0 will be returned because of no replacement required
     * otherwise some page in memory will be replaced by the given page
     * and number of frame where replaced page was located will be returned
     *
     * also note that function indexOfFirst returns -1
     * if all elements do not satisfy the given condition
     */
    fun singleRequest(newPage: Request) =
        with (memory) {
            if (size < maxMemorySize) {
                add(Frame(newPage, size + 1))
            }
            val positionInMemory = indexOfFirst { it.page == newPage }
            if (positionInMemory != -1) {
                updateWithoutReplacement(positionInMemory)
                noChange
            }
            else {
                updateWithReplacement(newPage)
            }
        }

    fun run(requests: List<Request>): Result {
        return requests.map { singleRequest(it) }
    }
}

class FIFO(frameCount: Int): VirtualMemoryAlgorithm {
    override val maxMemorySize = frameCount
    /**memory stores frames in order of addition time**/
    override val memory: Queue<Frame> = LinkedList<Frame>()
    override fun updateWithoutReplacement(positionInMemory: Int) {}

    /**this function takes page number to add in memory
     * frames are arranged in order of addition in memory,
     * so first-in page is located in the first frame of queue
     * page from this frame is replaced to new page
     * and frame is moving to the end of the queue
     * function returns number of frame where new page is located
     */
    override fun updateWithReplacement(newPage: Request) =
        with (memory) {
            val changedFrameID = poll().frameID
            add(Frame(newPage, changedFrameID))
            changedFrameID
        }
}

class LRU(frameCount: Int): VirtualMemoryAlgorithm {
    override val maxMemorySize = frameCount
    /**memory stores frames in order of time of last request to it**/
    override val memory: MutableList<Frame> = mutableListOf()

    /**this function updates memory after request to some frame
     * frames are arranged in ascending order of last request time,
     * so after request to some frame this frame moving to the end of the memory
     */
    override fun updateWithoutReplacement(positionInMemory: Int) {
        memory.apply {
            val requestedFrame = removeAt(positionInMemory)
            add(requestedFrame)
        }
    }

    /**this function takes page number to add in memory
     * frames are arranged in order last request time,
     * so the page that has not been requested the longest is in the first frame
     * page from this frame is replaced to new page
     * and frame is moving to the end of the memory
     * function returns number of frame where new page is located
     */
    override fun updateWithReplacement(newPage: Request) =
        with (memory) {
            val changedFrameID = removeAt(0).frameID
            add(Frame(newPage, changedFrameID))
            changedFrameID
        }
}

typealias RequestsForPage = Queue<Request>

class OPT(frameCount: Int, pageCount: Int, requests: List<Request>): VirtualMemoryAlgorithm {
    override val maxMemorySize = frameCount
    override val memory: MutableList<Frame> = mutableListOf()
    /**remainingRequestsForPage stores the requests for fixed page in ascending order of time**/
    private val remainingRequestsForPage: Array<RequestsForPage> = Array(pageCount) { LinkedList<Request>() }
    private fun get0Indexed(value: Int) = value - 1
    init {
        for ((requestID, page) in requests.withIndex()) {
            remainingRequestsForPage[get0Indexed(page)].add(requestID)
        }
    }

    /**this function updates remainingRequestsFor page after request to some page
     * it takes position in memory of frame where this page is located
     * **/
    override fun updateWithoutReplacement(positionInMemory: Int) {
        val requestedPage = memory[positionInMemory].page
        remainingRequestsForPage[get0Indexed(requestedPage)].poll()
    }

    /**this function takes page number to add in memory
     * firstly it finds the position in memory of the page with the maximum next request time
     * next request for fixed page is the first element in remainingRequests for this page
     * if for some page there are no requests remained it can be assumed
     * that next request time for this page is Int.MAX_VALUE
     * because this value is greater than all possible requests times
     * found page is replacing to page that must be added
     * after that remaining requests for new page are updated
     * function returns number of frame where new page is located
     */
    override fun updateWithReplacement(newPage: Request) =
        with (memory) {
            val positionInMemoryToChange = indices.maxBy {
                val currentPage = memory[it].page
                val nextRequestTime = remainingRequestsForPage[get0Indexed(currentPage)].firstOrNull() ?: Int.MAX_VALUE
                nextRequestTime
            } ?: error("memory is empty")
            val changedFrameID = removeAt(positionInMemoryToChange).frameID
            add(Frame(newPage, changedFrameID))
            remainingRequestsForPage[get0Indexed(newPage)].poll()
            changedFrameID
        }
}

fun printSingleResult(writer: PrintWriter, result: Result) {
    val memoryChanges = result.count { it != noChange }
    writer.append("Number of changes: $memoryChanges\n")
    writer.append("Sequence of answers: ")
    for (answer in result) {
        writer.append("$answer ")
    }
    writer.append("\n\n")
}

fun printResults(outputFileName: String, resultFIFO: Result, resultLRU: Result, resultOPT: Result) {
    val writer = PrintWriter(outputFileName)
    writer.append("Result for FIFO algorithm:\n")
    printSingleResult(writer, resultFIFO)
    writer.append("Result for LRU algorithm:\n")
    printSingleResult(writer, resultLRU)
    writer.append("Result for OPT algorithm:\n")
    printSingleResult(writer, resultOPT)
    writer.close()
}

fun parseInput(input: List<String>): Input {
    if (input.size != 2) {
        throw IllegalArgumentException("input must contain 2 lines")
    }
    val frameCount = input[0].toIntOrNull() ?: throw NumberFormatException("number of frames must be integer")
    if (frameCount < 1) {
        throw IllegalArgumentException("number of frames must be positive")
    }
    if (input[1].isEmpty()) {
        throw IllegalArgumentException("there is no queries")
    }
    val queries = input[1].split(" ").map{
        it.toIntOrNull() ?: throw NumberFormatException("all page numbers must be integer")
    }
    if (queries.any { currentPage -> currentPage < 1 }) {
        throw IllegalArgumentException("all page numbers must be positive")
    }
    val pageCount = queries.max() ?: 0
    return Input(queries, frameCount, pageCount)
}

fun processSingleTestCase(inputFileName: String, outputFileName: String) {
    if (!File(inputFileName).exists()) {
        throw FileNotFoundException("input file does not found")
    }
    val input = File(inputFileName).readLines()
    val data = parseInput(input)
    with (data) {
        val resultFIFO = FIFO(frameCount).run(queries)
        val resultLRU = LRU(frameCount).run(queries)
        val resultOPT = OPT(frameCount, pageCount, queries).run(queries)
        printResults(outputFileName, resultFIFO, resultLRU, resultOPT)
    }
}

typealias InputAndOutputFiles = Pair<String, String>

fun processTestCases(filesList: List<InputAndOutputFiles>) {
    for ((testID, filesForCurrentTest) in filesList.withIndex()) {
        val (inputFile, outputFile) = filesForCurrentTest
        try {
            processSingleTestCase(inputFile, outputFile)
        }
        catch(e: Exception) {
            println("error on test case $testID: $e")
        }
    }
}

const val defaultOutputFile = "output.txt"

fun List<String>.getFiles(): InputAndOutputFiles = when(this.size) {
    1 -> Pair(this[0], defaultOutputFile)
    2 -> Pair(this[0], this[1])
    else -> throw IllegalArgumentException("wrong number of files for test")
}

const val numberOfFilesForSingleTest = 2

fun main(args: Array<String>) {
    try {
        if (args.isEmpty()) {
            throw IllegalArgumentException("You do not give any files. Check DOC.md to learn arguments format")
        }
        val filesList = args.toList().chunked(numberOfFilesForSingleTest) { it.getFiles() }
        processTestCases(filesList)
    }
    catch(e: IllegalArgumentException) {
        println(e.toString())
    }
}