import java.io.*;
import javax.sound.midi.*;

import java.lang.Math;


public class Conversor {
	static private File arquivoMidi = null;
	static private Sequence sequenciaMidi = null;
	
	private Track[] trilhasDetectadas;
	private double durtique;
	float durSeminima, bpm;
	
	public Conversor(File arquivo){
		try{
			arquivoMidi = arquivo;
			sequenciaMidi = MidiSystem.getSequence(arquivoMidi); 
			
			LerArquivo();
			CriaArquivoJava();
		
		}
	    catch(InvalidMidiDataException e2){
	    	System.out.println(e2 + " : Erro nos dados midi.");
	    }
	    catch(IOException e3){
	    	System.out.println(e3 + " : O arquivo midi nao foi encontrado.");
	    }
	}
	
	public void LerArquivo (){
		int resolucao = sequenciaMidi.getResolution();
		long duracao = sequenciaMidi.getMicrosecondLength() / 1000000;
		long totaltiques = sequenciaMidi.getTickLength();
		durtique = (double) duracao/totaltiques;
        durSeminima = (float) (durtique * resolucao);
        bpm = 60 / durSeminima;
		trilhasDetectadas = sequenciaMidi.getTracks();
	}
	
	public void CriaArquivoJava(){
		String aux = arquivoMidi.getName();
		aux = aux.substring(0, aux.length()-4);
		System.out.println(aux + ".java");
		
		try (PrintWriter in = new PrintWriter(aux + ".java", "UTF-8")){	
			in.println("import sintese.*;");
			in.println("\npublic class " + aux + " {");
			in.println("\n\tpublic static void main(String[] args) {");
			
			byte[] dados;
			String tipoInst = "";
			int numInst = 0, status;
			
			for (int i = 0; i < trilhasDetectadas.length; i++){
				int tam = trilhasDetectadas[i].size();
				
				boolean instExiste = false;
			//	System.out.println("\n***********************\nTrilha " + i);
									
				for (int j = 0; j < trilhasDetectadas[i].size(); j++){
					double duracao = -1, decadencia = 0;
					MidiEvent evento = trilhasDetectadas[i].get(j);
					MidiMessage msg = evento.getMessage();
					status = msg.getStatus();
					dados = msg.getMessage();
					 
					// Note Off: 1000
					if (status >= 128 && status <= 143) {
						//System.out.println("Note Off -> numero da nota:" + dados[1] + ", decadencia: " + dados[2] + "\n");
					}
					//Note On:
	                if (status >= 144 && status <= 159) {
						if (instExiste) {
							//Procura Note Off
							if (j < tam-1){
	                    		MidiEvent proximo = trilhasDetectadas[i].get(j + 1);
	                            for (int k = j+1; k < trilhasDetectadas[i].size(); k++) {
									MidiEvent event = trilhasDetectadas[i].get(k);
									MidiMessage m = event.getMessage();
									status = m.getStatus();
									byte[] bTemp = m.getMessage();
									
									if (status >= 128 && status <= 143 && bTemp[1] == dados[1]) {
										proximo = trilhasDetectadas[i].get(k);
	                                    decadencia = (double) bTemp[1];
	                                    break;
	                                }
	                            }
	                            duracao = (double) durtique*(proximo.getTick() - evento.getTick() + decadencia );
							} //Se for o ultimo, so pega o final 
							else{
	                    		duracao = (double) durtique*(sequenciaMidi.getTickLength() - evento.getTick());
							} //if tam
						} // if instExiste
	                     
	                    //Calcula frequencia da nota
	                    double freq = (double) (440 * Math.pow(2.00, ((double) dados[1] - 69)/12));
	                    //Amplitude da nota
	                    int amp = (int) dados[2];

	                    if (duracao > 0) {
	                    	in.println("\t\tmelodia" + numInst + ".addNota(new Nota(" + duracao + ", " + freq + ", " + amp + "));");
	                    }
	                    //System.out.println("Note On " + duracao + "s " + freq + " Hertz | " + "ins" + 
	                    //		numInst + ".addNota(" + duracao + ", " + freq + ", " + amp + ");\n");

					} //if status
					// Control Change: 1011
	                else if (status >= 176 && status <= 191) {
	                	System.out.println("Control Change -> byte 2:" + dados[1] + ", valor de controle: " + dados[2] + "\n");
	                }
	                // Program Change: 1100
	                else if (status >= 192 && status <= 207) {
	                	instExiste = true;
	                    int inst = dados[1];
	                    
	                    // Determina as faixas que serao de cada instrumento
	                    if (inst >= 0 && inst <= 8) {
	                    	tipoInst = "new Instrumento1()";
	                    } else if (inst >= 9 && inst <= 16) {
	                    	tipoInst = "new Instrumento2()";
	                    } else if (inst >= 17 && inst <= 24) {
	                    	tipoInst = "BancoDeInstrumentos.timbreortogonal3()";
	                    } else if (inst >= 25 && inst <= 40) {
	                    	tipoInst = "new Instrumento1()";
	                    } else if (inst >= 41 && inst <= 56) {
	                        tipoInst = "new Instrumento3()";
	                    } else if (inst >= 57 && inst <= 72) {
	                    	tipoInst = "new Instrumento1()";
	                    } else if (inst >= 73 && inst <= 80) {
	                    	tipoInst = "new Instrumento2()";
	                    } else if (inst >= 81 && inst <= 112) {
	                    	tipoInst = "BancoDeInstrumentos.timbreortogonal3()";
	                    } else if (inst >= 113 && inst <= 120) {
	                    	tipoInst = "BancoDeInstrumentos.timbreortogonal3()";
	                    } else if (inst >= 121 && inst <= 128) {
	                        tipoInst = "BancoDeInstrumentos.timbre_quasetonal()";
	                    } else {
	                    	throw new RuntimeException("Numero do instrumento invalido");
	                    }
	                     
	                    numInst++;
	                    in.print("\n\n\t\tMelodia melodia" + numInst + " = new Melodia();\n");
	                  
	                } //else if status
	                // Pitch Bend: 1110
	                else if (status >= 224 && status <= 239) {
	                	System.out.println("Pitch Bend -> byte 2:" + dados[1] + ", byte 3: " + dados[2] + "\n");
	                }
				} //for trilhas size
			} //for trilhas length
			
			// Montando a polifonia
			in.print("\n\t\tPolifonia p = new Polifonia();\n");
			for (int i = 1; i <= numInst; i++) {
			   in.println("\t\tVoz voz" + i + " = new Voz("+tipoInst+");");
			   in.println("\t\tvoz"+ i +".addMelodia(melodia"+i+");");
			   in.println("\t\tp.addVoz(voz"+i+");");
			}
			//in.println("\t\tp.setAndamento("+Math.round(bpm)+");");
		        
			in.println("\n\t\tSom s = p.getSom();\n" + "\t\ts.visualiza();\n\n\t}\n\n}");
			System.out.println("\nFinalizado!\n");
		    
		    in.close();
		} catch (IOException e) {
		   System.out.println("Nao foi possivel abrir o arquivo de escrita");
		}
	}
	
}
