import java.util.*

fun main(args: Array<String>) {
    gridlandMetroDriver()
}

fun gridlandMetroDriver() {
    val a = arrayOf(
        arrayOf(2,2,3),
        arrayOf(3,1,2),
        arrayOf(3,2,4),
        arrayOf(4,4,4),
    )
    val nRows = 400L
    val nCols = 400L
    val result = gridlandMetro(nRows, nCols, 4, a)
    println("gridlandMetro = $result")  // 160,000 - 7 = 159,993
}

/**
 * Given an n x m matrix with rows 1 to n and cols 1 to m, and horizontal tracks that can
 * run in any row, find the number of unoccupied cells. Constraints:
 * 1 <= n,m <= 10^9
 * 0 <= nTracks <= 1000
 */
fun gridlandMetro(nRows: Long, nCols: Long, nTracks: Int, track: Array<Array<Int>>): Long {
    // 0 tracks? Return grid area.
    if (nTracks == 0) return nRows * nCols

    // Sort by row
    track.sortBy{ it[0] }

    // Number of streetlamp locations
    var count = 0L

    // Index in track array
    var i = 0

    // Current row
    var r = 1L

    while (r <= nRows) {
        // Find the next row occupied by track
        var nextOccupiedRow = 0L
        if (i <= track.lastIndex) {
            nextOccupiedRow = track[i][0].toLong()
        } else {
            // There are no more tracks. Count remaining rows and we're done.
            count += (nRows - r + 1) * nCols
            return count
        }

        // Count the cells in the empty rows before this track
        if (r < nextOccupiedRow) {
            val emptyRows = nextOccupiedRow - r
            count += nCols * emptyRows
            r = nextOccupiedRow
        }

        // Count the unoccupied cells in this row after subtracting out the tracks.
        // Tracks can overlap. First, extract all the track entries that are on this
        // row.
        val ri = r.toInt()
        val tracksOnThisRow = track.filter{ it[0] == ri }

        // Merge the tracks into non-overlapping intervals.
        val nonOverlapTracks = mergeIntervals(tracksOnThisRow.toTypedArray())

        // Count the occupied cells
        var occupiedCells = 0L
        for (t in nonOverlapTracks) occupiedCells += (t[2] - t[1] + 1).toLong()

        // Calculate the unoccupied cells
        count += nCols - occupiedCells

        // Move the index past these track entries
        i += tracksOnThisRow.size
        // Move to the next row
        r++
    }
    return count
}


/**
 * Merges overlapping intervals. Track array consists of [row, startCol, endCol]
 * All records must be on the same row.
 */
fun mergeIntervals(track: Array<Array<Int>>): Array<Array<Int>> {

    // Create an empty stack of tracks
    var trackStack = Stack<Array<Int>>()

    // Sort the tracks in increasing order of start position
    track.sortBy{ it[1] }

    // Push the first track to the stack
    trackStack.push(track[0])

    // Start from the next interval and merge if necessary
    for (i in 1..track.lastIndex) {
        val top = trackStack.peek()

        // If current track segment is not overlapping with stack top,
        // push it to the stack. Looks like this:
        //     ----             top
        //            -----     track[i]
        if (top[2] < track[i][1]) {
            trackStack.push(track[i])
        }

        // Otherwise, if end point of current segment is bigger than
        // endpoint of top, merge the top with this segment.
        // Looks like this:
        //     ----             top
        //       -----          track[i]
        // Update the end point of top.
        else if (top[2] < track[i][2]) {
            top[2] = track[i][2]
            trackStack.pop()
            trackStack.push(top)
        }
    }
    return trackStack.toTypedArray()
}
