package model

import java.util.*

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

    fun valueForLocation(location: Location) : Int {
        return grid[location.row - 1]?.get(location.column - 1)?.toInt()!!
    }

    fun movementInformation(currentLocation: Location) : List<Movement> {
        val currentValue = valueForLocation(currentLocation)
        val movements : ArrayList<Movement> = ArrayList()

        if(canMove(PuzzleDirection.U, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.U, currentValue))
        }

        if(canMove(PuzzleDirection.D, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.D, currentValue))
        }

        if(canMove(PuzzleDirection.L, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.L, currentValue))
        }

        if(canMove(PuzzleDirection.R, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.R, currentValue))
        }

        return movements
    }

    private fun canMove(direction: PuzzleDirection, location: Location, currentValue: Int): Boolean {
        when(direction) {
            PuzzleDirection.U -> return ((location.row - 1) - currentValue) > 0
            PuzzleDirection.D -> return ((location.row - 1) + currentValue) < yLength
            PuzzleDirection.L -> return ((location.column - 1) - currentValue) >= 0
            PuzzleDirection.R -> return ((location.column - 1) + currentValue) < xLength
            else -> return false
        }
    }

}
