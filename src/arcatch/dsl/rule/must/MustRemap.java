package arcatch.dsl.rule.must;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class MustRemap extends DesignRule {

	public MustRemap() {
		super();
	}

	public MustRemap(ModuleElement module, ExceptionElement from, ExceptionElement to) {
		super(module, from, to);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getModule().getLabel());
		template.append(") must remap (");
		template.append(this.getFromException().getLabel());
		template.append(") ot (");
		template.append(this.getToException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>M must remap E to F</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean mustRemap = true;

		final Set<ClassNode> moduleClasses = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> fromExceptionClasses = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		final Set<ClassNode> toExceptionClasses = CheckerUtil.toExceptionClassNodes(this.getToException().getImplElements());

		Violation violation;

		for (ClassNode moduleClass : moduleClasses) {
			Set<MethodNode> moduleClassMethods = moduleClass.getAllMethods();
			for (MethodNode moduleClassMethod : moduleClassMethods) {
				if (!moduleClassMethod.isConstructor() && !moduleClassMethod.isAbstract()) {
					mustRemap = false;
					for (ClassNode fromExceptionClass : fromExceptionClasses) {
						for (ClassNode toExceptionClass : toExceptionClasses) {
							if (CheckerUtil.remaps(moduleClassMethod, fromExceptionClass, toExceptionClass)) {
								mustRemap = false;
							}
						}
					}
					if (!mustRemap) {
						violation = new Violation();
						violation.setClassName(moduleClassMethod.getClassName());
						violation.setMethodName(moduleClassMethod.getName());
						violation.setMethodShortName(moduleClassMethod.getShortName());
						this.addViolation(violation);
					}
				}
			}
		}

		return mustRemap;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Method [");
			text.append(violation.getMethodName());
			text.append("] is not remapping at least one exception in [");
			text.append(this.getFromException().getLabel());
			text.append("] to an exception in [");
			text.append(this.getToException().getLabel());
			text.append("]\n");
		}
		return text.toString();
	}

}