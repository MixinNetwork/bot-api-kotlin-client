package one.mixin.bot.safe.tx

import com.ionspin.kotlin.bignum.integer.BigInteger
import okio.Buffer
import one.mixin.bot.extension.hexStringToByteArray
import one.mixin.bot.extension.toHex
import one.mixin.bot.util.toLeByteArray

private val magic = ByteArray(2) { 0x77 }
private val empty = ByteArray(2) { 0x00 }

private const val ExtraSizeStorageCapacity = 1024 * 1024 * 4

class Encoder {

    private val buffer = Buffer()

    private fun write(array: ByteArray) {
        buffer.write(array)
    }

    fun writeUint16(value: Int) {
        buffer.writeShort(value)
    }

    private fun writeUint32(value: Int) {
        buffer.writeInt(value)
    }

    private fun writeUint64(value: ULong) {
        buffer.writeLong(value.toLong())
    }

    private fun writeBigInteger(value: BigInteger) {
        val bytes = value.toByteArray()
        writeUint16(bytes.size)
        write(bytes)
    }

    fun encodeTransaction(tx: Transaction) {
        write(magic)
        write(byteArrayOf(0x00, tx.version))

        write(tx.asset.hexStringToByteArray())

        writeUint16(tx.inputs.size)
        for (input in tx.inputs) {
            encodeInput(input)
        }

        writeUint16(tx.outputs.size)
        for (output in tx.outputs) {
            encodeOutput(output)
        }

        writeUint16(tx.reference.size)
        for (reference in tx.reference) {
            write(reference.hexStringToByteArray())
        }

        val extra = tx.extra.toByteArray()
        require(extra.size <= ExtraSizeStorageCapacity) { "extra is too long" }
        writeUint32(extra.size)
        write(extra)

    }

    private fun encodeInput(i: Input) {
        require(i.index <= 1024) { "index is too large" }

        write(i.hash.hexStringToByteArray())
        writeUint16(i.index)

        val genesis = i.genesis?.toByteArray() ?: ByteArray(0)
        writeUint16(genesis.size)
        write(genesis)

        if (i.deposit == null) {
            write(empty)
        } else {
            val d = i.deposit

            write(magic)
            write(d.chain.hexStringToByteArray())

            d.assetKey.toByteArray().let {
                writeUint16(it.size)
                write(it)
            }

            d.transaction.toByteArray().let {
                writeUint16(it.size)
                write(it)
            }

            writeUint64(d.index)
            writeBigInteger(d.amount)

        }

        if (i.mint == null) {
            write(empty)
        } else {
            write(magic)

            i.mint.group.toByteArray().let {
                writeUint16(it.size)
                write(it)
            }

            writeUint64(i.mint.batch)
            writeBigInteger(i.mint.amount)
        }

    }

    private fun encodeOutput(output: Output) {
        write(byteArrayOf(0x00, output.type.value.toByte()))
        writeBigInteger(output.amount)

        writeUint16(output.keys.size)
        for (key in output.keys) {
            write(key.hexStringToByteArray())
        }

        write(output.mask?.hexStringToByteArray() ?: ByteArray(32))

        val script = output.script?.hexStringToByteArray() ?: ByteArray(0)
        writeUint16(script.size)
        write(script)

        if (output.withdrawal == null) {
            write(empty)
        } else {
            val w = output.withdrawal

            write(magic)

            w.address.toByteArray().let {
                writeUint16(it.size)
                write(it)
            }

            w.tag.toByteArray().let {
                writeUint16(it.size)
                write(it)
            }
        }
    }

    fun toHexString(): String {
        return buffer.snapshot().hex()
    }

    fun encodeSignature(sig: Map<Int, String>) {
        val ss = sig.entries.toList().sortedBy { it.key }
        writeUint16(ss.size)
        for (s in ss) {
            writeUint16(s.key)
            write(s.value.hexStringToByteArray())
        }
    }

}