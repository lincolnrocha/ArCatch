package arcatch.dsl.rule.must;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class MustRaise extends DesignRule {

	public MustRaise() {
		super();
	}

	public MustRaise(String label, ModuleElement from, ExceptionElement exception) {
		super(from, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") must raise (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M must raise E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean mustRaise = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode method : moduleClassMethods) {
				if (!method.isConstructor()) {
					Set<ClassNode> raisedExceptions = CheckerUtil.getRaisedExceptions(method);
					raisedExceptions.retainAll(exceptionClasses);
					if (raisedExceptions.isEmpty()) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());

						for (ClassNode raisedException : exceptionClasses) {
							violation.addExceptionName(raisedException.getClassName());
						}
						this.addViolation(violation);
						mustRaise = false;
					}
				}
			}
		}
		return mustRaise;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is not raising at least one exception in [");
			text.append(this.getException().getLabel());
			text.append("]\n");
		}
		return text.toString();
	}

}
