package arcatch.dsl.rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;

public abstract class DesignRule {

	private static int COUNT = 0;

	private String label = "";

	private ModuleElement module = null;

	private ExceptionElement fromException = null;

	private ExceptionElement toException = null;

	private List<ModuleElement> moduleList = new ArrayList<ModuleElement>();

	private Collection<Violation> violations = new Vector<Violation>();

	private long checkingTime = 0;

	public DesignRule() {
	}

	public DesignRule(ModuleElement module, ExceptionElement exception) {
		setModule(module);
		setException(exception);
	}

	public DesignRule(ModuleElement module, ExceptionElement from, ExceptionElement to) {
		setModule(module);
		setFromException(from);
		setToException(from);
	}

	public DesignRule(ExceptionElement exception, ModuleElement... modules) {
		setException(exception);
		setModuleList(modules);
	}

	public ExceptionElement getException() {
		return fromException;
	}

	public void setException(ExceptionElement exception) {
		this.fromException = exception;
	}

	public ExceptionElement getFromException() {
		return fromException;
	}

	public void setFromException(ExceptionElement exception) {
		this.fromException = exception;
	}

	public ExceptionElement getToException() {
		return toException;
	}

	public void setToException(ExceptionElement exception) {
		this.toException = exception;
	}

	public ModuleElement getModule() {
		return module;
	}

	public void setModule(ModuleElement module) {
		this.module = module;
	}

	public List<ModuleElement> getModuleList() {
		return moduleList;
	}

	public void setModuleList(ModuleElement... modules) {
		for (ModuleElement module : modules)
			this.moduleList.add(module);
	}

	public String getLabel() {
		if (this.label == null) {
			this.label = "R" + DesignRule.COUNT++;
		}
		return this.label;
	}

	public void addViolation(Violation violation) {
		this.violations.add(violation);
	}

	public Collection<Violation> getAllViolations() {
		return this.violations;
	}

	public boolean hasViolation() {
		return !this.violations.isEmpty();
	}

	public long getCheckingTime() {
		return checkingTime;
	}

	public void setCheckingTime(long checkingTime) {
		this.checkingTime = checkingTime;
	}

	public abstract String getRuleBody();

	public abstract String getReport();

	public abstract boolean check();

	protected String printMapping() {
		StringBuffer text = new StringBuffer();
		text.append("\n");
		if (this.getModule() != null) {
			text.append(" -" + this.getModule().getLabel() + " module implementation classes:");
			for (String classes : this.getModule().getImplElements()) {
				text.append("\n  -");
				text.append(classes);
			}
			text.append("\n\n");
		}

		if (this.getException() != null) {
			text.append(" -" + this.getException().getLabel() + " exception implementation classes:");
			for (String classes : this.getException().getImplElements()) {
				text.append("\n  -");
				text.append(classes);
			}
			text.append("\n\n");
		}

		if (this.getToException() != null) {
			text.append(" -" + this.getToException().getLabel() + " exception implementation classes:");
			for (String classes : this.getToException().getImplElements()) {
				text.append("\n  -");
				text.append(classes);
			}
			text.append("\n\n");
		}

		if (this.getModuleList() != null) {
			for (ModuleElement module : this.getModuleList()) {
				text.append(" -" + module.getLabel() + " module implementation classes:");
				for (String classes : module.getImplElements()) {
					text.append("\n  -");
					text.append(classes);
				}
				text.append("\n\n");
			}
		}

		return text.toString();
	}

	protected String getViolationHeader() {
		StringBuffer text = new StringBuffer();
		if (!this.hasViolation()) {
			text.append("(V) " + this.getLabel() + ": " + this.getRuleBody() + " " + this.getCheckingTime() + " ms\n");
			text.append(printMapping());
		} else {
			text.append("(X) " + this.getLabel() + ": " + this.getRuleBody() + " " + this.getCheckingTime() + " ms\n");
			text.append(printMapping());
			text.append(" -Rule Violations\n");
		}
		return text.toString();
	}

}