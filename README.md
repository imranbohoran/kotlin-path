# Path finding puzzle

A path finder of a puzzle implemented in Kotlin using backtracking.

Given a grid with numbers on it, the puzzle tries to find a path that could 
be taken to reach the bottom-right cell starting from the top-left cell.

The `/src/test/resources` folder has some samples of such grids.

To run the puzzle solver, a new `PuzzleGrid` instance should be initialised with 
contents of a grid. And then `PuzzleGrid.solve()` find the best path.

`PuzzleGridTest` contains tests for the Puzzle initialisation and solve functions.

While the tests could be simply run from the IDE, they could also be run as `mvn clean test`
