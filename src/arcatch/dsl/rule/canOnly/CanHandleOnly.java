package arcatch.dsl.rule.canOnly;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanHandleOnly extends DesignRule {

	public CanHandleOnly() {
		super();
	}

	public CanHandleOnly(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") can handle only (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M can handle only E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canHandleOnly = true;

		Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;

		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode method : moduleClassMethods) {
				Set<ClassNode> handledExceptions = method.getCatchedExceptions();
				handledExceptions.removeAll(exceptionClasses);
				if (!handledExceptions.isEmpty()) {
					for (ClassNode handledException : handledExceptions) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());
						violation.setExceptionName(handledException.getClassName());
						this.addViolation(violation);
						canHandleOnly = false;
					}
				}
			}
		}
		return canHandleOnly;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is handling the exception [");
			text.append(violation.getExceptionName());
			text.append("]\n");
		}
		return text.toString();
	}

}
