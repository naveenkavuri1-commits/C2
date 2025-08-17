package com.nightpillcylinder.darkcalc;

import java.util.LinkedList;
import java.util.List;
import java.util.Collections;

public final class NpcalcHistoryStore {
	private static final LinkedList<String> historyEntries = new LinkedList<>();
	private static final int MAX_SIZE = 200;

	private NpcalcHistoryStore() {}

	public static synchronized void addEntry(String entry) {
		if (entry == null) return;
		String trimmed = entry.trim();
		if (trimmed.isEmpty()) return;
		historyEntries.addFirst(trimmed);
		while (historyEntries.size() > MAX_SIZE) {
			historyEntries.removeLast();
		}
	}

	public static synchronized List<String> getSnapshot() {
		return Collections.unmodifiableList(new LinkedList<>(historyEntries));
	}

	public static synchronized void clearAll() {
		historyEntries.clear();
	}
}