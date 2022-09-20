import java.io.File
import kotlin.system.exitProcess

// Константы
const val Nb = 4
const val Nk = 4
const val Nr = 10

class Rijndael(input: File, password: String) {
    val text: ByteArray // Исходный файл в байтах
    val input: File // Файл ввода
    val output: File // Файл вывода
    val blocks = mutableListOf<Block>() // Блок в виде таблицы в байтах
    var key = ByteArray(4 * Nk) // Ключ

    init {
        // Проверка входногл файла
        this.input = input
        if (!input.exists() || !input.isFile || !input.canRead()) {
            print("Файл не существует или у вас недостаточно прав!")
            exitProcess(2)
        }
        // Проверка второго файла
        this.output = File(input.parent + "\\out." + input.name)
        if (!output.exists() && !output.createNewFile() || !output.canWrite()) {
            print("Не удалось создать выходной файл!")
            exitProcess(3)
        }
        // Опустошение файла
        output.writeText("")

        // Ввод исходного файла и проверка на пустоту
        text = input.readBytes()
        if (text.isEmpty()) {
            print("Входной файл пуст!")
            exitProcess(4)
        }

        // Ввод пароля и создание из него 128 bit ключа
        println("Введите пароль...")
        val tmp = ByteArray(4)
        val hashPassword = password.hashCode()
        tmp[0] = (hashPassword shr 0).toByte()
        tmp[1] = (hashPassword shr 8).toByte()
        tmp[2] = (hashPassword shr 16).toByte()
        tmp[3] = (hashPassword shr 24).toByte()
        for (i in 0 until Nk) {
            for (j in 0 until 4) {
                key[4 * i + j] = tmp[j]
            }
        }
        // Разбиение текста по блокам
        for (i in text.indices step 4 * Nb) {
            blocks.add(
                Block(
                    text.sliceArray(
                        IntRange(
                            i,
                            if ((i + 4 * Nb - 1) < text.size) (i + 4 * Nb - 1) else (text.size - 1)
                        )
                    ), key
                )
            )
        }
    }

    // Шифрование блоков
    fun encode() {
        var previous: List<ByteArray>? = null
        for (i in blocks) {
            previous = i.rijndael(output, previous)
        }
    }

    // Дешифрация блоков
    fun decode() {
        var previous: List<ByteArray>? = null
        for (i in blocks) {
            previous = i.deRijndael(output, previous)
        }
    }
}