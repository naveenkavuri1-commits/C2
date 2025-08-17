package com.nightpillcylinder.darkcalc;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.math.MathContext;

public class NpcalcMainActivity extends AppCompatActivity implements View.OnClickListener {

	private TextView txtNpPrimaryView;
	private TextView txtNpExpressionView;

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
		setContentView(R.layout.activity_npcalc_main);

		txtNpPrimaryView = findViewById(R.id.txtNpPrimary);
		txtNpExpressionView = findViewById(R.id.txtNpExpression);

		configureAllButtons();
		refreshPrimaryDisplay("0");
		refreshExpressionDisplay("");
	}

	private void configureAllButtons() {
		View[] allButtons = new View[] {
			findViewById(R.id.btnNpDigit0), findViewById(R.id.btnNpDigit1), findViewById(R.id.btnNpDigit2), findViewById(R.id.btnNpDigit3),
			findViewById(R.id.btnNpDigit4), findViewById(R.id.btnNpDigit5), findViewById(R.id.btnNpDigit6), findViewById(R.id.btnNpDigit7),
			findViewById(R.id.btnNpDigit8), findViewById(R.id.btnNpDigit9), findViewById(R.id.btnNpDot),
			findViewById(R.id.btnNpPlus), findViewById(R.id.btnNpMinus), findViewById(R.id.btnNpMultiply), findViewById(R.id.btnNpDivide),
			findViewById(R.id.btnNpEquals), findViewById(R.id.btnNpClear), findViewById(R.id.btnNpBackspace),
			findViewById(R.id.btnNpSign), findViewById(R.id.btnNpPercent), findViewById(R.id.btnNpHistory)
		};
		for (View v : allButtons) {
			if (v != null) v.setOnClickListener(this);
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btnNpClear) {
			resetAllState();
			return;
		}
		if (id == R.id.btnNpBackspace) {
			backspaceOneDigit();
			return;
		}
		if (id == R.id.btnNpSign) {
			toggleSign();
			return;
		}
		if (id == R.id.btnNpPercent) {
			applyPercentOperation();
			return;
		}

		if (id == R.id.btnNpEquals) {
			computeEquals();
			return;
		}
		if (id == R.id.btnNpHistory) {
			android.content.Intent intent = new android.content.Intent(this, NpcalcHistoryActivity.class);
			startActivity(intent);
			return;
		}

		if (id == R.id.btnNpPlus || id == R.id.btnNpMinus || id == R.id.btnNpMultiply || id == R.id.btnNpDivide) {
			handleBinaryOperator(((TextView) v).getText().toString());
			return;
		}

		if (id == R.id.btnNpDot) {
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
			String leftText = safeToPlainString(lastStoredValue);
			String opText = pendingBinaryOperator;
			String rightText = inputDigitsBuffer;
			BigDecimal right = new BigDecimal(inputDigitsBuffer);
			lastStoredValue = computeBinary(pendingBinaryOperator, lastStoredValue, right);
			String result = safeToPlainString(lastStoredValue);
			refreshPrimaryDisplay(result);
			NpcalcHistoryStore.addEntry(leftText + " " + opText + " " + rightText + " = " + result);
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
		txtNpPrimaryView.setText(formatForHuman(text));
	}

	private void refreshExpressionDisplay(String text) {
		expressionPreviewText = text == null ? "" : text;
		txtNpExpressionView.setText(expressionPreviewText);
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
}