package arcatch.dsl;

import arcatch.dsl.builder.ElementBuilder;
import arcatch.dsl.builder.RuleBuilder;

public class Rules {

	/**
	 * This method creates a design rule builder.
	 * 
	 * @return a new rule builder.
	 */
	public static arcatch.dsl.grammar.rule.Begin ruleBuilder() {
		return RuleBuilder.getInstance();
	}

	/**
	 * This method creates an architectural element builder.
	 * 
	 * @return a new architectural element builder builder.
	 */
	public static arcatch.dsl.grammar.element.Begin elementBuilder() {
		return new ElementBuilder();
	}

}
