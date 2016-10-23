package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ExceptionElement;

public interface CanNot {

	public End cannotRaise(ExceptionElement exception);

	public End cannotReraise(ExceptionElement exception);

	public To cannotRemap(ExceptionElement exception);

	public End cannotSignal(ExceptionElement exception);

	public End cannotHandle(ExceptionElement exception);
}
