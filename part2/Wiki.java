import module jdk.httpserver;
import module dev.mccue.jdk.httpserver;

record Page(String title, byte[] body) {
    void save() throws IOException {
        var filename = title + ".txt";
        Files.write(Path.of(filename), body);
    }

    static Page load(String title) throws IOException {
        var filename = title + ".txt";
        var body = Files.readAllBytes(Path.of(filename));
        return new Page(title, body);
    }
}

void viewHandler(HttpExchange exchange) throws IOException {
    var title = exchange.getRequestURI()
            .getPath()
            .substring("/view/".length());
    var p = Page.load(title);
    HttpExchanges.sendResponse(
            exchange,
            200,
            Body.of(
                    "<h1>%s</h1><div>%s</div>"
                            .formatted(
                                    p.title,
                                    new String(p.body, StandardCharsets.UTF_8)
                            )
            )
    );
}

void main() throws IOException {
    var server = HttpServer.create(
            new InetSocketAddress(8080),
            0
    );
    server.createContext("/view/", this::viewHandler);
    server.start();
}
