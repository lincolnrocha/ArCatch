package arcatch.dsl.rule.must;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class MustReraise extends DesignRule {

	public MustReraise() {
		super();
	}

	public MustReraise(ModuleElement module, ExceptionElement exception) {
		super(module, exception);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") must reraise (");
		template.append(this.getException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M must reraise E</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean mustReraise = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> exceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;

		for (ClassNode moduleClassNote : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClassNote.getAllMethods();
			for (MethodNode method : moduleClassMethods) {
				if (!method.isConstructor()) {
					mustReraise = false;
					for (ClassNode exceptionClass : exceptionClasses) {
						if (CheckerUtil.reraises(method, exceptionClass)) {
							mustReraise = true;
						}
					}
					if (!mustReraise) {
						violation = new Violation();
						violation.setClassName(method.getClassName());
						violation.setMethodName(method.getName());
						violation.setMethodShortName(method.getShortName());

						for (ClassNode raisedException : exceptionClasses) {
							violation.addExceptionName(raisedException.getClassName());
						}
						this.addViolation(violation);
					}
				}
			}
		}

		return mustReraise;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is not reraising at least one exception in [");
			text.append(this.getException().getLabel());
			text.append("]\n");
		}
		return text.toString();
	}

}