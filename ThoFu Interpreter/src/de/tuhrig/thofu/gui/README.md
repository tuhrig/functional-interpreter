ThoFu Interpreter
=============================

The whole project as well as aditional information can be found on my personal website [tuhrig.de](http://tuhrig.de/). The source code can be found and downloaded from my [repository at Bitbucket](https://bitbucket.org/wordless).

Implemented in **Java 7**

----------

# What's ThoFu Interpreter?

It's **Thomas functional interpreter** (in the following just ThoFu ;). So you can't eat it, I'm sorry. But you can programm in a LISP like syntax and also access Java class as you will see.  
It consists of two different parts: (1) a common LISP interpreter and (2) an API to access and use all Java classes on the class-path.

# Built-in Functions & Constants

## Basic functional language constructs
	Name			Description							Example		
	----------------------------------------------------------------------------------------
	+				sums a list of objects*				(+ 1 2 3)
	-				substracts a list of objects*		(- 1 2 3)
	*				multiplies a list of objects*		(* 1 2 3)
	/				devides a list of objects*			(/ 1 2 3)
	%				modulo*								(% 3 2)

	||				logical or							(|| true false)
	&&				logical and							(&& true false)
	!				logical inversion					(! false)

	<				lesser-then*						(< 3 1)
	<=				lesser-then or equals*				(<= 3 1)
	>				greater-then*						(> 3 1)
	>=				greater-then or equals*				(>= 3 1)
	eq?				equals*								(eq? 3 1)
	nq?				not-equals*							(nq? 3 1)

	if				if-clause							(if (true) (print "true) (print "false"))

	define			defines a new variable				(define a 3)
	set! 			redefines an excisting variable		(set! a 3)
	lambda			creates a user defined function		(lambda (x) (+ x 1))
	begin			executes a list of statements		(begin (+ 1 2) (+ 1 2))
	let				defines a local scope				(let ((x 1)) (+ x 1))
	try				simple exception handling     		(try (/ 42 0) (e (print e)))

	cons			creates a new pair					(cons 1 2)
	first			returns first value of a list		(first list)
	rest			returns the rest of a list			(rest list)
	last			returns last value of a list		(last list)
	length			length of a list					(length list)
	append2			appends 2 lists						(append list1 list2)
	append3			appends 3 lists						(append list1 list2 list3)
	filter			filters a list with a filter		(filter pred list)
	quicksort		sorts a list						(quicksort '(2 5 6 1))

	load			loads a script						(load "myScript.txt")
	resource		loads a script from inside the JAR	(resource "myScript.txt")
	inspect			returns information about an object	(inspect 42)
	print			prints a given value				(print "hello")

	inc				increments a value					(inc 1)
	double			doubles a value						(double 2)
	sqr				multiplies a value with itself		(sqr 2)

	null			null								(null)
	pi				the constant PI						(print pi)
	
	pair? 			instance of	pair					(pair? var)
	number?			instance of	number					(number? var)
	list? 			instance of	list					(list? var)
	string?			instance of	string					(string? var)
	symbol? 		instance of	symbol					(symbol? var)
	operation? 		instance of	operation				(operation? var)
	lambda?			instance of	lambda					(lambda? var)

## Java language constructs
	Name			Description							Example		
	----------------------------------------------------------------------------------------
	import			imports a package or a class		(import "java.lang.*")
	interface		creates a proxy for the interface	(interface Interface.class object)
	class			creates a proxy for the class		(class Class.class object)

	Class.			creates a new object				(define a (java.lang.Object.))
	.method			calls an instance method			(.toString a)	
	.field$			calls an instance field				(.someField$ a)

	Class.class		access a class						(java.lang.Math.class)
	Class.method	calls a class method				(java.lang.Math.round aNumber)
	Class.field$	calls a class field					(java.lang.Math.PI$)
	
	instance?		instance of							(instance? 3 de.tuhrig.thofu.types.LNumber.class)

*Some of these operations can not only be used for numbers, but also for strings (e.g. (+ "a" "b")), operations (+ a b) or lists (+ '(1 2) '(3 4)). If the method is not available for the given type, an exception will be thrown.

# The Java API

ThoFu provides the ability to access and use all classes on the class-path of the JVM. A simple syntax is used for that. The syntax is mostly the same as described by Peter Norvig in [http://jscheme.sourceforge.net/jscheme/doc/javadot.html](http://jscheme.sourceforge.net/jscheme/doc/javadot.html).

## Creating a new instance

It's possible to create an instance of any Java class on the class-path of the JVM. This instance can be used later in scripts. The following example creates an instance of `java.lang.Object` and stores it in a variable:

	(define anObject (java.lang.Object.))

It's also possible to create an instance using variouse parameters in the constructor. The following exmaple creates an instance of `java.math.BigDecimal` with an instance of `java.lang.Integer` as an input parameter:

	(define anInteger (java.lang.Integer. 3))
	(define aBigDecimal (java.math.BigDecimal. anInteger))

## Call an instance method

After creating a Java object, any instance method can be called on this object. The following example shows how to call the `toString()` method on a instance of `java.lang.Integer`:

	(define anInteger (java.lang.Integer. 3))
	(.toString anInteger)

## Access an instance field

Any instance field can be used in a script. The following line reads a field constant of an object:

	(.aField$)

## Accessa a class

A Java class can be accessed like the following:

	(define aClass (java.lang.Object.class))

After this call, the variable `aClass` contains a class object (`Class<Object>`).

## Call a class method

It's possible to call any class-method (a static method) of a class. The following example shows how a number can be rounded, using the `round(double a)` method of `java.lang.Math`:

	(define aDouble (java.lang.Double. 2.123))
	(java.lang.Math.round aDouble)

## Access a class field

Any static class field can be used in a script. The following line reads the `PI` constant of `java.lang.Math`:

	(java.lang.Math.PI$)

## Implement an interface or a class

ThoFu provides a wrapper-technique to implement any interface or (abstract class) using ThoFu-object-like structures. The following example shows hot to create an object that implements the `java.awt.event.ActionListener` interface:

	(define (listener name)
		(define (actionPerformed e) (.setText label "hello"))
		(if (eq? name 'actionPerformed) actionPerformed error))
	
	(define int (interface ActionListener.class listener))

First of all, a ThoFu structure is defined (called `listener` in the exmaple above). This structure must provide a getter-method for each method of the interface. In this case, the interface has only one method called `actionPerformed`. The ThoFu structure must return a user defined function (a *lambda*) when it's called with the method name.

In the second step, this structure is given to a helper method called `interface` (for a class use the keyword `class` instead). This method will return a Java object implementing the specified interface. The Java object will use the given ThoFu structure to handle the method calls.

# Resources

## Literature

- [Norvig.com](http://norvig.com) (a set of quite good tutorials on how to implement a LISP interpreter)
- [Robust Java benchmarking](http://www.ibm.com/developerworks/java/library/j-benchmark1/index.html) (an artical about micro benchmarking in Java)

## Libraries

- [Javassist](http://www.jboss.org/javassist) (used for creating interfaces and classes on the fly)
- [Apache Commons Lang 3](http://commons.apache.org/lang) (used to accesing the Java library in the Java extension)
- [Log4J](http://logging.apache.org/log4j) (for logging ;)
- [RSyntaxTextArea](http://fifesoft.com/rsyntaxtextarea) (used for the editors in the UI)
- [Autocomplete](http://fifesoft.com/autocomplete) (used fot the autocompletion of the editors in the UI)
- [MarkdownJ 1.0](http://code.google.com/p/markdownj) (to display Markdown code as HTML in a JEditorPane)
- [SwingX 1.6](http://swingx.java.net) (used for the resizable layout of the UI)
- [FEST](http://code.google.com/p/fest) (used for automated UI testing)
- [JFreeChart](http://www.jfree.org/jfreechart) (to visualize performance test results)
- [JBenchmark](http://www.ibm.com/developerworks/java/library/j-benchmark1/index.html) (used for performance tests)

## Tools

- [Racket](http://racket-lang.org) (a LISP implementation used as a reference for the project)
- [JScheme](http://jscheme.sourceforge.net/jscheme/main.html) (a Scheme implementation on the JVM)

# Project Organisation

The project is organized as a common **Java Eclipse projekt**. It has the following (simplified) file structure:

	project
	|-- src (Java source code)
		|-- de.tuhrig (contains basic classes)
			|-- gui (contains the GUI)
				|-- icons (iocns for the UI)
				|-- README.md (this file)
			|-- interfaces	(contains a set of interfaces)
			|-- java (contains the API to Java)
			|-- types (contains basic LISP types)
		|-- inti.txt (LISP file loaded on the startup of the interpreter)
	|-- test (JUnit tests)
	|-- perf (performance tests)
	|-- examples (runnable example scripts)
	|-- libs (third-party JARs)
 	|-- uml (UML diagrams)
		|-- Types.png
		|-- GUI.png
		|-- Interpreter.png
	|-- license.txt
	|-- ReadMe.txt
	

# Examples

The following section shows a couple of exampls for the ThoFu language. Every example gives the exact input that can be copied and pasted to the interpreter. Also, the exact output is shown.

## Basics

### A simple calculation

	>> (+ 1 2) 
	3

### A more sophisticated calculation

    >> (+ 1 (* 1 (+ 1 (- 2 (/ 12 3)))))
    0

## Algorithems

### Quicksort

**Input:**

	(define length (lambda (L) 
	    (if (eq? L null) 
	        0 
	    ;else
	        (+ (length (rest L)) 1))))
	(define (append2 l1 l2) 
	    (if (eq? l1 null) 
	        l2 (cons (first l1) 
	    ;else
	        (append2 (rest l1) l2))))
	(define (append3 a b c) (append2 a (append2 b c)))
	(define (filter pred list) 
	    (if (eq? list null) 
	        null 
	    (if (pred (first list)) 
	        (cons (first list) (filter  pred (rest list))) 
	    ;else
	        (filter pred (rest list)))))
	(define (quicksort list) 
	    (if (<= (length list) 1) 
	        list 
	    ;else
	        (let ((pivot (first list))) 
	            (append3 (quicksort (filter(lambda (x) (< x pivot)) list)) 
	            (filter(lambda (x) (eq? x pivot)) list) 
	            (quicksort (filter(lambda (x) (> x pivot)) list))))))
	(quicksort '(1 8 7 5 3))

**Output:**

	'(1 3 5 7 8)

## Java API

### Creating a simple Swing UI

The following exmaple creates a simple `JFrame` window with a `JButton` and a `JLabel`. When the button is pressed, it will change the text of the label from "click me" to "hello".

	; Imports
	(import "java.awt.event.*")
	(import "javax.swing.*")
	(import "java.lang.*")
	(import "java.awt.*")
		
	; Components
	(define win (JFrame. "JavaExample1"))
	(define button (JButton. "click me"))
	(define label (JLabel. "nothing"))
	(define layout (FlowLayout.))
	
	; Variables
	(define visible (Boolean. true))
	(define size (Integer. 300))
	
	; Listeners
	(define (listener name)
		(define (actionPerformed e) (.setText label "hello"))
		(if (eq? name 'actionPerformed) actionPerformed error))
	
	(define int (interface ActionListener.class listener))
	
	; Construction 
	(.setLayout win layout)
	(.setSize win size size)
	(.add win button)
	(.add win label)
	(.addActionListener button int)
	(.setVisible win visible))

# License

## Simplified BSD License

see: [http://opensource.org/licenses/bsd-license.php](http://opensource.org/licenses/bsd-license.php)

see: [www.tuhrig.de](www.tuhrig.de)

see: **Thomas Uhrig**

Copyright (c) 2012, Thomas Uhrig
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.