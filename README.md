# A simple CPU written in java.
Right now it only directly executes assembly code.

It has 3 registers ```rA,rB,rC```.
Memory size is 0x0000 - 0xFFFF but can be expanded at will through the classes constructor.

## The current instruction set:
```
nop: does nothing
hlt: halts the processor
add<r1><r2><r3>: adds r1 and r2 and places the result in r3
sub<r1><r2><r3>: subtracts r1 from r2 and places the result in r3
ldi<r1><v>: loads a value into the register/address
inc<r1><v>: increments the register/address by v
dec<r1><v>: decrements the register/address by v
jmp<adr>: jumps the program counter to an address
prt<r1>: prints the register/address to the output
bz<r1><adr>: branches if r1 is 0
bnz<r1><adr>: branches if r1 is not 0
brn<r1><v><adr>: branches if r1 is v
rtn: returns to the last branch or jump
shr<r1><v>: bitshifts the value in the register/address right by v
shl<r1><v>: bitshifts the value in the register/address left by v
```

## Further things:
Labels are also supported, just create one by wrapping the name in ```[ ]``` (example: ```[start]```) and reference it by just typing the name without the ```[ ]``` (example: ```jmp start```).
Comments can also be made with ```//``` in a new line or at the end of a line.

## How to run:
The prg.txt file has an example program written in it.
The CPU will execute the program in prg.txt by default.
