package com.nightpillcylinder.darkcalc;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class NpcalcHistoryActivity extends AppCompatActivity {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_npcalc_history);

		MaterialToolbar toolbar = findViewById(R.id.toolbarNpHistory);
		toolbar.setTitle(getString(R.string.npcalc_history_title));
		toolbar.setNavigationIcon(R.drawable.np_ic_arrow_back);
		toolbar.setNavigationOnClickListener(v -> finish());

		RecyclerView list = findViewById(R.id.listNpHistory);
		list.setLayoutManager(new LinearLayoutManager(this));
		List<String> snapshot = NpcalcHistoryStore.getSnapshot();
		NpcalcHistoryAdapter adapter = new NpcalcHistoryAdapter(snapshot);
		list.setAdapter(adapter);
	}
}