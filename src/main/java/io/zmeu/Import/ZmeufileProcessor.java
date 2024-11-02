package io.zmeu.Import;


import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ZmeufileProcessor {
    private Zmeufile zmeufile;

    public ZmeufileProcessor(Zmeufile zmeufile) {
        zmeufile = this.zmeufile;
    }

    public boolean load() {
        for (Dependency it : zmeufile.dependencies().dependencies()) {
            var req = HttpRequest.newBuilder(it.toUri())
                    .GET()
                    .build();
            try (var client = HttpClient.newBuilder().build()) {
                client.send(req, HttpResponse.BodyHandlers.ofFileDownload(zmeufile.pluginsPath()));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
