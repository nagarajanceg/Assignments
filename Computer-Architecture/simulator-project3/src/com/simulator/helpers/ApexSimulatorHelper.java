package com.simulator.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simulator.ParseInstructions;
import com.simulator.RenameTable;
import com.simulator.IssueQueue;
import com.simulator.LSQ;
import com.simulator.ArchRegTable;
import com.simulator.CfTag;
import com.simulator.InstructionStructure;
import com.simulator.PhysicalRegTable;
import com.simulator.ROB;

public enum ApexSimulatorHelper {
	INSTANCE;
	public int haltFlag = 0;
    public List<Integer> memoryOp = new ArrayList<>();
//    public List<RegisterHandler> registerOp = new ArrayList<>();
    private Map<String, InstructionStructure> pipelineStatus = new HashMap<>();
    public PhysicalRegTable physicalTable = new PhysicalRegTable() ;
    public ArchRegTable archTable;
    public RenameTable RNT = new RenameTable();
    public IssueQueue issueQ = new IssueQueue();
    public ROB rob = new ROB();
    public LSQ lsq = new LSQ(this.rob, this.physicalTable);
    public CfTag tag = new CfTag();
    public int actualCycle = 0;
    public boolean jbFlag = false; //This flag is set by branch , jump and JAL instr
    public InstructionStructure memoryInstr = null;
    public int memCycleCount = 0;
    public int Last_control_flow = -1;
    
    String[] stages = {"FETCH", "DECODE", "IFU", "MUL1", "MUL2", "DIV1", "DIV2", "DIV3", "DIV4", "MEM"};
    
    
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
        for (String stage : stages) {
            pipelineStatus.put(stage, null);
        }

    }
    
    //Initialize the register and memory values with 0
    private void resetRegister() {
    	physicalTable = new PhysicalRegTable(); 
        archTable  = new ArchRegTable();
        memoryOp.clear();
        new PhysicalRegTable();
//        for (int i = 0; i < 16; i++) {
//            registerOp.add(new RegisterHandler("R" + i, 0, false));
//        }
//	        for(int i=0; i < 32; i++){
//	        	physicalOp.add(new PhysicalRegHandler("P"+i, 0, false,"FREE",1 ));
//	        }
        for (int i = 0; i < 3999; i++) {
            memoryOp.add(0);
        }
//        registerOp.add(new RegisterHandler("PSW", -1, false));
    }
    //get register by name
//    public RegisterHandler getRegister(String destRegister) {
//        if (destRegister == null)
//            return null;
//        for (RegisterHandler registerHandler : registerOp) {
//            if (destRegister.equals(registerHandler.getName())) {
//                return registerHandler;
//            }
//        }
//        return null;
//    }
    public void lineSpacing(int pos){
    	if((pos+1)% 8 == 0){
    		System.out.println();
    	}
    }
    //print register and memory state
    public void printArchState() {
        System.out.println("Registers");
        for (int i = 0; i < 16; i++) {
            System.out.print("R" + i + "=" + archTable.getValue("R"+i) + "\t");
            lineSpacing(i);
        }
        for (int i = 0; i < 32; i++) {
            System.out.print("P" + i + "=" + physicalTable.getValue("P"+i) + "\t");
            lineSpacing(i);
        }
        System.out.println("PSW-zeroFlag = "+archTable.getValue("PSW"));
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
        RNT.display();
        issueQ.displayIQ();
        rob.displayAll();
        rob.displayCommit();
        lsq.display();
        System.out.println();
    }
    public void displayStatus(){
		System.out.println("Cycle No:" + (actualCycle -1));
		for (String stage : stages) {
        	printFormat(stage);
        }
		RNT.display();
        issueQ.displayIQ();
        pipelineStatus.clear();
        rob.displayAll();
        rob.displayCommit();
        lsq.display();
        System.out.println();
    }
}
