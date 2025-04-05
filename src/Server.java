import module jdk.httpserver;

void handler(HttpExchange exchange) throws IOException {
    exchange.sendResponseHeaders(200, 0);
    try (var os = exchange.getResponseBody()) {
        os.write(
                "Hi there, I love %s!"
                        .formatted(exchange.getRequestURI().getPath().substring(1))
                        .getBytes(StandardCharsets.UTF_8)
        );
    }
}

void main() throws IOException {
    var server = HttpServer.create(
            new InetSocketAddress(8700),
            0
    );
    server.createContext("/", this::handler);
    server.start();
}