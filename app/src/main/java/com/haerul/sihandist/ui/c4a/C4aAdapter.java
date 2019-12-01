package com.haerul.sihandist.ui.c4a;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.sihandist.R;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.ItemC4aBinding;

import java.util.List;

public class C4aAdapter extends RecyclerView.Adapter<C4aAdapter.RecyclerViewAdapter> {
    private List<Inspeksi> data;
    private C4aViewModel viewModel;

    public C4aAdapter(List<Inspeksi> account, C4aViewModel viewModel) {
        this.data = account;
        this.viewModel = viewModel;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = data.size();
        data.clear();
        notifyItemRangeRemoved(0, size);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemC4aBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_c4a, parent, false);
        return new RecyclerViewAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        final Inspeksi dataItem = data.get(position);
        holder.bind(dataItem, viewModel);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder {
        ItemC4aBinding binding;
        public RecyclerViewAdapter(ItemC4aBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(@NonNull Inspeksi data, C4aViewModel viewModel) {
            binding.setItem(data);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }
}