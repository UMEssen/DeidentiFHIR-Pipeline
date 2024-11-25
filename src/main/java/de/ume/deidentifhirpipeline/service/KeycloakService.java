package de.ume.deidentifhirpipeline.service;

import de.ume.deidentifhirpipeline.config.auth.KeycloakAuthConfig;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.handler.MessageContext;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.BasicHttpClientResponseHandler;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class KeycloakService {

  private KeycloakService() {
    throw new IllegalStateException();
  }

  /**
   * Refresh keycloak token.
   */
  public static String refreshToken(KeycloakAuthConfig keycloakConfig, BindingProvider bindingProvider) throws IOException {
    bindingProvider.getRequestContext().remove("Authorization");
    Map<String, List<String>> requestHeaders = new HashMap<>();
    String token = getKeycloakToken(keycloakConfig);
    requestHeaders.put("Authorization", List.of("Bearer " + token));
    bindingProvider.getRequestContext().put(MessageContext.HTTP_REQUEST_HEADERS, requestHeaders);
    return token;
  }

  public static String getKeycloakToken(KeycloakAuthConfig keycloakConfig) throws IOException {
    String response;
    try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
      HttpPost httppost = new HttpPost(keycloakConfig.getTokenUrl());

      List<NameValuePair> params = new ArrayList<>();
      params.add(new BasicNameValuePair("client_id", keycloakConfig.getClientId()));
      params.add(new BasicNameValuePair("client_secret", keycloakConfig.getClientSecret()));
      params.add(new BasicNameValuePair("username", keycloakConfig.getUsername()));
      params.add(new BasicNameValuePair("password", keycloakConfig.getPassword()));
      params.add(new BasicNameValuePair("grant_type", "password"));

      httppost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));

      response = httpclient.execute(httppost, new BasicHttpClientResponseHandler());
    }
    JSONObject jsonObject = new JSONObject(response);
    return jsonObject.getString("access_token");
  }
}
