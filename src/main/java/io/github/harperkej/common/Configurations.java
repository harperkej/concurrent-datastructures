package io.github.harperkej.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public final class Configurations {

    private static Configurations instance;

    private Configurations() {
    }

    public static Configurations getInstance() {
        if (instance == null) {
            synchronized (Configurations.class) {
                if (instance == null) {
                    instance = new Configurations();
                }
            }
        }
        return instance;
    }

    private static final String PRIME_NUMBERS_THREADS_KEY = "thread-count";
    public static final int NR_OF_THREADS;

    static {
        Properties properties = Configurations.getInstance().readProperties();
        NR_OF_THREADS = Integer.valueOf(properties.getProperty(PRIME_NUMBERS_THREADS_KEY));
    }

    public Properties readProperties() {
        String basePath = System.getProperty("user.dir");
        String relativePathToConfigFile = "src/main/java/resources/config.properties";
        Path configFile = Paths.get(basePath, relativePathToConfigFile);

        Properties configurations = new Properties();

        try (InputStream inputStream = new FileInputStream(configFile.toFile())) {
            configurations.load(inputStream);
        } catch (IOException e) {
            // In case something goes wrong when reading the configuration file, just set it to 4 threads.
            configurations.put(PRIME_NUMBERS_THREADS_KEY, 4);
            e.printStackTrace();
        }
        return configurations;
    }
}
