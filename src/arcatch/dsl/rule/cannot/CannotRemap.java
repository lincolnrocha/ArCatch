package arcatch.dsl.rule.cannot;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CannotRemap extends DesignRule {

	public CannotRemap() {
		super();
	}

	public CannotRemap(ModuleElement module, ExceptionElement from, ExceptionElement to) {
		super(module, from, to);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") cannot remap (");
		template.append(this.getFromException().getLabel());
		template.append(") to (");
		template.append(this.getToException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M cannot remap E to F</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean cannotRemap = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> fromExceptionClasse = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		final Set<ClassNode> toExceptionClasse = CheckerUtil.toExceptionClassNodes(this.getToException().getImplElements());

		Violation violation;

		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode method : moduleClassMethods) {
				for (ClassNode fromExceptionClass : fromExceptionClasse) {
					for (ClassNode toExceptionClass : toExceptionClasse) {
						if (CheckerUtil.remaps(method, fromExceptionClass, toExceptionClass)) {
							violation = new Violation();
							violation.setClassName(method.getClassName());
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(fromExceptionClass.getClassName());
							violation.setToExceptionName(toExceptionClass.getClassName());
							this.addViolation(violation);
							cannotRemap = false;
						}
					}
				}
			}
		}

		return cannotRemap;
	}

	@Override
	public String getReport() {
		// TODO Auto-generated method stub
		return null;
	}

}
