import java.io.File
import kotlin.experimental.xor
import kotlin.system.exitProcess

// Параметры алгоритма
var Nb = 4
var Nk = 4
var Nr = 10

private val rCon = arrayOf(
    byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x04.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x08.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x10.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x20.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x40.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x80.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x1b.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x36.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x6c.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xd8.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xab.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x4d.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x9a.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x2f.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x5e.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xbc.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xc6.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x97.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x35.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x6a.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xd4.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xb3.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x7d.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xfa.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xef.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0xc5.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x91.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x39.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
    byteArrayOf(0x72.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()),
)

class Rijndael(input: File, password: String, nb: Int, nk: Int) {
    private val text: ByteArray // Исходный файл в байтах
    private val input: File // Файл ввода
    private val output: File // Файл вывода
    private val blocks = mutableListOf<Block>() // Блок в виде таблицы в байтах
    private var key: ByteArray // Ключ
    private var extendedKey: MutableList<ByteArray> = mutableListOf() // Расширенный ключ

    init {
        // Проверка размера блока
        if (nb !in listOf(4, 6, 8)) {
            print("Неверный размер блока")
            exitProcess(1)
        }
        Nb = nb

        // Проверка размера ключа
        if (nk !in listOf(4, 6, 8)) {
            print("Неверный размер ключа")
            exitProcess(6)
        }
        Nk = nk

        // Выбор количества раундов
        if (Nb == 8 || Nk == 8) {
            Nr = 14
        } else if (Nb == 6 || Nk == 6) {
            Nr = 12
        }

        // Проверка входного файла
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

        // Опустошение выходного файла
        output.writeText("")

        // Ввод исходного файла и проверка на пустоту
        text = input.readBytes()
        if (text.isEmpty()) {
            print("Входной файл пуст!")
            exitProcess(4)
        }

        // Ввод пароля и создание из него 128 bit ключа
        val tmp = ByteArray(4)
        val hashPassword = password.hashCode()
        tmp[0] = (hashPassword shr 0).toByte()
        tmp[1] = (hashPassword shr 8).toByte()
        tmp[2] = (hashPassword shr 16).toByte()
        tmp[3] = (hashPassword shr 24).toByte()
        key = ByteArray(4 * Nk)
        for (i in 0 until Nk) {
            for (j in 0 until 4) {
                key[4 * i + j] = tmp[j]
            }
        }
        
        // Генерация расщиренного ключа
        keyExpansion()

        // Разбиение текста по блокам
        for (i in text.indices step 4 * Nb) {
            blocks.add(
                Block(
                    text.sliceArray(IntRange(i, if ((i + 4 * Nb - 1) < text.size) (i + 4 * Nb - 1) else (text.size - 1))),
                    extendedKey
                )
            )
        }
    }

    // сдвиг слова по вертикали на один байт влево
    private fun rotWord(n: Int) {
        val tmp = extendedKey[0][n]
        for (i in 0 until extendedKey.size - 1) {
            extendedKey[i][n] = extendedKey[i + 1][n]
        }
        extendedKey[extendedKey.size - 1][n] = tmp
    }

    // Замена байт ключа через таблицу S-box
    private fun byteSubWord(n: Int) {
        for (i in 0 until extendedKey.size) {
            extendedKey[i][n] = search(extendedKey[i][n], 0)
        }
    }

    // Генерация ключей для всех раундов
    private fun keyExpansion() {
        repeat(4) {
            extendedKey.add(ByteArray(Nb * (Nr + 1)))
        }
        for (i in 0 until Nk) {
            for (j in 0 until 4) {
                extendedKey[j][i] = key[4 * i + j]
            }
        }
        for (i in Nk until Nb * (Nr + 1)) {
            for (j in 0 until extendedKey.size) {
                extendedKey[j][i] = extendedKey[j][i - 1]
            }
            if (i % Nk == 0) {
                rotWord(i)
                byteSubWord(i)
                for (j in 0 until extendedKey.size) {
                    extendedKey[j][i] = extendedKey[j][i - Nk].xor(extendedKey[j][i]).xor(rCon[i / Nk - 1][j])
                }
            } else if (Nk > 6 && i % Nk == 4) {
                byteSubWord(i)
                for (j in 0 until extendedKey.size) {
                    extendedKey[j][i] = extendedKey[j][i - Nk].xor(extendedKey[j][i])
                }
            } else {
                for (j in 0 until extendedKey.size) {
                    extendedKey[j][i] = extendedKey[j][i - Nk].xor(extendedKey[j][i])
                }
            }
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