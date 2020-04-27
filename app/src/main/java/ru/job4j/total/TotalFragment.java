package ru.job4j.total;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TotalFragment extends Fragment {
    private FileModelAdapter adapter;
    private RecyclerView recycler;
    private List<String> listOfPaths = new ArrayList<>();
    private Context context;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_tree, container, false);
        context = getContext();
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        File directory = Environment.getExternalStorageDirectory();
        listOfPaths.add(directory.getAbsolutePath());
        File[] files = directory.listFiles();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
            Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.back();
            }
        });
        adapter = new FileModelAdapter(files);
        recycler.setAdapter(adapter);
        return view;
    }

    private final class FileModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        File[] files;

        FileModelAdapter(File[] files) {
            this.files = files;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.file_template, parent, false)) {
            };
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
            File file = files[position];
            final String absolutePath = file.getAbsolutePath();
            final TextView name = holder.itemView.findViewById(R.id.name);
            name.setText(file.getName());
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(absolutePath);
                    if (file.isDirectory()) {
                        Toast.makeText(getContext(), R.string.directory, Toast.LENGTH_SHORT).show();
                        listOfPaths.add(absolutePath);
                        update(absolutePath);
                        count++;
                    }
                    if (file.isFile()) {
                        playMusic(file);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return files.length;
        }

        void back() {
            if (count > 0) {
                count--;
                update(listOfPaths.get(count));
            }
        }

        private void update(String path) {
            File file = new File(path);
            files = file.listFiles();
            notifyDataSetChanged();
        }

        private void playMusic(File file) {
            Uri uri = FileProvider.getUriForFile(
                    context, BuildConfig.APPLICATION_ID + ".provider", file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "audio/*");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        }
    }
}

