package com.example.parallax.meteorclient.factories;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class TaskFactory {

    static public Task create(String id, String json) {
        final Task task = new Task();
        task.id = id;

        InstanceCreator<Task> creator = getTaskInstanceCreator(task);

        Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, creator).create();
        return gson.fromJson(json, Task.class);
    }

    static public Task update(Task task, String json) {
        InstanceCreator<Task> creator = getTaskInstanceCreator(task);
        Gson gson = new GsonBuilder().registerTypeAdapter(Task.class, creator).create();
        return gson.fromJson(json, Task.class);
    }

    @NonNull
    private static InstanceCreator<Task> getTaskInstanceCreator(final Task task) {
        return new InstanceCreator<Task>() {
                public Task createInstance(Type type) { return task; }
            };
    }
}
