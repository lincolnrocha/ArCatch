package arcatch.dsl.rule.canOnly;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanSignalOnly extends DesignRule {

	public CanSignalOnly() {
		super();
	}

	public CanSignalOnly(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") can signal only (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b> M can signal only E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canSignalOnly = true;

		Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;

		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode method : moduleClassMethods) {
				Set<ClassNode> signaledExceptions = method.getThrownExceptions();
				signaledExceptions.removeAll(exceptionClasses);
				if (!signaledExceptions.isEmpty()) {
					for (ClassNode signaledException : signaledExceptions) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());
						violation.setExceptionName(signaledException.getClassName());
						this.addViolation(violation);
						canSignalOnly = false;
					}
				}
			}
		}
		return canSignalOnly;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is signaling the exception [");
			text.append(violation.getExceptionName());
			text.append("]\n");
		}
		return text.toString();

	}
}
