package arcatch.dsl.rule.canOnly;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;

import arcatch.dsl.element.ExceptionElement;
import arcatch.dsl.element.ModuleElement;
import arcatch.dsl.rule.DesignRule;
import arcatch.dsl.rule.Violation;
import arcatch.util.CheckerUtil;

public class CanFlowOnly extends DesignRule {

	public CanFlowOnly() {
		super();
	}

	public CanFlowOnly(ExceptionElement exception, ModuleElement... modules) {
		super(exception, modules);
	}

	@Override
	public String getRuleBody() {
		StringBuffer template = new StringBuffer();
		template.append("(");
		template.append(this.getException().getLabel());
		template.append(") can flow only ( ");
		for (ModuleElement module : this.getModuleList()) {
			template.append(module.getLabel());
			template.append(" ");
		}
		template.append(")");
		return template.toString();
	}

	/**
	 * This method implements the verification algorithm of the following
	 * exception handling design rule: <b>E can flow only M1,...,Mn</b>.
	 * 
	 * @return <b>true</b> if the rule is satisfied, <b>false</b> otherwise.
	 */
	public boolean check() {
		boolean canFlowOnly = true;

		Set<ClassNode> listOfAllClassesOfModuleList = new HashSet<ClassNode>();

		for (ModuleElement module : this.getModuleList()) {
			listOfAllClassesOfModuleList.addAll(CheckerUtil.toClassNodes(module.getImplElements()));
		}

		Set<ClassNode> systemClasses = CheckerUtil.getAllSystemClasses();
		systemClasses.removeAll(listOfAllClassesOfModuleList);

		final Set<ClassNode> exceptionClassNodes = CheckerUtil.toExceptionClassNodes(this.getException().getImplElements());

		Violation violation;
		for (ClassNode systemClass : systemClasses) {
			for (MethodNode method : systemClass.getAllMethods()) {
				for (ClassNode exception : exceptionClassNodes) {
					if (CheckerUtil.signals(method, exception)) {
						if (!method.getCallerMethods().isEmpty()) {
							violation = new Violation();
							violation.setMethodName(method.getName());
							violation.setMethodShortName(method.getShortName());
							violation.setExceptionName(exception.getClassName());
							for (MethodNode methodCaller : method.getCallerMethods()) {
								violation.addMethodName(methodCaller.getClassName());
								canFlowOnly = false;
							}
							this.addViolation(violation);
						}
					}
				}
			}
		}
		return canFlowOnly;
	}

	@Override
	public String getReport() {
		StringBuffer text = new StringBuffer();

		text.append(getViolationHeader());

		for (Violation violation : this.getAllViolations()) {
			text.append("\t-Exception [");
			text.append(violation.getExceptionName());
			text.append("] is flowing from method [");
			text.append(violation.getMethodName());
			text.append("] to methods [");
			Iterator<String> iterator = violation.getMethodNameList().iterator();
			while (iterator.hasNext()) {
				String methodName = iterator.next();
				text.append(methodName);
				if (iterator.hasNext())
					text.append("\n");
			}
			text.append("]\n");
		}
		return text.toString();
	}
}
