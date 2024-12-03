import java.util.Stack;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class CPU
{
    int[] memory;

    String program;

    int pc;
    int regA;
    int regB;
    int regC;


    public CPU() {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        memory = new int[0xFFFF];
        program = "";
    }

    public CPU(String prg, int memorySize) throws IOException {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        memory = new int[memorySize];
        this.program = readFile(prg, Charset.defaultCharset());
        //System.out.println(program);
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] eninstructiond = Files.readAllBytes(Paths.get(path));
        return new String(eninstructiond, encoding);
    }

    /*
    nop: does nothing
    hlt: halts the processor
    add<r1><r2><r3>: adds r1 and r2 and places the result in r3
    sub<r1><r2><r3>: subtracts r1 from r2 and places the result in r3
    ldi<r1><v>: loads a value into the register
    inc<r1><v>: adds a value to the register
    dec<r1><v>: subtracts a value from the register
    jmp<adr>: jumps the program counter to an address
    prt<r1>: prints the register to the output
    bz<r1><adr>: branches if r1 is 0
    bnz<r1><adr>: branches if r1 is not 0
    brn<r1><v><adr>: branches if r1 is v
    rtn: returns to the last branch or jump
     */
    public void interp()
    {
        String[] lines = program.split("\n");

        HashMap<String, Integer> labels = new HashMap<>();
        for(int i = 0; i < lines.length; i++) {
            if(lines[i].startsWith("[")) {
                String s = lines[i].replace("[", "").replace("]", "").replaceAll("\\s+", "");
                labels.put(s, i+1);
                }
        }

        String[] instruction;
        int jmpAdr = 0;
        int reg;
        Stack<Integer> rtnStack = new Stack<>();
        while(pc < lines.length) {
            String result = (lines[pc].contains("//")) ? lines[pc].substring(0, lines[pc].indexOf("//")) : lines[pc];
            instruction = result.trim().replaceAll("\\s+", " ").split(" ");
            switch(instruction[0])
            {
                case "nop":
                    pc++;
                    break;

                case "hlt":
                    return;

                case "jmp":
                    rtnStack.push(pc);
                    if(labels.containsKey(instruction[1])) {
                        pc = labels.get(instruction[1])-1;
                    } else {
                        pc = Integer.parseInt(instruction[1]) - 1;
                    }
                    break;

                case "add":
                case "sub":
                    int reg1;
                    int reg2;

                    reg1 = switch (instruction[1]) {
                        case "rA" -> getRegVal("rA");
                        case "rB" -> getRegVal("rB");
                        case "rC" -> getRegVal("rC");
                        default -> Integer.decode(instruction[1]);
                    };

                    reg2 = switch (instruction[2]) {
                        case "rA" -> getRegVal("rA");
                        case "rB" -> getRegVal("rB");
                        case "rC" -> getRegVal("rC");
                        default -> Integer.decode(instruction[2]);
                    };

                    if(instruction[0].equals("add")) {
                        switch(instruction[3]) {
                            case "rA" -> regA = reg1 + reg2;
                            case "rB" -> regB = reg1 + reg2;
                            case "rC" -> regC = reg1 + reg2;
                            default -> memory[Integer.decode(instruction[3])] = memory[reg1] + memory[reg2];
                        }
                    } else if(instruction[0].equals("sub")) {
                        switch(instruction[3]) {
                            case "rA" -> regA = reg2 - reg1;
                            case "rB" -> regB = reg2 - reg1;
                            case "rC" -> regC = reg2 - reg1;
                            default -> memory[Integer.decode(instruction[3])] = memory[reg1] + memory[reg2];
                        }
                    }
                    pc++;
                    break;

                case "ldi":
                    switch(instruction[1]) {
                        case "rA" -> regA = Integer.parseInt(instruction[2]);
                        case "rB" -> regB = Integer.parseInt(instruction[2]);
                        case "rC" -> regC = Integer.parseInt(instruction[2]);
                        default -> memory[Integer.decode(instruction[1])] = Integer.parseInt(instruction[2]);
                    }
                    pc++;
                    break;

                case "inc":
                    switch (instruction[1]) {
                        case "rA" -> regA+=Integer.parseInt(instruction[2]);
                        case "rB" -> regB+=Integer.parseInt(instruction[2]);
                        case "rC" -> regC+=Integer.parseInt(instruction[2]);
                        default -> memory[Integer.decode(instruction[1])] += Integer.parseInt(instruction[2]);
                    }
                    pc++;
                    break;

                case "dec":
                    switch (instruction[1]) {
                        case "rA" -> regA-=Integer.parseInt(instruction[2]);
                        case "rB" -> regB-=Integer.parseInt(instruction[2]);
                        case "rC" -> regC-=Integer.parseInt(instruction[2]);
                        default -> memory[Integer.decode(instruction[1])] -= Integer.parseInt(instruction[2]);
                    }
                    pc++;
                    break;

                case "prt":
                    switch(instruction[1]) {
                        case "rA" -> System.out.println(regA);
                        case "rB" -> System.out.println(regB);
                        case "rC" -> System.out.println(regC);
                        default -> System.out.println(memory[Integer.decode(instruction[1])]);
                    }
                    pc++;
                    break;

                case "bz":
                    reg = getRegVal(instruction[1]);
                    if(reg == 0) {
                        if(labels.containsKey(instruction[2])) {
                            jmpAdr = labels.get(instruction[2])-1;
                        } else {
                            jmpAdr = Integer.parseInt(instruction[2]);
                        }
                    }
                    if(reg == 0) {
                        rtnStack.push(pc);
                        pc = jmpAdr;
                    } else {
                        pc++;
                    }
                    break;

                case "bnz":
                    reg = getRegVal(instruction[1]);
                    if(reg != 0) {
                        if(labels.containsKey(instruction[2])) {
                            jmpAdr = labels.get(instruction[2])-1;
                        } else {
                            jmpAdr = Integer.parseInt(instruction[2]);
                        }
                    }
                    if(reg != 0) {
                        rtnStack.push(pc);
                        pc = jmpAdr;
                    } else {
                        pc++;
                    }
                    break;

                case "brn":
                    reg = getRegVal(instruction[1]);
                    if(reg == Integer.parseInt(instruction[2])) {
                        if(labels.containsKey(instruction[3])) {
                            jmpAdr = labels.get(instruction[3])-1;
                        } else {
                            jmpAdr = Integer.parseInt(instruction[2]);
                        }
                    }
                    if(reg == Integer.parseInt(instruction[2])) {
                        rtnStack.push(pc);
                        pc = jmpAdr;
                    } else {
                        pc++;
                    }
                    break;

                case "rtn":
                    pc = rtnStack.pop() + 1;
                    break;

                default:
                    pc++;
                    break;
            }
        }
    }

    private int getRegVal(String register) {
        return switch (register) {
            case "rA" -> regA;
            case "rB" -> regB;
            case "rC" -> regC;
            default -> memory[Integer.decode(register)];
        };
    }


    public static void main(String[] args) throws IOException {
        CPU cpu = new CPU("prg.txt", 0xFFFF);
        cpu.interp();
    }
}
