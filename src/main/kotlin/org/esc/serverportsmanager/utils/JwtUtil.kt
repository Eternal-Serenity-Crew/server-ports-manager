package org.esc.serverportsmanager.utils

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.Jwts.SIG
import io.jsonwebtoken.security.Keys
import org.esc.serverportsmanager.dto.jwt.CreateJwtToken
import org.esc.serverportsmanager.dto.jwt.SaveRefreshTokenDto
import org.esc.serverportsmanager.entities.Users
import org.esc.serverportsmanager.entities.enums.Roles
import org.esc.serverportsmanager.exceptions.JwtAuthenticationException
import org.esc.serverportsmanager.io.BasicSuccessfulResponse
import org.esc.serverportsmanager.repositories.JwtTokensRepository
import org.esc.serverportsmanager.repositories.mappers.JwtTokensStorageMapper
import org.esc.serverportsmanager.services.UsersService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.Date
import java.util.UUID

@Component
class JwtUtil(
    @Lazy private val usersService: UsersService,
    private val jwtTokensRepository: JwtTokensRepository,
    private val jwtTokensStorageMapper: JwtTokensStorageMapper,
) {
    @Value("\${config.jwt.secret}")
    private lateinit var jwtSecret: String

    @Value("\${config.jwt.access_token_expiration}")
    private lateinit var accessExpirationTime: String

    @Value("\${config.jwt.refresh_token_expiration}")
    private lateinit var refreshExpirationTime: String

    fun generateAccessToken(data: CreateJwtToken): String = generateToken(data, accessExpirationTime)

    @Transactional
    fun generateRefreshToken(data: CreateJwtToken): String {
        val token = generateToken(data, refreshExpirationTime, false)
        saveRefreshToken(data, token)

        return token
    }

    private fun generateToken(data: CreateJwtToken, expiration: String, accessToken: Boolean = true): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["id"] = data.user.id
        claims["uuid"] = data.uuid
        if (accessToken) {
            claims["email"] = data.user.email
            claims["role"] = data.user.role
        }

        return Jwts.builder()
            .claims(claims)
            .issuedAt(Date())
            .expiration(Date.from(Instant.now().plusSeconds(expiration.toLong())))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()), SIG.HS512)
            .compact()
    }

    fun saveRefreshToken(data: CreateJwtToken, token: String): BasicSuccessfulResponse<String> {
        val o = SaveRefreshTokenDto(data.user, data.uuid, token)
        jwtTokensRepository.save(jwtTokensStorageMapper.tokenFromSaveRefreshDto(o))

        return BasicSuccessfulResponse("Token saved")
    }

    fun verifyToken(token: String, uuid: UUID? = null, throwTimeLimit: Boolean = true): Boolean {
        val claims = getClaims(token) ?: throw JwtAuthenticationException("Invalid token claims")
        if (!claims.expiration.after(Date()) && throwTimeLimit) {
            throw JwtAuthenticationException("Token expired")
        }

        if (uuid != null && (claims["uuid"].toString() != uuid.toString())) {
            jwtTokensRepository.findByUuid(uuid).let {
                if (it == null) throw JwtAuthenticationException("Invalid token metadata! JWT validity cannot be asserted and should not be trusted.")
            }

            jwtTokensRepository.deleteByToken(token)
            throw JwtAuthenticationException("Invalid token metadata! JWT validity cannot be asserted and should not be trusted.")
        }

        return true
    }

    @Transactional
    fun removeOldRefreshTokenByUUID(uuid: UUID) = jwtTokensRepository.deleteByUuid(uuid)

    fun getUserFromToken(token: String): Users? {
        val claims = getClaims(token)
        val user = usersService.getById((claims?.get("id") as Int).toLong())

        return user
    }

    fun getRoleFromToken(token: String): Roles = getClaims(token)?.get("role") as Roles

    fun getClaims(token: String): Claims? {
        val claims = Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(jwtSecret.toByteArray()))
            .build()
            .parseSignedClaims(token)
            .payload

        return claims
    }
}