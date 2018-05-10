package com.example.parallax.meteorclient.liststuff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.parallax.meteorclient.R;
import com.example.parallax.meteorclient.factories.Task;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends ArrayAdapter<Task> {

    private final LayoutInflater inflater;
    private final List<Task> tasks;


    public Adapter(@NonNull Context context, ArrayList<Task> tasks) {
        super(context, 0, tasks);
        this.inflater = LayoutInflater.from(context);
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        final Task task = getItem(position);

        if(convertView == null) {
            // If convertView is null we have to inflate a new layout
            convertView = this.inflater.inflate(R.layout.list_item, parent, false);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvDisplayText = (TextView) convertView.findViewById(R.id.tvDisplayText);
            viewHolder.cbCompleted   = convertView.findViewById(R.id.cb_task_completed);

            // We set the view holder as tag of the convertView so we can access the view holder later on.
            convertView.setTag(viewHolder);
        }

        // Retrieve the view holder from the convertView
        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        // Bind the values to the views
        viewHolder.tvDisplayText.setText(task.text);
        viewHolder.cbCompleted.setChecked(task.checked);

        return convertView;
    }

    /**
     *
     * @param id
     * @return
     */
    public Task getItem(String id) {
        for (Task task : tasks) {
            if (task.id.equals(id)) return task;
        }

        return null;
    }
}