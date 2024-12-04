# A simple CPU written in java.
Right now it only directly executes assembly code.

It has 3 registers ```rA,rB,rC```. <b/>
Memory size is 0x0000 - 0xFFFF but can be expanded at will through the classes constructor or the CPU variable memSize.

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
bn<r1><v><adr>: branches if r1 is v
bnn<r1><v><adr>: branches if r1 is not v
ret: returns to the last branch or jump
shr<r1><v>: bitshifts the value in the register/address right by v
shl<r1><v>: bitshifts the value in the register/address left by v
mov<r1><r2>: copies the value of r1 into r2
```

## Additionally:
Labels are also supported, just create one by wrapping the name in ```[ ]``` (example: ```[start]```) and reference it by just typing the name without the ```[ ]``` (example: ```jmp start```). <b/>
Comments can also be made with ```//``` in a new line or at the end of a line. <b/>
Certain CPU specific values can be set via a ```.``` followed by their name and arguments (e.g. ```.memSize 0xFFFF```). 
It is recommended to set them at the top of the prg file, although they should be able to be anywhere in the code. 
#### The current list of CPU variables:
```
memSize: Specifies the memory size of the CPU (if not done before via the constructor). This can be either a normal integer (e.g. 65535 for 0xFFFF) or a hex number prefixed with a 0x.
sLabel: Sets the label from which the processor will start running code. All code before this label will be skipped if it is set. 
```

## How to run:
The prg.txt file has an example program written in it.
The CPU will execute the program in prg.txt by default.
