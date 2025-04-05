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

void main() throws IOException {
    var p1 = new Page("TestPage", "This is a sample Page.".getBytes(StandardCharsets.UTF_8));
    p1.save();
    var p2 = Page.load("TestPage");
    IO.println(new String(p2.body, StandardCharsets.UTF_8));
}
