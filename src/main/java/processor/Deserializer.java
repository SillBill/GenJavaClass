package processor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import gsonObject.Input;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Deserializer {
    private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

    private static final List<Input> cache = new ArrayList<>();

    // universal gson
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<Input> deserialize(String pathStr) {
        cache.clear();

        Path path = Paths.get(pathStr);
        if (! Files.exists(path)) {
            logger.error("preset path doesn't exist!!!");
            return cache;
        }

        if (Files.isDirectory(path)) {
            // directory
            try {
                Files.find(Paths.get(pathStr),
                        Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile()
                                && filePath.toString().endsWith(".json"))
                        .forEach(Deserializer::handler);
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }
        else if (Files.isRegularFile(path)
                && path.toString().endsWith(".json")) {
            // single json file
            handler(path);
        } else {
            logger.error("Path is neither a directory nor a file.");
        }

        return cache;
    }

    private static void handler(Path path) {
        String filePathStr = path.toString();
        try {
            JsonReader json = new JsonReader(new FileReader(filePathStr));
            // read json array
            Type collectionType = new TypeToken<Collection<Input>>(){}.getType();
            Collection<Input> o = gson.fromJson(json, collectionType);

            cache.addAll(o);
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }
}
