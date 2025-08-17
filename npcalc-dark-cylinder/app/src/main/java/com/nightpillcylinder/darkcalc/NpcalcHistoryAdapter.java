package com.nightpillcylinder.darkcalc;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nightpillcylinder.darkcalc.databinding.ItemNpHistoryBinding;

import java.util.List;

public class NpcalcHistoryAdapter extends RecyclerView.Adapter<NpcalcHistoryAdapter.NpHistoryViewHolder> {

	private final List<String> entries;

	public NpcalcHistoryAdapter(List<String> entries) {
		this.entries = entries;
	}

	@NonNull
	@Override
	public NpHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ItemNpHistoryBinding binding = ItemNpHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
		return new NpHistoryViewHolder(binding);
	}

	@Override
	public void onBindViewHolder(@NonNull NpHistoryViewHolder holder, int position) {
		String text = entries.get(position);
		holder.textView.setText(text);
	}

	@Override
	public int getItemCount() {
		return entries == null ? 0 : entries.size();
	}

	static class NpHistoryViewHolder extends RecyclerView.ViewHolder {
		final TextView textView;
		NpHistoryViewHolder(ItemNpHistoryBinding binding) {
			super(binding.getRoot());
			this.textView = binding.txtNpHistoryItem;
		}
	}
}