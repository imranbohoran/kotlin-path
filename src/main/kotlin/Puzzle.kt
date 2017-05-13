import model.PuzzleGrid
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import javax.swing.JFileChooser

fun main(args: Array<String>) {
    val chosenFile = chooseFile()
    println(chosenFile ?: "No file selected")
    checkNotNull(chosenFile)
    val fileContents = String(Files.readAllBytes(Paths.get(chosenFile?.toURI())))
    val puzzleResult = PuzzleGrid(fileContents).solve()?.resultPath

    println("The Path is: $puzzleResult")
}

fun chooseFile(): File? {
    val chooser = JFileChooser()
    val fileChooser = chooser.showOpenDialog(null)
    when(fileChooser) {
        JFileChooser.APPROVE_OPTION -> return chooser.selectedFile
        JFileChooser.CANCEL_OPTION -> println("Completed")
        else -> println("Error")
    }
    return null
}
