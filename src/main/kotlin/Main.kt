import java.io.File
import java.util.*

fun main() {
    val scanner = Scanner(System.`in`)

    // Ввод пути к исходному файлу и инициализация алгоритма
    println("Введите путь к исходному файлу...")
    val file = File(scanner.next())

    // Ввод размера блока
    println("Введите размера блока: один из вариантов 128, 192, 256 бит")
    val Nb = scanner.nextInt()

    // Ввод размера ключа
    println("Введите размера ключа: один из вариантов 128, 192, 256 бит")
    val Nk = scanner.nextInt()

    // Ввод пароля
    println("Введите пароль...")
    val password = scanner.next()
    
    // Инициализация
    val cipher = Rijndael(file, password, Nb / 32, Nk / 32)

    //Ввод режима работы и запуск необходимого режима
    println("Введите режим 1 - шифрование, 2 - дешифрация...")
    when (scanner.nextInt()) {
        1    -> cipher.encode()
        2    -> cipher.decode()
        else -> println("Неверный режим!")
    }
}