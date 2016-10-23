package arcatch.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.designwizard.design.ClassNode;
import org.designwizard.design.MethodNode;
import org.designwizard.exception.InexistentEntityException;
import org.designwizard.main.DesignWizard;

import arcatch.ast.ExceptionRemapProcessor;
import arcatch.ast.ExceptionRemapScanner;
import arcatch.ast.ExceptionReraiseProcessor;
import arcatch.ast.ExceptionReraiseScanner;

public class CheckerUtil {

	private static DesignWizard designWizard = null;

	public static void config() {
		try {

			CheckerUtil.designWizard = new DesignWizard(TargetSystemInfo.getInstance().getBinaryPath());
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public static Set<ClassNode> getAllSystemClasses() {
		return CheckerUtil.designWizard.getAllClasses();
	}

	public static Set<ClassNode> getAllSystemExceptionClasses() {

		Collection<String> classNames = new ArrayList<String>();
		for (ClassNode classNode : CheckerUtil.designWizard.getAllClasses()) {
			classNames.add(classNode.getName());
		}

		Set<ClassNode> allExceptionClasses = new HashSet<ClassNode>();
		Class<?> exceptionClass = java.lang.Exception.class;
		if (classNames != null && !classNames.isEmpty()) {
			for (String className : classNames) {
				try {
					if (exceptionClass.isAssignableFrom(Class.forName(className))) {
						allExceptionClasses.add(CheckerUtil.designWizard.getClass(className));
					}
				} catch (ClassNotFoundException cne) {
					System.err.println(cne);
				} catch (InexistentEntityException iee) {
					System.err.println(iee);
				}
			}
		}
		return allExceptionClasses;
	}

	/**
	 * This method selects all exceptions raised by a specific method.
	 * 
	 * @param method
	 *            an method under consideration.
	 * @return a set of exceptions raised by the <b>method</b>.
	 */
	public static Set<ClassNode> getRaisedExceptions(MethodNode method) {
		Set<ClassNode> raisedExceptions = new HashSet<>();

		if (!method.isAbstract() && !method.getDeclaringClass().isInterface()) {

			Set<ClassNode> signaledExceptions = method.getThrownExceptions();
			for (ClassNode exception : signaledExceptions) {
				if (raises(method, exception)) {
					raisedExceptions.add(exception);
				}
			}

			Set<ClassNode> catchedExceptions = method.getCatchedExceptions();
			for (ClassNode exception : catchedExceptions) {
				if (raises(method, exception)) {
					raisedExceptions.add(exception);
				}
			}

			Set<MethodNode> callees = method.getCalleeMethods();
			for (MethodNode callee : callees) {
				Set<ClassNode> propagatedExceptions = callee.getThrownExceptions();
				for (ClassNode propagatedException : propagatedExceptions) {
					if (raises(method, propagatedException)) {
						raisedExceptions.add(propagatedException);
					}
				}
			}
		}

		return raisedExceptions;
	}

	/**
	 * This method checks if a given method raises a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method raises the exception, <b>false</b>
	 *         otherwise.
	 */
	public static boolean raises(MethodNode method, ClassNode exception) {
		if (method.isAbstract()) {
			return false;
		} else {
			boolean signals = signals(method, exception);
			boolean propagates = propagates(method, exception);
			boolean handle = handles(method, exception);
			boolean instanciates = instanciates(method, exception);
			return ((signals && !propagates) || (handle && !signals && !propagates) || (propagates && signals)) && instanciates;
		}
	}

	/**
	 * This method checks if a given method signals a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method signals the exception, <b>false</b>
	 *         otherwise.
	 */
	public static boolean signals(MethodNode method, ClassNode exception) {
		return method.getThrownExceptions().contains(exception);
	}

	/**
	 * This method checks if a given method handle a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method handle the exception, <b>false</b>
	 *         otherwise.
	 */
	public static boolean handles(MethodNode method, ClassNode exception) {
		return method.getCatchedExceptions().contains(exception);
	}

	/**
	 * This method checks if a given method propagates a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method propagates the exception, <b>false</b>
	 *         otherwise.
	 */
	public static boolean propagates(MethodNode method, ClassNode exception) {
		Set<MethodNode> callees = method.getCalleeMethods();
		for (MethodNode callee : callees) {
			if (signals(callee, exception)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method checks if a given method instantiates a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method instantiates the exception,
	 *         <b>false</b> otherwise.
	 */
	public static boolean instanciates(MethodNode method, ClassNode exception) {
		Set<MethodNode> calles = method.getCalleeMethods();
		Set<MethodNode> constructors = exception.getConstructors();
		calles.retainAll(constructors);
		return !calles.isEmpty();
	}

	/**
	 * This method checks if a given method re-raises a specific exception.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exception
	 *            is an exception under consideration.
	 * @return <b>true</b> if the method re-raises the exception, <b>false</b>
	 *         otherwise.
	 */
	public static boolean reraises(MethodNode method, ClassNode exception) {
		if (!method.isAbstract()) {
			final String srcPath = (TargetSystemInfo.getInstance().getSourcePath().endsWith("/")) ? TargetSystemInfo.getInstance().getSourcePath() : TargetSystemInfo.getInstance().getSourcePath()
					+ File.pathSeparator;
			final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

			try (final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null)) {
				String className = method.getClassNode().getClassName();
				className = className.replace(".", File.separator);
				File classFile = new File(srcPath + className + ".java");

				final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(classFile));

				final CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);
				String methodName = method.getShortName().replaceAll("<init>", method.getClassNode().getShortName());
				methodName = methodName.replaceAll("\\((.*)\\)", "");
				final ExceptionReraiseScanner scanner = new ExceptionReraiseScanner(methodName, exception.getShortName());
				final ExceptionReraiseProcessor processor = new ExceptionReraiseProcessor(scanner);
				task.setProcessors(Arrays.asList(processor));
				task.call();
				return scanner.isReraised();
			} catch (IOException ioe) {

			}
		}
		return false;
	}

	/**
	 * This method checks if a given method re-maps a specific exception to
	 * another.
	 * 
	 * @param method
	 *            is a given method.
	 * @param exceptionFrom
	 *            is the source exception.
	 * @param exceptionTo
	 *            is the target exception.
	 * 
	 * @return <b>true</b> if the method re-maps the exception exceptionFrom to
	 *         exceptionTo, <b>false</b> otherwise.
	 */
	public static boolean remaps(MethodNode method, ClassNode exceptionFrom, ClassNode exceptionTo) {
		if (!method.isAbstract()) {
			final String srcPath = (TargetSystemInfo.getInstance().getSourcePath().endsWith("/")) ? TargetSystemInfo.getInstance().getSourcePath() : TargetSystemInfo.getInstance().getSourcePath()
					+ File.pathSeparator;
			final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
			final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

			try (final StandardJavaFileManager manager = compiler.getStandardFileManager(diagnostics, null, null)) {
				String className = method.getClassNode().getClassName();
				className = className.replace(".", File.separator);
				File classFile = new File(srcPath + className + ".java");

				final Iterable<? extends JavaFileObject> sources = manager.getJavaFileObjectsFromFiles(Arrays.asList(classFile));

				final CompilationTask task = compiler.getTask(null, manager, diagnostics, null, null, sources);

				String methodName = method.getShortName().replaceAll("<init>", method.getClassNode().getShortName());
				methodName = methodName.replaceAll("\\((.*)\\)", "");
				final ExceptionRemapScanner scanner = new ExceptionRemapScanner(methodName, exceptionFrom.getShortName(), exceptionTo.getShortName());
				final ExceptionRemapProcessor processor = new ExceptionRemapProcessor(scanner);
				task.setProcessors(Arrays.asList(processor));
				task.call();
				return scanner.isRemapped();
			} catch (IOException ioe) {

			}
		}
		return false;
	}

	public static boolean flows(List<Set<MethodNode>> chain, ClassNode exception) {
		boolean isFlowing = false;
		for (int i = chain.size() - 1; i >= 0; i--) {
			for (MethodNode callee : chain.get(i)) {
				if (handles(callee, exception)) {
					isFlowing = flows(chain, callee, exception, chain.size() - 2);
					if (isFlowing) {
						return isFlowing;
					}
				}
			}
		}
		return isFlowing;
	}

	private static boolean flows(final List<Set<MethodNode>> chain, MethodNode caller, ClassNode exception, int level) {
		boolean isFlowing = false;
		if (level >= 0) {
			for (MethodNode callee : chain.get(level)) {
				if (isCalledBy(callee, caller) && signals(callee, exception)) {
					isFlowing = flows(chain, callee, exception, level - 1);
					if (isFlowing) {
						return isFlowing;
					}
				}
			}
		} else {
			return signals(caller, exception) ? true : false;
		}
		return isFlowing;
	}

	public static List<Set<MethodNode>> callChain(List<Set<ClassNode>> list) {
		List<Set<MethodNode>> chain = new ArrayList<Set<MethodNode>>();
		for (int i = 0; i < list.size() - 1; i++) {
			chain.add(calleeMethodsBy(list.get(i), list.get(i + 1)));
		}

		chain.add(callerMethods(list.get(list.size() - 2), list.get(list.size() - 1)));

		for (int i = 1; i < chain.size(); i++) {
			chain.get(i).retainAll(callerMetodos(chain.get(i - 1)));
		}
		return chain;
	}

	private static Set<MethodNode> callerMetodos(Set<MethodNode> callees) {
		Set<MethodNode> callers = new HashSet<MethodNode>();
		for (MethodNode callee : callees) {
			callers.addAll(callee.getCallerMethods());
		}
		return callers;
	}

	private static Set<MethodNode> calleeMethodsBy(Set<ClassNode> callees, Set<ClassNode> callers) {
		Set<MethodNode> calleeMethods = new HashSet<MethodNode>();
		for (ClassNode caller : callers) {
			for (ClassNode callee : callees) {
				calleeMethods.addAll(calleeMethodsBy(callee, caller));
			}
		}
		return calleeMethods;
	}

	private static Set<MethodNode> calleeMethodsBy(ClassNode callee, ClassNode caller) {
		Set<MethodNode> calleeMethods = new HashSet<MethodNode>();
		for (MethodNode callerMethod : caller.getAllMethods()) {
			for (MethodNode calleeMethod : callee.getAllMethods()) {
				if (isCalledBy(calleeMethod, callerMethod)) {
					calleeMethods.add(calleeMethod);
				}
			}
		}
		return calleeMethods;
	}

	private static Set<MethodNode> callerMethods(Set<ClassNode> callees, Set<ClassNode> callers) {
		Set<MethodNode> calleeMethods = new HashSet<MethodNode>();
		for (ClassNode callerClass : callers) {
			for (ClassNode calleeClass : callees) {
				calleeMethods.addAll(callerMethods(calleeClass, callerClass));
			}
		}
		return calleeMethods;
	}

	private static Set<MethodNode> callerMethods(ClassNode callee, ClassNode caller) {
		Set<MethodNode> calleeMethods = new HashSet<MethodNode>();
		for (MethodNode callerMethod : caller.getAllMethods()) {
			for (MethodNode calleeMethod : callee.getAllMethods()) {
				if (isCalledBy(calleeMethod, callerMethod)) {
					calleeMethods.add(callerMethod);
				}
			}
		}
		return calleeMethods;
	}

	private static boolean isCalledBy(MethodNode callee, MethodNode caller) {
		return caller.getCalleeMethods().contains(callee);
	}

	/**
	 * This method retrieves a set of Java classes (represented as
	 * <b>org.designwizard.design.ClassNode</b> from DesignWizard tool) that
	 * matches to the given regular expression.
	 * 
	 * @param regex
	 *            a given regular expression.
	 * @return a set of classes matched to the <b>regex</b>.
	 * 
	 */
	public static Set<ClassNode> toClassNodes(Set<String> moduleClassNames) {
		Set<Class<?>> moduleClasses = new HashSet<Class<?>>();

		if (moduleClassNames != null && !moduleClassNames.isEmpty()) {
			for (String className : moduleClassNames) {
				try {
					moduleClasses.add(Class.forName(className));
				} catch (ClassNotFoundException cne) {
					System.err.println(cne);
				}
			}
		}
		return convertClassTypesToClassNodes(moduleClasses);
	}

	public static Set<ClassNode> toExceptionClassNodes(Set<String> exceptionClassNames) {
		Set<Class<?>> moduleClasses = new HashSet<Class<?>>();
		if (exceptionClassNames != null && !exceptionClassNames.isEmpty()) {
			for (String exceptionName : exceptionClassNames) {
				try {
					moduleClasses.add(Class.forName(exceptionName));
				} catch (ClassNotFoundException cne) {
					System.err.println(cne);
				}
			}
		}
		return convertClassTypesToClassNodes(moduleClasses);
	}

	/**
	 * This method converts Java's class types (<b>java.lang.Class</b>) into a
	 * DesignWizard's class node (<b>org.designwizard.design.ClassNode</b>).
	 * 
	 * @param classTypes
	 * @return
	 */
	private static Set<ClassNode> convertClassTypesToClassNodes(Set<Class<?>> classTypes) {
		Set<ClassNode> classNodes = new HashSet<ClassNode>();
		for (Class<?> classType : classTypes) {
			try {
				classNodes.add(CheckerUtil.designWizard.getClass(classType));
			} catch (InexistentEntityException iee) {
				throw new RuntimeException(iee);
			}
		}
		return classNodes;
	}

	public static Collection<String> getResourcesFromClassPath(String regex) {
		final ArrayList<String> result = new ArrayList<String>();
		final String classPath = System.getProperty("java.class.path", ".") + ":" + TargetSystemInfo.getInstance().getBinaryPath();
		final String[] classPathElements = classPath.split(":");
		final Pattern pattern = Pattern.compile(regex + ".class");
		for (final String element : classPathElements) {
			result.addAll(getResources(element, pattern));
		}
		return result;
	}

	public static Collection<String> getResourcesFromClassPathAndRuntimeLib(String regex) {
		final ArrayList<String> result = new ArrayList<String>();
		final String classPath = System.getProperty("java.class.path", ".") + ":" + System.getProperty("java.home") + "/lib/rt.jar" + ":" + TargetSystemInfo.getInstance().getBinaryPath();
		final String[] classPathElements = classPath.split(":");
		final Pattern pattern = Pattern.compile(regex + ".class");
		for (final String element : classPathElements) {
			result.addAll(getResources(element, pattern));
		}
		return result;
	}

	private static Collection<String> getResources(final String element, final Pattern pattern) {
		final ArrayList<String> result = new ArrayList<String>();
		final File file = new File(element);
		if (file.isDirectory()) {
			result.addAll(getResourcesFromDirectory(file, pattern));
		} else {
			result.addAll(getResourcesFromJarFile(file, pattern));
		}
		return result;
	}

	private static Collection<String> getResourcesFromJarFile(final File file, final Pattern pattern) {
		final ArrayList<String> result = new ArrayList<String>();
		ZipFile zip;
		try {
			zip = new ZipFile(file);
		} catch (final ZipException zipe) {
			throw new Error(zipe);
		} catch (final IOException ioe) {
			throw new Error(ioe);
		}
		final Enumeration<? extends ZipEntry> element = zip.entries();
		while (element.hasMoreElements()) {
			final ZipEntry entry = (ZipEntry) element.nextElement();
			final String fileName = entry.getName();
			final boolean accept = pattern.matcher(fileName).matches();

			if (accept && !fileName.contains("$")) {
				result.add(fileName.replaceAll("/", ".").replace(".class", ""));
			}
		}
		try {
			zip.close();
		} catch (final IOException ioe) {
			throw new Error(ioe);
		}
		return result;
	}

	private static Collection<String> getResourcesFromDirectory(final File directory, final Pattern pattern) {
		final ArrayList<String> result = new ArrayList<String>();
		final File[] fileList = directory.listFiles();
		for (final File file : fileList) {
			if (file.isDirectory()) {
				result.addAll(getResourcesFromDirectory(file, pattern));
			} else {
				try {
					final String fileName;
					if (!TargetSystemInfo.getInstance().getBinaryPath().endsWith(".jar")) {
						fileName = file.getCanonicalPath().replaceAll(TargetSystemInfo.getInstance().getBinaryPath(), "");
					} else {
						fileName = file.getCanonicalPath();
					}
					final boolean accept = pattern.matcher(fileName).matches();

					if (accept && !fileName.contains("$")) {
						result.add(fileName.replaceAll("/", ".").replace(".class", ""));
					}
				} catch (final IOException ioe) {
					throw new Error(ioe);
				}
			}
		}
		return result;
	}
}
