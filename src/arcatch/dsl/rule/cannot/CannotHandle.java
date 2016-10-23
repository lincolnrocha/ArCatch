package arcatch.dsl.rule.cannot;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CannotHandle extends DesignRule {

	public CannotHandle() {
		super();
	}

	public CannotHandle(ModuleElement from, ExceptionElement exception) {
		super(from, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") cannot handle (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M cannot handle E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {

		boolean cannotHandle = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;

		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode moduleClassMethod : moduleClassMethods) {
				Set<ClassNode> handledExceptions = moduleClassMethod.getCatchedExceptions();
				handledExceptions.retainAll(exceptionClasses);
				if (!handledExceptions.isEmpty()) {
					for (ClassNode handledException : handledExceptions) {
						violation = new Violation();
						violation.setClassName(moduleClassMethod.getClassName());
						violation.setMethodName(moduleClassMethod.getName());
						violation.setMethodShortName(moduleClassMethod.getShortName());
						violation.setExceptionName(handledException.getClassName());
						this.addViolation(violation);
						cannotHandle = false;
					}
				}
			}
		}
		return cannotHandle;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is not handling at least one exception in [");
			text.append(this.getException().getLabel());
			text.append("]\n");
		}
		return text.toString();
	}

}
