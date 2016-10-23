package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ModuleElement;

public interface OnlyCanFlow {

	public End canFlow(ModuleElement... modules);
}
