package arcatch;

import arcatch.checker.Checker;
import arcatch.dsl.Rules;
import arcatch.util.CheckerUtil;
import arcatch.util.TargetSystemInfo;

public class ArCatch {

	public static arcatch.dsl.grammar.rule.Begin rule() {
		if (TargetSystemInfo.isConfigured()) {
			return Rules.ruleBuilder();
		} else {
			throw new RuntimeException("You must perform ArCatch.config(String, String) call first.");
		}
	}

	public static arcatch.dsl.grammar.element.Begin element() {
		if (TargetSystemInfo.isConfigured()) {
			return Rules.elementBuilder();
		} else {
			throw new RuntimeException("You must perform ArCatch.config(String, String) call first.");
		}

	}

	public static void config(String sourcePath, String binaryPath) {
		TargetSystemInfo.config(sourcePath, binaryPath);
		CheckerUtil.config();
	}

	public static Checker checker() {
		if (TargetSystemInfo.isConfigured()) {
			return Checker.getInstance();
		} else {
			throw new RuntimeException("You must perform ArCatch.config(String, String) call first.");
		}

	}
}
