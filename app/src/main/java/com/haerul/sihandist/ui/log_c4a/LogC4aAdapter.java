package com.haerul.sihandist.ui.log_c4a;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.sihandist.R;
import com.haerul.sihandist.data.entity.Inspeksi;
import com.haerul.sihandist.databinding.ItemLogC4aBinding;
import com.haerul.sihandist.utils.Util;

import java.util.List;

public class LogC4aAdapter extends RecyclerView.Adapter<LogC4aAdapter.RecyclerViewAdapter> {
    private List<Inspeksi> data;
    private LogC4aViewModel viewModel;

    public LogC4aAdapter(List<Inspeksi> account, LogC4aViewModel viewModel) {
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
        ItemLogC4aBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_log_c4a, parent, false);
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
        ItemLogC4aBinding binding;
        public RecyclerViewAdapter(ItemLogC4aBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(@NonNull Inspeksi data, LogC4aViewModel viewModel) {
            binding.setItem(data);
            setupExpanded(binding.expand, binding.moreData, binding.iconMore, "close");
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }

    public void setupExpanded(View viewExpanded, LinearLayout view, ImageView icon, String state) {

        final int[] trigger = {0};

        if (state.equals("open")) {
            trigger[0] = 1;
            Util.expandFast(view);
            icon.setImageDrawable(icon.getResources().getDrawable(R.drawable.ic_expand_less));
        } else {
            trigger[0] = 0;
            Util.collapseFast(view);
            icon.setImageDrawable(icon.getResources().getDrawable(R.drawable.ic_expand_more));
        }

        viewExpanded.setOnClickListener(v -> {
            switch (trigger[0]) {
                case 0:
                    Util.expandFast(view);
                    trigger[0] = 1;
                    icon.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_expand_less));
                    return;
                case 1:
                    Util.collapseFast(view);
                    icon.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_expand_more));
                    trigger[0] = 0;
            }
        });
    }
}