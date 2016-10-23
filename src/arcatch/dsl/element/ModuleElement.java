package arcatch.dsl.element;

import java.util.Collection;

import arcatch.util.CheckerUtil;

public class ModuleElement extends ArchitecturalElement {

	public ModuleElement(String label) {
		super(label);
	}

	public ModuleElement(String label, String regex) {
		super(label, regex);
	}

	@Override
	public void doMapping() {
		Collection<String> classNames = CheckerUtil.getResourcesFromClassPathAndRuntimeLib(getMappingRegex());
		Class<?> exceptionClass = java.lang.Exception.class;
		if (classNames != null && !classNames.isEmpty()) {
			for (String className : classNames) {
				try {
					if (!exceptionClass.isAssignableFrom(Class.forName(className))) {
						this.addImplElement(className);
					}
				} catch (ClassNotFoundException cne) {
					System.err.println(cne);
				}
			}
		}
	}
}
