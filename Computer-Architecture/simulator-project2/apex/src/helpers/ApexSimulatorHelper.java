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
    public boolean jbFlag = false; //This flag is set by branch , jump and JAL instr
    String[] stages = {"FETCH", "DECODE", "IFU", "MUL1", "MUL2", "DIV1", "DIV2", "DIV3", "DIV4", "MEM", "WB"};
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
        registerOp.add(new RegisterHandler("PSW", -1, false));
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
        if(stage.equals("FETCH")){
            stage = stage.substring(0, 1).toUpperCase() + stage.substring(1).toLowerCase(); 
        }else if(stage.equals("DECODE")){
            stage = "DRF";
        }    
        System.out.println(  stage+" : "+(instrInFetch == null ? "Empty" : "(I"+instrInFetch.toString().replace(":", ")"))
                +" "+(stage== "DRF"?(instrInFetch == null ? " " : instrInFetch.getStage()):" ")
                );
    }

    //Print the current cycle and instructions in their respective cycles during program simulation
    public void printState() {
        System.out.println("Cycle No: " + ParseInstructions.cycles);
        for (String stage : stages) {
            printFormat(stage);
        }
        
        System.out.println();
    }
    public void displayStatus(){
        System.out.println("Cycle No:" + (actualCycle -1));
        for (String stage : stages) {
            printFormat(stage);
        }
        pipelineStatus.clear();
        System.out.println();
    }
}
