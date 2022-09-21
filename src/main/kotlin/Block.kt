import java.io.File
import java.util.stream.Collectors
import kotlin.experimental.xor
import kotlin.system.exitProcess

class Block(source: ByteArray, key: List<ByteArray>) {
    private var block: MutableList<ByteArray> = mutableListOf() // Блок байтов
    private var roundKey: MutableList<ByteArray> = mutableListOf() // Ключ для i-го раунда
    private var extendedKey: List<ByteArray> = mutableListOf() // Расширенный ключ

    init {
        // Дополнение массива байт до развмера блока, если не полный, и запонение блока и ключа
        val fullSource = ByteArray(4 * Nb)
        val blockSize = source.size
        source.copyInto(fullSource)
        if (blockSize > 4 * Nb) {
            print("Source size = $blockSize")
            exitProcess(5)
        }
        repeat(4) {
            block.add(ByteArray(fullSource.size / 4))
        }
        repeat(4) {
            roundKey.add(ByteArray(Nb))
        }
        for (i in source.indices) {
            block[i % 4][i / 4] = fullSource[i]
        }
        extendedKey = key
    }

    // Вывод в файл только не нулевых байт
    private fun printBlock(output: File) {
        var blockSize = 4 * Nb
        val k = ByteArray(block[0].size * block.size)
        loop@ for (i in block[0].indices) {
            for (j in block.indices) {
                if (block[j][i] == 0x00.toByte()) {
                    blockSize = block.size * i + j
                    break@loop
                }
                k[block.size * i + j] = block[j][i]
            }
        }
        output.appendBytes(k.sliceArray(IntRange(0, blockSize - 1)))
    }

    // Вывод блока в файл
    private fun printBlockFull(output: File) {
        val k = ByteArray(block[0].size * block.size)
        for (i in block[0].indices) {
            for (j in block.indices) {
                k[block.size * i + j] = block[j][i]
            }
        }
        output.appendBytes(k)
    }

    // Замена байт через таблицу S-box
    private fun byteSub() {
        for (i in 0 until block.size) {
            for (j in 1 until block[i].size) {
                block[i][j] = search(block[i][j], 0)
            }
        }
    }

    // Обратная замена байт через таблицк invS-box
    private fun invByteSub() {
        for (i in 0 until block.size) {
            for (j in 1 until block[i].size) {
                block[i][j] = search(block[i][j], 1)
            }
        }
    }

    // Сдвиг массива байт влево на 1 байт
    private fun leftShift(arr: ByteArray) {
        val tmp = arr[0]
        for (i in 0 until arr.size - 1) {
            arr[i] = arr[i + 1]
        }
        arr[arr.size - 1] = tmp
    }

    // Сдвиг массива байт вправо на 1 байт
    private fun rightShift(arr: ByteArray) {
        val tmp = arr[arr.size - 1]
        for (i in arr.size - 1 downTo 1) {
            arr[i] = arr[i - 1]
        }
        arr[0] = tmp
    }

    // Сдвиг строк блока
    private fun shiftRow() {
        for (i in 1 until block.size) {
            for (j in 0 until i) {
                leftShift(block[i])
            }
        }
    }

    // Обратное перобразование сдвига строк блока
    private fun invShiftRow() {
        for (i in 1 until block.size) {
            for (j in 0 until i) {
                rightShift(block[i])
            }
        }
    }

    // Операция смешивания блока с полиномом использую обратную линейную трансформацию
    private fun mixColumns() {
        for (i in 0 until block[0].size) {
            val a: List<Byte> = listOf(block[0][i], block[1][i], block[2][i], block[3][i])
            block[0][i] = search(a[0], 2).xor(search(a[1], 3)).xor(a[2]).xor(a[3])
            block[1][i] = a[0].xor(search(a[1], 2)).xor(search(a[2], 3)).xor(a[3])
            block[2][i] = a[0].xor(a[1]).xor(search(a[2], 2)).xor(search(a[3], 3))
            block[3][i] = search(a[0], 3).xor(a[1]).xor(a[2]).xor(search(a[3], 2))
        }
    }

    // Обратное преобразование перемещивания стобцов
    private fun invMixColumns() {
        for (i in 0 until block[0].size) {
            val a: List<Byte> = listOf(block[0][i], block[1][i], block[2][i], block[3][i])
            block[0][i] = search(a[0], 14).xor(search(a[1], 11)).xor(search(a[2], 13)).xor(search(a[3], 9))
            block[1][i] = search(a[0], 9).xor(search(a[1], 14)).xor(search(a[2], 11)).xor(search(a[3], 13))
            block[2][i] = search(a[0], 13).xor(search(a[1], 9)).xor(search(a[2], 14)).xor(search(a[3], 11))
            block[3][i] = search(a[0], 11).xor(search(a[1], 13)).xor(search(a[2], 9)).xor(search(a[3], 14))
        }
    }

    // Применение сложение по модулю блока с ключом раунда
    private fun addRoundKey(n: Int) {
        for (i in 0 until Nb) {
            for (j in 0 until roundKey.size) {
                roundKey[j][i] = extendedKey[j][Nb * n + i]
            }
        }
        for (i in block.indices) {
            for (j in block[0].indices) {
                block[i][j] = block[i][j].xor(roundKey[i][j])
            }
        }
    }

    // Применение режима CBC
    private fun applyCBC(previous: List<ByteArray>) {
        for (i in block.indices) {
            for (j in block[0].indices) {
                block[i][j] = block[i][j].xor(previous[i][j])
            }
        }
    }

    // Шифрация
    fun rijndael(output: File, previous: List<ByteArray>?): List<ByteArray> {
        if (previous != null) {
            applyCBC(previous)
        }

        addRoundKey(0)
        for (i in 1 until Nr) {
            byteSub()
            shiftRow()
            mixColumns()
            addRoundKey(i)
        }
        byteSub()
        shiftRow()
        addRoundKey(Nr)
        printBlockFull(output)
        return block
    }

    // Дешифрация
    fun deRijndael(output: File, previous: List<ByteArray>?): List<ByteArray> {
        val copyBlock = block.stream().map { it.copyOf() }.collect(Collectors.toList())
        addRoundKey(Nr)
        for (i in Nr - 1 downTo 1) {
            invShiftRow()
            invByteSub()
            addRoundKey(i)
            invMixColumns()
        }
        invShiftRow()
        invByteSub()
        addRoundKey(0)

        if (previous != null) {
            applyCBC(previous)
        }

        printBlock(output)
        return copyBlock
    }
}