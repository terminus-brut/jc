# classpathless-compiler

## Logging properties
* `io.github.mkoncek.cplc.logging=[filename]` - sets the logging output, if not
provided, logging output is discarded, if empty logs into `stderr`.

* `io.github.mkoncek.cplc.loglevel=[[off] | severe | warning | info | config | fine | finer | finest | all]` -
sets the level of logging, default is `off`.

* `io.github.mkoncek.cplc.tracing` - enables very detailed logging of each
function call, requires `logging` and `loglevel` to be set.
