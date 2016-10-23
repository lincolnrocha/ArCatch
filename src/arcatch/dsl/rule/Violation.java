package arcatch.dsl.rule;

import java.util.HashSet;
import java.util.Set;

public class Violation {

	private String className;

	private String methodShortName;

	private String methodName;

	private String exceptionName;

	private String toExceptionName;

	private Set<String> exceptionNameList = new HashSet<>();

	private Set<String> methodNameList = new HashSet<>();

	public Violation() {
		super();
	}

	public Violation(String methodName, String exceptionName) {
		super();
		this.methodName = methodName;
		this.exceptionName = exceptionName;
	}

	public Violation(String methodName, String exceptionName, String toExceptionName) {
		this(methodName, exceptionName);
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getExceptionName() {
		return exceptionName;
	}

	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}

	public String getToExceptionName() {
		return toExceptionName;
	}

	public void setToExceptionName(String toExceptionName) {
		this.toExceptionName = toExceptionName;
	}

	public void addMethodName(String methodName) {
		this.methodNameList.add(methodName);
	}

	public Set<String> getMethodNameList() {
		return this.methodNameList;
	}

	public void setMethodNameList(Set<String> methodNameList) {
		this.methodNameList = methodNameList;
	}

	public void addExceptionName(String exceptionName) {
		this.exceptionNameList.add(exceptionName);
	}

	public Set<String> getExceptionNameList() {
		return this.exceptionNameList;
	}

	public void setExceptionNameList(Set<String> exceptionNameList) {
		this.exceptionNameList = exceptionNameList;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String fromClassName) {
		this.className = fromClassName;
	}

	public String getMethodShortName() {
		return methodShortName;
	}

	public void setMethodShortName(String methodShortName) {
		this.methodShortName = methodShortName;
	}

}
