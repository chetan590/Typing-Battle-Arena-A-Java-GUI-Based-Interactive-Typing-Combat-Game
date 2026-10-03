package com.typingbattle.engine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.typingbattle.model.CharacterType;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Embedded pure Java SE HTTP server using com.sun.net.httpserver.
 * Powers real-time cross-platform two-player combat between desktop
 * and mobile/tablet/laptop browsers over local network with zero external dependencies.
 */
public class MultiplayerServer {

    private static MultiplayerServer instance;

    private HttpServer server;
    private int boundPort = 8080;
    private String localIp = "127.0.0.1";
    private final Map<String, MultiplayerSession> activeRooms = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private MultiplayerServer() {
        determineLocalIp();
    }

    public static synchronized MultiplayerServer getInstance() {
        if (instance == null) {
            instance = new MultiplayerServer();
        }
        return instance;
    }

    private void determineLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nif = interfaces.nextElement();
                if (nif.isLoopback() || !nif.isUp()) continue;

                Enumeration<InetAddress> addresses = nif.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address && !addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        this.localIp = addr.getHostAddress();
                        return;
                    }
                }
            }
            this.localIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            this.localIp = "127.0.0.1";
        }
    }

    public synchronized void startServer() {
        if (server != null) return;

        int port = 8080;
        int maxAttempts = 10;
        while (port < 8080 + maxAttempts) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                boundPort = port;
                break;
            } catch (IOException e) {
                port++;
            }
        }

        if (server == null) {
            System.err.println("Notice: Could not bind HTTP server to default ports, trying ephemeral port");
            try {
                server = HttpServer.create(new InetSocketAddress(0), 0);
                boundPort = server.getAddress().getPort();
            } catch (IOException e) {
                System.err.println("Error launching multiplayer server: " + e.getMessage());
                return;
            }
        }

        server.setExecutor(Executors.newCachedThreadPool());

        // Handlers
        server.createContext("/join", new WebAppHandler());
        server.createContext("/", new WebAppHandler());
        server.createContext("/api/status", new ApiStatusHandler());
        server.createContext("/api/join", new ApiJoinHandler());
        server.createContext("/api/input", new ApiInputHandler());
        server.createContext("/api/attack", new ApiAttackHandler());
        server.createContext("/api/special", new ApiSpecialHandler());

        server.start();
        System.out.println(">>> Multiplayer Server online at http://" + localIp + ":" + boundPort + " <<<");
    }

    public synchronized void stopServer() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public synchronized MultiplayerSession createRoom(String hostPlayerName, CharacterType hostCombatant) {
        startServer();
        String roomCode = generateRoomCode();
        MultiplayerSession session = new MultiplayerSession(roomCode, localIp, boundPort, hostPlayerName, hostCombatant);
        activeRooms.put(roomCode, session);
        return session;
    }

    public MultiplayerSession getRoom(String roomCode) {
        if (roomCode == null) return null;
        return activeRooms.get(roomCode.trim().toUpperCase());
    }

    public void removeRoom(String roomCode) {
        if (roomCode != null) {
            activeRooms.remove(roomCode.trim().toUpperCase());
        }
    }

    private String generateRoomCode() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder();
        do {
            sb.setLength(0);
            for (int i = 0; i < 5; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
        } while (activeRooms.containsKey(sb.toString()));
        return sb.toString();
    }

    public String getLocalIp() { return localIp; }
    public int getBoundPort() { return boundPort; }

    // ==========================================
    // HTTP Handlers
    // ==========================================

    private class WebAppHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String roomCode = parseQueryParam(query, "room");

            byte[] responseBytes = buildMobileWebAppHtml(roomCode).getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.sendResponseHeaders(200, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }

    private class ApiStatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");

            String query = exchange.getRequestURI().getQuery();
            String roomCode = parseQueryParam(query, "room");
            MultiplayerSession session = getRoom(roomCode);

            if (session == null) {
                sendJsonResponse(exchange, 404, "{\"error\":\"Room not found or expired\"}");
                return;
            }

            session.recordHeartbeat();
            session.updateTimer(System.currentTimeMillis());

            StringBuilder json = new StringBuilder("{");
            json.append("\"room\":\"").append(session.getRoomCode()).append("\",");
            json.append("\"state\":\"").append(session.getState().name()).append("\",");
            json.append("\"timeRemaining\":").append(session.getTimeRemainingSeconds()).append(",");
            json.append("\"winner\":\"").append(escapeJson(session.getWinnerName())).append("\",");
            json.append("\"outcomeReason\":\"").append(escapeJson(session.getOutcomeReason())).append("\",");

            // P1 stats (Desktop)
            json.append("\"p1\":{");
            json.append("\"name\":\"").append(escapeJson(session.getP1Name())).append("\",");
            json.append("\"type\":\"").append(session.getP1Type().name()).append("\",");
            json.append("\"hp\":").append((int) Math.round(session.getP1Hp())).append(",");
            json.append("\"maxHp\":").append((int) Math.round(session.getP1MaxHp())).append(",");
            json.append("\"power\":").append((int) Math.round(session.getP1Power())).append(",");
            json.append("\"score\":").append(session.getP1Score()).append(",");
            json.append("\"combo\":").append(session.getP1Combo());
            json.append("},");

            // P2 stats (Mobile client)
            json.append("\"p2\":{");
            json.append("\"connected\":").append(session.isP2Connected()).append(",");
            json.append("\"name\":\"").append(escapeJson(session.getP2Name())).append("\",");
            json.append("\"type\":\"").append(session.getP2Type().name()).append("\",");
            json.append("\"hp\":").append((int) Math.round(session.getP2Hp())).append(",");
            json.append("\"maxHp\":").append((int) Math.round(session.getP2MaxHp())).append(",");
            json.append("\"power\":").append((int) Math.round(session.getP2Power())).append(",");
            json.append("\"score\":").append(session.getP2Score()).append(",");
            json.append("\"combo\":").append(session.getP2Combo()).append(",");
            json.append("\"currentWord\":\"").append(escapeJson(session.getP2CurrentWord())).append("\",");
            json.append("\"typedInput\":\"").append(escapeJson(session.getP2TypedInput())).append("\",");
            json.append("\"hasError\":").append(session.isP2HasError());
            json.append("}");

            json.append("}");

            sendJsonResponse(exchange, 200, json.toString());
        }
    }

    private class ApiJoinHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            Map<String, String> params = parseBody(exchange.getRequestBody());
            String roomCode = params.get("room");
            String name = params.get("name");
            String combatant = params.get("combatant");

            MultiplayerSession session = getRoom(roomCode);
            if (session == null) {
                sendJsonResponse(exchange, 404, "{\"success\":false,\"error\":\"Invalid room code\"}");
                return;
            }

            CharacterType cType = CharacterType.SAMURAI;
            try {
                if (combatant != null) cType = CharacterType.valueOf(combatant.toUpperCase());
            } catch (Exception ignored) {}

            boolean ok = session.joinPlayer2(name, cType);
            if (ok) {
                sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Joined room successfully\"}");
            } else {
                sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Room is full or battle in progress\"}");
            }
        }
    }

    private class ApiInputHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            Map<String, String> params = parseBody(exchange.getRequestBody());
            String roomCode = params.get("room");
            String input = params.getOrDefault("input", "");

            MultiplayerSession session = getRoom(roomCode);
            if (session != null && session.getState() == MultiplayerSession.State.BATTLE) {
                session.setP2TypedInput(input);
                String target = session.getP2CurrentWord();
                boolean err = !input.isEmpty() && !target.startsWith(input);
                session.setP2HasError(err);
                if (err) {
                    session.setP2Combo(0);
                }
            }
            sendJsonResponse(exchange, 200, "{\"status\":\"ok\"}");
        }
    }

    private class ApiAttackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            Map<String, String> params = parseBody(exchange.getRequestBody());
            String roomCode = params.get("room");
            String word = params.getOrDefault("word", "");

            MultiplayerSession session = getRoom(roomCode);
            if (session != null && session.getState() == MultiplayerSession.State.BATTLE) {
                if (word.equalsIgnoreCase(session.getP2CurrentWord())) {
                    int dmg = Math.max(25, word.length() * 16);
                    session.executeP2Attack(dmg, word);
                    sendJsonResponse(exchange, 200, "{\"success\":true,\"damage\":" + dmg + "}");
                    return;
                }
            }
            sendJsonResponse(exchange, 400, "{\"success\":false,\"error\":\"Word mismatch or not in battle\"}");
        }
    }

    private class ApiSpecialHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            Map<String, String> params = parseBody(exchange.getRequestBody());
            String roomCode = params.get("room");
            MultiplayerSession session = getRoom(roomCode);
            if (session != null) {
                session.triggerP2Special();
                sendJsonResponse(exchange, 200, "{\"success\":true}");
            } else {
                sendJsonResponse(exchange, 400, "{\"success\":false}");
            }
        }
    }

    private static String parseQueryParam(String query, String param) {
        if (query == null) return "";
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2 && kv[0].equalsIgnoreCase(param)) {
                try {
                    return URLDecoder.decode(kv[1], "UTF-8");
                } catch (Exception e) {
                    return kv[1];
                }
            }
        }
        return "";
    }

    private static Map<String, String> parseBody(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int read;
        while ((read = is.read(buf)) != -1) {
            baos.write(buf, 0, read);
        }
        String body = baos.toString(StandardCharsets.UTF_8);
        Map<String, String> map = new HashMap<>();

        if (body.startsWith("{") && body.endsWith("}")) {
            // Primitive JSON parser
            String trimmed = body.substring(1, body.length() - 1);
            for (String pair : trimmed.split(",")) {
                String[] parts = pair.split(":", 2);
                if (parts.length == 2) {
                    String k = parts[0].trim().replaceAll("^\"|\"$", "");
                    String v = parts[1].trim().replaceAll("^\"|\"$", "");
                    map.put(k, v);
                }
            }
        } else {
            // Form encoded
            for (String pair : body.split("&")) {
                String[] parts = pair.split("=", 2);
                if (parts.length == 2) {
                    try {
                        map.put(URLDecoder.decode(parts[0], "UTF-8"), URLDecoder.decode(parts[1], "UTF-8"));
                    } catch (Exception ignored) {}
                }
            }
        }
        return map;
    }

    private static void sendJsonResponse(HttpExchange exchange, int code, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    /**
     * Builds responsive, high-definition HTML5 mobile web app for Player 2.
     */
    private String buildMobileWebAppHtml(String defaultRoom) {
        return "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "<head>\n" +
            "<meta charset=\"UTF-8\">\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\">\n" +
            "<title>Typing Battle Arena - Multiplayer Companion</title>\n" +
            "<style>\n" +
            "  * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }\n" +
            "  body { background: #0F172A; color: #F8FAFC; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 16px; -webkit-tap-highlight-color: transparent; }\n" +
            "  .container { width: 100%; max-width: 480px; }\n" +
            "  .card { background: #1E293B; border: 1.5px solid #475569; border-radius: 16px; padding: 24px; box-shadow: 0 10px 25px rgba(0,0,0,0.5); text-align: center; }\n" +
            "  .title { font-size: 24px; font-weight: 800; color: #FFFFFF; letter-spacing: 1px; margin-bottom: 6px; }\n" +
            "  .sub { font-size: 13px; font-weight: 700; color: #06B6D4; letter-spacing: 2px; text-transform: uppercase; margin-bottom: 20px; }\n" +
            "  .room-tag { display: inline-block; background: rgba(6,182,212,0.15); border: 1px solid #06B6D4; color: #06B6D4; padding: 6px 14px; border-radius: 20px; font-weight: 800; font-size: 15px; margin-bottom: 16px; }\n" +
            "  input[type=\"text\"] { width: 100%; background: #0F172A; border: 1.5px solid #475569; border-radius: 10px; padding: 14px; color: #FFFFFF; font-size: 16px; text-align: center; outline: none; margin-bottom: 16px; transition: border 0.2s; }\n" +
            "  input[type=\"text\"]:focus { border-color: #06B6D4; }\n" +
            "  .char-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 20px; text-align: left; }\n" +
            "  .char-btn { background: #0F172A; border: 1.5px solid #334155; border-radius: 10px; padding: 12px; cursor: pointer; color: #94A3B8; font-size: 13px; font-weight: 600; transition: all 0.2s; }\n" +
            "  .char-btn.selected { border-color: #10B981; color: #FFFFFF; background: rgba(16,185,129,0.12); }\n" +
            "  .btn-action { width: 100%; background: linear-gradient(135deg, #10B981, #059669); color: #FFFFFF; border: none; border-radius: 12px; padding: 16px; font-size: 16px; font-weight: 700; cursor: pointer; letter-spacing: 1px; transition: transform 0.1s; }\n" +
            "  .btn-action:active { transform: scale(0.98); }\n" +
            "  /* Battle Screen */\n" +
            "  #battleScreen { display: none; }\n" +
            "  .hud { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; gap: 12px; }\n" +
            "  .fighter-col { flex: 1; text-align: left; }\n" +
            "  .fighter-col.right { text-align: right; }\n" +
            "  .fighter-name { font-size: 13px; font-weight: 700; color: #94A3B8; margin-bottom: 4px; }\n" +
            "  .hp-track { width: 100%; height: 16px; background: #0F172A; border-radius: 8px; border: 1px solid #475569; overflow: hidden; }\n" +
            "  .hp-fill { height: 100%; width: 100%; background: #10B981; transition: width 0.2s linear, background-color 0.3s; }\n" +
            "  .timer-box { font-size: 20px; font-weight: 800; color: #F59E0B; padding: 4px 8px; background: #0F172A; border-radius: 8px; border: 1px solid #334155; }\n" +
            "  .word-card { background: #0F172A; border: 2px solid #06B6D4; border-radius: 16px; padding: 22px 12px; margin: 18px 0; text-align: center; min-height: 85px; display: flex; flex-direction: column; justify-content: center; }\n" +
            "  .target-word { font-family: monospace; font-size: 32px; font-weight: 800; letter-spacing: 3px; }\n" +
            "  .target-word .correct { color: #10B981; }\n" +
            "  .target-word .error { color: #EF4444; background: rgba(239,68,68,0.2); border-radius: 4px; }\n" +
            "  .target-word .pending { color: #FFFFFF; }\n" +
            "  .special-btn { width: 100%; background: #334155; color: #64748B; border: none; border-radius: 12px; padding: 14px; font-size: 15px; font-weight: 800; margin-top: 10px; cursor: not-allowed; }\n" +
            "  .special-btn.ready { background: linear-gradient(135deg, #F59E0B, #D97706); color: #FFFFFF; cursor: pointer; animation: pulse 1.2s infinite; }\n" +
            "  @keyframes pulse { 0% { transform: scale(1); box-shadow: 0 0 10px rgba(245,158,11,0.5); } 50% { transform: scale(1.02); box-shadow: 0 0 20px rgba(245,158,11,0.8); } 100% { transform: scale(1); box-shadow: 0 0 10px rgba(245,158,11,0.5); } }\n" +
            "  .event-banner { font-size: 12px; color: #94A3B8; min-height: 20px; margin-top: 8px; font-style: italic; }\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "<div class=\"container\">\n" +
            "  <!-- JOIN CARD -->\n" +
            "  <div id=\"joinScreen\" class=\"card\">\n" +
            "    <div class=\"title\">TYPING BATTLE ARENA</div>\n" +
            "    <div class=\"sub\">CROSS-DEVICE MULTIPLAYER</div>\n" +
            "    <div class=\"room-tag\">ROOM CODE: <span id=\"roomBadge\">" + (defaultRoom.isEmpty() ? "----" : defaultRoom) + "</span></div>\n" +
            "    <input type=\"text\" id=\"playerNameInput\" placeholder=\"Enter Your Pilot Name\" value=\"Player 2\" maxlength=\"16\">\n" +
            "    <div class=\"char-grid\">\n" +
            "      <div class=\"char-btn\" onclick=\"pickChar('NINJA', this)\">Hayato [Ninja]</div>\n" +
            "      <div class=\"char-btn selected\" onclick=\"pickChar('SAMURAI', this)\">Kenji [Samurai]</div>\n" +
            "      <div class=\"char-btn\" onclick=\"pickChar('MAGE', this)\">Aurelia [Mage]</div>\n" +
            "      <div class=\"char-btn\" onclick=\"pickChar('ROBOT_WARRIOR', this)\">Unit VX-9000 [Robot]</div>\n" +
            "      <div class=\"char-btn\" onclick=\"pickChar('SHADOW_FIGHTER', this)\" style=\"grid-column: span 2;\">Malakor [Shadow]</div>\n" +
            "    </div>\n" +
            "    <button class=\"btn-action\" onclick=\"joinLobby()\">CONNECT & ENTER DUEL</button>\n" +
            "  </div>\n" +
            "\n" +
            "  <!-- BATTLE SCREEN -->\n" +
            "  <div id=\"battleScreen\" class=\"card\">\n" +
            "    <div class=\"hud\">\n" +
            "      <div class=\"fighter-col\">\n" +
            "        <div id=\"p1HudName\" class=\"fighter-name\">P1 (Desktop)</div>\n" +
            "        <div class=\"hp-track\"><div id=\"p1HpBar\" class=\"hp-fill\"></div></div>\n" +
            "      </div>\n" +
            "      <div id=\"timerDisplay\" class=\"timer-box\">90</div>\n" +
            "      <div class=\"fighter-col right\">\n" +
            "        <div id=\"p2HudName\" class=\"fighter-name\">YOU (P2)</div>\n" +
            "        <div class=\"hp-track\"><div id=\"p2HpBar\" class=\"hp-fill\"></div></div>\n" +
            "      </div>\n" +
            "    </div>\n" +
            "    <div class=\"word-card\" id=\"wordCard\">\n" +
            "      <div id=\"wordDisplay\" class=\"target-word\">READY</div>\n" +
            "    </div>\n" +
            "    <input type=\"text\" id=\"typingBox\" placeholder=\"Type target word here...\" autocomplete=\"off\" autocorrect=\"off\" autocapitalize=\"off\" spellcheck=\"false\" oninput=\"onTypeInput()\">\n" +
            "    <button id=\"specialBtn\" class=\"special-btn\" onclick=\"castSpecial()\">SPECIAL ATTACK [0% brevity]</button>\n" +
            "    <div id=\"eventDisplay\" class=\"event-banner\">Match in progress. Type swiftly!</div>\n" +
            "  </div>\n" +
            "</div>\n" +
            "\n" +
            "<script>\n" +
            "  let room = '" + defaultRoom + "';\n" +
            "  let selectedCombatant = 'SAMURAI';\n" +
            "  let pollInterval = null;\n" +
            "  let currentTargetWord = '';\n" +
            "  let isGameOver = false;\n" +
            "\n" +
            "  function pickChar(type, el) {\n" +
            "    selectedCombatant = type;\n" +
            "    document.querySelectorAll('.char-btn').forEach(b => b.classList.remove('selected'));\n" +
            "    el.classList.add('selected');\n" +
            "  }\n" +
            "\n" +
            "  async function joinLobby() {\n" +
            "    const name = document.getElementById('playerNameInput').value.trim() || 'Player 2';\n" +
            "    if (!room) {\n" +
            "      const p = new URLSearchParams(window.location.search);\n" +
            "      room = p.get('room') || prompt('Enter 5-Letter Room Code:');\n" +
            "    }\n" +
            "    try {\n" +
            "      const res = await fetch('/api/join', {\n" +
            "        method: 'POST',\n" +
            "        headers: {'Content-Type': 'application/json'},\n" +
            "        body: JSON.stringify({room: room.toUpperCase(), name: name, combatant: selectedCombatant})\n" +
            "      });\n" +
            "      const data = await res.json();\n" +
            "      if (data.success) {\n" +
            "        document.getElementById('joinScreen').style.display = 'none';\n" +
            "        document.getElementById('battleScreen').style.display = 'block';\n" +
            "        document.getElementById('typingBox').focus();\n" +
            "        pollInterval = setInterval(pollSession, 120);\n" +
            "      } else {\n" +
            "        alert(data.error || 'Failed to join');\n" +
            "      }\n" +
            "    } catch(err) { alert('Connection error: ' + err.message); }\n" +
            "  }\n" +
            "\n" +
            "  async function pollSession() {\n" +
            "    if (!room) return;\n" +
            "    try {\n" +
            "      const res = await fetch('/api/status?room=' + encodeURIComponent(room));\n" +
            "      if (!res.ok) return;\n" +
            "      const s = await res.json();\n" +
            "      updateHud(s);\n" +
            "    } catch(e) {}\n" +
            "  }\n" +
            "\n" +
            "  function updateHud(s) {\n" +
            "    if (s.state === 'FINISHED' && !isGameOver) {\n" +
            "      isGameOver = true;\n" +
            "      document.getElementById('eventDisplay').innerText = 'BATTLE FINISHED! Winner: ' + s.winner + ' (' + s.outcomeReason + ')';\n" +
            "      document.getElementById('typingBox').disabled = true;\n" +
            "    }\n" +
            "    document.getElementById('timerDisplay').innerText = s.timeRemaining;\n" +
            "    document.getElementById('p1HudName').innerText = s.p1.name + ' (' + s.p1.hp + ' HP)';\n" +
            "    document.getElementById('p2HudName').innerText = s.p2.name + ' (' + s.p2.hp + ' HP)';\n" +
            "\n" +
            "    const p1Pct = Math.max(0, Math.min(100, (s.p1.hp / s.p1.maxHp) * 100));\n" +
            "    const p2Pct = Math.max(0, Math.min(100, (s.p2.hp / s.p2.maxHp) * 100));\n" +
            "    const p1Bar = document.getElementById('p1HpBar');\n" +
            "    const p2Bar = document.getElementById('p2HpBar');\n" +
            "    p1Bar.style.width = p1Pct + '%';\n" +
            "    p2Bar.style.width = p2Pct + '%';\n" +
            "    p1Bar.style.backgroundColor = p1Pct > 50 ? '#10B981' : (p1Pct > 20 ? '#F59E0B' : '#EF4444');\n" +
            "    p2Bar.style.backgroundColor = p2Pct > 50 ? '#10B981' : (p2Pct > 20 ? '#F59E0B' : '#EF4444');\n" +
            "\n" +
            "    // Update power button\n" +
            "    const sBtn = document.getElementById('specialBtn');\n" +
            "    if (s.p2.power >= 100) {\n" +
            "      sBtn.className = 'special-btn ready';\n" +
            "      sBtn.innerText = 'UNLEASH SPECIAL ATTACK! [100%]';\n" +
            "    } else {\n" +
            "      sBtn.className = 'special-btn';\n" +
            "      sBtn.innerText = 'SPECIAL ATTACK [' + s.p2.power + '%]';\n" +
            "    }\n" +
            "\n" +
            "    // Word Sync\n" +
            "    if (s.p2.currentWord && s.p2.currentWord !== currentTargetWord) {\n" +
            "      currentTargetWord = s.p2.currentWord;\n" +
            "      document.getElementById('typingBox').value = '';\n" +
            "      renderWordLetters('');\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  function onTypeInput() {\n" +
            "    const input = document.getElementById('typingBox').value.toLowerCase();\n" +
            "    renderWordLetters(input);\n" +
            "\n" +
            "    // Sync input with server\n" +
            "    fetch('/api/input', {\n" +
            "      method: 'POST',\n" +
            "      headers: {'Content-Type': 'application/json'},\n" +
            "      body: JSON.stringify({room: room, input: input})\n" +
            "    });\n" +
            "\n" +
            "    // Complete word check\n" +
            "    if (input === currentTargetWord.toLowerCase()) {\n" +
            "      fetch('/api/attack', {\n" +
            "        method: 'POST',\n" +
            "        headers: {'Content-Type': 'application/json'},\n" +
            "        body: JSON.stringify({room: room, word: currentTargetWord})\n" +
            "      });\n" +
            "      document.getElementById('typingBox').value = '';\n" +
            "    }\n" +
            "  }\n" +
            "\n" +
            "  function renderWordLetters(typed) {\n" +
            "    const word = currentTargetWord;\n" +
            "    let html = '';\n" +
            "    const isErr = typed.length > 0 && !word.toLowerCase().startsWith(typed);\n" +
            "    const card = document.getElementById('wordCard');\n" +
            "    card.style.borderColor = isErr ? '#EF4444' : '#06B6D4';\n" +
            "\n" +
            "    for (let i = 0; i < word.length; i++) {\n" +
            "      const c = word[i];\n" +
            "      if (i < typed.length) {\n" +
            "        if (isErr && i === typed.length - 1) {\n" +
            "          html += '<span class=\"error\">' + c + '</span>';\n" +
            "        } else {\n" +
            "          html += '<span class=\"correct\">' + c + '</span>';\n" +
            "        }\n" +
            "      } else {\n" +
            "        html += '<span class=\"pending\">' + c + '</span>';\n" +
            "      }\n" +
            "    }\n" +
            "    document.getElementById('wordDisplay').innerHTML = html || word;\n" +
            "  }\n" +
            "\n" +
            "  async function castSpecial() {\n" +
            "    await fetch('/api/special', {\n" +
            "      method: 'POST',\n" +
            "      headers: {'Content-Type': 'application/json'},\n" +
            "      body: JSON.stringify({room: room})\n" +
            "    });\n" +
            "  }\n" +
            "</script>\n" +
            "</body>\n" +
            "</html>";
    }
}
