package arcatch.dsl.rule.cannot;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CannotSignal extends DesignRule {

	public CannotSignal() {
		super();
	}

	public CannotSignal(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") cannot signal (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M cannot signals E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean cannotSignal = true;

		final Set<ClassNode> moduleClassNodes = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClassNodes = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode classNode : moduleClassNodes) {
			Set<MethodNode> methodNodes = classNode.getAllMethods();
			for (MethodNode methodNode : methodNodes) {
				Set<ClassNode> signaledExceptions = methodNode.getThrownExceptions();
				signaledExceptions.retainAll(exceptionClassNodes);
				if (!signaledExceptions.isEmpty()) {
					for (ClassNode signaledException : signaledExceptions) {
						violation = new Violation();
						violation.setClassName(methodNode.getClassName());
						violation.setMethodName(methodNode.getName());
						violation.setMethodShortName(methodNode.getShortName());
						violation.setExceptionName(signaledException.getClassName());
						this.addViolation(violation);
						cannotSignal = false;
					}
				}
			}
		}
		return cannotSignal;

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
