package com.project.app.components;

import com.project.app.classBase.UserBase;
import com.project.app.models.TokenRegister;
import com.project.app.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.InvalidParameterException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private int expiration; //save to environment

    @Value("${jwt.expirationRegister}")
    private int expirationRegister; //save to environment

    @Value("${jwt.expirationForgot}")
    private int expirationForgot; //save to environment

    @Value("${jwt.secretKey}")
    private String secretKey;

    public String generateToken(UserBase user) {
        return createToken(user,expiration);
    }

    public String generateTokenRegister(UserBase user) {
        return createToken(user,expirationRegister);
    }
    public String generateTokenForgot(UserBase user) {
        return createToken(user,expirationForgot);
    }

    private String createToken(UserBase user, int expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", user.getName());
        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getName())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime * 1000L))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
            return token;
        } catch (Exception e) {
            throw new InvalidParameterException("Cannot create Jwt token: " + e.getMessage());
        }
    }

    private Key getSignInKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private Claims extractAllClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = this.extractAllClaim(token);
        return claimsTFunction.apply(claims);
    }

    public String exactUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //    check expiration token
    public boolean isTokenExpired(String token) {
        Date expirationDate = this.extractClaim(token, Claims::getExpiration);
        Date now = new Date();
        return expirationDate.before(now);
    }

    public boolean validatedToken(String token, UserDetails userDetails) {
        String userName = exactUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validatedTokenRegister(String token, TokenRegister tokenRegister) {
        String userName = exactUserName(token);
        return (userName.equals(tokenRegister.getUserName()) && !isTokenExpired(token));
    }
}
