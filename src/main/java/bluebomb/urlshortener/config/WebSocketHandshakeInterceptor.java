package bluebomb.urlshortener.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;
import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * Add User-Agent as websocket attribute
     *
     * @param request    request
     * @param response   response
     * @param wsHandler  wsHandler
     * @param attributes attributes
     * @return true
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userAgent = "";
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            List<String> userAgentList = servletRequest.getHeaders().get("user-agent");
            if (userAgentList != null && userAgentList.size() == 1) {
                userAgent = userAgentList.get(0);
            }
        }

        attributes.put("user-agent", userAgent);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        // Empty
    }

}
