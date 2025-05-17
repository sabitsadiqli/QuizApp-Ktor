import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import model.user.User
import repository.auth.AuthRepository
import java.util.*

class AuthFacade(
    private val repository: AuthRepository
) {
    private val secret = "superSecret" // config file-dan götür
    private val issuer = "quizApp"
    private val accessTokenExpiry = 30000L  // 15 dəqiqə
    private val refreshTokenExpiry = 60000L * 60 * 24 * 7 // 7 gün
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("quizApp") // issuer eyni olmalıdır
        .withSubject("AccessToken") // eyni subject olmalıdır
        .build()

    fun generateAccessToken(userId: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject("AccessToken")
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + accessTokenExpiry))
            .sign(Algorithm.HMAC256(secret))
    }

    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withIssuer(issuer)
            .withSubject("RefreshToken")
            .withClaim("userId", userId)
            .withExpiresAt(Date(System.currentTimeMillis() + refreshTokenExpiry))
            .sign(Algorithm.HMAC256(secret))
    }

    suspend fun login(userId: String, password: String): Pair<String, String>? {
        return if (repository.validateUserCredentials(userId, password)) {
            val accessToken = generateAccessToken(userId)
            val refreshToken = generateRefreshToken(userId)
            repository.saveRefreshToken(userId, refreshToken)
            Pair(accessToken, refreshToken)
        } else null
    }

    suspend fun refreshAccessToken(userId: String, givenRefreshToken: String): String? {
        val savedToken = repository.getRefreshToken(userId)
        return if (savedToken == givenRefreshToken) {
            generateAccessToken(userId)
        } else null
    }

    suspend fun register(userId: String, password: String): Boolean {
        return repository.register(userId,password)
    }

    suspend fun getUserByUserId(userId: String): User? {
        return repository.getUserByUserId(userId)
    }
}