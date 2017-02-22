package model

import java.util.*

class PuzzleGrid(contents: String) {

    val grid = mutableMapOf<Int, List<String>>()
    var xLength : Int
    var yLength : Int

    val startingLocation : Location = Location(1,1)

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

    fun movementInformation(currentLocation: Location) : ArrayList<Movement> {
        val currentValue = valueForLocation(currentLocation)
        val movements : ArrayList<Movement> = ArrayList()

        if(canMove(PuzzleDirection.U, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.U, currentValue, currentLocation))
        }

        if(canMove(PuzzleDirection.D, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.D, currentValue, currentLocation))
        }

        if(canMove(PuzzleDirection.L, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.L, currentValue, currentLocation))
        }

        if(canMove(PuzzleDirection.R, currentLocation, currentValue)) {
            movements.add(Movement(PuzzleDirection.R, currentValue, currentLocation))
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

    fun isSolved(location: Location): Boolean {
        return (location.row == xLength && location.column == yLength)
    }

    fun solve(): PuzzleResult? {
        val result : ArrayList<Movement> = ArrayList()
        val movements = movementInformation(startingLocation)
        println("Starting with location: "+ startingLocation)
//        return solvePuzzle(startingLocation, result, movements)
        return solvePuzzle2(startingLocation, movements, ArrayList(), ArrayList(), result)
    }

    tailrec private fun solvePuzzle2(location: Location, moves: ArrayList<Movement>,
                             visited: ArrayList<Location>, originatingMoves: ArrayList<Movement>,
                             result: ArrayList<Movement>) : PuzzleResult? {
        var puzzleResult : PuzzleResult? = null

        println("Processing location: "+ location)
        if(isSolved(location)) {
            return PuzzleResult.Success(result.map(Movement::direction))
        }
        if(needToGoBack(moves, location)) {
            println("----No moves for " + location)
            println("---- Before removing: "+ result)
            result.remove(result.last())
            println("---- After removing: "+ result)
            val newLocation = visited.last()
//            println("Going back to previous location: $newLocation with originating movements of : $originatingMoves")
            visited.remove(newLocation)
            return solvePuzzle2(newLocation, originatingMoves, visited, originatingMoves, result)
        }
        else {
            val nextMove = moves.first()
//            println("Next move is: "+ nextMove)
            val newLocation = travel(location, nextMove)
            println("+++++After moving new location: $newLocation with move: $nextMove")
            result.add(nextMove)
            println("+++++After moving result: "+ result)
            println("")
            visited.add(location)
            val newMoves = movementInformation(newLocation)
//            println("Moves for new location: "+ newMoves)
            moves.remove(nextMove)
            originatingMoves.addAll(moves)
//            println("Originating moves are : "+ originatingMoves)
            return solvePuzzle2(newLocation, newMoves, visited, originatingMoves, result)
        }

        return puzzleResult
    }

    private fun needToGoBack(moves: ArrayList<Movement>, currentLocation: Location): Boolean {
        if(moves.isEmpty())
            return true
        else
            return moves.first().originatingLocation != currentLocation
    }

    private fun travel(location: Location, nextMove: Movement) : Location {
        var newRow = location.row
        var newColumn = location.column
        if(nextMove.direction == PuzzleDirection.L)
            newColumn -= nextMove.distance
        if(nextMove.direction == PuzzleDirection.R)
            newColumn += nextMove.distance
        if(nextMove.direction == PuzzleDirection.U)
            newRow -= nextMove.distance
        if(nextMove.direction == PuzzleDirection.D)
            newRow += nextMove.distance

        return Location(newRow, newColumn)
    }

    private fun solvePuzzle(location: Location, result: ArrayList<PuzzleDirection>, movements: List<Movement>): PuzzleResult? {
        var puzzleResult : PuzzleResult? = null

        loop@
        for(movement in movements) {
            println("Processing movement: "+ movement)
            puzzleResult = runMove(location, result, movement)
            when(puzzleResult) {
                is PuzzleResult.Success -> break@loop
            }
        }
        return puzzleResult
    }

    private fun runMove(location: Location, result: ArrayList<PuzzleDirection>, movement: Movement): PuzzleResult? {
        if(isSolved(location))
            return PuzzleResult.Success(result)
        else
            result.add(movement.direction)
            var newRow = location.row
            var newColumn = location.column
            if(movement.direction == PuzzleDirection.R)
                newColumn += movement.distance
            if(movement.direction == PuzzleDirection.L)
                newColumn -= movement.distance
            if(movement.direction == PuzzleDirection.U)
                newRow -= movement.distance
            if(movement.direction == PuzzleDirection.D)
                newRow += movement.distance

            val newLocation = Location(newRow, newColumn)
            println("New location : "+ newLocation)
            return solvePuzzle(newLocation, result, movementInformation(newLocation))

    }

}
