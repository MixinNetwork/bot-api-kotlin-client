package one.mixin.bot.safe

import one.mixin.bot.vo.safe.MixAddress
import one.mixin.bot.vo.safe.toMixAddress
import kotlin.test.Test

class MixAddressTest {

    @Test
    fun `test 1`() {
        val members = listOf("67a87828-18f5-46a1-b6cc-c72a97a77c43")
        val address = MixAddress.newUuidMixAddress(members, 1).toString()
        assert(address == "MIX3QEeg1WkLrjvjxyMQf6Xc8dxs81tpPc")

        val ma = "MIX3QEeg1WkLrjvjxyMQf6Xc8dxs81tpPc".toMixAddress()
        assert(ma != null)
        assert(ma!!.uuidMembers.joinToString(",") == members.joinToString(","))
        assert(ma.threshold == 1.toByte())
    }

    @Test
    fun `test 2`() {
        val members = listOf(
            "67a87828-18f5-46a1-b6cc-c72a97a77c43",
            "c94ac88f-4671-3976-b60a-09064f1811e8",
            "c6d0c728-2624-429b-8e0d-d9d19b6592fa",
            "67a87828-18f5-46a1-b6cc-c72a97a77c43",
            "c94ac88f-4671-3976-b60a-09064f1811e8",
            "c6d0c728-2624-429b-8e0d-d9d19b6592fa",
            "67a87828-18f5-46a1-b6cc-c72a97a77c43",
        )
        val address = MixAddress.newUuidMixAddress(members, 4).toString()
        assert(
            address == "MIX4fwusRK88p5GexHWddUQuYJbKMJTAuBvhudgahRXKndvaM8FdPHS2Hgeo7DQxNVoSkKSEDyZeD8TYBhiwiea9PvCzay1A9Vx1C2nugc4iAmhwLGGv4h3GnABeCXHTwWEto9wEe1MWB49jLzy3nuoM81tqE2XnLvUWv"
        )
        val ma =
            "MIX4fwusRK88p5GexHWddUQuYJbKMJTAuBvhudgahRXKndvaM8FdPHS2Hgeo7DQxNVoSkKSEDyZeD8TYBhiwiea9PvCzay1A9Vx1C2nugc4iAmhwLGGv4h3GnABeCXHTwWEto9wEe1MWB49jLzy3nuoM81tqE2XnLvUWv".toMixAddress();
        assert(ma != null)
        assert(ma!!.uuidMembers.joinToString() == members.joinToString())
    }


    @Test
    fun `test 3`() {
        val members = listOf(
            "XIN3BMNy9pQyj5XWDJtTbaBVE2zQ66zBo2weyc43iL286asdqwApWswAzQC5qba26fh3fzHK9iMoxyx1q3Lgj45KJftzGD9q"
        )
        val address = MixAddress.newMainnetMixAddress(members, 1).toString()
        assert(
            address == "MIXPYWwhjxKsbFRzAP2Dcb2mMjj7sQQo4MpCSv3NYaYCdQ2kEcbcimpPT81gaxtuNhunLWPx7Sv7fawjZ8DhRmEj8E2hrQM4Z6e"
        )
        val ma =
            "MIXPYWwhjxKsbFRzAP2Dcb2mMjj7sQQo4MpCSv3NYaYCdQ2kEcbcimpPT81gaxtuNhunLWPx7Sv7fawjZ8DhRmEj8E2hrQM4Z6e".toMixAddress();
        assert(ma != null)
        assert(ma!!.xinMembers.joinToString() == members.joinToString())
    }

    @Test
    fun `test 4`() {
        val members = listOf(
            "XINGNzunRUMmKGqDhnf1MT8tR7ek6ozg2V6dXFHCCg3tndnSRcAdzET8Fw4ktcQKshzteDmyV2RE8aFiKPz8ewrvsj3s7fvC",
            "XINMd9kCbxEoEetZuDM8gGJS11X3TVrRLwzhnqgMr65qjJBkCncNqSAngESpC7Hddnsw1D9Jo2QJakbFPr8WyrM6VkskGkB8",
            "XINLM7VuMYSjvKiEQPyLpaG7NDLDPngWWFBZpVJjhGamMsgPbmeSsGs3fQzNoqSr6syBTyLM3i69T7iSN8Tru7aQadiKLkSV",
        )
        val address = MixAddress.newMainnetMixAddress(members, 2).toString()
        assert(
            address == "MIXBCirWksVv9nuphqbtNRZZvwKsXHHMUnB5hVrVY1P7f4eBdLpDoLwiQoHYPvXia2wFepnX6hJwTjHybzBiroWVEMaFHeRFfLpcU244tzRM8smak9iRAD4PJRHN1MLHRWFtErottp9t7piaRVZBzsQXpSsaSgagj93voQdUuXhuQGZNj3Fme5YYMHfJBWjoRFHis4mnhBgxkyEGRUHAVYnfej2FhrypJmMDu74irRTdj2xjQYr6ovBJSUBYDBcvAyLPE3cEKc4JsPz7b9"
        )
        val ma =
            "MIXBCirWksVv9nuphqbtNRZZvwKsXHHMUnB5hVrVY1P7f4eBdLpDoLwiQoHYPvXia2wFepnX6hJwTjHybzBiroWVEMaFHeRFfLpcU244tzRM8smak9iRAD4PJRHN1MLHRWFtErottp9t7piaRVZBzsQXpSsaSgagj93voQdUuXhuQGZNj3Fme5YYMHfJBWjoRFHis4mnhBgxkyEGRUHAVYnfej2FhrypJmMDu74irRTdj2xjQYr6ovBJSUBYDBcvAyLPE3cEKc4JsPz7b9".toMixAddress()
        assert(ma != null)
        assert(ma!!.xinMembers.joinToString() == members.joinToString())

    }
}