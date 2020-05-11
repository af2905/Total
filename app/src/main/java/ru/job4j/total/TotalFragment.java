package ru.job4j.total;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class TotalFragment extends Fragment {
    private FileModelAdapter adapter;
    private List<String> listOfPaths = new ArrayList<>();
    private Context context;
    private AppCompatActivity activity;
    private int count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_tree, container, false);
        context = getContext();
        activity = (AppCompatActivity) getActivity();
        RecyclerView recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOfPaths.add(FilesRepo.ABSOLUTE_PATH);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (activity != null) {
            activity.setSupportActionBar(toolbar);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.back();
            }
        });
        adapter = new FileModelAdapter(getFiles());
        recycler.setAdapter(adapter);
        return view;
    }

    private File[] getFiles() {
        FilesRepo filesRepo = new FilesRepo();
        final ArrayList<File> list = new ArrayList<>();
        Observable<File> observable = Observable.fromArray(filesRepo.getFiles());
        Observer<File> observer = new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(File file) {
                list.add(file);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.subscribe(observer);
        return list.toArray(new File[0]);
    }

    private final class FileModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        File[] files;
        TextView created, size;

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
            final TextView name = holder.itemView.findViewById(R.id.file_name);
            created = holder.itemView.findViewById(R.id.file_created);
            size = holder.itemView.findViewById(R.id.file_size);
            name.setText(file.getName());
            if (file.isFile()) {
                addBasicFileAttributes(absolutePath);
            } else {
                created.setText(null);
                size.setText(null);
            }
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(absolutePath);
                    if (file.isDirectory()) {
                        Toast.makeText(getContext(), R.string.directory, Toast.LENGTH_SHORT).show();
                        listOfPaths.add(absolutePath);
                        update(absolutePath);
                        count++;
                        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                    }
                    if (file.isFile()) {
                        if (file.getName().contains(".mp3")) {
                            playMusic(file);
                        }
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
                int temp = count;
                count--;
                update(listOfPaths.get(count));
                listOfPaths.remove(temp);
                if (count == 0) {
                    Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
                }
            }
        }

        private void update(String absolutePath) {
            File file = new File(absolutePath);
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

        private void addBasicFileAttributes(String absolutePath) {
            BasicFileAttributes basicFileAttributes = null;
            FileTime create;
            long dateCreated = 0;
            int fileSize = 0;
            Path path = Paths.get(absolutePath);
            try {
                basicFileAttributes = Files.readAttributes(path, BasicFileAttributes.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (basicFileAttributes != null) {
                create = basicFileAttributes.creationTime();
                dateCreated = create.toMillis();
                fileSize = (int) basicFileAttributes.size();
            }
            created.setText(Utils.getDate(dateCreated));
            size.setText(String.valueOf(fileSize));
        }
    }
}

