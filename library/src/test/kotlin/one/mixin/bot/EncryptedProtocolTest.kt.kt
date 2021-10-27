package one.mixin.bot

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.i2p.crypto.eddsa.EdDSAPrivateKey
import net.i2p.crypto.eddsa.EdDSAPublicKey
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.extension.toByteArray
import one.mixin.bot.util.*
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class EncryptedProtocolTest {

    @Test
    fun testText() {
        val content = "L".toByteArray()
        testEncryptAndDecrypt(content)
    }

    @Test
    fun testSticker() {
        val mockStickerId = UUID.randomUUID().toString()
        val mockStickerPayload = StickerMessagePayload(mockStickerId)
        val content = Gson().toJson(mockStickerPayload).base64Encode().toByteArray()
        testEncryptAndDecrypt(content)
    }

    @Test
    fun testAes() {
        val content = "LA".toByteArray()
        val aesGcmKey = generateAesKey()
        val encodedContent = aesEncrypt(aesGcmKey, content)
        val decryptedContent = aesDecrypt(
            aesGcmKey,
            encodedContent.slice(IntRange(0, 15)).toByteArray(),
            encodedContent.slice(IntRange(16, encodedContent.size - 1)).toByteArray(),
        )
        assertEquals("LA", String(decryptedContent))
    }

    @Test
    fun testImage() {
        val mockAttachmentMessagePayload = AttachmentMessagePayload(
            key = base64Decode("2IFv82k/nPZJlQFYRCD7SgWNtDK+Bi5vo0VXhk4A9DAp/RE5r+Shfgn+xEuQiyn8Hjf+Ox9356geoceH926BJQ=="),
            digest = base64Decode("z9YuqavioY+hYLB1slFaRzSc9ggBlp+nUOZGHwS8LaU="),
            attachmentId = "5a3574ca-cc17-470d-88dc-845613d471b4",
            mimeType = "image/jpeg",
            height = 949,
            width = 1080,
            size = 168540,
            name = null,
            thumbnail = """/9j/4AAQSkZJRgABAQAAAQABAAD/4gIoSUNDX1BST0ZJTEUAAQEAAAIYAAAAAAIQAABtbnRyUkdCIFhZWiAAAAAAAAAAAAAAAABhY3
                NwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQAA9tYAAQAAAADTLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                lkZXNjAAAA8AAAAHRyWFlaAAABZAAAABRnWFlaAAABeAAAABRiWFlaAAABjAAAABRyVFJDAAABoAAAAChnVFJDAAABoAAAAChiVFJDAAABoAAAACh3dHB0AAABy
                AAAABRjcHJ0AAAB3AAAADxtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAFgAAAAcAHMAUgBHAEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
                AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFhZWiAAAAAAAABvogAAOPUAAAOQWFlaIAAAAAAAAGKZAAC3hQAAGNpYWVogAAAAAAAAJKAAAA
                +EAAC2z3BhcmEAAAAAAAQAAAACZmYAAPKnAAANWQAAE9AAAApbAAAAAAAAAABYWVogAAAAAAAA9tYAAQAAAADTLW1sdWMAAAAAAAAAAQAAAAxlblVTAAAAIAAAABwA
                RwBvAG8AZwBsAGUAIABJAG4AYwAuACAAMgAwADEANv/bAEMAAwICAwICAwMDAwQDAwQFCAUFBAQFCgcHBggMCgwMCwoLCw0OEhANDhEOCwsQFhARExQVFRUMDxcYFh
                QYEhQVFP/bAEMBAwQEBQQFCQUFCRQNCw0UFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFBQUFP/AABEIADcAPwMBIgACEQEDEQH/
                xAAbAAACAwEBAQAAAAAAAAAAAAAFBgAECAMHAf/EACsQAAEDAwIEBgIDAAAAAAAAAAEAAgMEBREGEgchMZETFCIyUaEjQRYzQ//EABkBAAMBAQEAAAAAAAAAAAAAAA
                IDBAEABf/EAB4RAAICAwEAAwAAAAAAAAAAAAABAhEDEiExBDJR/9oADAMBAAIRAxEAPwABWcHGsB/GOyXLjwl2ZwwdlpG4VMDwdu1LVfGyQnACli2PcfwzhW8L3NziM
                dkKm4YSHpF9LRFVbml3QKq22sz7QjcqDWK1ZnaThdLn+n6VeThRLJ/iey0xHa4nOGWDsilHYqZ5G6NvZEm2IlGjO2kOFUtPVsd4JGD8LWvCuwvttExpaR6cLlZrFRR
                vB2NXoVn8vSxANwOSkzp0FDh4NS3h1UB6lZMpPPckS3XYQt5uVt+pmtB9f2mvjH3rGxnqJOR5obJVFruTkCl1IDETvQiq1Mxp96H7CsefaWo7w3N7D1XV+qvKjm4DC
                8/GqWBmd4SpqLWfhhwD/tPhJeFeXG6s9pp+JLYZAN47pio+JzS0fk/XysjN1o51R7/38or/ADt8IHrPT5Q5e8IfBnqbm6EHBKXqnUUjHO9RRiekfKDyKWbpbJGhxw
                UxxsO7jR0fql/hEb/tBazUUrskOKpupJXPI5q7DYHzR5IWKFEsFrKwfJqmWNhG4pbu+oZKgn1FH7tp18TTyStVWh4kxhCoU7PTl8jaNFWC4yCTOSrc1zkf0X2Gzu
                B9qsstbt3RH6Q+mmpraIAcgJZu8LTuGAoojTDfBd8g0y5wEWpYWsYBhRRaKKV2p2SM6JPrbe3xeiiixnHyGhbu6BdPIgSHkFFEs4//2Q==""".trimMargin()
        )
        val content = Gson().toJson(mockAttachmentMessagePayload).toByteArray()
        testEncryptAndDecrypt(content)
    }

    private fun testEncryptAndDecrypt(content: ByteArray) {
        val otherSessionId = UUID.randomUUID().toString()
        val encryptedProtocol = EncryptedProtocol()

        val senderKeyPair = generateEd25519KeyPair()
        val senderPrivateKey = senderKeyPair.private as EdDSAPrivateKey

        val receiverKeyPair = generateEd25519KeyPair()
        val receiverPrivateKey = receiverKeyPair.private as EdDSAPrivateKey
        val receiverPublicKey = receiverKeyPair.public as EdDSAPublicKey
        val receiverCurvePublicKey = publicKeyToCurve25519(receiverPublicKey)

        val encodedContent = encryptedProtocol.encryptMessage(senderPrivateKey, content, receiverCurvePublicKey, otherSessionId)

        val decryptedContent = encryptedProtocol.decryptMessage(receiverPrivateKey, UUID.fromString(otherSessionId).toByteArray(), encodedContent)

        assert(decryptedContent.contentEquals(content))
    }

    @Test
    fun calculateAgreement() {
        val senderKeyPair = generateEd25519KeyPair()
        val senderPrivateKey = senderKeyPair.private as EdDSAPrivateKey
        val senderPublicKey = senderKeyPair.public as EdDSAPublicKey

        val receiverKeyPair = generateEd25519KeyPair()
        val receiverPrivateKey = receiverKeyPair.private as EdDSAPrivateKey
        val receiverPublicKey = receiverKeyPair.public as EdDSAPublicKey

        val senderPrivate = privateKeyToCurve25519(senderPrivateKey.seed)
        val senderSecret =
            calculateAgreement(publicKeyToCurve25519(receiverPublicKey), senderPrivate)

        val receiverPrivate = privateKeyToCurve25519(receiverPrivateKey.seed)
        val receiverSecret =
            calculateAgreement(publicKeyToCurve25519(senderPublicKey), receiverPrivate)

        assert(senderSecret.contentEquals(receiverSecret))
    }
}

data class StickerMessagePayload(
    @SerializedName("sticker_id")
    val stickerId: String? = null,
    @SerializedName("album_id")
    val albumId: String? = null,
    @SerializedName("name")
    val name: String? = null
)

data class AttachmentMessagePayload(
    @SerializedName("key")
    var key: ByteArray?,
    @SerializedName("digest")
    var digest: ByteArray?,
    @SerializedName("attachment_id")
    var attachmentId: String,
    @SerializedName("mime_type")
    var mimeType: String,
    @SerializedName("size")
    var size: Long,
    @SerializedName("name")
    var name: String?,
    @SerializedName("width")
    var width: Int?,
    @SerializedName("height")
    var height: Int?,
    @SerializedName("thumbnail")
    var thumbnail: String?,
    @SerializedName("duration")
    var duration: Long? = null,
    @SerializedName("waveform")
    var waveform: ByteArray? = null,
    @SerializedName("caption")
    var caption: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AttachmentMessagePayload

        if (key != null) {
            if (other.key == null) return false
            if (!key.contentEquals(other.key)) return false
        } else if (other.key != null) return false
        if (digest != null) {
            if (other.digest == null) return false
            if (!digest.contentEquals(other.digest)) return false
        } else if (other.digest != null) return false
        if (attachmentId != other.attachmentId) return false
        if (mimeType != other.mimeType) return false
        if (size != other.size) return false
        if (name != other.name) return false
        if (width != other.width) return false
        if (height != other.height) return false
        if (thumbnail != other.thumbnail) return false
        if (duration != other.duration) return false
        if (waveform != null) {
            if (other.waveform == null) return false
            if (!waveform.contentEquals(other.waveform)) return false
        } else if (other.waveform != null) return false
        if (caption != other.caption) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = key?.contentHashCode() ?: 0
        result = 31 * result + (digest?.contentHashCode() ?: 0)
        result = 31 * result + attachmentId.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (width ?: 0)
        result = 31 * result + (height ?: 0)
        result = 31 * result + (thumbnail?.hashCode() ?: 0)
        result = 31 * result + (duration?.hashCode() ?: 0)
        result = 31 * result + (waveform?.contentHashCode() ?: 0)
        result = 31 * result + (caption?.hashCode() ?: 0)
        result = 31 * result + (createdAt?.hashCode() ?: 0)
        return result
    }
}
