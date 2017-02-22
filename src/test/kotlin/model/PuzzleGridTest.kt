package model

import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PuzzleGridTest {

    @Test
    fun gridDimensionsSetCorrectlyForSingleLineContent() : Unit {
        val grid = PuzzleGrid("1")
        assertThat(grid.xLength, CoreMatchers.`is`(1))
        assertThat(grid.yLength, CoreMatchers.`is`(1))
    }

    @Test
    fun gridDimensionsSetCorrectlyForMultiLineContent() : Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val grid = PuzzleGrid(fileContents)
        assertThat(grid.xLength, CoreMatchers.`is`(4))
        assertThat(grid.yLength, CoreMatchers.`is`(4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun failWithIllegalArgumentExceptionIfNotSquare() : Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input-invalid.txt")
                .bufferedReader()
                .use { it.readText() }
        PuzzleGrid(fileContents)
    }

    @Test
    fun populatesTheGridWithSuppliedData(): Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        assertEquals(listOf("1","2","3","4"), puzzleGrid.grid[0])
        assertEquals(listOf("3","5","2","1"), puzzleGrid.grid[1])
        assertEquals(listOf("3","6","8","9"), puzzleGrid.grid[2])
        assertEquals(listOf("2","6","1","7"), puzzleGrid.grid[3])
    }

    @Test
    fun providesTheValueForGivenCoordinatesInGrid() : Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        assertEquals(1, puzzleGrid.valueForLocation(Location(1,1)))
        assertEquals(2, puzzleGrid.valueForLocation(Location(1,2)))
        assertEquals(9, puzzleGrid.valueForLocation(Location(3,4)))
        assertEquals(2, puzzleGrid.valueForLocation(Location(2,3)))
    }

    @Test
    fun providesMovementInformationForGivenCoordinatesInGrid(): Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        assertThat(puzzleGrid.movementInformation(Location(1,1)),Matchers.containsInAnyOrder(
                Movement(PuzzleDirection.D, 1, Location(1,1)), Movement(PuzzleDirection.R, 1, Location(1,1))))

        assertThat(puzzleGrid.movementInformation(Location(3,4)),Matchers.emptyIterable())
    }

    @Test
    fun puzzleSolvedWhenLocationIsTheEnd(): Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        assertTrue(puzzleGrid.isSolved(Location(4,4)))
    }

    @Test
    fun puzzleNotSolvedWhenLocationIsNotTheEnd(): Unit {
        val fileContents = javaClass.getResourceAsStream("/test-multiline-input.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        assertFalse(puzzleGrid.isSolved(Location(3,4)))
    }

    @Test
    fun shouldSolvePuzzle(): Unit {
        val fileContents = javaClass.getResourceAsStream("/test-puzzle.txt")
                .bufferedReader()
                .use { it.readText() }
        val puzzleGrid = PuzzleGrid(fileContents)

        val puzzleResult = puzzleGrid.solve()

        assertThat(puzzleResult?.resultPath, Matchers.contains(PuzzleDirection.R, PuzzleDirection.D, PuzzleDirection.R))

    }
}
