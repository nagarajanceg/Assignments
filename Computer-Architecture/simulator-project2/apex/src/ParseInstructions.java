package apex;

import apex.helpers.ApexSimulatorHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ParseInstructions {
	public static int cycles = 1; //variable to hold cycles count
	protected static List<InstructionStructure> instructions = null; //hold list of instructions read from the input file
	public List<Integer> memoryOp = ApexSimulatorHelper.INSTANCE.getStateofMemory(); //get memory instance
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
						break;
					case "JAL":
						inst.setDest(instParts[1]);
						inst.setSrc1(instParts[2]);
						// System.out.println("next instruction"+instructions.get(i)+" == "+i);
						int jliteral = Integer.parseInt(instParts[3]);
						inst.setLiteral(jliteral);
						inst.decodedSrc2 = jliteral;
						break;
				}
			}
			lineReader.close();
			//check the looping cycle(extracycle) with in cycle limit
			while (extraCycle >= cycles){
				InstructionExecution instructionExecution = new InstructionExecution();
				Map<String, InstructionStructure>  pipelineStatus = ApexSimulatorHelper.INSTANCE.getPipelineStatus();
				if(ApexSimulatorHelper.INSTANCE.jbFlag == true){
					System.out.println("flush instructions in DEcode and fetch stage");
					pipelineStatus.put("DECODE", null);
					pipelineStatus.put("FETCH", null);
					ApexSimulatorHelper.INSTANCE.jbFlag = false;
				}
				new StageExecution().ExeInstructionInStages();
				if(pipelineStatus.get("WB") != null ){
					InstructionStructure instr = pipelineStatus.get("WB");
					String destRegister = instr.getDest();
					RegisterHandler PSWreg = ApexSimulatorHelper.INSTANCE.getRegister("PSW");
					RegisterHandler registerOp = ApexSimulatorHelper.INSTANCE.getRegister(destRegister);
					if(instr.getLogicOpSign().equals("JAL")){
						int inst_num = ((instr.getInst_number()+1 )*4) + 4000;
						registerOp.setValue(inst_num);
					}
					if(registerOp != null){
						registerOp.setStatus(false);
					}
					if(PSWreg.isStatus()){
						PSWreg.setStatus(false);
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
					InstructionStructure instr = pipelineStatus.get("MEM");
					instructionExecution.ExecuteInstr(instr);

					String prevDest = instr.getDest();
					if( prevDest != null){
						RegisterHandler source1 = ApexSimulatorHelper.INSTANCE.getRegister(prevDest);
						source1.setCanBeForwarded(false);
					}
					
					switch(instr.getLogicOpSign()){
						case "LOAD":
							if(instr.getDest() != null){
								RegisterHandler destination = ApexSimulatorHelper.INSTANCE.getRegister(instr.getDest());
								destination.setStatus(true);
								int src1 = instr.decodedSrc1;
								int literal = instr.decodedSrc2;
								int address = src1 + literal;
								destination.setValue(memoryOp.get(address));
							}
							break;
						case "STORE":
							if(instr.getSrc2()!=null){
								int lit = instr.decodedDest;
								int src1 = instr.decodedSrc1;
								int dest1 = instr.decodedSrc2;
								int address = dest1 + lit;
								memoryOp.set(address, src1);
							}
							break;
					}
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
				}
				if(pipelineStatus.get("IFU") != null){
					InstructionStructure instr = pipelineStatus.get("IFU");
					instructionExecution.ExecuteInstr(instr);
					instructionExecution.IntegerFUExecution(instr);
				}
				if(pipelineStatus.get("DECODE") != null){
					InstructionStructure instr = pipelineStatus.get("DECODE");
					String src1 = instr.getSrc1();
					String src2 = instr.getSrc2();
					String dest1 = instr.getDest();
					RegisterHandler source1 = null;
					RegisterHandler source2 = null;
					RegisterHandler PSWreg = ApexSimulatorHelper.INSTANCE.getRegister("PSW");
					if(src1 != null){
						source1 = ApexSimulatorHelper.INSTANCE.getRegister(src1);
						if(!source1.isStatus()){
							instr.decode_source1();
						}else{
							if(instr.isFwSrc1Status()){
								instr.decodedSrc1 = instr.forwardSrc1;
							}
						}
					}
					if(src2 != null){
						source2 = ApexSimulatorHelper.INSTANCE.getRegister(src2);
						if(!source2.isStatus()){
							instr.decode_source2();
						}else{
							if(instr.isFwSrc2Status()){
								instr.decodedSrc2 = instr.forwardSrc2;
							}
						}
					}
					if((source1 == null || source1.isStatus() == false || instr.isFwSrc1Status() ) 
							&& (source2 == null || source2.isStatus() == false || instr.isFwSrc2Status())){
						instr.setStage("ready");
					}else{
						instr.setStage("stalled");
					}
					//Check for output dependency, if it exists stall the instruction in Decode stage itself
					if (dest1 != null && ApexSimulatorHelper.INSTANCE.getRegister(dest1).isStatus()){
						instr.setStage("stalled");
					}
					if(instr.getLogicOpSign().equals("BZ") || instr.getLogicOpSign().equals("BNZ")){
						if(PSWreg.isStatus()){
							if(!instr.isFwpswFlag()){
								instr.setStage("stalled");
							}
						}
					}
//					if(instr.getLogicOpSign().equals("BNZ")){
//						
//					}
					
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
