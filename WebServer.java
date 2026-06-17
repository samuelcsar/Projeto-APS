import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.net.InetSocketAddress;

public class WebServer {
    public static void main(String[] args) throws Exception {
        int port = 8000;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                if (path.equals("/")) {
                    path = "/index.html";
                }
                File file = new File("." + path);
                if (file.exists() && !file.isDirectory()) {
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String contentType = "text/plain";
                    if (path.endsWith(".html")) contentType = "text/html; charset=utf-8";
                    else if (path.endsWith(".css")) contentType = "text/css; charset=utf-8";
                    else if (path.endsWith(".js")) contentType = "application/javascript; charset=utf-8";
                    else if (path.endsWith(".png")) contentType = "image/png";
                    else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) contentType = "image/jpeg";
                    else if (path.endsWith(".svg")) contentType = "image/svg+xml";
                    
                    exchange.getResponseHeaders().set("Content-Type", contentType);
                    exchange.sendResponseHeaders(200, bytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(bytes);
                    os.close();
                } else {
                    String response = "404 Not Found";
                    exchange.sendResponseHeaders(404, response.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(response.getBytes());
                    os.close();
                }
            }
        });
        System.out.println("Servidor rodando em http://localhost:" + port);
        server.start();
    }
}
