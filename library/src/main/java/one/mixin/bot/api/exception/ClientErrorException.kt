package one.mixin.bot.api.exception;

import java.io.IOException

class ClientErrorException(val code: Int) : IOException()