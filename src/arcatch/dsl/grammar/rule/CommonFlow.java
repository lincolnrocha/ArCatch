package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ModuleElement;

public interface CommonFlow {

	public End canFlowOnly(ModuleElement... modules);

	public End cannotFlow(ModuleElement... modules);

	public End mustFlow(ModuleElement... modules);
}
