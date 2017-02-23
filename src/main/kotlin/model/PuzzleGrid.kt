package model

import model.PuzzleDirection.*
import java.util.*

class PuzzleGrid(contents: String) {

    val grid = mutableMapOf<Int, List<String>>()
    var xLength: Int
    var yLength: Int

    val startingLocation: Location = Location(1, 1)

    init {
        val allContent = contents.trim().lines()
        var index: Int = 0
        allContent.map { line ->
            val values = line.split(",")
            if (values.size != allContent.size) {
                throw IllegalArgumentException("Supplied data does not form a square")
            }
            grid.put(index, values)
            index++
        }
        xLength = allContent.size
        yLength = allContent.size
    }

    fun valueForLocation(location: Location): Int {
        return grid[location.row - 1]?.get(location.column - 1)?.toInt()!!
    }

    fun movementInformation(currentLocation: Location, previousMove: Movement?): ArrayList<Movement> {
        val currentValue = valueForLocation(currentLocation)
        val movements: ArrayList<Movement> = ArrayList()

        if (canMove(R, currentLocation, currentValue, previousMove)) {
            movements.add(Movement(R, currentValue, currentLocation))
        }

        if (canMove(D, currentLocation, currentValue, previousMove)) {
            movements.add(Movement(D, currentValue, currentLocation))
        }

        if (canMove(U, currentLocation, currentValue, previousMove)) {
            movements.add(Movement(U, currentValue, currentLocation))
        }

        if (canMove(L, currentLocation, currentValue, previousMove)) {
            movements.add(Movement(L, currentValue, currentLocation))
        }

        return movements
    }

    private fun canMove(direction: PuzzleDirection, location: Location, currentValue: Int, previousMove: Movement?): Boolean {
        if (currentValue == 0)
            return false

        when (direction) {
            U -> {
                if ((D == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                return ((location.row - 1) - currentValue) > 0
            }
            D -> {
                if ((U == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                return ((location.row - 1) + currentValue) < yLength
            }
            L -> {
                if ((R == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                return ((location.column - 1) - currentValue) >= 0
            }
            R -> {
                if ((L == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                return ((location.column - 1) + currentValue) < xLength
            }
            else -> return false
        }
    }


    fun isSolved(location: Location): Boolean {
        return (location.row == xLength && location.column == yLength)
    }

    fun solve(): PuzzleResult? {
        val movements = movementInformation(startingLocation, null)
        return solvePuzzle(startingLocation, movements, ArrayList(), ArrayList())
    }

    tailrec fun solvePuzzle(location: Location, moves: ArrayList<Movement>,
                            puzzlePath: ArrayList<Path>, movesToTry: ArrayList<PathToExercise>): PuzzleResult {
        println("")
        println("Processing location: (" + location.row + ", " + location.column + ") -> " + valueForLocation(location));
        println("Current path taken: "+ puzzlePath)
        println("")
        println("Moves to be tried: "+ movesToTry)
        println("--------------------------")
        if (isSolved(location))
            return PuzzleResult.Success(puzzlePath.map(Path::movement).map(Movement::direction))
        if (noMoreMoves(movesToTry, puzzlePath))
            return PuzzleResult.Fail(puzzlePath.map(Path::movement).map(Movement::direction))
        if (needToGoBack(moves, location)) {
            println("Have to go back **********")
            val nextPathToExercise = movesToTry.last()
            // reverse
            val lastPath = puzzlePath.last()
            puzzlePath.remove(lastPath)
            val reverseMove = Movement(getReverseMove(lastPath.movement.direction), lastPath.movement.distance, lastPath.location)
            puzzlePath.add(Path(nextPathToExercise.location, reverseMove))
            movesToTry.remove(nextPathToExercise)

            return solvePuzzle(nextPathToExercise.location, nextPathToExercise.moves, puzzlePath, movesToTry)
        } else {
            val nextMove = chooseNextMove(moves, puzzlePath)
            val previousMove = if (puzzlePath.isNotEmpty()) puzzlePath.last() else null

            val newLocation = travel(location, nextMove)

            val currentPath = Path(newLocation, nextMove)
            puzzlePath.add(currentPath)

            val movesForNewLocation = movementInformation(newLocation, previousMove?.movement)
            moves.remove(nextMove)
            movesToTry.add(PathToExercise(location, moves))
            return solvePuzzle(newLocation, movesForNewLocation, puzzlePath, movesToTry)
        }
    }

    private fun getReverseMove(direction: PuzzleDirection): PuzzleDirection {
        val reverseDirection = when (direction) {
            U -> D
            D -> U
            L -> R
            R -> L
        }
        return reverseDirection
    }

    private fun chooseNextMove(moves: ArrayList<Movement>, puzzlePath: ArrayList<Path>): Movement {
        // Avoid the opposite direction of the previous move first
        val previousMove = if (puzzlePath.isNotEmpty()) puzzlePath.last() else null
        val avoidDirection = getDirectionToAvoid(previousMove)
        println("Direction to avoid $avoidDirection")
        val nextPotentialMoves = moves.filter {
            it.direction != avoidDirection
        }
        val nextMove = if (nextPotentialMoves.isNotEmpty()) nextPotentialMoves.first() else moves.first()
        println(">> Previous move: $previousMove")
        println(">> Next move: $nextMove")
        return nextMove
    }

    private fun getDirectionToAvoid(previousMove: Path?): PuzzleDirection? {
        when (previousMove?.movement?.direction) {
            R -> return L
            L -> return R
            U -> return D
            D -> return U
        }
        return null
    }

    private fun noMoreMoves(movesToTry: ArrayList<PathToExercise>, puzzlePath: ArrayList<Path>): Boolean {
        return movesToTry.isEmpty() && puzzlePath.isNotEmpty()
    }

    private fun needToGoBack(moves: ArrayList<Movement>, currentLocation: Location): Boolean {
        if (moves.isEmpty())
            return true
        else
            return moves.first().originatingLocation != currentLocation
    }

    private fun travel(location: Location, nextMove: Movement): Location {
        var newRow = location.row
        var newColumn = location.column
        if (nextMove.direction == L)
            newColumn -= nextMove.distance
        if (nextMove.direction == R)
            newColumn += nextMove.distance
        if (nextMove.direction == U)
            newRow -= nextMove.distance
        if (nextMove.direction == D)
            newRow += nextMove.distance

        return Location(newRow, newColumn)
    }
}
