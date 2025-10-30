package ru.tbank.education.school.homework

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

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
            if (!inputFile.exists() || !inputFile.isFile) {
                println("Ошибка: Входной файл не существует или не является файлом")
                return false
            }
            var lineCount = 0
            var wordCount = 0
            inputFile.bufferedReader().use { reader ->
                reader.forEachLine { line ->
                    lineCount++
                    wordCount += line.split(' ', '\t', '\n', '\r')
                        .filter { it.isNotBlank() }
                        .size
                }
            }
            File(outputFilePath).writeText("Общее количество строк: $lineCount\nОбщее количество слов: $wordCount")
            true
        } catch (e: SecurityException) {
            println("Ошибка безопасности: ${e.message}")
            false
        } catch (e: Exception) {
            println("Ошибка при обработке файла: ${e.message}")
            false
        }
    }
}
class NIOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val inputPath = Paths.get(inputFilePath)
            if (!Files.exists(inputPath) || !Files.isRegularFile(inputPath)) {
                println("Ошибка: Входной файл не существует или не является файлом")
                return false
            }
            val lines = Files.readAllLines(inputPath)
            val lineCount = lines.size
            val wordCount = lines.sumOf { line ->
                line.split(' ', '\t', '\n', '\r')
                    .filter { it.isNotBlank() }
                    .size
            }
            val outputPath = Paths.get(outputFilePath)
            Files.write(
                outputPath,
                listOf("Общее количество строк: $lineCount", "Общее количество слов: $wordCount"),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
            )
            true
        } catch (e: NoSuchFileException) {
            println("Ошибка: Файл не найден - ${e.message}")
            false
        } catch (e: AccessDeniedException) {
            println("Ошибка доступа: ${e.message}")
            false
        } catch (e: SecurityException) {
            println("Ошибка безопасности: ${e.message}")
            false
        } catch (e: Exception) {
            println("Ошибка при обработке файла: ${e.message}")
            false
        }
    }
}