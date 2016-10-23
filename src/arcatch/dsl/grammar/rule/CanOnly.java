package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ExceptionElement;

public interface CanOnly {

	public End canRaiseOnly(ExceptionElement exception);

	public End canReraiseOnly(ExceptionElement exception);

	public To canRemapOnly(ExceptionElement exception);

	public End canSignalOnly(ExceptionElement exception);

	public End canHandleOnly(ExceptionElement exception);
}
