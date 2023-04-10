package com.arnyminerz.core.security

import com.arnyminerz.core.Logger
import com.arnyminerz.core.utils.allLowerCase
import com.arnyminerz.core.utils.allUpperCase
import com.arnyminerz.core.utils.isNumber
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import java.util.Arrays
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

enum class PasswordSafety {
    /** The password given is safe */
    Safe,

    /** The password given is not safe because it contains things related to the current app */
    Forbidden,

    /** The password is in the top 10000 unsafe passwords list */
    Unsafe,

    /** The password given is not safe because it's too short */
    Short,

    /** The password given is not safe because it contains only capital letters */
    AllCaps,

    /** The password given is not safe because it contains only lower case letters */
    AllLowercase,

    /** The password given is not safe because it only contains numbers. */
    AllNumbers,
}

/**
 * A utility class to hash passwords and check passwords vs hashed values. It uses a combination of hashing and unique
 * salt. The algorithm used is PBKDF2WithHmacSHA1 which, although not the best for hashing password (vs. bcrypt) is
 * still considered robust and <a href="https://security.stackexchange.com/a/6415/12614"> recommended by NIST </a>.
 * The hashed value has 256 bits.
 *
 * Java code taken from StackOverflow, converted to Kotlin by Arnau Mora.
 * @see <a href="https://stackoverflow.com/a/18143616/5717211">StackOverflow</a>
 *
 * [topics] can be overridden to forbid some words to be contained in the password. In this case,
 * [PasswordSafety.Forbidden] will be thrown when calling [Passwords.isSafePassword], for example.
 */
open class Passwords {
    companion object {
        private const val ITERATIONS = 100000
        private const val KEY_LENGTH = 256
    }

    private val random = SecureRandom()

    private val unsafePasswords = this::class.java.classLoader
        .getResourceAsStream("unsafe-passwords.txt")!!
        .bufferedReader()
        .readLines()

    /** All the words forbidden for passwords because they break [PasswordSafety.Forbidden]. Case insensitive. */
    protected open val topics = emptyList<String>()

    /** The minimum password length */
    protected open val minLength = 8

    /**
     * Returns a random salt to be used to hash a password.
     * @return a 16 bytes random salt
     */
    fun getNextSalt(): ByteArray {
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    /**
     * Returns a salted and hashed password using the provided hash.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param password the password to be hashed
     * @param salt     a 16 bytes salt, ideally obtained with the getNextSalt method
     *
     * @throws NoSuchAlgorithmException If the algorithm is not available in the current system.
     * @throws InvalidKeySpecException If there's an error with the generated key spec.
     *
     * @return the hashed password with a pinch of salt
     */
    fun hash(password: CharArray, salt: ByteArray = getNextSalt()): ByteArray {
        val spec = PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH)
        Arrays.fill(password, Char.MIN_VALUE)
        try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            return skf.generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    /**
     * Returns true if the given password and salt match the hashed value, false otherwise.<br>
     * Note - side effect: the password is destroyed (the char[] is filled with zeros)
     *
     * @param password     the password to check
     * @param salt         the salt used to hash the password
     * @param expectedHash the expected hashed value of the password
     *
     * @throws NoSuchAlgorithmException If the algorithm is not available in the current system.
     * @throws InvalidKeySpecException If there's an error with the generated key spec.
     *
     * @return true if the given password and salt match the hashed value, false otherwise
     */
    fun isExpectedPassword(password: CharArray, salt: ByteArray, expectedHash: ByteArray): Boolean {
        val pwdHash = hash(password, salt)
        Arrays.fill(password, Char.MIN_VALUE)
        if (pwdHash.size != expectedHash.size) return false
        for (i in pwdHash.indices)
            if (pwdHash[i] != expectedHash[i]) return false
        return true
    }

    /**
     * Generates a random password of a given length, using letters and digits.
     *
     * @param length the length of the password
     *
     * @return a random password
     */
    fun generateRandomPassword(length: Int = minLength): String {
        val sb = StringBuilder(length)
        for (i in 0 until length) {
            // 90 is ASCII for Z, 35 for #. See: https://www.asciitable.com/
            val c = random.nextInt(90 - 35) + 35
            sb.append(Char(c))
        }
        return sb.toString()
            .takeIf { isSafePassword(it) == PasswordSafety.Safe }
            ?: run {
                Logger.w("Generated unsafe password: $sb")
                generateRandomPassword(length)
            }
    }

    /**
     * Checks if a given password is safe or not.
     */
    fun isSafePassword(password: String): PasswordSafety =
        when {
            password.length < minLength -> PasswordSafety.Short
            password.allLowerCase -> PasswordSafety.AllLowercase
            password.allUpperCase -> PasswordSafety.AllCaps
            password.isNumber -> PasswordSafety.AllNumbers
            topics.any { password.contains(it, true) } -> PasswordSafety.Forbidden
            unsafePasswords.any { password.contains(it, true) } -> PasswordSafety.Unsafe
            else -> PasswordSafety.Safe
        }
}
