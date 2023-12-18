package one.mixin.bot.safe

import one.mixin.bot.extension.stripAmountZero
import one.mixin.bot.util.sha256

object TipBody {
    private const val TIPVerify = "TIP:VERIFY:"
    private const val TIPAddressAdd = "TIP:ADDRESS:ADD:"
    private const val TIPAddressRemove = "TIP:ADDRESS:REMOVE:"
    private const val TIPUserDeactivate = "TIP:USER:DEACTIVATE:"
    private const val TIPEmergencyContactCreate = "TIP:EMERGENCY:CONTACT:CREATE:"
    private const val TIPEmergencyContactRead = "TIP:EMERGENCY:CONTACT:READ:"
    private const val TIPEmergencyContactRemove = "TIP:EMERGENCY:CONTACT:REMOVE:"
    private const val TIPPhoneNumberUpdate = "TIP:PHONE:NUMBER:UPDATE:"
    private const val TIPMultisigRequestSign = "TIP:MULTISIG:REQUEST:SIGN:"
    private const val TIPMultisigRequestUnlock = "TIP:MULTISIG:REQUEST:UNLOCK:"
    private const val TIPCollectibleRequestSign = "TIP:COLLECTIBLE:REQUEST:SIGN:"
    private const val TIPCollectibleRequestUnlock = "TIP:COLLECTIBLE:REQUEST:UNLOCK:"
    private const val TIPTransferCreate = "TIP:TRANSFER:CREATE:"
    private const val TIPWithdrawalCreate = "TIP:WITHDRAWAL:CREATE:"
    private const val TIPRawTransactionCreate = "TIP:TRANSACTION:CREATE:"
    private const val TIPOAuthApprove = "TIP:OAUTH:APPROVE:"
    private const val TIPProvisioningCreate = "TIP:PROVISIONING:UPDATE:"
    private const val TIPBodyForSequencerRegister = "SEQUENCER:REGISTER:"

    @JvmStatic
    fun forVerify(timestamp: Long): ByteArray = String.format("%s%032d", TIPVerify, timestamp).toByteArray()

    @JvmStatic
    fun forRawTransactionCreate(
        assetId: String,
        opponentKey: String,
        opponentReceivers: List<String>,
        opponentThreshold: Int,
        amount: String,
        traceId: String?,
        memo: String?,
    ): ByteArray {
        var body = assetId + opponentKey
        body += opponentReceivers.joinToString(separator = "")
        body = body + opponentThreshold + amount.stripAmountZero() + (traceId ?: "") + (memo ?: "")
        return (TIPRawTransactionCreate + body).hashToBody()
    }

    @JvmStatic
    fun forWithdrawalCreate(
        addressId: String,
        amount: String,
        fee: String?,
        traceId: String,
        memo: String?,
    ): ByteArray =
        (TIPWithdrawalCreate + addressId + amount.stripAmountZero() + (fee?.stripAmountZero() ?: "") + traceId + (memo ?: "")).hashToBody()

    @JvmStatic
    fun forTransfer(
        assetId: String,
        counterUserId: String,
        amount: String,
        traceId: String?,
        memo: String?,
    ): ByteArray {
        return (TIPTransferCreate + assetId + counterUserId + amount.stripAmountZero() + (traceId ?: "") + (memo ?: "")).hashToBody()
    }

    @JvmStatic
    fun forPhoneNumberUpdate(
        verificationId: String,
        code: String,
    ): ByteArray = (TIPPhoneNumberUpdate + verificationId + code).hashToBody()

    @JvmStatic
    fun forEmergencyContactCreate(
        verificationId: String,
        code: String,
    ): ByteArray = (TIPEmergencyContactCreate + verificationId + code).hashToBody()

    @JvmStatic
    fun forAddressAdd(
        assetId: String,
        publicKey: String?,
        keyTag: String?,
        name: String?,
    ): ByteArray = (TIPAddressAdd + assetId + (publicKey ?: "") + (keyTag ?: "") + (name ?: "")).hashToBody()

    @JvmStatic
    fun forAddressRemove(addressId: String): ByteArray = (TIPAddressRemove + addressId).hashToBody()

    @JvmStatic
    fun forUserDeactivate(phoneVerificationId: String): ByteArray = (TIPUserDeactivate + phoneVerificationId).hashToBody()

    @JvmStatic
    fun forEmergencyContactRead(): ByteArray = (TIPEmergencyContactRead + "0").hashToBody()

    @JvmStatic
    fun forEmergencyContactRemove(): ByteArray = (TIPEmergencyContactRemove + "0").hashToBody()

    @JvmStatic
    fun forMultisigRequestSign(requestId: String): ByteArray = (TIPMultisigRequestSign + requestId).hashToBody()

    @JvmStatic
    fun forMultisigRequestUnlock(requestId: String): ByteArray = (TIPMultisigRequestUnlock + requestId).hashToBody()

    @JvmStatic
    fun forCollectibleRequestSign(requestId: String): ByteArray = (TIPCollectibleRequestSign + requestId).hashToBody()

    @JvmStatic
    fun forCollectibleRequestUnlock(requestId: String): ByteArray = (TIPCollectibleRequestUnlock + requestId).hashToBody()

    @JvmStatic
    fun forOAuthApprove(authorizationId: String): ByteArray = (TIPOAuthApprove + authorizationId).hashToBody()

    @JvmStatic
    fun forProvisioningCreate(
        id: String,
        secret: String,
    ): ByteArray = (TIPProvisioningCreate + id + secret).hashToBody()

    @JvmStatic
    fun forSequencerRegister(
        userId: String,
        publicKey: String,
    ): ByteArray = (TIPBodyForSequencerRegister + userId + publicKey).hashToBody()

    private fun String.hashToBody() = sha256()
}
