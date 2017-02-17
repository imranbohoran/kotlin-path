package model

class PuzzleGrid(val contents: String) {

    val grid = mutableMapOf<Int, List<String>>()
    var xLength : Int
    var yLength : Int

    init {
        val values : List<String> = contents.split(",")
        xLength = values.size
        yLength = values.size
        println(contents)
    }

}
