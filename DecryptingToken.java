package com.albertsons.api.framework.support.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.apache.commons.collections4.MapUtils;



   public class DecryptingToken {

        public static String get(String key, Jwt<Header, Claims> jwtToken) {
            Claims claims = jwtToken.getBody();
            return MapUtils.isNotEmpty(claims) ? claims.containsKey(key) ? claims.get(key).toString() : null : null;
        }

        public static Jwt<Header, Claims> getHeaderClaimsJwt(String authorization) {
            return Jwts.parser().parseClaimsJwt(authorization.substring(0, authorization.lastIndexOf('.') + 1));
        }

    }


