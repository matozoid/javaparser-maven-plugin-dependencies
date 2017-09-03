JavaParser Maven plugin sample
---

A fully working sample Maven plugin that parses and generates code with [JavaParser](http://www.javaparser.org)

If you want to do this without bulding a custom plugin,
get some inspiration from [JavaParser's code generator](https://github.com/javaparser/javaparser/blob/master/javaparser-core-generators/pom.xml)
which uses the `exec-maven-plugin`. 

* `trace-code-plugin` is the actual maven plugin.
It demonstrates how to read and process source code and how to write it out again.
The plugin itself simply adds a line to each method that prints its name (for "tracing.")
* `use-plugin` is a little project that executes the plugin on some source code.
The results can be found in its target directory.

The Maven dependencies may lag behind the official releases a bit.

If you notice some problems with this setup, please open an issue. 