package huydqpc07859.ecommerce.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {

    @Value("${jwt.secret:Default value}")
    private String secret;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver){
        Claims claims = this.extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String generateJWT(Map<String, Object> extraClaim, UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setClaims(extraClaim)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJWTExpires() {
        return Jwts.builder()
                .setSubject("")
                .setClaims(Collections.EMPTY_MAP)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date())
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateJWT(UserDetails userDetails){
            return this.generateJWT(Collections.EMPTY_MAP, userDetails);
    }

    public boolean isInvalidToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token){
        final Date expiredAt = extractClaim(token, Claims::getExpiration);
        return expiredAt.before(new Date());
    }
}
