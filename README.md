A simple CPU written in java.
Right now it only directly executes assembly code.

It has 3 registers ```rA,rB,rC```.
Memory will be added soon.

The current instruction set:
```
nop: does nothing
hlt: halts the processor
add<r1><r2><r3>: adds r1 and r2 and places the result in r3
sub<r1><r2><r3>: subtracts r1 from r2 and places the result in r3
ldi<r1><v>: loads a value into the register
adi<r1><v>: adds a value to the register
sdi<r1><v>: subtracts a value from the register
jmp<adr>: jumps the program counter to an address
prt<r1>: prints the register to the output
bz<r1><adr>: branches if r1 is 0
bnz<r1><adr>: branches if r1 is not 0
brn<r1><v><adr>: branches if r1 is v
```
Labels are also supported, just create one by wrapping the name in ```[ ]``` (example: ```[start]```) and reference it by just typing the name without the ```[ ]``` (example: ```jmp start```).

Comments can also be made with ```//``` in a new line or at the end of a new line.

The prg.txt file has an example program written in it.

The CPU will execute the program in prg.txt by default.
