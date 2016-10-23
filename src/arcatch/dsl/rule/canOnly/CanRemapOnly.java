package arcatch.dsl.rule.canOnly;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanRemapOnly extends DesignRule {

	public CanRemapOnly() {
		super();
	}

	public CanRemapOnly(ModuleElement module, ExceptionElement from, ExceptionElement to) {
		super(module, from, to);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") can remap only (");
		template.append(this.getFromException().getLabel());
		template.append(") to (");
		template.append(this.getToException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M can remap only E to F</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canRemapOnly = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> fromExceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		final Set<ClassNode> toExceptionClasses = CheckerUtil.toExceptionClassNodes(this.getToException().getImplElements());

		Set<ClassNode> systemExceptionClasses;

		Violation violation;

		for (ClassNode moduleClasse : moduleClasses) {
			Set<MethodNode> moduleClasseMethods = moduleClasse.getAllMethods();
			for (MethodNode method : moduleClasseMethods) {

				systemExceptionClasses = CheckerUtil.getAllSystemExceptionClasses();

				systemExceptionClasses.removeAll(fromExceptionClasses);

				for (ClassNode fromExceptionClass : systemExceptionClasses) {
					for (ClassNode toExceptionClass : toExceptionClasses) {
						if (CheckerUtil.remaps(method, fromExceptionClass, toExceptionClass)) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(fromExceptionClass.getClassName());
							violation.setToExceptionName(toExceptionClass.getClassName());
							this.addViolation(violation);
							canRemapOnly = false;
						}
					}
				}

				systemExceptionClasses = CheckerUtil.getAllSystemExceptionClasses();

				systemExceptionClasses.removeAll(toExceptionClasses);

				for (ClassNode fromExceptionClass : fromExceptionClasses) {
					for (ClassNode toExceptionClass : systemExceptionClasses) {
						if (CheckerUtil.remaps(method, fromExceptionClass, toExceptionClass)) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(fromExceptionClass.getClassName());
							violation.setToExceptionName(toExceptionClass.getClassName());
							this.addViolation(violation);
							canRemapOnly = false;
						}
					}
				}

				systemExceptionClasses = CheckerUtil.getAllSystemExceptionClasses();

				for (ClassNode fromExceptionClass : systemExceptionClasses) {
					for (ClassNode toExceptionClass : systemExceptionClasses) {
						if (CheckerUtil.remaps(method, fromExceptionClass, toExceptionClass)) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(fromExceptionClass.getClassName());
							violation.setToExceptionName(toExceptionClass.getClassName());
							this.addViolation(violation);
							canRemapOnly = false;
						}
					}
				}
			}
		}

		return canRemapOnly;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is remapping the exception [");
			text.append(violation.getExceptionName());
			text.append("] to exception [");
			text.append(violation.getToExceptionName());
			text.append("]\n");
		}

		return text.toString();
	}

}
