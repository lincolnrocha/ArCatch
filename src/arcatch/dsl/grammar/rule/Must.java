package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ExceptionElement;

public interface Must {

	public End mustRaise(ExceptionElement exception);

	public End mustReraise(ExceptionElement exception);

	public To mustRemap(ExceptionElement exception);

	public End mustSignal(ExceptionElement exception);

	public End mustHandle(ExceptionElement exception);
}
