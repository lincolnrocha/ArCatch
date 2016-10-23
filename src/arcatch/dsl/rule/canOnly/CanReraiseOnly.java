package arcatch.dsl.rule.canOnly;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanReraiseOnly extends DesignRule {

	public CanReraiseOnly() {
		super();
	}

	public CanReraiseOnly(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") can reraise only (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M can reraise only E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canReraiseOnly = false;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Set<ClassNode> systemClasses = CheckerUtil.getAllSystemExceptionClasses();

		systemClasses.removeAll(exceptionClasses);

		Violation violation;
		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMathods = moduleClass.getAllMethods();
			for (MethodNode method : moduleClassMathods) {
				for (ClassNode exceptionClass : systemClasses) {
					if (CheckerUtil.reraises(method, exceptionClass)) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());
						violation.setExceptionName(exceptionClass.getClassName());
						this.addViolation(violation);
						canReraiseOnly = false;
					}
				}
			}
		}

		return canReraiseOnly;
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
