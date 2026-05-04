package com.sabore;

import com.sabore.rutas.config.TomTomProperties;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(TomTomProperties.class)
public class SaboreApplication {

    public static void main(String[] args) {
        loadDotEnvIfPresent();
        SpringApplication.run(SaboreApplication.class, args);
    }

    /**
     * Lee {@code .env} si existe (carpeta actual o {@code backend/.env} si el cwd es la raíz del repo).
     * Solo aplica variables que no vengan ya del entorno del sistema (prioridad al SO / IDE).
     */
    private static void loadDotEnvIfPresent() {
        Path cwd = Paths.get(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path envDir = null;
        for (Path candidate : List.of(cwd.resolve(".env"), cwd.resolve("backend").resolve(".env"))) {
            if (Files.isRegularFile(candidate)) {
                envDir = candidate.getParent();
                break;
            }
        }
        DotenvBuilder dotenvBuilder = Dotenv.configure().ignoreIfMissing();
        if (envDir != null) {
            dotenvBuilder = dotenvBuilder.directory(envDir.toString());
        }
        Dotenv dotenv = dotenvBuilder.load();
        for (DotenvEntry e : dotenv.entries()) {
            String key = e.getKey();
            if (key == null || key.isBlank()) {
                continue;
            }
            if (System.getenv(key) != null) {
                continue;
            }
            System.setProperty(key, e.getValue());
        }
    }
}
