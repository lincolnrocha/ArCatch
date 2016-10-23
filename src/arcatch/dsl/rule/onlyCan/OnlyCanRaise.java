package arcatch.dsl.rule.onlyCan;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class OnlyCanRaise extends DesignRule {

	public OnlyCanRaise() {
		super();
	}

	public OnlyCanRaise(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("only (");
		template.append(this.getModule().getLabel());
		template.append(") can raise (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>only M can raise E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean onlyCanRaise = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> systemClasses = CheckerUtil.getAllSystemClasses();

		systemClasses.removeAll(moduleClasses);

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode exceptionNode : exceptionClasses) {
			for (ClassNode systemClass : systemClasses) {
				if (!systemClass.isInterface()) {
					for (MethodNode method : systemClass.getAllMethods()) {
						if (CheckerUtil.raises(method, exceptionNode)) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(exceptionNode.getClassName());
							this.addViolation(violation);
							onlyCanRaise = false;
						}
					}
				}
			}
		}
		return onlyCanRaise;
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