package ru.job4j.total;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class TotalFragment extends Fragment {
    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private RecyclerView recycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.files_tree, container, false);
        recycler = view.findViewById(R.id.recycler);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<FileModel> list = FilesTree.getInstance()
                .filesList(Environment.getExternalStorageDirectory().getAbsolutePath());
        adapter = new FileModelAdapter(list);
        recycler.setAdapter(adapter);
        return view;
    }

    private final class FileModelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<FileModel> filesList;

        FileModelAdapter(List<FileModel> filesList) {
            this.filesList = filesList;
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
            final FileModel fileModel = filesList.get(position);
            final TextView name = holder.itemView.findViewById(R.id.name);
            name.setText(fileModel.getName());

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file = new File(fileModel.getAbsolutePath());
                    if (file.isDirectory()) {
                        Toast.makeText(getContext(), "Directory", Toast.LENGTH_SHORT).show();
                        filesList = FilesTree.getInstance()
                                .filesList(fileModel.getAbsolutePath());
                        notifyDataSetChanged();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return filesList.size();
        }
    }

}

