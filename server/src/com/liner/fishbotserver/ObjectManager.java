package com.liner.fishbotserver;

import com.google.gson.Gson;
import com.liner.fishbotserver.utilities.Files;

import java.io.File;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class ObjectManager {
    private final File rootDirectory = new File(Paths.get("").toAbsolutePath().normalize().toString());
    private File dataBaseDirectory = new File(rootDirectory, "database");

    public ObjectManager() {
        if (!Files.ensureDirectory(dataBaseDirectory)) {
            throw new RuntimeException("Cannot initialize " + ObjectManager.class.getSimpleName() + "!");
        }
    }

    public ObjectManager(File directory) {
        dataBaseDirectory = directory;
    }

    public void save(String name, Object object) {
        Files.writeFile(new File(dataBaseDirectory, name), new Gson().toJson(object));
    }

    public <T> T load(String name, Class<T> tClass){
        return new Gson().fromJson(Files.readFile(new File(dataBaseDirectory, name), StandardCharsets.UTF_8), tClass);
    }

    public <T> T loadList(String name, Class<T> clazz) {
        List<T> list = new Gson().fromJson(Files.readFile(new File(dataBaseDirectory, name), StandardCharsets.UTF_8), new ListTypeToken<>(clazz));
        if (list == null)
            list = new ArrayList<>();
        return (T) list;
    }


    public static class ListTypeToken<T> implements ParameterizedType {
        private final Class<?> clazz;

        public ListTypeToken(Class<T> wrapper) {
            this.clazz = wrapper;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{clazz};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
