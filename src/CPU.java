import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;

public class CPU
{
    View ui;

    ArrayList<String> trackedList = new ArrayList<>();

    boolean noUI = false;
    boolean stepMode = false;
    boolean step = false;

    int[] memory;

    String program;

    int pc, regA, regB, regC;


    public CPU() {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        memory = new int[0xFFFF + 1];
        program = "";
    }

    public CPU(String prg, int memorySize, boolean pUI) throws IOException {
        pc = 0;
        regA = 0;
        regB = 0;
        regC = 0;
        memory = new int[memorySize + 1];
        this.program = readFile(prg, Charset.defaultCharset());
        noUI = pUI;
        if(!noUI) {
            ui = new View();
            JFrame frame = new JFrame("CPU");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.getContentPane().add(ui);
            frame.pack();
            frame.setVisible(true);

            // Synchronize `stepMode` with the initial state of `pauseBtn`
            stepMode = ui.pauseBtn.isSelected();

            ui.pauseBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!ui.pauseBtn.isSelected()) {
                        ui.pauseBtn.setText("PAUSE");
                        resumeStep();
                    } else {
                        ui.pauseBtn.setText("RESUME");
                    }
                }
            });

            ui.stepBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    resumeStep();
                }
            });

            ui.trackBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!ui.trackTextField.getText().matches("^[0-9a-fA-F]+$") && Integer.decode(ui.trackTextField.getText()) < memory.length + 1 && Integer.decode(ui.trackTextField.getText()) > -1) {
                        trackedList.add(ui.trackTextField.getText());
                        ui.trackTextField.setText("");
                    }
                }
            });

            ui.removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(!ui.trackTextField.getText().matches("^[0-9a-fA-F]+$") && Integer.decode(ui.trackTextField.getText()) < memory.length + 1 && Integer.decode(ui.trackTextField.getText()) > -1) {
                        trackedList.remove(ui.trackTextField.getText());
                        ui.trackTextField.setText("");
                    }
                }
            });
        }
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
    bn<r1><v><adr>: branches if r1 is v
    bnn<r1><v><adr>: branches if r1 is not v
    rtn: returns to the last branch or jump
    shr<r1><v>: bitshifts the value in the register/address right by v
    shl<r1><v>: bitshifts the value in the register/address left by v
    mov<r1><r2>: copies the value of r1 into r2
    and<r1><r2><r3>: AND's r1 and r2 and stores the result in r3
    or<r1><r2><r3>: OR's r1 and r2 and stores the result in r3
    xor<r1><r2><r3>: XOR's r1 and r2 and stores the result in r3
    not<r1>: NOT's r1 and stores the value in that same register/address
     */
    public void interp()
    {
        String[] lines = program.split("\n");
        String startLabel = null;

        //Store the labels in a map with their line number and set cpu vars
        HashMap<String, Integer> labels = new HashMap<>();
        for(int i = 0; i < lines.length; i++) {
            if(lines[i].startsWith("[")) {
                String s = lines[i].replace("[", "").replace("]", "").replaceAll("\\s+", "");
                labels.put(s, i+1);
            } else if(lines[i].startsWith(".")) {
                //set vars
                String[] conf = lines[i].trim().split(" ");
                switch(conf[0].replace(".", "").replaceAll("\\s+", "")) {
                    case "memSize" -> memory = new int[Integer.decode(conf[1]) + 1];
                    case "sLabel" -> startLabel = conf[1];
                    default -> System.out.println("Unknown CPU var");
                }
            }
        }

        String[] instruction;
        int jmpAdr = 0;
        int reg;
        String out = "";
        Stack<Integer> rtnStack = new Stack<>();
        if(startLabel != null) {
            pc = labels.get(startLabel);
        }
        while(pc < lines.length) {
            String result = (lines[pc].contains("//")) ? lines[pc].substring(0, lines[pc].indexOf("//")) : lines[pc];
            instruction = result.trim().replaceAll("\\s+", " ").split(" ");

            if(!noUI) {
                stepMode = ui.pauseBtn.isSelected();
                if (stepMode) {
                    updateView(result, lines, out);
                    waitForStep();
                }
            }

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
                case "and":
                case "or":
                case "xor":
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

                    switch (instruction[0]) {
                        case "add" -> {
                            switch (instruction[3]) {
                                case "rA" -> regA = reg1 + reg2;
                                case "rB" -> regB = reg1 + reg2;
                                case "rC" -> regC = reg1 + reg2;
                                default -> memory[Integer.decode(instruction[3])] = memory[reg1] + memory[reg2];
                            }
                        }
                        case "sub" -> {
                            switch (instruction[3]) {
                                case "rA" -> regA = reg2 - reg1;
                                case "rB" -> regB = reg2 - reg1;
                                case "rC" -> regC = reg2 - reg1;
                                default -> memory[Integer.decode(instruction[3])] = memory[reg1] - memory[reg2];
                            }
                        }
                        case "and" -> {
                            switch (instruction[3]) {
                                case "rA" -> regA = reg2 & reg1;
                                case "rB" -> regB = reg2 & reg1;
                                case "rC" -> regC = reg2 & reg1;
                                default -> memory[Integer.decode(instruction[3])] = memory[reg1] & memory[reg2];
                            }
                        }

                        case "or" -> {
                            switch (instruction[3]) {
                                case "rA" -> regA = reg2 | reg1;
                                case "rB" -> regB = reg2 | reg1;
                                case "rC" -> regC = reg2 | reg1;
                                default -> memory[Integer.decode(instruction[3])] = memory[reg1] | memory[reg2];
                            }
                        }

                        case "xor" -> {
                            switch (instruction[3]) {
                                case "rA" -> regA = reg2 ^ reg1;
                                case "rB" -> regB = reg2 ^ reg1;
                                case "rC" -> regC = reg2 ^ reg1;
                                default -> memory[Integer.decode(instruction[3])] = memory[reg1] ^ memory[reg2];
                            }
                        }
                    }
                    pc++;
                    break;

                case "not":
                    switch (instruction[1]) {
                        case "rA" -> regA = ~getRegVal("rA");
                        case "rB" -> regB = ~getRegVal("rB");
                        case "rC" -> regC = ~getRegVal("rC");
                        default -> memory[Integer.decode(instruction[1])] = ~memory[Integer.decode(instruction[1])];
                    };
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
                    out = String.valueOf(getRegVal(instruction[1]));
                    System.out.println(out);
                    if(!noUI) {
                        ui.consoleField.append(out + "\n");
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

                case "bnn":
                    reg = getRegVal(instruction[1]);
                    if(reg != Integer.parseInt(instruction[2])) {
                        if(labels.containsKey(instruction[3])) {
                            jmpAdr = labels.get(instruction[3])-1;
                        } else {
                            jmpAdr = Integer.parseInt(instruction[2]);
                        }
                    }
                    if(reg != Integer.parseInt(instruction[2])) {
                        rtnStack.push(pc);
                        pc = jmpAdr;
                    } else {
                        pc++;
                    }
                    break;

                case "bn":
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

                case "ret":
                    pc = rtnStack.pop() + 1;
                    break;

                case "shr":
                case "shl":
                    reg = getRegVal(instruction[1]);
                    if(instruction[0].equals("shr")) {
                        reg = reg >> Integer.parseInt(instruction[2]);
                    }
                    else {
                        reg = reg << Integer.parseInt(instruction[2]);
                    }
                    switch(instruction[1]) {
                        case "rA" -> regA = reg;
                        case "rB" -> regB = reg;
                        case "rC" -> regC = reg;
                        default -> memory[getRegVal(instruction[2])] = reg;
                    }
                    pc++;
                    break;    

                case "mov":
                    reg = getRegVal(instruction[1]);
                    switch(instruction[2]) {
                        case "rA" -> regA = reg;
                        case "rB" -> regB = reg;
                        case "rC" -> regC = reg;
                        default -> memory[getRegVal(instruction[2])] = reg;
                    }
                    pc++;
                    break;

                default:
                    if (!instruction[0].startsWith(".") && !instruction[0].isBlank() && !labels.containsKey(instruction[0].replace("[", "").replace("]", ""))) {
                        out = "Unknown instruction: " + instruction[0] + " at line " + (pc + 1);
                        System.out.println(out);
                        if(!noUI) {
                            ui.consoleField.append(out + "\n");
                        }
                    }                    
                    pc++;
                    break;
            }
            if(!noUI) {
                updateView(result, lines, out);
            }
        }
    }

    private void waitForStep() {
        if (stepMode) {
            synchronized (this) {
                try {
                    while (!step) {
                        wait();
                    }
                    step = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void resumeStep() {
        synchronized (this) {
            step = true;
            notify();
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

    public void updateView(String inst, String[] lines, String out) {
        // Update instruction text
        ui.pcTxt.setText("PC: " + pc);
        ui.instructionTxt.setText("Current instruction: " + inst);

        // Determine format type
        boolean isHex = ui.hexBtn.isSelected();
        boolean isBin = ui.binBtn.isSelected();

        // Preformat register values
        String regAFormatted, regBFormatted, regCFormatted;
        if (isHex) {
            regAFormatted = "A: 0x" + Integer.toHexString(regA).toUpperCase();
            regBFormatted = "B: 0x" + Integer.toHexString(regB).toUpperCase();
            regCFormatted = "C: 0x" + Integer.toHexString(regC).toUpperCase();
        } else if (isBin) {
            regAFormatted = "A: " + String.format("%8s", Integer.toBinaryString(regA)).replace(' ', '0');
            regBFormatted = "B: " + String.format("%8s", Integer.toBinaryString(regB)).replace(' ', '0');
            regCFormatted = "C: " + String.format("%8s", Integer.toBinaryString(regC)).replace(' ', '0');
        } else {
            regAFormatted = "A: " + regA;
            regBFormatted = "B: " + regB;
            regCFormatted = "C: " + regC;
        }

        ui.regATxt.setText(regAFormatted);
        ui.regBTxt.setText(regBFormatted);
        ui.regCTxt.setText(regCFormatted);

        // Use StringBuilder for memory and program display
        StringBuilder memoryBuilder = new StringBuilder();
        StringBuilder programBuilder = new StringBuilder();
        StringBuilder trackedBuilder = new StringBuilder();

        for (int i = 0; i < memory.length; i++) {
            if (i > 0) {
                if (i % 4 == 0) {
                    memoryBuilder.append("\n");
                } else {
                    memoryBuilder.append(" | ");
                }
            }

            if (isHex) {
                memoryBuilder.append(String.format("0x%04X: 0x%04X", i, memory[i]));
            } else if (isBin) {
                memoryBuilder.append(String.format("0x%04X: %8s", i, Integer.toBinaryString(memory[i]).replace(' ', '0')));
            } else {
                memoryBuilder.append(String.format("0x%04X: %d", i, memory[i]));
            }
        }

        for (int j = 0; j < lines.length; j++) {
            programBuilder.append(lines[j]);
            if (j == pc) {
                programBuilder.append(" <-");
            }
            programBuilder.append("\n");
        }

        for(String s: trackedList) {
            if (isHex) {
                trackedBuilder.append(s).append(String.format(": 0x%04X", memory[Integer.decode(s)])).append("\n");
            } else if (isBin) {
                trackedBuilder.append(s).append(String.format(": %8s", Integer.toBinaryString(memory[Integer.decode(s)]).replace(' ', '0'))).append("\n");
            } else {
                trackedBuilder.append(s).append(String.format(": %d", memory[Integer.decode(s)])).append("\n");
            }
        }

        // Bulk update UI
        ui.trackedField.setText(trackedBuilder.toString());
        ui.memoryField.setText(memoryBuilder.toString());
        ui.programField.setText(programBuilder.toString());
    }


    public static void main(String[] args) throws IOException {
        CPU cpu = new CPU("prg.txt", 0xFFFF, true);
        // Start the interpreter loop
        cpu.interp();
    }
}
