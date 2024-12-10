import java.awt.*;
import javax.swing.*;

public class View extends JPanel {
    public JTextArea memoryField;
    public JLabel memoryTxt;
    public JRadioButton binBtn;
    public JRadioButton decimalBtn;
    public JRadioButton hexBtn;
    public JLabel pcTxt;
    public JLabel cpuTxt;
    public JLabel regATxt;
    public JLabel regBTxt;
    public JLabel regCTxt;
    public JLabel instructionTxt;
    public JScrollPane memoryScroll;
    public JScrollPane programScroll;
    public JScrollPane outputScroll;
    public JScrollPane trackedScroll;
    public ButtonGroup btnGroup;
    public JLabel programTxt;
    public JTextArea programField;
    public JTextArea consoleField;
    public JLabel outputTxt;
    public JButton stepBtn;
    public JToggleButton pauseBtn;
    public JLabel trackedTxt;
    public JTextArea trackedField;
    public JTextField trackTextField;
    public JButton trackBtn;
    public JButton removeBtn;
    public JSeparator sep1;
    public JSeparator sep2;

    public View() {
        //construct components
        memoryField = new JTextArea (15, 15);
        memoryTxt = new JLabel ("<HTML><U>Memory:</U></HTML>");
        binBtn = new JRadioButton ("Binary");
        decimalBtn = new JRadioButton ("Decimal", true);
        hexBtn = new JRadioButton ("Hex");
        pcTxt = new JLabel ("PC:");
        cpuTxt = new JLabel ("<HTML><U>Values:</U></HTML>");
        regATxt = new JLabel ("A:");
        regBTxt = new JLabel ("B:");
        regCTxt = new JLabel ("C:");
        instructionTxt = new JLabel ("<HTML><U>Current instruction:</U></HTML>");
        memoryScroll = new JScrollPane (memoryField);
        btnGroup = new ButtonGroup();
        programTxt = new JLabel ("<HTML><U>Program:</U></HTML>");
        programField = new JTextArea (15, 15);
        consoleField = new JTextArea (15, 15);
        outputTxt = new JLabel ("<HTML><U>Output:</U></HTML>");
        programScroll = new JScrollPane(programField);
        outputScroll = new JScrollPane(consoleField);
        stepBtn = new JButton ("Step");
        pauseBtn = new JToggleButton ("RESUME", true);
        trackedTxt = new JLabel ("<HTML><U>Tracked Addresses:</U></HTML>");
        trackedField = new JTextArea (15, 15);
        trackedScroll = new JScrollPane(trackedField);
        trackTextField = new JTextField (1);
        trackBtn = new JButton ("+");
        removeBtn = new JButton ("-");

        sep1 = new JSeparator();
        sep2 = new JSeparator();

        //adjust size and set layout
        setPreferredSize (new Dimension (946, 578));
        setLayout (null);

        //add components
        //add (memoryField);
        add(memoryScroll);
        add (memoryTxt);
        add(binBtn);
        add(hexBtn);
        add(decimalBtn);
        btnGroup.add(binBtn);
        btnGroup.add(decimalBtn);
        btnGroup.add(hexBtn);
        add (pcTxt);
        add (cpuTxt);
        add (regATxt);
        add (regBTxt);
        add (regCTxt);
        add (instructionTxt);
        add (programTxt);
        //add (programField);
        add (programScroll);
        //add (consoleField);
        add (outputScroll);
        add (outputTxt);
        add (stepBtn);
        add (pauseBtn);
        add(trackedTxt);
        //add(trackedField);
        add(trackedScroll);
        add (trackTextField);
        add (trackBtn);
        add (removeBtn);

        add (sep1);
        add (sep2);

        //set component bounds (only needed by Absolute Positioning)
        //memoryField.setBounds (530, 30, 415, 545);
        memoryScroll.setBounds (530, 30, 415, 545);
        memoryTxt.setBounds (530, 5, 55, 25);
        binBtn.setBounds (730, 5, 65, 25);
        decimalBtn.setBounds (845, 5, 75, 25);
        hexBtn.setBounds (795, 5, 50, 25);
        pcTxt.setBounds (200, 30, 75, 25);
        cpuTxt.setBounds (200, 5, 100, 25);
        regATxt.setBounds (200, 55, 100, 25);
        regBTxt.setBounds (200, 80, 100, 25);
        regCTxt.setBounds (200, 105, 100, 25);
        instructionTxt.setBounds (200, 130, 500, 25);
        programTxt.setBounds (5, 5, 100, 25);
        //programField.setBounds (5, 30, 180, 545);
        programScroll.setBounds (5, 30, 180, 545);
        //consoleField.setBounds (190, 380, 335, 195);
        outputScroll.setBounds (190, 380, 335, 195);
        outputTxt.setBounds (190, 355, 100, 25);
        stepBtn.setBounds (250, 325, 90, 40);
        pauseBtn.setBounds (360, 325, 90, 40);
        trackedTxt.setBounds (195, 160, 120, 25);
        //trackedField.setBounds (195, 180, 145, 135);
        trackedScroll.setBounds (195, 180, 145, 135);
        trackTextField.setBounds (350, 175, 130, 35);
        trackBtn.setBounds (350, 220, 45, 40);
        removeBtn.setBounds (400, 220, 45, 40);

        sep1.setBounds(programField.getX() + programField.getWidth(), instructionTxt.getY() + instructionTxt.getHeight() + 4, 600, 10);
        sep2.setBounds(programField.getX() + programField.getWidth(), trackedScroll.getY() + trackedScroll.getHeight() + 4, 600, 10);

        memoryField.setEditable(false);
        programField.setEditable(false);
        consoleField.setEditable(false);
        trackedField.setEditable(false);
    }
}
