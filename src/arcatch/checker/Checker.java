package arcatch.checker;

import java.util.Collection;
import java.util.Vector;

import arcatch.dsl.rule.DesignRule;
import arcatch.report.ReportMaker;

public class Checker {

	private static Checker INSTANCE;

	private static Collection<DesignRule> rules;

	private Checker() {
	}

	public static Checker getInstance() {
		if (INSTANCE == null) {
			Checker.INSTANCE = new Checker();
		}
		return INSTANCE;
	}

	/**
	 * This method is responsible to add a <b>rule</b> in the Design Rule list
	 * to be checked during the exception handling conformance checking.
	 * 
	 * @param rule
	 *            a design rule to be checked.
	 */
	public void addRule(DesignRule rule) {
		if (Checker.rules == null) {
			Checker.rules = new Vector<DesignRule>();
		}

		if (!Checker.rules.contains(rule)) {
			Checker.rules.add(rule);
		}
	}

	/**
	 * This method is responsible to perform the exception handling conformance
	 * checking. Calling this method, all rules added in the Design Rule list
	 * will be checked and a conformance checking report will be made.
	 * 
	 */
	public void checkAll() {
		for (DesignRule rule : rules) {
			check(rule);
			ReportMaker.report(rule);
		}
	}

	/**
	 * This method is responsible to check the conformance of a specific
	 * exception handling design <b>rule</b>.
	 *
	 * @param rule
	 *            a design rule to be checked.
	 * @return <b>true</b> if the rule conforms, <b>false</b> otherwise.
	 * @throws ClassNotFoundException
	 *             if no class is matched to the ModuleElement or
	 *             ExceptionElement elements in the rule.
	 */
	public boolean check(DesignRule rule) {
		long startRuleCheckingTime = System.currentTimeMillis();
		boolean result = rule.check();
		rule.setCheckingTime(System.currentTimeMillis() - startRuleCheckingTime);
		return result;
	}

}
