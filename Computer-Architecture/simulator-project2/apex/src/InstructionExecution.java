package apex;

import java.util.Map;

import apex.helpers.ApexSimulatorHelper;

public class InstructionExecution {
	public Map<String, InstructionStructure> pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();

	public void ExecuteInstr(InstructionStructure instr){
		//Set the status of the executing instruction destination register
		if(instr.is_arthimetic()){
			RegisterHandler PSW = ApexSimulatorHelper.INSTANCE.getRegister("PSW");
			PSW.setStatus(true);
		}
		if(instr.getDest() != null){
			RegisterHandler dest = ApexSimulatorHelper.INSTANCE.getRegister(instr.getDest());
//			if(dest.isAlreadyForwarded()){
//				dest.setStatus(false);
//			}else{
				dest.setStatus(true);
//			}
		}
	}
	//Routine to execute at branching 
	public void BranchProcedure(InstructionStructure inst, int conditionValue){
		int address = inst.decodedSrc2;
		int PSWValue;
		RegisterHandler PSW = ApexSimulatorHelper.INSTANCE.getRegister("PSW");
		if(inst.isFwpswFlag()){
			PSWValue = inst.getPswFlagValue();
		}else{
			PSWValue = PSW.getValue();
		}
		
		if(PSWValue == conditionValue){
			int inst_num = (inst.getInst_number() * 4) + 4000;
			StageExecution.pc = address + inst_num;
			ApexSimulatorHelper.INSTANCE.jbFlag = true;
			System.out.println("PC Value after "+inst.getLogicOpSign()+" :" +StageExecution.pc);
			// System.out.println("Branch taken. So flushing instructions in Fetch and Decode Stage");
			// pipelineStatus.put("DECODE", null);
			// pipelineStatus.put("FETCH", null);
			if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
				System.out.println("Newly Generated PC Value is out of range of instruction size. So exiting system....");
				ApexSimulatorHelper.INSTANCE.printArchState();
				System.exit(0);
			}
		}
	}
	//Routine to execute Jump instructions
	public void JumpRoutine(InstructionStructure inst){
		StageExecution.pc = inst.decodedSrc1 + inst.decodedSrc2;				
		System.out.println("New Pc value : "+ StageExecution.pc);
		ApexSimulatorHelper.INSTANCE.jbFlag = true;
//		System.out.println("flush instructions in DEcode and fetch stage");
//		pipelineStatus.put("DECODE", null);
//		pipelineStatus.put("FETCH", null);
		if((StageExecution.pc > StageExecution.max_size) || (StageExecution.pc < 4000)){
			System.out.println("New Pc value is out of range");
			ApexSimulatorHelper.INSTANCE.printArchState();
			System.exit(0);
		}
	}

	public void Div1Execution(InstructionStructure inst){
		if(inst.getLogicOpSign().equals("HALT")){
			ApexSimulatorHelper.INSTANCE.haltFlag = 1;
			//clear fetch and decode stages
			pipelineStatus.put("FETCH", null);
			pipelineStatus.put("DECODE", null);
			System.out.println("Halt reached");
		}
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
				break;
			default:
				break;
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
			if(InstrDecode != null){
				String s1 = InstrDecode.getSrc1();
				String s2 = InstrDecode.getSrc2();
				if(s1 != null && s1.equals(inst.getDest())){
					InstrDecode.setFwSrc1Status(true);
					InstrDecode.setForwardSrc1(result);
				}
				
				if(s2 != null && s2.equals(inst.getDest())){
					InstrDecode.setFwSrc2Status(true);
					InstrDecode.setForwardSrc2(result);
				}
			}
		}
		
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
				break;
			}	
			case "SUB":{
				int result = inst.decodedSrc1 - inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				setPSWflagRegister(inst);
				break;
			}
			case "MOVC":{
				int result = inst.decodedSrc1;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				break;
			}
			case "LOAD":{
				break;
			}
			case "STORE":{
				break;
			}
			case "AND":{
				int result = inst.decodedSrc1 & inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				break;
			}
			case "OR":{
				int result = inst.decodedSrc1 | inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
				break;
			}
			case "EXOR":{
				int result = inst.decodedSrc1 ^ inst.decodedSrc2;
				inst.setResult(result);
				inst.setCurrentValue(true);
				setForwardingLogic(inst, result);
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
