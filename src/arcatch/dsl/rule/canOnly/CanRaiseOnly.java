package arcatch.dsl.rule.canOnly;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanRaiseOnly extends DesignRule {

	public CanRaiseOnly() {
		super();
	}

	public CanRaiseOnly(ModuleElement from, ExceptionElement exception) {
		super(from, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") can raise only (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M can raise only E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canRaiseOnly = false;

		Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode moduleClasse : moduleClasses) {
			Set<MethodNode> moduleClasseMethods = moduleClasse.getAllMethods();
			for (MethodNode method : moduleClasseMethods) {
				Set<ClassNode> raisedExceptions = CheckerUtil.getRaisedExceptions(method);
				if (raisedExceptions != null && !raisedExceptions.isEmpty()) {
					raisedExceptions.removeAll(exceptionClasses);
					if (!raisedExceptions.isEmpty()) {
						for (ClassNode raisedException : raisedExceptions) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(raisedException.getClassName());
							this.addViolation(violation);
							canRaiseOnly = false;
						}
					}
				}
			}
		}

		return canRaiseOnly;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is raising the exception [");
			text.append(violation.getExceptionName());
			text.append("]\n");
		}
		return text.toString();
	}
}
