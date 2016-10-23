package arcatch.dsl.element;

import java.util.HashSet;
import java.util.Set;

public abstract class ArchitecturalElement {

	private String label;

	private String regex;

	private Set<String> implElements = new HashSet<>();

	public ArchitecturalElement(String label) {
		this.label = label;
	}

	public ArchitecturalElement(String label, String regex) {
		this(label);
		this.regex = regex;
		doMapping();
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getMappingRegex() {
		return regex;
	}

	public String getMappingDescription() {
		StringBuffer mappingDescription = new StringBuffer();
		mappingDescription.append(this.getLabel());
		mappingDescription.append(" = ");
		mappingDescription.append(this.getMappingRegex());
		return mappingDescription.toString();
	}

	public void addImplElement(String element) {
		this.implElements.add(element);
	}

	public Set<String> getImplElements() {
		return this.implElements;
	}

	public String implElementsToString() {
		StringBuffer buffer = new StringBuffer();
		for (String element : this.implElements) {
			buffer.append(element);
		}
		return buffer.toString();
	}

	public boolean hasImplElements() {
		return !this.implElements.isEmpty();
	}

	public abstract void doMapping();
}