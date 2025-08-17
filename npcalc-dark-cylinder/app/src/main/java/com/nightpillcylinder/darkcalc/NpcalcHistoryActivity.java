package com.nightpillcylinder.darkcalc;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nightpillcylinder.darkcalc.databinding.ActivityNpcalcHistoryBinding;

import java.util.List;

public class NpcalcHistoryActivity extends AppCompatActivity {

	private ActivityNpcalcHistoryBinding binding;
	private NpcalcHistoryAdapter adapter;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNpcalcHistoryBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.toolbarNpHistory.setTitle(getString(R.string.npcalc_history_title));
		binding.toolbarNpHistory.setNavigationIcon(R.drawable.np_ic_arrow_back);
		binding.toolbarNpHistory.setNavigationOnClickListener(v -> finish());

		binding.listNpHistory.setLayoutManager(new LinearLayoutManager(this));
		List<String> snapshot = NpcalcHistoryStore.getSnapshot();
		adapter = new NpcalcHistoryAdapter(snapshot);
		binding.listNpHistory.setAdapter(adapter);
	}
}