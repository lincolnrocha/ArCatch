package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ExceptionElement;

public interface OnlyCan {

	public End canRaise(ExceptionElement exception);

	public End canReraise(ExceptionElement exception);

	public To canRemap(ExceptionElement exception);

	public End canSignal(ExceptionElement exception);

	public End canHandle(ExceptionElement exception);
}