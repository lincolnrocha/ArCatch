package arcatch.dsl.builder;

import arcatch.dsl.grammar.element.Begin;
import arcatch.dsl.grammar.element.ExceptionMatching;
import arcatch.dsl.grammar.element.ModuleMatching;

public class ElementBuilder implements Begin {

	private String name;

	private String regex;

	public ElementBuilder() {
	}

	public ElementBuilder(String name) {
		this.name = name;
	}

	@Override
	public ExceptionMatching exception(String name) {
		return new ExceptionBuilder(name);
	}

	@Override
	public ModuleMatching module(String name) {
		return new ModuleBuilder(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

}
