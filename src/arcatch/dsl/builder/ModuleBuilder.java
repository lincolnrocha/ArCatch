package arcatch.dsl.builder;

import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.grammar.element.ModuleEnd;
import arcatch.dsl.grammar.element.ModuleMatching;

public class ModuleBuilder extends ElementBuilder implements ModuleMatching, ModuleEnd {

	public ModuleBuilder(String name) {
		super(name);
	}

	@Override
	public ModuleEnd matching(String regex) {
		this.setRegex(regex);
		return this;
	}

	@Override
	public ModuleElement build() {
		return new ModuleElement(this.getName(), this.getRegex());
	}

}
