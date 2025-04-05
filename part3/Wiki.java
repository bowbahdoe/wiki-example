import module jdk.httpserver;
import module dev.mccue.jdk.httpserver;
import module com.samskivert.jmustache;

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

void renderTemplate(
        HttpExchange exchange,
        String tmpl,
        Page p
) throws IOException {
    var template = Mustache.compiler()
            .compile(Files.readString(Path.of(tmpl + ".html")));
    HttpExchanges.sendResponse(
            exchange,
            200,
            Body.of(template.execute(Map.of(
                    "title", p.title,
                    "body", new String(p.body, StandardCharsets.UTF_8)
            )))
    );
}

void viewHandler(HttpExchange exchange) throws IOException {
    var title = exchange.getRequestURI()
            .getPath()
            .substring("/view/".length());
    var p = Page.load(title);
    renderTemplate(exchange, "view", p);
}

void editHandler(HttpExchange exchange) throws IOException {
    var title = exchange.getRequestURI()
            .getPath()
            .substring("/view/".length());
    Page p;
    try {
        p = Page.load(title);
    } catch (NoSuchFileException e) {
        p = new Page(title, new byte[]{});
    }

    renderTemplate(exchange, "edit", p);
}

void saveHandler(HttpExchange exchange) throws IOException {

}

void main() throws IOException {
    var server = HttpServer.create(
            new InetSocketAddress(8700),
            0
    );
    server.createContext("/view/", this::viewHandler);
    server.createContext("/edit/", this::editHandler);
    server.createContext("/save/", this::saveHandler);
    server.start();
}
