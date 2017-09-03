JavaParser Maven plugin sample
---

A starting point for building a plugin that parses and generates code with [JavaParser](http://www.javaparser.org)

If you want to do this without bulding a custom plugin,
get some inspiration from [JavaParser's code generator](https://github.com/javaparser/javaparser/blob/master/javaparser-core-generators/pom.xml)
which uses the `exec-maven-plugin`. 

* `trace-code-plugin` is the actual maven plugin.
It shows how to set up dependencies and access directories and such.
The plugin itself simply adds a line to each method that prints its name (for "tracing.")
* `use-plugin` is a little project that executes the plugin on some source code.
The results can be found in its target directory.

The Maven dependencies may lag behind the official releases a bit.

If you notice some problems with this setup, please open an issue. 