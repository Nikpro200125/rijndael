import java.io.File
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)
    // Ввод пути к исходному файлу и инициализация алгоритма
    println("Введите путь к исходному файлу...")
    val cipher = Rijndael(File(scanner.next()), "a", 8, 4)
    //Ввод режима работы и запуск необходимого режима
    println("Введите режим 1 - шифрование, 2 - дешифрация...")
    when (scanner.nextInt()) {
        1    -> cipher.encode()
        2    -> cipher.decode()
        else -> println("Неверный режим!")
    }
}