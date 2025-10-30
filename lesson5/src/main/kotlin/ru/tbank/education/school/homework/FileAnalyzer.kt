package ru.tbank.education.school.homework
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.file.InvalidPathException
import java.nio.file.Paths
import java.nio.file.Files

/**
 * Интерфейс для подсчёта строк и слов в файле.
 */
interface FileAnalyzer {

    /**
     * Считает количество строк и слов в указанном входном файле и записывает результат в выходной файл.
     *
     * Словом считается последовательность символов, разделённая пробелами,
     * табуляциями или знаками перевода строки. Пустые части после разделения не считаются словами.
     *
     * @param inputFilePath путь к входному текстовому файлу
     * @param outputFilePath путь к выходному файлу, в который будет записан результат
     * @return true если операция успешна, иначе false
     */
    fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean
}

class IOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val inputFile = File(inputFilePath)
            val outputFile = File(outputFilePath)

            BufferedReader(FileReader(inputFile)).use { reader ->
                var lines = 0
                var words = 0

                reader.forEachLine { line ->
                    lines++
                    words += line.splitToSequence(Regex("\\s+"))
                        .filter { it.isNotBlank() }
                        .count()
                }

                FileWriter(outputFile).use { writer ->
                    writer.write("Общее количество строк: $lines\n")
                    writer.write("Общее количество слов: $words\n")
                }
            }
            true
        } catch (e: IOException) {
            println("Ошибка ввода-вывода: ${e.message}")
            false
        } catch (e: SecurityException) {
            println("Ошибка безопасности: ${e.message}")
            false
        } catch (e: InvalidPathException) {
            println("Некорректный путь: ${e.message}")
            false
        } catch (e: Exception) {
            println("Неизвестная ошибка: ${e.message}")
            false
        }
    }
}

class NIOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
         return try{
            val inputPath = Paths.get(inputFilePath)
            val outputPath = Paths.get(outputFilePath)
            val lines = Files.readAllLines(inputPath)
            val linesCnt = lines.size
            val wordsCnt = lines
                .flatMap{it.splitToSequence(Regex("\\s+"))}
                .count {it.isNotBlank()}
            Files.writeString(
                outputPath,
                "Общее количество строк: $linesCnt\nОбщее количество слов: $wordsCnt\n"
            )
            true
        } catch (e:OutOfMemoryError){
            println("Файл слишком большой ${e.message}")
            false
        } catch (e: InvalidPathException) {
            println("Некорректный путь: ${e.message}")
             false
        } catch (e: Exception) {
             System.err.println("Неизвестная ошибка: ${e.message}")
             false
        }
    }
}