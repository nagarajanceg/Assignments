package com.simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simulator.IssueQueue;
import com.simulator.helpers.ApexSimulatorHelper;

public class StageExecution{
	public Map<String, InstructionStructure> pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();
	public static int pc = ApexSimulator.pc;
	protected static List<InstructionStructure> instructions = ParseInstructions.instructions;
	public IssueQueue issueQ = ApexSimulatorHelper.INSTANCE.issueQ;
	public ROB rob = ApexSimulatorHelper.INSTANCE.rob;
	public LSQ lsq = ApexSimulatorHelper.INSTANCE.lsq;
	public List<Integer> memoryOp = ApexSimulatorHelper.INSTANCE.getStateofMemory();
	public CfTag tag = ApexSimulatorHelper.INSTANCE.tag;
	
	RenameTable RNT= ApexSimulatorHelper.INSTANCE.RNT;
	HashMap<String, String> RNTregMap =RNT.getRegMap();
	HashMap<String, Boolean> RNTregCommit = RNT.getRegCommited();
	public static int max_size = 4000 + ((instructions.size()-1)*4);
	//private InstructionStructure memoryInstr = ApexSimulatorHelper.INSTANCE.memoryInstr ;
	//int cycleCount = ApexSimulatorHelper.INSTANCE.memCycleCount;
	
	public void ExeInstructionInStages(){
//		execWriteBack();
		execRobCommit();
		execMemory();
		execDIV4();
		execDIV3();
		execDIV2();
		execDIV1();
		execMUL2();
		execMUL1();
		execIntFU();
		execROB();
		execLSQ();
		execIQ();
		execDecode();
		execFetch();
	}
	public void execRobCommit(){
		InstructionStructure head = null;
		for (int i=0; i<2;i ++){
			head = rob.getHead();
				
			if(head != null && head.getLogicOpSign().equals("HALT")  == true){
				rob.setCommitInst(head);
				rob.set(0, null);
				rob.pushInstruction();
				pipelineStatus.put("DECODE", null);
			}
			if(head != null && (head.isBranching() == true)){
				if(head.getStage().equals("completed")){
					rob.setCommitInst(head);
					rob.set(0, null);
					rob.pushInstruction();
				}
			}
			if(head != null && head.getDest() != null){		
				PhysicalRegHandler phyReg = ApexSimulatorHelper.INSTANCE.physicalTable.get(head.getDest());
				if(head.getStage().equals("completed")){
//					RNTregMap.put(key, value)
					rob.setCommitInst(head);
//					if(head.original )
					String archRegName = head.getOriginal();
//					head.resetOriginal();
					RegisterHandler archRegister = ApexSimulatorHelper.INSTANCE.archTable.get(archRegName);
					archRegister.setValue(phyReg.getValue());
//					System.out.println("arch === "+archRegister.getValue());
//					System.out.println("phy === "+phyReg.getValue());
					archRegister.setStatus(true);
					
					if(RNTregMap.get(archRegName) != null){
//						RNTregCommit.remove(archRegName);
						
						RNTregCommit.put(archRegName, true);
						if(RNTregMap.get(archRegName).equals(phyReg.getName())){
							RNTregMap.remove(archRegName);
							phyReg.reset();
						}
					}
					rob.set(0, null);
					rob.pushInstruction();
				}
			}
		}
	}
	public void execFetch() {
		if (pipelineStatus.get("FETCH") == null && ApexSimulatorHelper.INSTANCE.haltFlag == 0){
			if(pc <= max_size){
				int addr = (pc-4000)/4; //fetch address calculation
				InstructionStructure InstrExec = instructions.get(addr);
				InstructionStructure instr = (InstructionStructure) InstrExec.clone();
				pipelineStatus.put("FETCH", instr);
				InstrExec.setStage("FETCH");
				pc = pc+4;
			}
		}
	}
	private void execLSQ(){	
		if(tag.isFull() == false && lsq.isFull() == false && rob.isState() == true){
			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			if(InstrExec != null && InstrExec.is_lsq() && InstrExec.is_halt() == false){
				int lsqSize = lsq.getSize();
				lsq.set(lsqSize, InstrExec);
				lsq.setSize(lsqSize+1);
			}
		}
	}
	private void execROB(){
		if(rob.isFull() == false){
			rob.setState(true);
		}else{
			System.out.println("ROB spaced out");
			rob.setState(false); // no capacity to hold instruction
		}
		if(tag.isFull() == false && issueQ.isFull() == false && rob.isState() == true && ApexSimulatorHelper.INSTANCE.haltFlag == 0){
			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			if(InstrExec != null){
				if(!InstrExec.getLogicOpSign().equals("STORE")){
					InstrExec.setInstValues();
				}
				if(InstrExec.getLogicOpSign().equals("HALT")){
					ApexSimulatorHelper.INSTANCE.haltFlag = 1;
					pipelineStatus.put("FETCH", null);
				}
//				if(InstrExec.isIQFull() == false && InstrExec.isRobFull() == false){
					int robSize = rob.getSize();
					rob.set(robSize, InstrExec);
					rob.setSize(robSize+1);
//				}else{
//					System.out.println("FROM ROB: Either ROB or IQ is full");
//				}
			}
		}else{
			//System.out.println("FROM ROB: Either ROB or IQ is full");
		}
	}
	private void execIQ(){
		if(tag.isFull() == false && issueQ.isFull() == false && rob.state == true){
			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			if(InstrExec != null && InstrExec.is_halt() == false){
//				InstrExec.setInstValues();
//				if(InstrExec.isIQFull() == false && InstrExec.isRobFull() == false){
					pipelineStatus.put("DECODE", null);
					InstrExec.setStage("ready");
					int qSize = issueQ.getSize(); 
					issueQ.setIssueQ(qSize, InstrExec);
					issueQ.setSize(qSize+1);
			}
		}else{
			System.out.println(" From IQ : Either ROB or IQ is full");
		}
	}

	private void execDecode() {
		if(pipelineStatus.get("DECODE") == null){
			InstructionStructure InstrExec = pipelineStatus.get("FETCH");
			if(InstrExec != null){
				pipelineStatus.put("FETCH", null); //Reset Fetch stage
				InstrExec.setStage("DECODE");
				pipelineStatus.put("DECODE",InstrExec);
			}
		}
	
	}
	private void execDIV1() {
//		if(pipelineStatus.get("DIV1") != null){
//			pipelineStatus.put("DIV1", null);
//		}
		if(pipelineStatus.get("DIV1") == null){
//			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			InstructionStructure InstrExec = issueQ.getIQinstruction("DIV");
			//Execute only for div operation
//			if(InstrExec != null && (InstrExec.is_div() == true || InstrExec.is_halt() == true)){
			if(InstrExec != null){
				issueQ.moveInstruction();
				if(InstrExec.getStage() == "ready"){
//				if(InstrExec.getStage() == "ready"){
//					pipelineStatus.put("DECODE", null);
					InstrExec.setStage("DIV1");
					pipelineStatus.put("DIV1", InstrExec);
				}
			}
		}	
	}
	private void execDIV2() {
		if(pipelineStatus.get("DIV2") == null){
			InstructionStructure InstrExec = pipelineStatus.get("DIV1");
			//Execute only for div operation
			if(InstrExec != null){
				if(InstrExec.getStage() == "DIV1"){
					pipelineStatus.put("DIV1", null);
					InstrExec.setStage("DIV2");
					pipelineStatus.put("DIV2", InstrExec);
				}
			}
		}	
	}
	private void execDIV3() {
		if(pipelineStatus.get("DIV3") == null){
			InstructionStructure InstrExec = pipelineStatus.get("DIV2");
			//Execute only for div operation
			if(InstrExec != null){
				if(InstrExec.getStage() == "DIV2"){
					pipelineStatus.put("DIV2", null);
					InstrExec.setStage("DIV3");
					pipelineStatus.put("DIV3", InstrExec);
				}
			}
		}	
	}
	private void execDIV4() {
		setExecCompletion("DIV4");
		if(pipelineStatus.get("DIV4") == null){
			InstructionStructure InstrExec = pipelineStatus.get("DIV3");
			//Execute only for div operation
			if(InstrExec != null){
				if(InstrExec.getStage() == "DIV3"){
					pipelineStatus.put("DIV3", null);
					InstrExec.setStage("DIV4");
					pipelineStatus.put("DIV4", InstrExec);
				}
			}
		}	
	}
	private void execIntFU() {
		setExecCompletion("IFU");
		if(pipelineStatus.get("IFU") == null){
//			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			InstructionStructure InstrExec = issueQ.getIQinstruction("IFU");
			//Check the instruction going to execute is not null and not a multiply operation
//			if(InstrExec != null && InstrExec.is_mul() != true && InstrExec.is_div() != true){
			if(InstrExec != null){
				issueQ.moveInstruction();
				if(InstrExec.getStage() == "ready" ){
//				if(InstrExec.getStage() == "ready"){
//					pipelineStatus.put("DECODE", null);
					InstrExec.setStage("IFU");
					pipelineStatus.put("IFU",InstrExec);
				}
			}
		}
		
	}
	private void setExecCompletion(String stage){
		if(pipelineStatus.get(stage) != null){
			InstructionStructure InstrExec = pipelineStatus.get(stage);
			pipelineStatus.put(stage, null);
//			InstrExec.setStage("completed");
		}
	}
	private void execMUL1() {
		if(pipelineStatus.get("MUL1") == null){
//			InstructionStructure InstrExec = pipelineStatus.get("DECODE");
			InstructionStructure InstrExec = issueQ.getIQinstruction("MUL");
			//Execute only for mul operation
//			if(InstrExec != null && InstrExec.is_mul() == true){
			if(InstrExec != null){
				issueQ.moveInstruction();
				if(InstrExec.getStage() == "ready" ){
					InstrExec.setStage("MUL1");
					pipelineStatus.put("MUL1", InstrExec);
				}
			}
		}
		
	}

	private void execMUL2() {
		setExecCompletion("MUL2");
		if(pipelineStatus.get("MUL2") == null){
			InstructionStructure InstrExec = pipelineStatus.get("MUL1");
			if(InstrExec != null){
				if(InstrExec.getStage() == "MUL1"){
					pipelineStatus.put("MUL1",null);
					InstrExec.setStage("MUL2");
					pipelineStatus.put("MUL2",InstrExec);
				}
			}
		}
	}

	private void execMemory() {
		InstructionStructure memoryInstr = ApexSimulatorHelper.INSTANCE.memoryInstr;
		int cycleCount = ApexSimulatorHelper.INSTANCE.memCycleCount; 
		if(memoryInstr == null){
				memoryInstr = lsq.getLsqinstr();
				if(memoryInstr != null){
					cycleCount = 3;
					if(memoryInstr.getLogicOpSign().equals("STORE")){
//						InstructionStructure head = rob.getHead();
//						if(head != null && head.getLogicOpSign().equals("STORE")){
//							
//						}
						rob.set(0, null); //remove gurantee the memory wirte
						rob.pushInstruction();
						rob.setCommitInst(memoryInstr);
					}
					// System.out.println("In Memory == "+memoryInstr);
//					System.out.println("value == "+ memoryInstr.getAddress());
					pipelineStatus.put("MEM", memoryInstr);
					lsq.moveInstruction();
				}
				ApexSimulatorHelper.INSTANCE.memoryInstr = memoryInstr;
				ApexSimulatorHelper.INSTANCE.memCycleCount = cycleCount;
				
		}else{
			cycleCount = cycleCount -1;
			
			if(cycleCount == 0 ){
				if(memoryInstr.getLogicOpSign().equals("LOAD")){
					PhysicalRegHandler destination = ApexSimulatorHelper.INSTANCE.physicalTable.get(memoryInstr.getDest());
					destination.setValue(memoryOp.get(memoryInstr.address));
				}else{
					memoryOp.set(memoryInstr.getAddress(), memoryInstr.decodedSrc1);
				}
				memoryInstr.setStage("completed");
//				System.out.println("The instruction is " + memoryInstr);
				memoryInstr = null;
				pipelineStatus.put("MEM", null);
				ApexSimulatorHelper.INSTANCE.memoryInstr = memoryInstr;
				ApexSimulatorHelper.INSTANCE.memCycleCount = cycleCount;
				execMemory();
			}else{
				ApexSimulatorHelper.INSTANCE.memoryInstr = memoryInstr;
				ApexSimulatorHelper.INSTANCE.memCycleCount = cycleCount;
				
			}
		}
		
		
	}

	public void execWriteBack(){
		if(pipelineStatus.get("WB") != null){
			InstructionStructure InstrExec = pipelineStatus.get("WB");
			pipelineStatus.put("WB", null);
			InstrExec.setStage("completed");
		}
		
		if(pipelineStatus.get("WB") == null){
			InstructionStructure InstrExec = pipelineStatus.get("MEM");
			if(InstrExec != null){
				pipelineStatus.put("MEM", null);
				InstrExec.setStage("WB");
				pipelineStatus.put("WB",InstrExec);
			}
		}
	}
	
	//To check all the instructions getting executed
		public boolean allInstructionsExecuted(){
			if ( null==pipelineStatus.get("FETCH") && null ==pipelineStatus.get("DECODE")
					&&null == pipelineStatus.get("DIV1")&&null == pipelineStatus.get("DIV2")
					&&null == pipelineStatus.get("DIV3")&&null == pipelineStatus.get("DIV4")
					&& null ==pipelineStatus.get("IFU")
					&& null == pipelineStatus.get("MUL1") && null == pipelineStatus.get("MUL2")
					&& rob.getSize() <= 0 && null == pipelineStatus.get("MEM")){
					/*&& null == pipelineStatus.get("WB")
					/*&& null == pipelineStatus.get("MEM")){*/
				return true;
			}
			return false;
		}
}
