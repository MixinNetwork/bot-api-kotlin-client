package one.mixin.bot.api.exception

import java.io.IOException

class ServerErrorException(val code: Int) : IOException()
