package apex.helpers;

import apex.InstructionStructure;
import apex.ParseInstructions;
import apex.RegisterHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ApexSimulatorHelper {
    INSTANCE;

    public int haltFlag = 0;
    public List<Integer> memoryOp = new ArrayList<>();
    public List<RegisterHandler> registerOp = new ArrayList<>();
    private Map<String, InstructionStructure> pipelineStatus = new HashMap<>();
    public int actualCycle = 0;
    public Map<String, InstructionStructure> getPipelineStatus() {
        return this.pipelineStatus;
    }

    public List<Integer> getStateofMemory() {
        return memoryOp;
    }

    public void initialize() {
        resetRegister();
        resetStages();
    }

    private void resetStages() {
        String[] stages = {"FETCH", "DECODE", "MUL1", "MUL2", "IFU", "MEM", "WB"};
        // TODO Auto-generated method stub
        for (String stage : stages) {
            pipelineStatus.put(stage, null);
        }

    }
    //Initialize the register and memory values with 0
    private void resetRegister() {
        registerOp.clear();
        memoryOp.clear();
        for (int i = 0; i < 16; i++) {
            registerOp.add(new RegisterHandler("R" + i, 0, false));
        }
        for (int i = 0; i < 3999; i++) {
            memoryOp.add(0);
        }
        registerOp.add(new RegisterHandler("PSW", 0, false));
    }
    //get register by name
    public RegisterHandler getRegister(String destRegister) {
        if (destRegister == null)
            return null;
        for (RegisterHandler registerHandler : registerOp) {
            if (destRegister.equals(registerHandler.getName())) {
                return registerHandler;
            }
        }
        return null;
    }
    public void lineSpacing(int pos){
    	if((pos+1)% 8 == 0){
    		System.out.println();
    	}
    }
    //print register and memory state
    public void printArchState() {
        System.out.println("Registers");
        for (int i = 0; i < 16; i++) {
            System.out.print("R" + i + "=" + registerOp.get(i).getValue() + "\t");
            lineSpacing(i);
        }
        System.out.println("PSW-zeroFlag = "+getRegister("PSW").getValue());
        System.out.println("First 100 Memory Address ");
        for (int j = 0; j < 100; j++) {
            System.out.print("Memory[" + j + "]" + "=" + memoryOp.get(j) + "\t");
            lineSpacing(j);
        }
        System.out.println();
    }

    public void printFormat(String stage) {
        InstructionStructure instrInFetch = pipelineStatus.get(stage);
        System.out.println((instrInFetch == null ? "Empty" : instrInFetch) + "     " + stage.toLowerCase() + "-Stage: "
                + (instrInFetch == null ? "No Operation" : instrInFetch.getStage()));
    }

    //Print the current cycle and instructions in their respective cycles during program simulation
    public void printState() {
        System.out.println("Cycle No: " + ParseInstructions.cycles);
        printFormat("FETCH");
        printFormat("DECODE");
        printFormat("MUL1");
        printFormat("MUL2");
        printFormat("IFU");
        printFormat("MEM");
        printFormat("WB");
        System.out.println();
    }
    public void displayStatus(){
		System.out.println("Cycle No:" + (actualCycle -1));
    	printFormat("FETCH");
        printFormat("DECODE");
        printFormat("MUL1");
        printFormat("MUL2");
        printFormat("IFU");
        printFormat("MEM");
        printFormat("WB");
        pipelineStatus.clear();
        System.out.println();
    }
}
