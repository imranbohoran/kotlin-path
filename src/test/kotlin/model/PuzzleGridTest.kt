package model

import org.hamcrest.CoreMatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test

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

        assertEquals(1, puzzleGrid.valueForLocation(Location(0,0)))
        assertEquals(6, puzzleGrid.valueForLocation(Location(1,2)))
        assertEquals(9, puzzleGrid.valueForLocation(Location(3,2)))
        assertEquals(1, puzzleGrid.valueForLocation(Location(2,3)))
    }
}
