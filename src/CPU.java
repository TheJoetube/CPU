import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class CPU
{
    HashMap<String, String> memory = new HashMap<>();

    String program;

    int pc;
    int regA;
    int regB;
    int regC;


    public CPU() throws FileNotFoundException {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        program = "";
    }

    public CPU(String prg) throws IOException {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        this.program = readFile(prg, Charset.defaultCharset());
        //System.out.println(program);
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    /*
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

        String[] code;
        while(pc < lines.length) {
            String result = (lines[pc].contains("//")) ? lines[pc].substring(0, lines[pc].indexOf("//")) : lines[pc];
            code = result.trim().replaceAll("\\s+", " ").split(" ");
            int jmpAdr;
            int reg;
            switch(code[0])
            {
                case "nop":
                    pc++;
                    break;

                case "hlt":
                    return;

                case "jmp":
                    if(labels.containsKey(code[1])) {
                        pc = labels.get(code[1])-1;
                    }else {
                        pc = Integer.parseInt(code[1]) - 1;
                    }
                    break;

                case "add":
                case "sub":
                    int reg1;
                    int reg2;

                    reg1 = switch (code[1]) {
                        case "rA" -> getRegisterValue("rA");
                        case "rB" -> getRegisterValue("rB");
                        case "rC" -> getRegisterValue("rC");
                        default -> throw new IllegalArgumentException("Unknown register: " + code[1]);
                    };

                    reg2 = switch (code[1]) {
                        case "rA" -> getRegisterValue("rA");
                        case "rB" -> getRegisterValue("rB");
                        case "rC" -> getRegisterValue("rC");
                        default -> throw new IllegalArgumentException("Unknown register: " + code[2]);
                    };

                    if(code[0].equals("add")) {
                        switch(code[3]) {
                            case "rA" -> regA = reg1 + reg2;
                            case "rB" -> regB = reg1 + reg2;
                            case "rC" -> regC = reg1 + reg2;
                        }
                    } else if(code[0].equals("sub")) {
                        switch(code[3]) {
                            case "rA" -> regA = reg2 - reg1;
                            case "rB" -> regB = reg2 - reg1;
                            case "rC" -> regC = reg2 - reg1;
                        }
                    }
                    pc++;
                    break;

                case "ldi":
                    switch(code[1]) {
                        case "rA" -> regA = Integer.parseInt(code[2]);
                        case "rB" -> regB = Integer.parseInt(code[2]);
                        case "rC" -> regC = Integer.parseInt(code[2]);
                    }
                    pc++;
                    break;

                case "adi":
                    switch (code[1]) {
                        case "rA" -> regA+=Integer.parseInt(code[2]);
                        case "rB" -> regB+=Integer.parseInt(code[2]);
                        case "rC" -> regC+=Integer.parseInt(code[2]);
                    }
                    pc++;
                    break;

                case "sdi":
                    switch (code[1]) {
                        case "rA" -> regA-=Integer.parseInt(code[2]);
                        case "rB" -> regB-=Integer.parseInt(code[2]);
                        case "rC" -> regC-=Integer.parseInt(code[2]);
                    }
                    pc++;
                    break;

                case "prt":
                    switch(code[1]) {
                        case "rA" -> System.out.println(regA);
                        case "rB" -> System.out.println(regB);
                        case "rC" -> System.out.println(regC);
                    }
                    pc++;
                    break;

                case "bz":
                    jmpAdr = Integer.parseInt(code[2]);
                    reg = getRegisterValue(code[1]);
                    if(reg == 0 && labels.containsKey(code[3])) {
                        jmpAdr = labels.get(code[3])-1;
                    }
                    pc = reg == 0 ? jmpAdr : pc + 1;
                    break;

                case "bnz":
                    jmpAdr = Integer.parseInt(code[2]);
                    reg = getRegisterValue(code[1]);
                    if(reg != 0 && labels.containsKey(code[3])) {
                        jmpAdr = labels.get(code[3])-1;
                    }
                    pc = reg != 0 ? jmpAdr : pc + 1;
                    break;

                case "brn":
                    jmpAdr = Integer.parseInt(code[2]);
                    reg = getRegisterValue(code[1]);
                    if(reg == Integer.parseInt(code[2]) && labels.containsKey(code[3])) {
                        jmpAdr = labels.get(code[3])-1;
                    }
                    pc = reg == Integer.parseInt(code[2]) ? jmpAdr : pc + 1;
                    break;

                default:
                    pc++;
                    break;
            }
        }
    }

    private int getRegisterValue(String register) {
        return switch (register) {
            case "rA" -> regA;
            case "rB" -> regB;
            case "rC" -> regC;
            default -> throw new IllegalArgumentException("Unknown register: " + register);
        };
    }


    public static void main(String[] args) throws IOException {
        CPU cpu = new CPU("prg.txt");
        cpu.interp();
    }
}
