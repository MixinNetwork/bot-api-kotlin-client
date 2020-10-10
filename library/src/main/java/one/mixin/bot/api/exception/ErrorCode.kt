package one.mixin.bot.api.exception

object ErrorCode {
    const val BAD_REQUEST = 400 // The request data has invalid field
    const val AUTHENTICATION = 401 // Authentication Failure
    const val FORBIDDEN = 403 // Access denied
    const val NOT_FOUND = 404
    const val TOO_MANY_REQUEST = 429 // Too many request
    const val SERVER = 500

    const val TRANSACTION = 10001
    const val BAD_DATA = 10002 // The request data has invalid field
    const val PHONE_SMS_DELIVERY = 10003 // Failed to deliver SMS
    const val RECAPTCHA_IS_INVALID = 10004
    const val NEED_RECAPTCHA = 10005
    const val OLD_VERSION = 10006 // Outdated version
    const val PHONE_INVALID_FORMAT = 20110
    const val INSUFFICIENT_IDENTITY_NUMBER = 20111
    const val INVALID_INVITATION_CODE = 20112
    const val PHONE_VERIFICATION_CODE_INVALID = 20113
    const val PHONE_VERIFICATION_CODE_EXPIRED = 20114
    const val INVALID_QR_CODE = 20115
    const val GROUP_CHAT_FULL = 20116
    const val INSUFFICIENT_BALANCE = 20117 // Insufficient balance
    const val INVALID_PIN_FORMAT = 20118 // Invalid PIN format
    const val PIN_INCORRECT = 20119 // PIN incorrect
    const val TOO_SMALL = 20120 // The amount is too small
    const val USED_PHONE = 20122
    const val INSUFFICIENT_TRANSACTION_FEE = 20124
    const val TOO_MANY_STICKERS = 20126
    const val WITHDRAWAL_AMOUNT_SMALL = 20127
    const val INVALID_CODE_TOO_FREQUENT = 20129
    const val INVALID_EMERGENCY_CONTACT = 20130
    const val WITHDRAWAL_MEMO_FORMAT_INCORRECT = 20131
    const val FAVORITE_LIMIT = 20132
    const val CIRCLE_LIMIT = 20133
    const val CONVERSATION_CHECKSUM_INVALID_ERROR = 20140
    const val BLOCKCHAIN_ERROR = 30100
    const val INVALID_ADDRESS = 30102
}