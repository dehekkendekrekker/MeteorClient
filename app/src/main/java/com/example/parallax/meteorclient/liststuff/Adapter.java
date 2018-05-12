package com.example.parallax.meteorclient.liststuff;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.parallax.meteorclient.MainActivity;
import com.example.parallax.meteorclient.R;
import com.example.parallax.meteorclient.factories.Task;

import java.util.ArrayList;
import java.util.List;

import im.delight.android.ddp.Meteor;

public class Adapter extends ArrayAdapter<Task> {

    private final LayoutInflater inflater;
    private final List<Task> tasks;
    private Meteor meteor;


    public Adapter(@NonNull Context context, ArrayList<Task> tasks, Meteor meteor) {
        super(context, 0, tasks);
        this.inflater = LayoutInflater.from(context);
        this.tasks = tasks;
        this.meteor = meteor;
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
            viewHolder.bDelete       = convertView.findViewById(R.id.deleteItem);
            viewHolder.bPubPriv      = convertView.findViewById(R.id.pubprivbutton);

            // We set the view holder as tag of the convertView so we can access the view holder later on.
            convertView.setTag(viewHolder);
        }

        // Retrieve the view holder from the convertView
        final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        // Bind the values to the views
        handleDisplayText(task, viewHolder);
        handleCheckbox(task, viewHolder);
        handleDeleteButton(task, viewHolder);
        handlePubPrivButton(task, viewHolder);

        return convertView;
    }

    private void handleDisplayText(Task task, ViewHolder viewHolder) {
        viewHolder.tvDisplayText.setText(task.text);
    }

    private void handleCheckbox(Task task, ViewHolder viewHolder) {
        viewHolder.cbCompleted.setChecked(task.checked);
        viewHolder.cbCompleted.setTag(task);
        viewHolder.cbCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox)v;
                Task task = (Task) v.getTag();
                boolean checked = cb.isChecked();
                meteor.call("tasks.setChecked", new Object[] {task.id, checked});
            }
        });
    }

    private void handleDeleteButton(Task task, ViewHolder viewHolder) {
        viewHolder.bDelete.setTag(task);
        viewHolder.bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = (Task) v.getTag();
                meteor.call("tasks.remove", new Object[] {task.id});
            }
        });
    }

    private void handlePubPrivButton(Task task, ViewHolder viewHolder) {
        String userId;

        MainActivity context = (MainActivity)getContext();
        if (!context.isLoggedIn()) {
            viewHolder.bPubPriv.setVisibility(AppCompatImageButton.INVISIBLE);
            return;
        }

        userId = context.getLoggedInUserId();
        if (!task.owner.equals(userId)) {
            viewHolder.bPubPriv.setVisibility(AppCompatImageButton.INVISIBLE);
            return;
        }

        viewHolder.bPubPriv.setVisibility(AppCompatImageButton.VISIBLE);

        if (task.priv) {
            viewHolder.bPubPriv.setImageResource(android.R.drawable.ic_secure);
        } else {
            viewHolder.bPubPriv.setImageResource(android.R.drawable.ic_partial_secure);
        }

        viewHolder.bPubPriv.setTag(task);
        viewHolder.bPubPriv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = (Task) v.getTag();
                meteor.call("tasks.setPrivate", new Object[] {task.id, !task.priv});
            }
        });
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