import sintese.Curva;
import sintese.Envoltoria;
import sintese.InstrumentoAditivo;

import sintese.Dispositivo;
import sintese.Oscilador;
import sintese.UnidadeH;


public class Instrumento2 extends Dispositivo {
	protected boolean canal;
	protected float lambda;
	protected float lambdaComplementar;
	protected float fase;
	protected float ganho = 1;
	protected float fatorCorte;
	protected int valorUH1,valorUH2,valorUH3;
	protected float frequenciaUnidadeH;

	Curva   curva1, curva2, curva3, curvaOsc;      
	Envoltoria   envoltoria1;   
	Envoltoria   envoltoria2;   
	Envoltoria   envoltoria3;
	
	Envoltoria	envoltoriaOsc = new Envoltoria();
	UnidadeH     unidadeH1    = new UnidadeH();
	UnidadeH     unidadeH2    = new UnidadeH();
	UnidadeH     unidadeH3    = new UnidadeH();
	InstrumentoAditivo instrumento; 
	Oscilador oscilador;
	
	public Instrumento2(){
		valorUH1 = 2; valorUH2 = 3; valorUH3 = 5;
		
		frequencia = 300;
		
		curva1 = constroiCurva(10, 600, 220, 230, 720, 0);
		curva2 = constroiCurva(10, 230, 210, 250, 720, 10);
		curva3 = constroiCurva(30, 150, 200, 180, 720, 10);
		curvaOsc = constroiCurva(10,1300,300, 1000,500,900);
		
		envoltoria1 = new Envoltoria(curva1);
		envoltoria2 = new Envoltoria(curva2);
		envoltoria3 = new Envoltoria(curva3);
		envoltoriaOsc = new Envoltoria(curvaOsc);
		
		unidadeH1.setEnvoltoria(envoltoria1);
	 	unidadeH2.setEnvoltoria(envoltoria2);
	 	unidadeH3.setEnvoltoria(envoltoria3);
	 	
	 	unidadeH1.setH(valorUH1); unidadeH2.setH(valorUH2); unidadeH3.setH(valorUH3);
	    
	 	unidadeH1.setFase(0f); unidadeH2.setFase(0f); unidadeH3.setFase(0f);
	 	
	 	unidadeH1.setLambda(lambda);unidadeH2.setLambda(lambda);unidadeH3.setLambda(lambda);
		 
	    instrumento = new InstrumentoAditivo();
	    instrumento.addUnidade(unidadeH1);
	    instrumento.addUnidade(unidadeH2);
	    instrumento.addUnidade(unidadeH3);
	    instrumento.setFrequencia(frequencia);
		
		oscilador = new Oscilador(envoltoriaOsc,instrumento);
		setRelogio(0);
		setLambda (0.5f);
		setFase(0f);
	}
	
	public void relogio (){
		oscilador.relogio();
	}
	
	public void setRelogio(long n){
		envoltoriaOsc.setRelogio(n);
		instrumento.setRelogio(n);
		oscilador.setRelogio(n);
		saida = oscilador.getSaida()*((canal) ? lambdaComplementar : lambda);
		canal = !canal;
		reset();
	}
	
	public float getSaida(){
		saida = oscilador.getSaida()*((canal) ? lambdaComplementar : lambda);
		canal = !canal;
		return (ganho *3* saida);
	}
	
	public void setUH1(int numero ,int harmonico){
		switch (numero) {
		case 1:
			valorUH1 = harmonico;
			break;
		case 2:
			valorUH2 = harmonico;
			break;
		case 3:
			valorUH3 = harmonico;
			break;
		default:
			break;
		}		
		reset();
	}
	
	public void setGanho(float ganho){
		this.ganho = ganho;
	}
	
	public void setFrequencia(float frequencia){
		this.frequencia = frequencia;
		reset();
	}
	
	public void setLambda(double lambda){
		this.lambda = (float) lambda;
		this.lambdaComplementar = 1 - this.lambda;
		reset();
	}
	
	public void setEnvoltoria(int numero ,Envoltoria envoltoria){
		switch (numero) {
		case 1:
			envoltoria1 = envoltoria;
			break;
		case 2:
			envoltoria2 = envoltoria;
			break;
		case 3:
			envoltoria3 = envoltoria;
			break;

		default:
			break;
		}		
		reset();

	}
	
	public void setFase(float fase){
		this.fase = fase;
		reset();
	}
	
	public Curva constroiCurva(double x1, double y1, double x2, double y2, double x3, double y3){
		Curva novaCurva = new Curva(720);
		novaCurva.addPonto (0f, 0f);
		novaCurva.addPonto (x1, y1);
		novaCurva.addPonto(x2, y2);
		novaCurva.addPonto(x3, y3);
		
		return novaCurva;
	}
	
	public float getFase(){
		return fase;
	}
	
	public float getLambda(){
		return lambda;
	}
	
	public void reset(){
		unidadeH1.setEnvoltoria(envoltoria1);
	 	unidadeH2.setEnvoltoria(envoltoria2);
	 	unidadeH3.setEnvoltoria(envoltoria3);
	 	
	 	unidadeH1.setH(valorUH1); unidadeH2.setH(valorUH2); unidadeH3.setH(valorUH3);
	    
	 	unidadeH1.setFase(fase); unidadeH2.setFase(fase); unidadeH3.setFase(fase);
	 	
	 	unidadeH1.setLambda(lambda);unidadeH2.setLambda(lambda);unidadeH3.setLambda(lambda);
		 
	    instrumento = new InstrumentoAditivo();
	    
	    instrumento.addUnidade(unidadeH1);
	    instrumento.addUnidade(unidadeH2);
	    instrumento.addUnidade(unidadeH3);
	    
	    instrumento.setFrequencia(frequencia);
	    instrumento.setGanho(10);
		
		oscilador = new Oscilador(envoltoriaOsc,instrumento);
		oscilador.setFrequencia(frequencia);
		oscilador.setFase(fase);
		oscilador.reset();
	}

}