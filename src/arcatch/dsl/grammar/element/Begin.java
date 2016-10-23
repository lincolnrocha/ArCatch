package arcatch.dsl.grammar.element;

public interface Begin {

	public ExceptionMatching exception(String name);

	public ModuleMatching module(String name);

}
