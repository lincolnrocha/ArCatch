package arcatch.dsl.rule.cannot;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CannotReraise extends DesignRule {

	public CannotReraise() {
		super();
	}

	public CannotReraise(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") cannot reraise (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M cannot reraise E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean cannotReraise = true;

		final Set<ClassNode> moduleClassNodes = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClassNodes = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;

		for (ClassNode classNode : moduleClassNodes) {
			Set<MethodNode> methodNodes = classNode.getAllMethods();
			for (MethodNode methodNode : methodNodes) {
				for (ClassNode exception : exceptionClassNodes) {
					if (CheckerUtil.reraises(methodNode, exception)) {
						violation = new Violation();
						violation.setClassName(methodNode.getClassName());
						violation.setMethodName(methodNode.getName());
						violation.setMethodShortName(methodNode.getShortName());
						violation.setExceptionName(exception.getClassName());
						this.addViolation(violation);
						cannotReraise = false;
					}
				}
			}
		}

		return cannotReraise;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is reraising the exception [");
			text.append(violation.getExceptionName());
			text.append("]\n");
		}
		return text.toString();
	}
}
