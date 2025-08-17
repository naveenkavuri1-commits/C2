package com.nightpillcylinder.darkcalc;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NpcalcHistoryAdapter extends RecyclerView.Adapter<NpcalcHistoryAdapter.NpHistoryViewHolder> {

	private final List<String> entries;

	public NpcalcHistoryAdapter(List<String> entries) {
		this.entries = entries;
	}

	@NonNull
	@Override
	public NpHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_np_history, parent, false);
		return new NpHistoryViewHolder(view);
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
		NpHistoryViewHolder(View itemView) {
			super(itemView);
			this.textView = itemView.findViewById(R.id.txtNpHistoryItem);
		}
	}
}