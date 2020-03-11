# Brainf*ck Interpreter and REPL

This is a Java implementation of a Brainf*ck interpreter. The interpreter restricts the tape size to length 300 and loops on under/overflow. I made this interpreter many years ago after encountering a Ruby REPL (pry) during an internship. The debugging capabilities REPLs provide, especially for esoteric languages like bf, allow the user to really get an idea of what their code is doing. I have not used this REPL tool in a long time, so there is a fair chance there are bugs.

In order to activate the REPL, the .bf program must contain the debug symbol (DEBUGCONST = '?' in the source). When this symbol is reached by the interpreter, the REPL is activated, allowing the user to look at the contents of the tape and insert BF code on the fly.