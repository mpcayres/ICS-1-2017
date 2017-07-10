import sintese.Dispositivo;
import sintese.Oscilador;
import sintese.Ruido;


public class InstrumentoB extends Dispositivo {
	protected boolean canal;
	protected float lambda;
	protected float lambdaComplementar;
	protected float frequenciaDeCorte1, frequenciaDeCorte2;
	protected float amplitude1, amplitude2;
	protected float ganho;
	
	Oscilador oscilador;
	Ruido ampl, freq;
	
	public InstrumentoB(){
		ganho = 1;
		frequenciaDeCorte1 = 50; frequenciaDeCorte2 = 30;
		amplitude1 = 0.5f; amplitude2 = 0.8f;
		ampl = new Ruido(amplitude1, frequenciaDeCorte1, 50);
		freq = new Ruido(amplitude2, frequenciaDeCorte2, 0);
		oscilador = new Oscilador(ampl,freq);
		setRelogio(0);
		setLambda (0.5f);
	}
	public void relogio (){
		oscilador.relogio();
	}
	public void setGanho(float ganho){
		this.ganho = ganho;
	}
	public void setRelogio(long n){
		ampl.setRelogio(n);
		freq.setRelogio(n);
		oscilador.setRelogio(n);
		saida = oscilador.getSaida()*((canal) ? lambdaComplementar : lambda);
		canal = !canal;
		reset();
	}
	public float getSaida(){
		saida = oscilador.getSaida()*((canal) ? lambdaComplementar : lambda);
		canal = !canal;
		return (ganho * saida);
	}
	
	public void setDuracao(float duracao){
		this.duracao = duracao;
		reset();
	}
	public void setFrequenciaDeCorte(int numero , float frequencia){
		if(numero == 1)
			frequenciaDeCorte1 = frequencia;
		else
			frequenciaDeCorte2 = frequencia;
		reset();
	}
	public void setAmplitude(int numero , float frequencia){
		if(numero == 1)
			frequenciaDeCorte1 = frequencia;
		else
			frequenciaDeCorte2 = frequencia;
		reset();
	}
	public void setLambda(double lambda){
		this.lambda = (float) lambda;
		lambdaComplementar = 1 - this.lambda;
		reset();
	}
	public float getLambda(){
		return lambda;
	}
	
	public void reset(){
		ampl = new Ruido(amplitude1, frequenciaDeCorte1, 0);
		freq = new Ruido(amplitude2, frequenciaDeCorte2, 0);
		oscilador = new Oscilador(ampl,freq);
		oscilador.setFrequencia(frequencia);
		oscilador.setFase(0);
		oscilador.setDuracao(duracao);
		oscilador.reset();
	}
}