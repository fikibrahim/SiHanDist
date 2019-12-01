package com.haerul.sihandist.ui.inspeksi;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.sihandist.R;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.ItemInspeksiBinding;

import java.util.List;

public class InspeksiAdapter extends RecyclerView.Adapter<InspeksiAdapter.RecyclerViewAdapter> {
    private List<Inspeksi> data;
    private InspeksiViewModel viewModel;

    public InspeksiAdapter(List<Inspeksi> account, InspeksiViewModel viewModel) {
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
        ItemInspeksiBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_inspeksi, parent, false);
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
        ItemInspeksiBinding binding;
        public RecyclerViewAdapter(ItemInspeksiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(@NonNull Inspeksi data, InspeksiViewModel viewModel) {
            binding.setItem(data);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }
}