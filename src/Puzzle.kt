import java.io.File
import javax.swing.JFileChooser

fun main(args: Array<String>) {
    val chosenFile = chooseFile()
    println(chosenFile ?: "No file selected")
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
