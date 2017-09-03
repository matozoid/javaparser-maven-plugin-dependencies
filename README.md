A sample setup for a maven plugin that runs JavaParser.
---

* `trace-code-plugin` is the actual maven plugin.
It shows how to set up dependencies and access directories and such.
The plugin itself simply adds a line to each method that prints its name (for "tracing.")
* `use-plugin` is a little project that executes the plugin on some source code.
The results can be found in its target directory.
