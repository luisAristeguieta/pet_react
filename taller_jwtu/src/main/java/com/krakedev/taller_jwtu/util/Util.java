package com.krakedev.taller_jwtu.util;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public class Util {

	private static final Logger log = LogManager.getLogger(Util.class);
	
	private static final String CLAVE_SECRETA = "EstaEsUnaClaveSuperSecretaYMuyLarga1234567890!";
    private static final String EMISOR = "KrakeDevBackend";
    private static final long TIEMPO_EXPIRACION = 3600000; // 1 hora en milisegundos

    public static String generarToken(String username, String rol) {
        Algorithm algoritmo = Algorithm.HMAC256(CLAVE_SECRETA);
        long tiempoActual = System.currentTimeMillis();
        Date fechaExpiracion = new Date(tiempoActual + TIEMPO_EXPIRACION);

        String tokenGenerado = JWT.create()
                .withIssuer(EMISOR)
                .withSubject(username)
                .withIssuedAt(new Date(tiempoActual))
                .withExpiresAt(fechaExpiracion)
                .withClaim("rol", rol)
                .sign(algoritmo);

        return tokenGenerado;
    }
	
    public static DecodedJWT validatorToken(String token) {
        try {
        Algorithm algorithm = Algorithm.HMAC256(CLAVE_SECRETA);
        JWTVerifier verificador = JWT.require(algorithm).withIssuer(EMISOR).build();
        DecodedJWT tokenDecodificado = verificador.verify(token);

        return tokenDecodificado;
        } catch (Exception e) {
        	log.error("Error de validación del Token: " + e.getMessage());
        return null;
        }
    }
    
}
