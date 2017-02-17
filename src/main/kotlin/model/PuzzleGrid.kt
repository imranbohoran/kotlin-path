package model

class PuzzleGrid(contents: String) {

    val grid = mutableMapOf<Int, List<String>>()
    var xLength : Int
    var yLength : Int

    init {
        val allContent = contents.trim().lines()
        var index : Int = 0
        allContent.map { line ->
            val values = line.split(",")
            if(values.size != allContent.size) {
                throw IllegalArgumentException("Supplied data does not form a square")
            }
            grid.put(index, values)
            index++
        }
        xLength = allContent.size
        yLength = allContent.size
    }

}
