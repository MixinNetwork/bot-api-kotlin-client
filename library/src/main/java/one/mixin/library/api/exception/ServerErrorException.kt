package one.mixin.library.api.exception

import java.io.IOException

class ServerErrorException(val code: Int) : IOException()