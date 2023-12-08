package jvmMain.kotlin

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import one.mixin.bot.HttpClient
import one.mixin.bot.SessionToken
import one.mixin.bot.extension.base64Decode
import one.mixin.bot.extension.base64Encode
import one.mixin.bot.util.calculateAgreement
import one.mixin.bot.util.decryASEKey
import one.mixin.bot.util.generateEd25519KeyPair
import one.mixin.bot.util.newKeyPairFromPrivateKey
import one.mixin.bot.util.privateKeyToCurve25519

fun main() = runBlocking {
    val keyPair = newKeyPairFromPrivateKey(Config.privateKey.base64Decode())
    val pinToken = decryASEKey(Config.pinTokenPem, keyPair.privateKey) ?: return@runBlocking
    val client =
        HttpClient.Builder().useCNServer().configEdDSA(Config.userId, Config.sessionId, keyPair).build()

    // create alice keys
    val aliceSessionKey = generateEd25519KeyPair()
    val aliceSessionSecret = aliceSessionKey.publicKey.base64Encode()

    // create alice
    val alice = createUser(client, aliceSessionSecret)
    alice ?: return@runBlocking
    println("alice: $alice")
    val aliceToken = SessionToken.EdDSA(
        alice.userId,
        alice.sessionId,
        aliceSessionKey,
    )
    client.setUserToken(aliceToken)
    // decrypt pin token
    val aliceAesKey = calculateAgreement(alice.pinToken.base64Decode(), privateKeyToCurve25519(aliceSessionKey.privateKey)).base64Encode()
    // create alice's pin
    createPin(client, aliceAesKey)

    // use bot's token
    client.setUserToken(null)
    // create bob keys
    val bobSessionKey = generateEd25519KeyPair()
    val bobSessionSecret = bobSessionKey.publicKey.base64Encode()

    // create bob
    val bob = createUser(client, bobSessionSecret)
    bob ?: return@runBlocking
    println("bob: $bob")
    val bobToken = SessionToken.EdDSA(
        bob.userId,
        bob.sessionId,
        bobSessionKey,
    )
    client.setUserToken(bobToken)
    // decrypt pin token
    val bobAesKey = calculateAgreement(bob.pinToken.base64Decode(), privateKeyToCurve25519(bobSessionKey.privateKey)).base64Encode()
    // create bob's pin
    createPin(client, bobAesKey)

    // use bot's token
    client.setUserToken(null)
    // bot transfer to alice
    val snapshotBot2Alice = transferToUser(client, alice.userId, pinToken, Config.pin)

    delay(4000)

    // use alice's token
    client.setUserToken(aliceToken)
    if (snapshotBot2Alice != null) {
        // alice check transfer
        networkSnapshot(client, snapshotBot2Alice.snapshotId)

        val aliceNetworkSnapshots = networkSnapshots(client, CNB_ID, limit = 5)
        assert(aliceNetworkSnapshots?.find { it.snapshotId == snapshotBot2Alice.snapshotId } != null)
    }

    // alice transfer to bob
    val snapshotAlice2Bob = transferToUser(client, bob.userId, aliceAesKey, DEFAULT_PIN)

    delay(4000)

    // use bob's token
    client.setUserToken(bobToken)
    if (snapshotAlice2Bob != null) {
        networkSnapshot(client, snapshotAlice2Bob.snapshotId)

        val bobNetworkSnapshots = networkSnapshots(client, CNB_ID, limit = 5)
        assert(bobNetworkSnapshots?.find { it.snapshotId == snapshotAlice2Bob.snapshotId } != null)
    }

    // use bot's token
    client.setUserToken(null)
    val botNetworkSnapshot = networkSnapshots(client, CNB_ID, limit = 10)
    if (snapshotBot2Alice != null) {
        assert(botNetworkSnapshot?.find { it.snapshotId == snapshotBot2Alice.snapshotId } != null)
    }
    if (snapshotAlice2Bob != null) {
        assert(botNetworkSnapshot?.find { it.snapshotId == snapshotAlice2Bob.snapshotId } != null)
    }
}
