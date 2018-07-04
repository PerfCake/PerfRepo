package org.perfrepo.client;

import org.perfrepo.client.exception.ClientException;
import org.perfrepo.dto.exception.ValidationException;
import org.perfrepo.dto.util.authentication.AuthenticationResult;
import org.perfrepo.dto.util.authentication.LoginCredentialParams;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

public class Connection {

    private static final String MEDIA_TYPE = MediaType.APPLICATION_JSON;
    private static final String REST_ENDPOINT = "rest/json";

    private final String username;
    private final String password;

    private String token; //TODO: can reconnect if the token is valid
    private Client client;
    private WebTarget baseTarget;

    public Connection(String url, String username, String password) {
        this.username = username;
        this.password = password;

        client = ClientBuilder.newClient();
        baseTarget = client.target(url + "/" + REST_ENDPOINT);

        login();
    }

    private void login() {
        LoginCredentialParams credentialParams = new LoginCredentialParams();
        credentialParams.setUsername(username);
        credentialParams.setPassword(password);

        WebTarget authenticationTarget = baseTarget.path("authentication");

        Invocation.Builder invocationBuilder = authenticationTarget.request(MEDIA_TYPE);
        Response response;

        try {
            response = invocationBuilder.post(Entity.entity(credentialParams, MEDIA_TYPE));
        } catch (ProcessingException ex) {
            String message;
            if (ex.getCause() != null && ex.getCause() instanceof UnknownHostException) {
                message = "Invalid URL. Details: " + ex.getCause();
            } else {
                message = "Exception during login. Cause: " + ex;
            }
            throw new ClientException(message);
        }

        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new ClientException("Error occurred during login. Status code: " + response.getStatus() + "; "
            + "Status message: " + response.getStatusInfo());
        }

        AuthenticationResult result = response.readEntity(AuthenticationResult.class);

        token = result.getToken();
    }

    public <T> T get(String path, Class<T> clazz) {
        Response response = createInvocationBuilder(path).get();
        return response.readEntity(clazz);
    }

    public <T> T get(String path, GenericType<T> clazz) {
        Response response = createInvocationBuilder(path).get();
        return response.readEntity(clazz);
    }

    public <T> String post(String path, T payload) {
        Response response = createInvocationBuilder(path).post(Entity.entity(payload, MEDIA_TYPE));

        if (response.getStatus() == 422) {
            throw new ClientException("Validation error occurred when doing post to server. Status code: " + response.getStatus(), response.readEntity(ValidationException.class));
        } else if (response.getStatus() == HttpURLConnection.HTTP_CREATED) {
            return response.getHeaderString("Location");
        } else if (response.getStatus() == HttpURLConnection.HTTP_OK) {
          throw new UnsupportedOperationException("Not supported yet.");
        } else {
            throw new ClientException("Error occurred when doing post to server. Status code: " + response.getStatus() + "; "
            + "Status message: " + response.readEntity(String.class));
        }
    }

    public <T, K> K post(String path, T payload, GenericType<K> clazz) {
        Response response = createInvocationBuilder(path).post(Entity.entity(payload, MEDIA_TYPE));
        return response.readEntity(clazz);
    }

    public void delete(String path) {
        Response response = createInvocationBuilder(path).delete();

        if (response.getStatus() != HttpURLConnection.HTTP_NO_CONTENT) {
            throw new ClientException("Error occurred when doing delete to server. Status code: " + response.getStatus() + "; "
                    + "Status message: " + response.readEntity(String.class));
        }
    }

    public String getToken() {
        return token;
    }

    private Invocation.Builder createInvocationBuilder(String path) {
        WebTarget target = baseTarget.path(path);

        Invocation.Builder invocationBuilder = target.request(MEDIA_TYPE);
        invocationBuilder.header("Authorization", "Bearer " + token);

        return invocationBuilder;
    }
}
