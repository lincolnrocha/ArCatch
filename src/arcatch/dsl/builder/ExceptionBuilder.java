package arcatch.dsl.builder;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.grammar.element.ExceptionEnd;
import arcatch.dsl.grammar.element.ExceptionMatching;

public class ExceptionBuilder extends ElementBuilder implements ExceptionMatching, ExceptionEnd {

	public ExceptionBuilder(String name) {
		super(name);
	}

	@Override
	public ExceptionEnd matching(String regex) {
		this.setRegex(regex);
		return this;
	}

	@Override
	public ExceptionElement build() {
		return new ExceptionElement(this.getName(), this.getRegex());
	}
}