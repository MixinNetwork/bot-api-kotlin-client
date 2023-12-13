package jvmMain.kotlin

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.calculateAgreement
import one.mixin.bot.util.decryptPinToken
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.newKeyPairFromPrivateKey
import one.mixin.bot.util.privateKeyToCurve25519

fun main() =
    runBlocking {
        val keyPair = newKeyPairFromPrivateKey(Config.privateKey.base64Decode())
        val pinToken = decryptPinToken(Config.pinTokenPem.base64Decode(), keyPair.privateKey)
        val client =
            HttpClient.Builder().useCNServer().configSafeUser(Config.userId, Config.sessionId, keyPair.privateKey).build()

        // create alice keys
        val aliceSessionKey = generateEd25519KeyPair()
        val aliceSessionSecret = aliceSessionKey.publicKey.base64Encode()

        // create alice
        val alice = createUser(client, aliceSessionSecret)
        alice ?: return@runBlocking
        println("alice: $alice")
        val aliceClient =
            HttpClient.Builder().useCNServer().configSafeUser(alice.userId, alice.sessionId, aliceSessionKey.privateKey).build()
        // decrypt pin token
        val aliceAesKey = calculateAgreement(alice.pinToken.base64Decode(), privateKeyToCurve25519(aliceSessionKey.privateKey))
        // create alice's pin
        createPin(aliceClient, aliceAesKey)

        // create bob keys
        val bobSessionKey = generateEd25519KeyPair()
        val bobSessionSecret = bobSessionKey.publicKey.base64Encode()

        // create bob
        val bob = createUser(client, bobSessionSecret)
        bob ?: return@runBlocking
        println("bob: $bob")
        val bobClient =
            HttpClient.Builder().useCNServer().configSafeUser(bob.userId, bob.sessionId, bobSessionKey.privateKey).build()
        // decrypt pin token
        val bobAesKey = calculateAgreement(bob.pinToken.base64Decode(), privateKeyToCurve25519(bobSessionKey.privateKey))
        // create bob's pin
        createPin(bobClient, bobAesKey)

        // bot transfer to alice
        val snapshotBot2Alice = transferToUser(client, alice.userId, pinToken, Config.pin)

        delay(4000)

        if (snapshotBot2Alice != null) {
            // alice check transfer
            networkSnapshot(aliceClient, snapshotBot2Alice.snapshotId)

            val aliceNetworkSnapshots = networkSnapshots(aliceClient, CNB_ID, limit = 5)
            assert(aliceNetworkSnapshots?.find { it.snapshotId == snapshotBot2Alice.snapshotId } != null)
        }

        // alice transfer to bob
        val snapshotAlice2Bob = transferToUser(aliceClient, bob.userId, aliceAesKey, DEFAULT_PIN)

        delay(4000)

        if (snapshotAlice2Bob != null) {
            networkSnapshot(bobClient, snapshotAlice2Bob.snapshotId)

            val bobNetworkSnapshots = networkSnapshots(client, CNB_ID, limit = 5)
            assert(bobNetworkSnapshots?.find { it.snapshotId == snapshotAlice2Bob.snapshotId } != null)
        }

        val botNetworkSnapshot = networkSnapshots(client, CNB_ID, limit = 10)
        if (snapshotBot2Alice != null) {
            assert(botNetworkSnapshot?.find { it.snapshotId == snapshotBot2Alice.snapshotId } != null)
        }
        if (snapshotAlice2Bob != null) {
            assert(botNetworkSnapshot?.find { it.snapshotId == snapshotAlice2Bob.snapshotId } != null)
        }
    }
