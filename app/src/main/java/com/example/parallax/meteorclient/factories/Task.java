package com.example.parallax.meteorclient.factories;

public class Task {
    public String id       = "";
    public String text     = "";
    public boolean checked = false;



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        boolean result =  task.id.equals(this.id);
        return  result;
    }

//    @Override
//    public int hashCode() {
//        return displayText != null ? displayText.hashCode() : 0;
//    }
}