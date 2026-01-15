package ru.aston.spi.session;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

@Path("/custom-session")
public class CustomSessionResource {
  private final KeycloakSession session;

  public CustomSessionResource(KeycloakSession session) {
    this.session = session;
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getSessions() {
    RealmModel realmModel = session.getContext().getRealm();
//    AuthenticationManager.AuthResult authResult = new AppAuthManager.BearerTokenAuthenticator(session)
//      .setRealm(realmModel)
//      .setUriInfo(session.getContext().getUri())
//      .setConnection(session.getContext().getConnection())
//      .setHeaders(session.getContext().getRequestHeaders())
//      .authenticate();
//
//    if (authResult == null || authResult.user() == null) {
//      authResult = new AppAuthManager().authenticateIdentityCookie(session, realmModel);
//    }
//
//    if (authResult == null || authResult.user() == null) {
//      return Response.status(Response.Status.UNAUTHORIZED).build();
//    }

    UserModel userModel = session.users().getUserById(realmModel, "ae431c30-1426-45f4-93ae-91c0aa9573f7");
    String currentSessionId = "test-id";

    if (userModel == null) {
      return Response.status(400).entity("Test user not found").build();
    }

    List<UserSessionModel> userSessions = session
      .sessions()
      .getUserSessionsStream(realmModel, userModel)
      .toList();

    List<Map<String, Object>> sessionsList = new ArrayList<>();
    int totalActive = 0;

    for (UserSessionModel userSession : userSessions) {
      if (userSession.getState() == UserSessionModel.State.LOGGED_IN) {
        totalActive++;
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("session_id", userSession.getId());
        sessionData.put("user_id", userModel.getId());
        sessionData.put("ip", userSession.getIpAddress());
        sessionData.put("login_time", Instant.ofEpochSecond(userSession.getStarted()).toString());
        sessionData.put("last_activity", Instant.ofEpochSecond(userSession.getLastSessionRefresh()).toString());
        sessionData.put("user_agent", userSession.getNote("PARSED_USER_AGENT"));
        sessionsList.add(sessionData);
      }
    }

    Map<String, Object> responseJson = new HashMap<>();
    responseJson.put("sessions", sessionsList);
    responseJson.put("total_active", totalActive);
    responseJson.put("current_session_id", currentSessionId);

    return Response.ok(responseJson).build();
  }
}
