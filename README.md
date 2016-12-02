# ArCatch 

ArCatch is a tool for static-architecture conformance checking of exception handling design. ArCatch aims at enforcing exception handling design decisions in Java projects, providing: 
* A declarative domain-specific language (ArCatch.Rules) for expressing design constraints regarding exception handling; and 
* A design rule checker (ArCatch.Checker) to automatically verify the exception handling conformance. 

Furthermore, ArCatch provides support for several kinds of dependence relation concerning the exception handling design, such as raising, re-raising, remapping, signaling, handling, and flow.

ArCatch in 5 steps
------------------

1. Configuration

    ```java
    ArCatch.config("source code path", "binary code (.jar) path");
    ```
2. Module Declaration
    ```java
    ModuleElement view = ArCatch.element()
    	.module("View")
		.matching("banksys.view.\\w+")
		.build();

    ModuleElement control = ArCatch.element()
    	.module("Control")
		.matching("banksys.control.\\w+")
		.build();

    ModuleElement model = ArCatch.element()
    	.module("Model")
		.matching("banksys.model.\\w+")
		.build();
    ```
3. Exception Declaration
    ```java
    ExceptionElement controlEx = ArCatch.element()
    	.exception("ControlEx")
		.matching("banksys.control.exception.\\w+")
		.build();

    ExceptionElement modelEx = ArCatch.element()
    	.exception("ModelEx")
		.matching("banksys.model.exception.\\w+")
		.build();
    ```
4. Design Rule Especification
    ```java
    DesignRule r1 = ArCatch.rule()
    	.only(model)
		.canRaise(modelEx)
		.build();

    DesignRule r2 = ArCatch.rule()
    	.only(model)
		.canSignal(modelEx)
		.build();
    
    DesignRule r3 = ArCatch.rule()
    	.module(control)
		.mustHandle(modelEx)
		.build();

    DesignRule r4 = ArCatch.rule()
    	.module(control)
		.canOnlySignal(controlEx)
		.build();

    DesignRule r5 = ArCatch.rule()
    	.exception(modelEx)
		.cannotFlow(model, control, view)
		.build();

    DesignRule r6 = ArCatch.rule()
    	.only(control)
		.canRemap(modelEx)
		.to(controlEx)
		.build();

    DesignRule r7 = ArCatch.rule()
    	.module(view)
		.mustHandle(controlEx)
		.build();
    
    DesignRule r8 = ArCatch.rule()
    	.module(view)
		.cannotHandle(modelEx)
		.build();
    ```
5. Checking Rules
    ```java
    ArCatch.checker().addRule(r1);
    ...
    ArCatch.checker().addRule(r8);

    ArCatch.checker().checkAll();
    ```
    OR
    ```java
    boolean resultR1 = ArCatch.checker().check(r1);
    ...
    boolean resultR7 = ArCatch.checker().check(r8);
    ```

Conformance Report
------------------
ArCatch.Checker provides a report containing useful information on which design rules have been violated and where such violations take place in the software source code. If ```ArCatch.checker().checkAll()``` is used a text-based conformance chacking report is generated at ```./report``` folder. However, if ```ArCatch.checker().check(rule)``` is employed, you can access information abou rule violation performing ```rule.getReport()```. In the following we show an example of ArCatch.Checker conformance checking report:

```
========================================================================================================
ArCatch.Checker Exception Handling Conformance Checking Report
--------------------------------------------------------------------------------------------------------
Label: (V) = Rule Pass | (X) = Rule Fail
========================================================================================================
...
--------------------------------------------------------------------------------------------------------
(X) R2: only (Model) can signal (ModelEx) 8 ms

 -Model module implementation classes:
  -banksys.model.AbstractAccount
  -banksys.model.OrdinaryAccount
  -banksys.model.SavingsAccount
  -banksys.model.SpecialAccount
  -banksys.model.TaxAccount

 -ModelEx exception implementation classes:
  -banksys.model.exception.InsufficientFundsException
  -banksys.model.exception.NegativeAmountException

 -Rule Violations
	-Method [banksys.control.BankController.doDebit(java.lang.String, double)] is signaling the exception [lib.exceptions.InsufficientFundsException]
	-Method [banksys.control.BankController.doDebit(java.lang.String, double)] is signaling the exception [lib.exceptions.NegativeAmountException]
--------------------------------------------------------------------------------------------------------
...
```

External Dependencies
---------------------
In ArCatch.Checker, all source code information relevant for the checking process is extracted using:
* [Design Wizard](https://github.com/joaoarthurbm/designwizard)
* [Java Compiler Tree API](https://docs.oracle.com/javase/7/docs/jdk/api/javac/tree/)


