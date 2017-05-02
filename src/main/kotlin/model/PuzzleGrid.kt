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

    fun movementInformation(currentLocation: Location, previousMove: Movement?,
                            failedLocations: MutableList<Location>,
                            movesToTry: MutableList<PathToExercise>): ArrayList<Movement> {
        val currentValue = valueForLocation(currentLocation)
        val movements: ArrayList<Movement> = ArrayList()

        if (canMove(R, currentLocation, currentValue, previousMove, failedLocations, movesToTry)) {
            movements.add(Movement(R, currentValue, currentLocation))
        }

        if (canMove(D, currentLocation, currentValue, previousMove, failedLocations, movesToTry)) {
            movements.add(Movement(D, currentValue, currentLocation))
        }

        if (canMove(U, currentLocation, currentValue, previousMove, failedLocations, movesToTry)) {
            movements.add(Movement(U, currentValue, currentLocation))
        }

        if (canMove(L, currentLocation, currentValue, previousMove, failedLocations, movesToTry)) {
            movements.add(Movement(L, currentValue, currentLocation))
        }

        return movements
    }

    private fun canMove(direction: PuzzleDirection, fromLocation: Location, currentValue: Int,
                        previousMove: Movement?,
                        failedLocations: MutableList<Location>,
                        movesToTry: MutableList<PathToExercise>): Boolean {
        if (currentValue == 0)
            return false

        when (direction) {
            U -> {
                if ((D == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                if (((fromLocation.row) - currentValue) <= 0)
                    return false

                val upwardLocation = Location((fromLocation.row - currentValue), fromLocation.column)
                if(failedLocations.contains(upwardLocation))
                    return false
                if (movesToTry.map { it.location }.contains(upwardLocation))
                    return false

                return true
            }
            D -> {
                if ((U == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                if (((fromLocation.row) + currentValue) > yLength)
                    return false

                val downwardLocation = Location((fromLocation.row + currentValue), fromLocation.column)
                if(failedLocations.contains(downwardLocation))
                    return false
                if (movesToTry.map { it.location }.contains(downwardLocation))
                    return false

                return true
            }
            L -> {
                if ((R == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                if (((fromLocation.column) - currentValue) <= 0)
                    return false

                val leftLocation = Location(fromLocation.row, (fromLocation.column - currentValue))
                if(failedLocations.contains(leftLocation))
                    return false
                if (movesToTry.map { it.location }.contains(leftLocation))
                    return false

                return true
            }
            R -> {
                if ((L == previousMove?.direction) && (currentValue == previousMove?.distance))
                    return false

                if (((fromLocation.column) + currentValue) > xLength)
                    return false

                val rightLocation = Location(fromLocation.row, (fromLocation.column + currentValue))
                if(failedLocations.contains(rightLocation))
                    return false
                if (movesToTry.map { it.location }.contains(rightLocation))
                    return false

                return true
            }
            else -> return false
        }
    }

    fun isSolved(location: Location): Boolean {
        return (location.row == xLength && location.column == yLength)
    }

    fun solve(): PuzzleResult? {
        val movements = movementInformation(startingLocation, null, ArrayList(), ArrayList())
        return solvePuzzle(startingLocation, movements, ArrayList(), ArrayList(), ArrayList())
    }

    tailrec fun solvePuzzle(location: Location, moves: MutableList<Movement>,
                            puzzlePath: MutableList<Path>, movesToTry: MutableList<PathToExercise>,
                            failedLocations: MutableList<Location>): PuzzleResult {
        println("--------------------------")
        println("")
        println("Processing location: (" + location.row + ", " + location.column + ") -> " + valueForLocation(location));
        println("Current path taken: "+ puzzlePath)
        println("")
        println("Failed locations: "+ failedLocations)
        println("")
        println("Moves to be tried: "+ movesToTry)
        println("")
        println("Moves for location: "+ moves)
        println("--------------------------")
        if (isSolved(location))
            return PuzzleResult.Success(puzzlePath.map(Path::movement).map(Movement::direction))
        if (noMoreMoves(movesToTry, puzzlePath))
            return PuzzleResult.Fail(puzzlePath.map(Path::movement).map(Movement::direction))
        if (needToGoBack(moves, location)) {
            println("Have to go back **********")
            println("")
            failedLocations.add(location)
            val nextPathToExercise = movesToTry.last()
            val lastPath = puzzlePath.last()
            puzzlePath.remove(lastPath)
            movesToTry.remove(nextPathToExercise)

            return solvePuzzle(nextPathToExercise.location, nextPathToExercise.moves, puzzlePath, movesToTry, failedLocations)
        } else {
            println("Processing next move....")
            val nextMove = chooseNextMove(moves, puzzlePath)
            val previousMove = if (puzzlePath.isNotEmpty()) puzzlePath.last() else null

            val newLocation = travel(location, nextMove)

            val currentPath = Path(newLocation, nextMove)
            puzzlePath.add(currentPath)

            val movesForNewLocation = movementInformation(newLocation, previousMove?.movement, failedLocations, movesToTry)
            moves.remove(nextMove)
            movesToTry.add(PathToExercise(location, moves))
            return solvePuzzle(newLocation, movesForNewLocation, puzzlePath, movesToTry, failedLocations)
        }
    }

    private fun chooseNextMove(moves: MutableList<Movement>, puzzlePath: MutableList<Path>): Movement {
        // Avoid the opposite direction of the previous move first
        val previousMove = if (puzzlePath.isNotEmpty()) puzzlePath.last() else null
        val directionsToAvoid = getDirectionsToAvoid(puzzlePath)
        println("Direction to avoid $directionsToAvoid")

        // Working out moves avoiding previous opposite directions.
        val nextPotentialMoves = moves.filterNot {
            directionsToAvoid.contains(it.direction)
        }

        val nextMove = if (nextPotentialMoves.isNotEmpty()) nextPotentialMoves.first() else moves.first()
        println(">> Previous move: $previousMove")
        println(">> Next move: $nextMove")
        return nextMove
    }

    private fun getDirectionsToAvoid(puzzlePath: MutableList<Path>): List<PuzzleDirection> {
        val avoidingList : MutableList<PuzzleDirection> = ArrayList()
        if(puzzlePath.isEmpty())
            return avoidingList

        val currentPath = puzzlePath
        val previousMove = currentPath.last()
        avoidingList.add(getOppositeDirectionFor(previousMove.movement.direction))

        val pathWithoutPrevious = currentPath.dropLast(1)

        if(pathWithoutPrevious.isEmpty())
            return avoidingList

        val moveBeforePrevious = pathWithoutPrevious.last()
        avoidingList.add(getOppositeDirectionFor(moveBeforePrevious.movement.direction))

        return avoidingList
    }

    private fun getOppositeDirectionFor(direction: PuzzleDirection) : PuzzleDirection {
        when(direction) {
            R -> return L
            L -> return R
            U -> return D
            D -> return U
        }
    }

    private fun noMoreMoves(movesToTry: MutableList<PathToExercise>, puzzlePath: MutableList<Path>): Boolean {
        return movesToTry.isEmpty() && puzzlePath.isNotEmpty()
    }

    private fun needToGoBack(moves: MutableList<Movement>, currentLocation: Location): Boolean {
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
