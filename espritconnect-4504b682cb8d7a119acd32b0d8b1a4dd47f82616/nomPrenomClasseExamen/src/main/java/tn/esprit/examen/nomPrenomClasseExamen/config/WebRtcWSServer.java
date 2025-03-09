package tn.esprit.examen.nomPrenomClasseExamen.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint(value = "/webrtc/{username}")
public class WebRtcWSServer {

    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionMap.put(username, session);
    }

    @OnClose
    public void onClose(Session session) {
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            HashMap<String, Object> map = mapper.readValue(message, HashMap.class);

            String type = (String) map.get("type");
            String toUser = (String) map.get("toUser");
            String fromUser = (String) map.get("fromUser");
            String sdp = (String) map.get("sdp");
            Map iceCandidate = (Map) map.get("iceCandidate");

            Session toUserSession = sessionMap.get(toUser);
            if (toUserSession == null) {
                toUserSession = session;
                map.put("type", "call_back");
                map.put("fromUser", "system message");
                map.put("msg", "Sorry, user not online!");
                send(toUserSession, mapper.writeValueAsString(map));
                return;
            }

            map.put("fromUser", fromUser);
            send(toUserSession, mapper.writeValueAsString(map));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send(Session session, String message) {
        try {
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}