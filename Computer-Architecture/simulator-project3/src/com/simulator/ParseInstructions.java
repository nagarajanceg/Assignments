package com.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simulator.InstructionStructure;
import com.simulator.helpers.ApexSimulatorHelper;

public class ParseInstructions {
	public static int cycles = 1; //variable to hold cycles count
	protected static List<InstructionStructure> instructions = null; //hold list of instructions read from the input file
	public List<Integer> memoryOp = ApexSimulatorHelper.INSTANCE.getStateofMemory(); //get memory instance
	RenameTable rnt = ApexSimulatorHelper.INSTANCE.RNT;
	HashMap<String, String> RNTregMap = ApexSimulatorHelper.INSTANCE.RNT.getRegMap();
	HashMap<String, Boolean> RNTregCommit = ApexSimulatorHelper.INSTANCE.RNT.getRegCommited();
	IssueQueue issueQ = ApexSimulatorHelper.INSTANCE.issueQ;
	public ROB rob = ApexSimulatorHelper.INSTANCE.rob;
	public CfTag tag = ApexSimulatorHelper.INSTANCE.tag;
	public static int extraCycle = 0;
	
	public void readInstFromFile(int exeCycle, String fileName){
		//read the input file
		try (FileReader fp = new FileReader(fileName)) {
			BufferedReader lineReader = new BufferedReader(fp);
			instructions = new ArrayList<>();
			extraCycle += exeCycle;
			int instrCount = 0;
			String line;
			//readline untill new line found
			for(int i=0; (line = lineReader.readLine()) != null; i++){
	//				line = line.replaceAll("[\\,\\#]","");
				line = line.replaceAll("[\\,]",","); //regex to remove commas in the instructions
				line = line.replaceAll("[\\#]","");
	//				System.out.println(line);
				String[] instParts = line.split(",");
				InstructionStructure inst = new InstructionStructure();
				inst.setInst_number(instrCount); //set the instruction number which helps in execution order 
				instrCount += 1;
				instructions.add(inst);
			
				inst.setLogicOpSign(instParts[0]);
				inst.setStage("Read"); 
				//Based on the operation available instructions 
				// the instructions is split and assign to the Instruction structure POJO
				switch(instParts[0]){
					case "ADD":
					case "SUB":
					case "AND":
					case "OR":
					case "EXOR":
					case "MUL":
					case "DIV":	
						inst.setDest(instParts[1]);
						inst.setSrc1(instParts[2]);
						inst.setSrc2(instParts[3]);
						break;
					case "MOVC":
						inst.setDest(instParts[1]);
						try{
							int literal = Integer.parseInt(instParts[2]);
							inst.setLiteral(literal);
							inst.setSrc2CanDispacted(true);
							inst.setSrc1CanDispacted(true);
							inst.decodedSrc1 = literal;
						}catch(Exception e){
							inst.setSrc1(instParts[2]);
							e.printStackTrace();
						}
						break;
					case "BZ":
					case "BNZ":
						InstructionStructure lastInst = instructions.get(i-1); // getting the previous instruction in pipeline
						if(lastInst != null){
							int literal2 = Integer.parseInt(instParts[1]);
							inst.setSrc1(lastInst.getDest()); // set previous instruction destination register as source
							inst.setLiteral(literal2);
							inst.decodedSrc2 = literal2;
							inst.setSrc2CanDispacted(true);
							
						}else{
							System.out.println("Branch without any last Instruction");
							System.exit(0);
						}
						break;
					case "JUMP":
						inst.setSrc1(instParts[1]);
						int literal3 = Integer.parseInt(instParts[2]);
						inst.setLiteral(literal3);
						inst.decodedSrc2 = literal3;
						inst.setSrc2CanDispacted(true);
						break;
					case "STORE":
						inst.setSrc1(instParts[1]);
						inst.setSrc2(instParts[2]);
						int literal = Integer.parseInt(instParts[3]);
						inst.setLiteral(literal);
						inst.decodedDest = literal;
						break;
					case "LOAD":
						inst.setDest(instParts[1]);
						inst.setSrc1(instParts[2]);
						int literal1 = Integer.parseInt(instParts[3]);
						inst.setLiteral(literal1);
						inst.decodedSrc2 = literal1;
						inst.setSrc2CanDispacted(true);
						break;
					case "JAL":
						inst.setDest(instParts[1]);
						inst.setSrc1(instParts[2]);
						// System.out.println("next instruction"+instructions.get(i)+" == "+i);
						int jliteral = Integer.parseInt(instParts[3]);
						inst.setLiteral(jliteral);
						inst.decodedSrc2 = jliteral;
						inst.setSrc2CanDispacted(true);
						break;
				}
			}
			lineReader.close();
			//check the looping cycle(extracycle) with in cycle limit
			while (extraCycle >= cycles){
				InstructionExecution instructionExecution = new InstructionExecution();
				Map<String, InstructionStructure>  pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();
				if(ApexSimulatorHelper.INSTANCE.jbFlag == true){
					System.out.println("flush instructions in DEcode,fetch stage and issue Queue");
					pipelineStatus.put("DECODE", null);
					pipelineStatus.put("FETCH", null);
//					issueQ.clearIQ();
					ApexSimulatorHelper.INSTANCE.jbFlag = false;
				}
				new StageExecution().ExeInstructionInStages();
				if(pipelineStatus.get("WB") != null ){
					InstructionStructure instr = pipelineStatus.get("WB");
					String destRegister = instr.getDest();
					RegisterHandler PSWreg = ApexSimulatorHelper.INSTANCE.archTable.get("PSW");
					PhysicalRegHandler registerOp = ApexSimulatorHelper.INSTANCE.physicalTable.get(destRegister);
					if(instr.getLogicOpSign().equals("JAL")){
						int inst_num = ((instr.getInst_number()+1 )*4) + 4000;
						registerOp.setValue(inst_num);
					}
					if(registerOp != null){
//						registerOp.setStatus(false);
					}
					if(PSWreg.isStatus()){
//						PSWreg.setStatus(false);
					}
					if(instr.isCurrentValue()){
						registerOp.setValue(instr.getResult());
						if(instr.is_arthimetic()){
							if(instr.getResult() == 0){
								PSWreg.setValue(1); // set psw zero flag because the computed result is 0.
							}else{
								PSWreg.setValue(0);
							}
						}
					}
				}
				if(pipelineStatus.get("MEM") != null){
//					InstructionStructure instr = pipelineStatus.get("MEM");
//					instructionExecution.ExecuteInstr(instr);
	
//					String prevDest = instr.getDest();
//					if( prevDest != null){
//						PhysicalRegHandler source1 = ApexSimulatorHelper.INSTANCE.physicalTable.get(prevDest);
//					}
//					
//					switch(instr.getLogicOpSign()){
//						case "LOAD":
//							if(instr.getDest() != null){
//								PhysicalRegHandler destination = ApexSimulatorHelper.INSTANCE.physicalTable.get(instr.getDest());
////								destination.setStatus(true);
//								int src1 = instr.decodedSrc1;
//								int literal = instr.decodedSrc2;
//								int address = src1 + literal;
//								destination.setValue(memoryOp.get(address));
//							}
//							break;
//						case "STORE":
//							if(instr.getSrc2()!=null){
//								int lit = instr.decodedDest;
//								int src1 = instr.decodedSrc1;
//								int dest1 = instr.decodedSrc2;
//								int address = dest1 + lit;
//								memoryOp.set(address, src1);
//							}
//							break;
//					}
				}
					if(pipelineStatus.get("DIV1") != null){
						InstructionStructure instr = pipelineStatus.get("DIV1");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Div1Execution(instr);
					}
					if(pipelineStatus.get("DIV2") != null){
						InstructionStructure instr = pipelineStatus.get("DIV2");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Div2Execution(instr);
					}
					if(pipelineStatus.get("DIV3") != null){
						InstructionStructure instr = pipelineStatus.get("DIV3");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Div3Execution(instr);
					}
					if(pipelineStatus.get("DIV4") != null){
						InstructionStructure instr = pipelineStatus.get("DIV4");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Div4Execution(instr);
						instr.setStage("completed"); //set the stage as completed and helpful in ROB commit
					}
					if(pipelineStatus.get("MUL1") != null){
						InstructionStructure instr = pipelineStatus.get("MUL1");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Mul1Execution(instr);
					}
					if(pipelineStatus.get("MUL2") != null){
						InstructionStructure instr = pipelineStatus.get("MUL2");
						instructionExecution.ExecuteInstr(instr);
						instructionExecution.Mul2Execution(instr);
						instr.setStage("completed");
					}
				if(pipelineStatus.get("IFU") != null){
					InstructionStructure instr = pipelineStatus.get("IFU");
					instructionExecution.ExecuteInstr(instr);
					instructionExecution.IntegerFUExecution(instr);
					if(instr.is_lsq() == false){
						instr.setStage("completed");
					}else{
						instr.setStage("lsq");
					}
					
				}
				if(pipelineStatus.get("IQ") != null){
					InstructionStructure instr = pipelineStatus.get("IQ");
					int qSize = issueQ.getSize(); 
					issueQ.setIssueQ(qSize, instr);
					issueQ.setSize(qSize+1);
				}
				if(pipelineStatus.get("DECODE") != null){
					InstructionStructure instr = pipelineStatus.get("DECODE");
					String src1 = instr.getSrc1();
					String src2 = instr.getSrc2();
					String dest1 = instr.getDest();
					
					boolean PSWreg = ApexSimulatorHelper.INSTANCE.archTable.isStatus("PSW");
					//Src reg renaming moved before destination to handle output dependency, still facing rename problem at ROB commit. 
					//To do fix -> ADD R5,R5,R4
					if(src1 != null){
						instr.setSrc1Original(src1);
					}
					if(src2 != null){
						instr.setSrc2Original(src2);
					}
					if(RNTregMap.containsKey(src1)){
							src1 = RNTregMap.get(src1);
							instr.setNewSrc1(src1);
//							PhysicalRegHandler src1Reg = ApexSimulatorHelper.INSTANCE.physicalTable.get(src1);
							instr.decode_source1();
					}else{
						instr.decode_source1();
					}
					
					if(RNTregMap.containsKey(src2)){
							src2 = RNTregMap.get(src2);
							instr.setNewSrc2(src2);
							instr.decode_source2();
					}else{
						instr.decode_source2();
					}
					if(dest1 != null){
						instr.setOriginal(instr.getDest());
						PhysicalRegHandler tempReg = ApexSimulatorHelper.INSTANCE.physicalTable.getFreeRegister();
						if(tempReg == null){
							System.out.println("No Physical Register available");
							instr.setStage("stalled");
						}else{
							if(!instr.getStage().equals("stalled") ){
								String temp = tempReg.getName();
								tempReg.setSlot(false);
								tempReg.setBusy(true);
								RNTregMap.put(dest1, temp);
								if(instr.is_arthimetic()){
									rnt.setZ(temp);
								}
								RNTregCommit.put(dest1, false);
								dest1  = temp;
								instr.setNewDest(dest1);
								
							}
						}
					}
					if(instr.isBranching() == true){
//						int size = tag.getSize();
						if(tag.isFull() ==false){
							
							TagClone clonedTag =new TagClone(); 
							PhysicalRegTable phyMap = ApexSimulatorHelper.INSTANCE.physicalTable.deepClone();
							RenameTable RntTable = ApexSimulatorHelper.INSTANCE.RNT.deepClone();
							clonedTag.renameTabCopy = RntTable;
							clonedTag.physicalTabCopy = phyMap;
							int index = tag.setTag(instr, clonedTag);
							if(instr.getLogicOpSign().equals("BNZ") == true || instr.getLogicOpSign().equals("BZ")){
								if(rnt.z != null){
									instr.setNewSrc1(rnt.z);
								}else{
									instr.setSrc1CanDispacted(true);
								}
								
								instr.decode_source2();
//								instr.setSrc1CanDispacted(true);
							}
							
//							tag.setSize(size+1);
							ApexSimulatorHelper.INSTANCE.Last_control_flow = index;
//							clonedTag.display();
//							phyMap.display();
						//	System.out.println("custom Rename table for Instr"+ instr + "Index ===" + index);
//							RntTable.display();
						}
					}
					instr.setCfid(ApexSimulatorHelper.INSTANCE.Last_control_flow);
					// System.out.println(instr+" CF-ID == "+instr.cfid);
					
					if(issueQ.isFull() == true || rob.isFull() == true || 
							instr.getLogicOpSign().equals("HALT") == true || tag.isFull() == true){
						instr.setStage("stalled");
					}else{
						instr.setStage("ready");
					}
					if(instr.getLogicOpSign().equals("BZ") || instr.getLogicOpSign().equals("BNZ")){
						if(PSWreg){
							if(!instr.isFwpswFlag()){
								instr.setStage("stalled");
							}
						}
					}
				}
				
				ApexSimulatorHelper.INSTANCE.printState();
				cycles += 1;
				StageExecution stageExecution = new StageExecution();
				if(!stageExecution.allInstructionsExecuted()){
					if(cycles > extraCycle){
						ApexSimulatorHelper.INSTANCE.printArchState();
						System.out.println("Do you want to Simulate for some more cycles?Y/N");
						BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
						if(br.readLine().equalsIgnoreCase("Y")){
							System.out.println("Please enter the number of cycles you want to simulate more");
							extraCycle = extraCycle + Integer.parseInt(br.readLine());
						}else{
							ApexSimulatorHelper.INSTANCE.actualCycle = cycles;
							cycles = 1;
							StageExecution.pc = 4000;
							return;
						}
					}
				}else{
					ApexSimulatorHelper.INSTANCE.actualCycle = cycles; 
					ApexSimulatorHelper.INSTANCE.printArchState();
					return;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public List<InstructionStructure> getInstuctions(){
		return instructions;
	}
}
