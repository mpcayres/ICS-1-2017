
public class IntervalosMusicais {


	public static final float C1 = 65.4f; //Do 1 para referencia
	public static final float C2 = oitavaAcima (C1);
	public static final float C3 = oitavaAcima (C2);
	public static final float C4 = oitavaAcima (C3); // esse é oq fica no meio do piano
	public static final float C5 = oitavaAcima (C4);

	
	public static float Csust (float doBase){ //DO
		return (float)(doBase * Math.pow(2, (double)1/12));
	}
	
	public static float D (float doBase){ //RE
		return (float)(doBase * Math.pow(2, (double)2/12));
	}
	
	public static float Dsust (float doBase){ //RE SUST
		return (float)(doBase * Math.pow(2, (double)3/12));
	}
	
	
	public static float E (float doBase){ 	//MI
		return (float)(doBase * Math.pow(2, (double)4/12));
	}
	
	
	public static float F (float doBase){ //FA
		return (float)(doBase * Math.pow(2, (double)5/12));
	}
	
	public static float Fsust (float doBase){ //FA SUST
		return (float)(doBase * Math.pow(2, (double)6/12));
	}

	
	public static float G (float doBase){ // SOL
		return (float)(doBase * Math.pow(2, (double)7/12));
	}
	

	public static float Gsust (float doBase){ // SOL SUST
		return (float)(doBase * Math.pow(2, (double)8/12));
	}
	
	
	public static float A (float doBase){ // LA
		return (float)(doBase * Math.pow(2, (double)9/12));
	}
	
	
	public static float Asust (float doBase){ //LA SUST
		return (float)(doBase * Math.pow(2, (double)10/12));
	}
	
	
	public static float B (float doBase){ //SI
		return (float)(doBase * Math.pow(2, (double)11/12));
	}
	
	public static float oitavaAcima (float doBase){
		return (doBase * 2f);
	}
	
	public static float oitavaAbaixo (float doBase){
		return (doBase * .5f);
	}
	public static float semibreve (){
		return 4f * Melodias.melodiaPrincipal.getSeminima ();
	}
	

	public static float minima (){
		return 2f * Melodias.melodiaPrincipal.getSeminima();
	}
	

	public static float seminima (){
		return Melodias.melodiaPrincipal.getSeminima();
	}
	
	public static float colcheia (){
		return .5f  * Melodias.melodiaPrincipal.getSeminima();
	}
	

	public static float semicolcheia (){
		return .25f * Melodias.melodiaPrincipal.getSeminima();
	}

	public static float fusa (){
		return .125f * Melodias.melodiaPrincipal.getSeminima();
	}
	

	public static float semifusa (){
		return .0625f * Melodias.melodiaPrincipal.getSeminima();
	}
}
