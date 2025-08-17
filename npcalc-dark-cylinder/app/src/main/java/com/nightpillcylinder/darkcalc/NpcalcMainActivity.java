package com.nightpillcylinder.darkcalc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nightpillcylinder.darkcalc.databinding.ActivityNpcalcMainBinding;

import java.math.BigDecimal;
import java.math.MathContext;

public class NpcalcMainActivity extends AppCompatActivity implements View.OnClickListener {

	private ActivityNpcalcMainBinding binding;

	private String inputDigitsBuffer = "";
	private BigDecimal lastStoredValue = BigDecimal.ZERO;
	private String pendingBinaryOperator = "";
	private boolean nextInputShouldReset = false;

	private final MathContext preciseContext = new MathContext(16);

	private String expressionPreviewText = "";

	private final java.util.LinkedList<String> calcHistoryLog = new java.util.LinkedList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityNpcalcMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		configureAllButtons();
		refreshPrimaryDisplay("0");
		refreshExpressionDisplay("");
	}

	private void configureAllButtons() {
		View[] allButtons = new View[] {
			binding.btnNpDigit0, binding.btnNpDigit1, binding.btnNpDigit2, binding.btnNpDigit3,
			binding.btnNpDigit4, binding.btnNpDigit5, binding.btnNpDigit6, binding.btnNpDigit7,
			binding.btnNpDigit8, binding.btnNpDigit9, binding.btnNpDot,
			binding.btnNpPlus, binding.btnNpMinus, binding.btnNpMultiply, binding.btnNpDivide,
			binding.btnNpEquals, binding.btnNpClear, binding.btnNpBackspace,
			binding.btnNpSign, binding.btnNpPercent, binding.btnNpHistory
		};
		for (View v : allButtons) {
			v.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == binding.btnNpClear.getId()) {
			resetAllState();
			return;
		}
		if (id == binding.btnNpBackspace.getId()) {
			backspaceOneDigit();
			return;
		}
		if (id == binding.btnNpSign.getId()) {
			toggleSign();
			return;
		}
		if (id == binding.btnNpPercent.getId()) {
			applyPercentOperation();
			return;
		}

		if (id == binding.btnNpEquals.getId()) {
			computeEquals();
			return;
		}
		if (id == binding.btnNpHistory.getId()) {
			showHistoryPeek();
			return;
		}

		if (id == binding.btnNpPlus.getId() || id == binding.btnNpMinus.getId() ||
				id == binding.btnNpMultiply.getId() || id == binding.btnNpDivide.getId()) {
			handleBinaryOperator(((TextView) v).getText().toString());
			return;
		}

		if (id == binding.btnNpDot.getId()) {
			appendDot();
			return;
		}

		// Digits
		appendDigit(((TextView) v).getText().toString());
	}

	private void appendDigit(String digit) {
		if (nextInputShouldReset) {
			inputDigitsBuffer = "";
			nextInputShouldReset = false;
		}
		if (inputDigitsBuffer.equals("0")) {
			inputDigitsBuffer = digit;
		} else {
			inputDigitsBuffer += digit;
		}
		refreshPrimaryDisplay(inputDigitsBuffer);
		refreshExpressionDisplay(buildExpressionPreview());
	}

	private void appendDot() {
		if (nextInputShouldReset) {
			inputDigitsBuffer = "";
			nextInputShouldReset = false;
		}
		if (inputDigitsBuffer.isEmpty()) {
			inputDigitsBuffer = "0.";
		} else if (!inputDigitsBuffer.contains(".")) {
			inputDigitsBuffer += ".";
		}
		refreshPrimaryDisplay(inputDigitsBuffer);
		refreshExpressionDisplay(buildExpressionPreview());
	}

	private void handleBinaryOperator(String operatorSymbol) {
		if (!inputDigitsBuffer.isEmpty()) {
			if (pendingBinaryOperator.isEmpty()) {
				lastStoredValue = new BigDecimal(inputDigitsBuffer);
			} else {
				lastStoredValue = computeBinary(pendingBinaryOperator, lastStoredValue, new BigDecimal(inputDigitsBuffer));
				refreshPrimaryDisplay(safeToPlainString(lastStoredValue));
			}
			inputDigitsBuffer = "";
		}
		pendingBinaryOperator = operatorSymbol;
		refreshExpressionDisplay(buildExpressionPreview());
	}

	private void computeEquals() {
		if (!pendingBinaryOperator.isEmpty() && !inputDigitsBuffer.isEmpty()) {
			BigDecimal right = new BigDecimal(inputDigitsBuffer);
			lastStoredValue = computeBinary(pendingBinaryOperator, lastStoredValue, right);
			refreshPrimaryDisplay(safeToPlainString(lastStoredValue));
			appendHistoryEntry(buildExpressionPreview() + " = " + safeToPlainString(lastStoredValue));
			pendingBinaryOperator = "";
			nextInputShouldReset = true;
			refreshExpressionDisplay("");
		}
	}

	private BigDecimal computeBinary(String op, BigDecimal left, BigDecimal right) {
		switch (op) {
			case "+":
				return left.add(right, preciseContext);
			case "-":
				return left.subtract(right, preciseContext);
			case "×":
				return left.multiply(right, preciseContext);
			case "÷":
				if (right.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
				return left.divide(right, preciseContext);
			default:
				return right;
		}
	}

	private void resetAllState() {
		inputDigitsBuffer = "";
		lastStoredValue = BigDecimal.ZERO;
		pendingBinaryOperator = "";
		nextInputShouldReset = false;
		refreshPrimaryDisplay("0");
		refreshExpressionDisplay("");
	}

	private void backspaceOneDigit() {
		if (!inputDigitsBuffer.isEmpty()) {
			inputDigitsBuffer = inputDigitsBuffer.substring(0, inputDigitsBuffer.length() - 1);
			if (inputDigitsBuffer.isEmpty()) inputDigitsBuffer = "0";
			refreshPrimaryDisplay(inputDigitsBuffer);
			refreshExpressionDisplay(buildExpressionPreview());
		}
	}

	private void toggleSign() {
		if (inputDigitsBuffer.isEmpty()) return;
		if (inputDigitsBuffer.startsWith("-")) {
			inputDigitsBuffer = inputDigitsBuffer.substring(1);
		} else if (!inputDigitsBuffer.equals("0")) {
			inputDigitsBuffer = "-" + inputDigitsBuffer;
		}
		refreshPrimaryDisplay(inputDigitsBuffer);
		refreshExpressionDisplay(buildExpressionPreview());
	}

	private void applyPercentOperation() {
		if (inputDigitsBuffer.isEmpty()) return;
		BigDecimal value = new BigDecimal(inputDigitsBuffer);
		BigDecimal hundred = new BigDecimal("100");
		value = value.divide(hundred, preciseContext);
		inputDigitsBuffer = safeToPlainString(value);
		refreshPrimaryDisplay(inputDigitsBuffer);
		refreshExpressionDisplay(buildExpressionPreview());
	}

	private void refreshPrimaryDisplay(String text) {
		binding.txtNpPrimary.setText(formatForHuman(text));
	}

	private void refreshExpressionDisplay(String text) {
		expressionPreviewText = text == null ? "" : text;
		binding.txtNpExpression.setText(expressionPreviewText);
	}

	private String buildExpressionPreview() {
		String left = lastStoredValue != null ? (pendingBinaryOperator.isEmpty() ? inputDigitsBuffer : safeToPlainString(lastStoredValue)) : "";
		String op = pendingBinaryOperator;
		String right = pendingBinaryOperator.isEmpty() ? "" : inputDigitsBuffer;
		StringBuilder sb = new StringBuilder();
		if (left != null && !left.isEmpty()) sb.append(left);
		if (op != null && !op.isEmpty()) sb.append(" ").append(op).append(" ");
		if (right != null && !right.isEmpty()) sb.append(right);
		return sb.toString();
	}

	private String safeToPlainString(BigDecimal big) {
		try {
			return big.stripTrailingZeros().toPlainString();
		} catch (Exception e) {
			return big.toPlainString();
		}
	}

	private String formatForHuman(String raw) {
		if (raw == null || raw.isEmpty()) return "0";
		try {
			BigDecimal big = new BigDecimal(raw);
			return safeToPlainString(big);
		} catch (Exception e) {
			return raw;
		}
	}

	private void appendHistoryEntry(String entry) {
		if (entry == null || entry.trim().isEmpty()) return;
		calcHistoryLog.addFirst(entry.trim());
		while (calcHistoryLog.size() > 50) {
			calcHistoryLog.removeLast();
		}
	}

	private void showHistoryPeek() {
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for (String s : calcHistoryLog) {
			sb.append(s);
			count++;
			if (count >= 3) break;
			sb.append("\n");
		}
		android.widget.Toast.makeText(this, sb.length() == 0 ? "No history" : sb.toString(), android.widget.Toast.LENGTH_SHORT).show();
	}
}