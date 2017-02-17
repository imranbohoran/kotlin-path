package model

class PuzzleGrid(val contents: String) {

    val grid = mutableMapOf<Int, List<String>>()
    var xLength : Int
    var yLength : Int

    init {
        xLength = contents.length
        yLength = contents.length
        println(contents)
    }

}
