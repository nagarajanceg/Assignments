package com.simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.simulator.helpers.ApexSimulatorHelper;

public class InstructionExecution {
	public Map<String, InstructionStructure> pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();
	IssueQueue issueQ = ApexSimulatorHelper.INSTANCE.issueQ;
	LSQ lsq = ApexSimulatorHelper.INSTANCE.lsq;
	ROB rob = ApexSimulatorHelper.INSTANCE.rob;
	CfTag tag = ApexSimulatorHelper.INSTANCE.tag;
	PhysicalRegTable phyTable = ApexSimulatorHelper.INSTANCE.physicalTable;
	RenameTable RntTable = ApexSimulatorHelper.INSTANCE.RNT;
	public void ExecuteInstr(InstructionStructure instr){
		//Set the status of the executing instruction destination register
		if(instr.is_arthimetic()){
			RegisterHandler PSW = ApexSimulatorHelper.INSTANCE.archTable.get("PSW");
			PSW.setStatus(true);
		}
		if(instr.getDest() != null){
			PhysicalRegHandler dest = ApexSimulatorHelper.INSTANCE.physicalTable.get(instr.getDest());
//			if(dest.isAlreadyForwarded()){
//				dest.setStatus(false);
//			}else{
//				dest.setStatus(true);
//			}
		}
	}
	public void tagBranching(InstructionStructure inst){
		TagClone tempTag = tag.getTagClone(inst.cfid);
//		System.out.println("branch execution ===== ");
//		phyTable.display();
//		RntTable.display();
		rob.ClearAfterBranch(inst);
		phyTable = tempTag.physicalTabCopy;
		RntTable = tempTag.renameTabCopy;
		List<Integer> deleteIds = new ArrayList<>();
		deleteIds = tag.getDeleteIds(inst);
		issueQ.changeInstr(deleteIds);
	}
	//Routine to execute at branching 
	public void BranchProcedure(InstructionStructure inst, int conditionValue){
		int address = inst.decodedSrc2;
		int PSWValue = -1;
		if(inst.getSrc1() != null ){
			int value = inst.decodedSrc1==0? phyTable.getValue(inst.getSrc1()): inst.decodedSrc1;
			if(value == 0 ){
				PSWValue = 1;
			}else{
				PSWValue = 0;
			}
//			if(inst.getSrc1())
			
		}
//		RegisterHandler PSW = ApexSimulatorHelper.INSTANCE.archTable.get("PSW");
//		if(inst.isFwpswFlag()){
//			PSWValue = inst.getPswFlagValue();
//		}else{
//			PSWValue = PSW.getValue();
//		}
		
		if(PSWValue == conditionValue){
			int inst_num = (inst.getInst_number() * 4) + 4000;
			StageExecution.pc = address + inst_num;
			ApexSimulatorHelper.INSTANCE.jbFlag = true;
			
//			rob.ClearAfterBranch(inst);
//			rob.swapBranchTable();
			tagBranching(inst);
//			phyTable.display();
//			RntTable.display();
			System.out.println("PC Value after "+inst.getLogicOpSign()+" :" +StageExecution.pc);
			if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
				System.out.println("Newly Generated PC Value is out of range of instruction size. So exiting system....");
				ApexSimulatorHelper.INSTANCE.printArchState();
				System.exit(0);
			}
		}else{
			System.out.println("tag removed ==== > "+inst.cfid);
			tag.removeTag(inst.cfid);
		}
		inst.setStage("completed");
	}
	//Routine to execute Jump instructions
	public void JumpRoutine(InstructionStructure inst){
		StageExecution.pc = inst.decodedSrc1 + inst.decodedSrc2;				
		System.out.println("New Pc value : "+ StageExecution.pc);
		ApexSimulatorHelper.INSTANCE.jbFlag = true;
//		System.out.println("flush instructions in DEcode and fetch stage");
//		pipelineStatus.put("DECODE", null);
//		pipelineStatus.put("FETCH", null);
		rob.ClearAfterBranch(inst);
		if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
			System.out.println("New Pc value is out of range");
			ApexSimulatorHelper.INSTANCE.printArchState();
			System.exit(0);
		}
	}

	public void Div1Execution(InstructionStructure inst){
//		if(inst.getLogicOpSign().equals("HALT")){
//			ApexSimulatorHelper.INSTANCE.haltFlag = 1;
			//clear fetch and decode stages
//			pipelineStatus.put("FETCH", null);
//			pipelineStatus.put("DECODE", null);
//			System.out.println("Halt reached");
//		}
		return;
	}
	public void Div2Execution(InstructionStructure inst){
		return;
	}
	public void Div3Execution(InstructionStructure inst){
		return;
	}
	public void Div4Execution(InstructionStructure inst){
		if(inst.getLogicOpSign().equals("DIV")){
			int result = inst.decodedSrc1 / inst.decodedSrc2;
			inst.setResult(result);
			inst.setCurrentValue(true);
			setForwardingLogic(inst, result);
			setPSWflagRegister(inst);
			setPhysicalRegister(inst);
		}
		return;
	}
	//No operation
	public void Mul1Execution(InstructionStructure inst){
		return;
	}
	public void Mul2Execution(InstructionStructure inst){
		switch(inst.getLogicOpSign()){
			//Multiplication logic
			case "MUL":
				int result = inst.decodedSrc1 * inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPSWflagRegister(inst);
				setPhysicalRegister(inst);
				break;
			default:
				break;
		}
	}
	public void setPhysicalRegister(InstructionStructure instr){
		if(instr.getDest() != null){
			PhysicalRegHandler dest = ApexSimulatorHelper.INSTANCE.physicalTable.get(instr.getDest());
			dest.setBusy(false);
			dest.setValue(instr.getResult());
		}
	}
	public void setPSWflagRegister(InstructionStructure instr){
		InstructionStructure InstrDecode = pipelineStatus.get("DECODE");
		if(InstrDecode != null && (InstrDecode.getLogicOpSign().equals("BZ")|| InstrDecode.getLogicOpSign().equals("BNZ"))){
			InstrDecode.setFwpswFlag(true);
			if(instr.getResult() == 0){
				InstrDecode.setPswFlagValue(1);
			}else{
//				InstrDecode.setFwpswFlag(false);
				InstrDecode.setPswFlagValue(0);
			}
		}
	}

	public void setForwardingLogic(InstructionStructure inst, int result){
		if(inst.getDest() != null){
			InstructionStructure InstrDecode = pipelineStatus.get("DECODE");
			issueQ.updateInstructions(inst); // Updating the list of instructions in issue queue
			if(InstrDecode != null){
				String s1 = InstrDecode.getSrc1();
				String s2 = InstrDecode.getSrc2();
				if(s1 != null && s1.equals(inst.getDest())){
					InstrDecode.setSrc1CanDispacted(true);
					InstrDecode.decodedSrc1 = result;
				}
				
				if(s2 != null && s2.equals(inst.getDest())){
					InstrDecode.setSrc2CanDispacted(true);
					InstrDecode.decodedSrc2 = result;
				}
			}
		}
		
	}
	public void setLSQflag(InstructionStructure inst){
		inst.setLsqFlag(true);
		if(inst.getLogicOpSign().equals("LOAD")){
			lsq.updateLsq(inst);
		}
//		lsq.updateAddress(inst);
	}
	public void IntegerFUExecution(InstructionStructure inst){
		//match the operand to the corresponding operation
		switch(inst.getLogicOpSign()){
			case "ADD":{	
				int result = inst.decodedSrc1 + inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPSWflagRegister(inst);
				setPhysicalRegister(inst);
				break;
			}	
			case "SUB":{
				int result = inst.decodedSrc1 - inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPSWflagRegister(inst);
				setPhysicalRegister(inst);
				break;
			}
			case "MOVC":{
				int result = inst.decodedSrc1;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPhysicalRegister(inst);
				break;
			}
			case "LOAD":{
				int src1 = inst.decodedSrc1;
				int literal = inst.decodedSrc2;
				int address = src1 + literal;
				inst.setAddress(address);
				setLSQflag(inst);
//				setAddressLsq(inst);
				
//				setForwardingLogic(inst, result);
				break;
			}
			case "STORE":{
				int lit = inst.decodedDest;
				int src1 = inst.decodedSrc1;
				int dest1 = inst.decodedSrc2;
				int address = dest1 + lit;
				inst.setAddress(address);
				setLSQflag(inst);
				break;
			}
			case "AND":{
				int result = inst.decodedSrc1 & inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPhysicalRegister(inst);
				break;
			}
			case "OR":{
				int result = inst.decodedSrc1 | inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPhysicalRegister(inst);
				break;
			}
			case "EXOR":{
				int result = inst.decodedSrc1 ^ inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPhysicalRegister(inst);
				break;
			}
			case "BZ":{
				//Branch only if the psw flag is set
				BranchProcedure(inst,1);
				break;
			}
			case "BNZ":{
				BranchProcedure(inst,0);
				break;
			}
			case "JUMP":{
				JumpRoutine(inst);				
				break;
			}
			case "JAL":{
//				String dest = inst.getDest();
//				RegisterHandler destReturnReg = ApexSimulatorHelper.INSTANCE.getRegister(dest);
//				int inst_num = ((inst.getInst_number()+1 )*4) + 4000;
//				System.out.println("inst_num == "+inst_num);
//				destReturnReg.setValue(inst_num);
//				inst.decodedSrc1 = ((inst.getInst_number()+1 )*4) + 4000;
				JumpRoutine(inst);
				break;
			}
			
			default:
				break;
		}
	}
}
