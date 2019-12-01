package com.haerul.sihandist.ui.log_gangguan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.sihandist.R;
import com.haerul.sihandist.data.entity.Gangguan;
import com.haerul.sihandist.databinding.ItemLogGgnBinding;
import com.haerul.sihandist.utils.Util;

import java.util.List;

public class LogGgnAdapter extends RecyclerView.Adapter<LogGgnAdapter.RecyclerViewAdapter> {
    private List<Gangguan> data;
    private LogGgnViewModel viewModel;

    public LogGgnAdapter(List<Gangguan> account, LogGgnViewModel viewModel) {
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
        ItemLogGgnBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_log_ggn, parent, false);
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
        ItemLogGgnBinding binding;
        public RecyclerViewAdapter(ItemLogGgnBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        void bind(@NonNull Gangguan data, LogGgnViewModel viewModel) {
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