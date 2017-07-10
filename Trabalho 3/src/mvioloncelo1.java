import sintese.*;

public class mvioloncelo1 {

	public static void main(String[] args) {


		Melodia melodia1 = new Melodia();
		melodia1.addNota(new Nota(1.316, 233.08188075904496, 80));
		melodia1.addNota(new Nota(0.9520000000000001, 130.8127826502993, 80));
		melodia1.addNota(new Nota(0.47200000000000003, 329.6275569128699, 80));
		melodia1.addNota(new Nota(0.608, 123.47082531403103, 80));
		melodia1.addNota(new Nota(0.446, 164.81377845643496, 80));
		melodia1.addNota(new Nota(0.458, 220.0, 80));
		melodia1.addNota(new Nota(0.618, 164.81377845643496, 80));
		melodia1.addNota(new Nota(0.456, 220.0, 80));
		melodia1.addNota(new Nota(0.464, 261.6255653005986, 80));
		melodia1.addNota(new Nota(0.28600000000000003, 233.08188075904496, 80));
		melodia1.addNota(new Nota(0.46, 233.08188075904496, 80));
		melodia1.addNota(new Nota(0.454, 207.65234878997256, 80));
		melodia1.addNota(new Nota(0.444, 146.8323839587038, 80));
		melodia1.addNota(new Nota(0.268, 138.59131548843604, 80));
		melodia1.addNota(new Nota(0.452, 184.9972113558172, 80));
		melodia1.addNota(new Nota(0.45, 184.9972113558172, 80));
		melodia1.addNota(new Nota(0.296, 293.6647679174076, 80));
		melodia1.addNota(new Nota(0.298, 311.1269837220809, 80));
		melodia1.addNota(new Nota(0.266, 130.8127826502993, 80));
		melodia1.addNota(new Nota(0.454, 195.99771799087463, 80));
		melodia1.addNota(new Nota(0.438, 130.8127826502993, 80));
		melodia1.addNota(new Nota(0.274, 155.56349186104046, 80));
		melodia1.addNota(new Nota(0.32, 587.3295358348151, 80));
		melodia1.addNota(new Nota(0.278, 184.9972113558172, 80));
		melodia1.addNota(new Nota(0.438, 123.47082531403103, 80));
		melodia1.addNota(new Nota(0.276, 174.61411571650194, 80));
		melodia1.addNota(new Nota(0.274, 155.56349186104046, 80));
		melodia1.addNota(new Nota(0.274, 155.56349186104046, 80));
		melodia1.addNota(new Nota(0.3, 329.6275569128699, 80));
		melodia1.addNota(new Nota(0.268, 138.59131548843604, 80));
		melodia1.addNota(new Nota(0.274, 155.56349186104046, 80));
		melodia1.addNota(new Nota(0.28800000000000003, 233.08188075904496, 80));
		melodia1.addNota(new Nota(0.28, 195.99771799087463, 80));
		melodia1.addNota(new Nota(0.266, 123.47082531403103, 80));
		melodia1.addNota(new Nota(0.276, 164.81377845643496, 80));
		melodia1.addNota(new Nota(0.28400000000000003, 207.65234878997256, 80));
		melodia1.addNota(new Nota(0.27, 146.8323839587038, 80));
		melodia1.addNota(new Nota(0.272, 146.8323839587038, 80));
		melodia1.addNota(new Nota(0.28200000000000003, 195.99771799087463, 80));
		melodia1.addNota(new Nota(0.28600000000000003, 233.08188075904496, 80));
		melodia1.addNota(new Nota(0.276, 164.81377845643496, 80));
		melodia1.addNota(new Nota(0.274, 155.56349186104046, 80));

		Polifonia p = new Polifonia();
		Voz voz1 = new Voz(new Instrumento3());
		voz1.addMelodia(melodia1);
		p.addVoz(voz1);

		Som s = p.getSom();
		s.visualiza();

	}

}
