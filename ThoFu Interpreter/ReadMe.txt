ThoFu Interpreter Project ReaMe
-----------------------------------------------------------

General: 

	- all source code can be found in "src"
	- all unit tests can be found in "test"
	- all performance tests can be found in "perf"
	- all used libraries can be found in "libs"
	- some UML diagrams (made with ObjectAid) can be found in "uml"
	- example scripts can be found in "examples" (as well as in the unit tests)

ReadMe:

	A ReadMe for using the interpreter can be found in "src > de.tuhrig.thofu > gui > README.md"
	(as well as in the UI itself). It contains all commands, examples as well as additional information
	and links (e.g. to literature).

More:

	https://bitbucket.org/wordless/thofu-interpreter
	www.tuhrig.de
	
Libraries:

	I used different libraries to implement the project. Some libraries are just used for
	testing, e.g. FEST. Others are used in the project itself. Here are the necessary 
	libraries:
	
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