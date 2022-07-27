package com.rating.business.logic.impl;

import com.rating.utils.PasswordUtil;
import com.google.gson.Gson;
import com.rating.bo.ApiUser;
import com.rating.business.logic.ApiUserService;
import com.rating.business.logic.TokenValidationService;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
@Transactional("RatingAndReviewTransactionManager")
public class TokenValidationServiceImpl implements TokenValidationService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ApiUserService apiUserService;

    @Autowired
    private Gson gson;

  

    @Override
    public boolean validateAuthToken(String authToken, String endPointUrl, HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        /*
         * CASE of ApiUser API
         */
        ApiUser user = null;
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            request.setAttribute(key, request.getHeader(key));
            logger.debug("key: " + key + ". Value: " + request.getHeader(key));
        }
        logger.debug("x-jwt-assertion: {}", request.getHeader("x-jwt-assertion"));
        if (request.getHeader("x-jwt-assertion") != null) {
            String[] split_string = request.getHeader("x-jwt-assertion").split("\\.");
            String base64EncodedBody = split_string[1];

            String decodedBody = new String(Base64Utils.decode(base64EncodedBody.getBytes()));
            Map<String, Object> jsonObject = gson.fromJson(decodedBody, HashMap.class);

            logger.debug(jsonObject.toString());
            String userVal = jsonObject.get("http://wso2.org/claims/enduser").toString();
            String userName = userVal.substring(0, userVal.indexOf("@"));
            user = apiUserService.getUserByUserName(userName);

            if (user == null) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
               
                
                return false;
            }
            logger.debug("Got User profile! " + userName);
        } else if (authToken != null) {
            if (authToken.startsWith("Basic ")) {
                authToken = authToken.replace("Basic ", "");
                String[] credentials = decode(authToken);
                if (credentials == null || credentials.length < 2) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                   
                    return false;
                }

                String userName = credentials[0];
                String password = credentials[1];
                user = apiUserService.getUserByUserName(userName);

                if (user == null || !PasswordUtil.matchPassword(password, user.getPassword())) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                   
                    return false;
                }
            } else {
                authToken = authToken.replace("Bearer ", "");
                user = apiUserService.getUserByToken(authToken);

                if (user == null || user.getTokenExpiryDate().before(new Date())) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                   
                    return false;
                }
            }

        }

        logger.debug("Got User profile:{} ", user);

        if (user != null && (user.isDeleted() || user.isLocked())) {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           

            return false;
        }

     

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
       

        return false;

    }


 

    private String[] decode(final String encoded) {
        try {
            final byte[] decodedBytes = Base64.decodeBase64(encoded.getBytes());
            final String pair = new String(decodedBytes);
            final String[] userDetails = pair.split(":", 2);
            return userDetails;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
