package arcatch.dsl.grammar.rule;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;

public interface Begin {

	public OnlyCan only(ModuleElement module);

	public OnlyCanFlow only(ExceptionElement exception);

	public Common module(ModuleElement module);

	public CommonFlow exception(ExceptionElement exception);

}