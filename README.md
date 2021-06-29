# classpathless-compiler
Classpathless compiler is a tool intended to be used to recompile java sources
from decompiled classes. This tool works differently from the traditional java
compiler in that it doesn't use a provided classpath but instead pulls
dependencies using an API.

## Logging properties
* `io.github.mkoncek.cplc.logging=[filename]` - sets the logging output, if not
provided, logging output is discarded, if empty logs into `stderr`.

* `io.github.mkoncek.cplc.loglevel=[[off] | severe | warning | info | config | fine | finer | finest | all]` -
sets the level of logging, default is `off`.

* `io.github.mkoncek.cplc.tracing` - enables very detailed logging of each
function call, requires `logging` and `loglevel` to be set.
