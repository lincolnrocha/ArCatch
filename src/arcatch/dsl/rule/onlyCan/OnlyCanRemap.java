package arcatch.dsl.rule.onlyCan;

import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class OnlyCanRemap extends DesignRule {

	public OnlyCanRemap() {
		super();
	}

	public OnlyCanRemap(ModuleElement module, ExceptionElement from, ExceptionElement to) {
		super(module, from, to);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("only (");
		template.append(this.getModule().getLabel());
		template.append(") can remap (");
		template.append(this.getFromException().getLabel());
		template.append(") to (");
		template.append(this.getToException().getLabel());
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>only M can remap E to F</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean onlyCanRemap = true;

		final Set<ClassNode> moduleClassNodes = CheckerUtil.toClassNodes(this.getModule().getImplElements());

		final Set<ClassNode> allClassNodes = CheckerUtil.getAllSystemClasses();

		allClassNodes.removeAll(moduleClassNodes);

		final Set<ClassNode> fromExceptionClassNodes = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		final Set<ClassNode> toExceptionClassNodes = CheckerUtil.toExceptionClassNodes(this.getToException().getImplElements());

		Violation violation;

		for (ClassNode classNode : allClassNodes) {
			Set<MethodNode> methodNodes = classNode.getAllMethods();
			for (MethodNode methodNode : methodNodes) {
				for (ClassNode fromException : fromExceptionClassNodes) {
					for (ClassNode toException : toExceptionClassNodes) {
						if (CheckerUtil.remaps(methodNode, fromException, toException)) {
							violation = new Violation();
							violation.setClassName(methodNode.getClassName());
							violation.setMethodName(methodNode.getName());
							violation.setMethodShortName(methodNode.getShortName());
							violation.setExceptionName(fromException.getClassName());
							violation.setToExceptionName(toException.getClassName());
							this.addViolation(violation);
							onlyCanRemap = false;
						}
					}
				}
			}
		}
		return onlyCanRemap;
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