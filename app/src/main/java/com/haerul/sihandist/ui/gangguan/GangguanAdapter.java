package com.haerul.sihandist.ui.gangguan;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.sihandist.R;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.databinding.ItemGangguanBinding;

import java.util.List;

public class GangguanAdapter extends RecyclerView.Adapter<GangguanAdapter.RecyclerViewAdapter> {
    private List<Gangguan> data;
    private GangguanViewModel viewModel;

    public GangguanAdapter(List<Gangguan> data, GangguanViewModel viewModel) {
        this.data = data;
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
        ItemGangguanBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_gangguan, parent, false);
        return new RecyclerViewAdapter(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter holder, int position) {
        final Gangguan dataItem = data.get(position);
        holder.bind(dataItem, viewModel);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class RecyclerViewAdapter extends RecyclerView.ViewHolder {
        ItemGangguanBinding binding;
        public RecyclerViewAdapter(ItemGangguanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(@NonNull Gangguan data, GangguanViewModel viewModel) {
            binding.setItem(data);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }
}