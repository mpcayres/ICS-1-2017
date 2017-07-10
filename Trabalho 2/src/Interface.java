import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import sintese.BancoDeInstrumentos;
import sintese.ColecaoDeFrases;
import sintese.Dispositivo;
import sintese.Escala;
import sintese.Melodia;
import sintese.Som;
import sintese.Tema;

public class Interface{
	private int slideruni1, slideruni2, slideruni3, sliderganho;
	private float sliderfrq1, sliderfrq2, slideramp1, slideramp2, fase;
	private double lambda;
	private int escolhidoMusica = 0;
	private int escolhidoInstrumento = 0;
	final private int largura = 800;
	final private int altura = 450;
	private Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
    private int posx = (int) tamanhoTela.getWidth()/2-largura/2;
    private int posy = (int) tamanhoTela.getHeight()/2-altura/2;
	   
    private JFrame mainFrame = null;
    private JSlider sliderFase = null;
    private JSlider sliderEstereofonia = null;
    private JSlider sliderUnidade1 = null;
    private JSlider sliderUnidade2 = null;
    private JSlider sliderUnidade3 = null;
    private JSlider sliderGanho = null;
    private JSlider sliderFrq1 = null;
    private JSlider sliderFrq2 = null;
    private JSlider sliderAmp1 = null;
    private JSlider sliderAmp2 = null;
    private JButton salvarButton = null;
    private JButton tocarButton = null;
    private JButton visualizarButton = null;
    
	Melodia melodiaEscolhida = null;
	Dispositivo instrumentoEscolhido = null;
	String nomeSom = "sem_nome";
	Som som = null;
	private boolean melodiaSelecionada = false;
	private boolean instrumentoSelecionado = false;

    
	public Interface(){
		mainFrame = new JFrame("Trabalho 2 de Introducao a Computacao Sonica");
		mainFrame.setContentPane(criaMainPanel());
		
		ImageIcon frameIcon = new ImageIcon("img/icon.png");
		mainFrame.setIconImage(frameIcon.getImage());
		
		mainFrame.setSize(new Dimension(largura, altura));
		mainFrame.setLocation(posx, posy); 
		mainFrame.setResizable(false);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
	}
	
	private  JPanel criaMainPanel(){
		JPanelFundo mainPanel = new JPanelFundo("img/background0.jpg");
		
		JPanel auxPanel = new JPanel(new BorderLayout());
		auxPanel.setOpaque(false);
		
		JPanel configPanel = new JPanel();
		configPanel.setOpaque(false);
		
		configPanel.add(criaPanelGeral());
		configPanel.add(criaInstrumentoAPanel());
		configPanel.add(criaInstrumentoBPanel());
		auxPanel.add(configPanel, BorderLayout.CENTER);
		auxPanel.add(criaSalvarPanel(), BorderLayout.PAGE_END);
		mainPanel.add(auxPanel);
		
		return mainPanel;
	}
	
	private  JPanel criaPanelGeral(){
		JPanel geralPanel = new JPanel();
		geralPanel.setOpaque(false);
		geralPanel.setLayout(new GridLayout(8,1));
		geralPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		geralPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JLabel melodiaLabel = new JLabel("Melodias: ");
		melodiaLabel.setForeground(Color.WHITE);
		geralPanel.add(melodiaLabel);
		
		String melodias[] = {"", "Skyrim", "Fairy Tail", "Sonata Scarlatti",
				"Drawing Quintet Flauta", "Duda No Frevo", "Frase Corne Ingles", 
				"Frase Terca Maior Harpa", "Afinacao Igualmente Temperada", "Akebono"};
		final JComboBox<Object> melodiasBox = new JComboBox<Object>(melodias);
		melodiasBox.setBackground(Color.BLACK);
		melodiasBox.setForeground(Color.WHITE);
		
		melodiasBox.setToolTipText("Abrir melodias disponivel");
		
		melodiasBox.setActionCommand("escolherMelodia");
		melodiasBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	escolhidoMusica = melodiasBox.getSelectedIndex();
            	escolherMelodias();
            	if(escolhidoMusica != 0 && escolhidoInstrumento != 0){
            		salvarButton.setEnabled(true);
            		tocarButton.setEnabled(true);
            		visualizarButton.setEnabled(true);
            	} else{
            		salvarButton.setEnabled(false);
            		tocarButton.setEnabled(false);
            		visualizarButton.setEnabled(false);
            	}
            }
		});
		geralPanel.add(melodiasBox);
		
		JLabel instrumentoLabel = new JLabel("Instrumentos: ");
		instrumentoLabel.setForeground(Color.WHITE);
		geralPanel.add(instrumentoLabel);
		
		String instrumentos[] = {"", "Instrumento A", "Instrumento B", 
				"Flauta Nao Harmonica Tonal", "Marimba", "Trompete", "Gongo", 
				"Som puro"};
		final JComboBox<Object> instrumentosBox = new JComboBox<Object>(instrumentos);
		instrumentosBox.setBackground(Color.BLACK);
		instrumentosBox.setForeground(Color.WHITE);
		
		instrumentosBox.setToolTipText("Abrir instrumento disponivel");
		instrumentosBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	escolhidoInstrumento = instrumentosBox.getSelectedIndex();
            	escolherInstrumentos();
            	if(escolhidoMusica != 0 && escolhidoInstrumento != 0){
            		salvarButton.setEnabled(true);
            		tocarButton.setEnabled(true);
            		visualizarButton.setEnabled(true);
            	} else{
            		salvarButton.setEnabled(false);
            		tocarButton.setEnabled(false);
            		visualizarButton.setEnabled(false);
            	}
            }
		});
		geralPanel.add(instrumentosBox);
		
		JLabel estereofoniaLabel = new JLabel("Estereofonia: ");
		estereofoniaLabel.setForeground(Color.WHITE);
		geralPanel.add(estereofoniaLabel);
		sliderEstereofonia = constroiSliderAmplitude(0, 10, 5, 10);
		lambda = (double) sliderEstereofonia.getValue()/10;
		sliderEstereofonia.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				lambda = (double) sliderEstereofonia.getValue()/10;
				//System.out.println(lambda);
				if(escolhidoInstrumento == 1){
					((InstrumentoA) instrumentoEscolhido).setLambda(lambda);
				} else if(escolhidoInstrumento == 2){
					((InstrumentoB) instrumentoEscolhido).setLambda(lambda);
				}
			}
		});
		geralPanel.add(sliderEstereofonia);
		
		JLabel ganhoLabel = new JLabel("Ganho: ");
		ganhoLabel.setForeground(Color.WHITE);
		geralPanel.add(ganhoLabel);
		sliderGanho = constroiSlider(1, 10, 1, 1);
		sliderganho = sliderGanho.getValue();
		sliderGanho.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderganho = sliderGanho.getValue();
				//System.out.println(sliderganho);
				if(escolhidoInstrumento == 1){
					((InstrumentoA) instrumentoEscolhido).setGanho(sliderganho);
				} else if(escolhidoInstrumento == 2){
					((InstrumentoB) instrumentoEscolhido).setGanho(sliderganho);
				}
			}
		});
		geralPanel.add(sliderGanho);
		
		sliderEstereofonia.setEnabled(false);
		sliderGanho.setEnabled(false);
		
		return geralPanel;
	}
	
	private  JPanel criaInstrumentoAPanel(){
		JPanel instrumentoPanel = new JPanel();
		instrumentoPanel.setOpaque(false);
		instrumentoPanel.setLayout(new GridLayout(9,1));
		instrumentoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		instrumentoPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JLabel harmonicosLabel = new JLabel("Instrumento A: ");
		harmonicosLabel.setForeground(Color.WHITE);
		instrumentoPanel.add(harmonicosLabel);
		
		JLabel unidade1Label = new JLabel("Harmonico Unidade 1: ");
		unidade1Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade1Label);
		sliderUnidade1 = constroiSlider(1, 10, 1, 1);
		slideruni1 = sliderUnidade1.getValue();
		sliderUnidade1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				slideruni1 = sliderUnidade1.getValue();
				//System.out.println(slideruni1);
				((InstrumentoA) instrumentoEscolhido).setUH1(1, slideruni1);
			}
		});
		instrumentoPanel.add(sliderUnidade1);
		
		JLabel unidade2Label = new JLabel("Harmonico Unidade 2: ");
		unidade2Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade2Label);
		sliderUnidade2 = constroiSlider(1, 10, 2, 1);
		slideruni2 = sliderUnidade2.getValue();
		sliderUnidade2.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				slideruni2 = sliderUnidade2.getValue();
				//System.out.println(slideruni2);
				((InstrumentoA) instrumentoEscolhido).setUH1(2, slideruni2);
			}
		});
		instrumentoPanel.add(sliderUnidade2);
		
		JLabel unidade3Label = new JLabel("Harmonico Unidade 3: ");
		unidade3Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade3Label);
		sliderUnidade3 = constroiSlider(1, 10, 5, 1);
		slideruni3 = sliderUnidade3.getValue();
		sliderUnidade3.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				slideruni3 = sliderUnidade3.getValue();
				//System.out.println(slideruni3);
				((InstrumentoA) instrumentoEscolhido).setUH1(3, slideruni3);
			}
		});
		instrumentoPanel.add(sliderUnidade3);
		
		JLabel faseLabel = new JLabel("Fase dos harmonicos: ");
		faseLabel.setForeground(Color.WHITE);
		instrumentoPanel.add(faseLabel);
		sliderFase = constroiSliderFase(0, 1440, 0, 4);
		fase = (float) sliderFase.getValue()/4;
		sliderFase.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				fase = (float) sliderFase.getValue()/4;
				//System.out.println(fase);
				((InstrumentoA) instrumentoEscolhido).setFase(fase);
			}
		});
		instrumentoPanel.add(sliderFase);
		
		sliderUnidade1.setEnabled(false);
		sliderUnidade2.setEnabled(false);
		sliderUnidade3.setEnabled(false);
		sliderFase.setEnabled(false);
				
		return instrumentoPanel;
	}

	private  JPanel criaInstrumentoBPanel(){
		JPanel instrumentoPanel = new JPanel();
		instrumentoPanel.setOpaque(false);
		instrumentoPanel.setLayout(new GridLayout(9,1));
		instrumentoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		instrumentoPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
		
		JLabel harmonicosLabel = new JLabel("Instrumento B: ");
		harmonicosLabel.setForeground(Color.WHITE);
		instrumentoPanel.add(harmonicosLabel);
		
		JLabel unidade1Label = new JLabel("Frequencia de corte da amplitude: ");
		unidade1Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade1Label);
		sliderFrq1 = constroiSliderFrequencia(0, 100, 50);
		sliderfrq1 = (float) sliderFrq1.getValue();
		sliderFrq1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderfrq1 = (float) sliderFrq1.getValue();
				//System.out.println(sliderfrq1);
				((InstrumentoB) instrumentoEscolhido).setFrequenciaDeCorte(1, sliderfrq1);
			}
		});
		instrumentoPanel.add(sliderFrq1);
		
		JLabel unidade2Label = new JLabel("Frequencia de corte da frequencia: ");
		unidade2Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade2Label);
		sliderFrq2 = constroiSliderFrequencia(0, 100, 30);
		sliderfrq2 = (float) sliderFrq2.getValue();
		sliderFrq2.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				sliderfrq2 = (float) sliderFrq2.getValue();
				//System.out.println(sliderfrq2);
				((InstrumentoB) instrumentoEscolhido).setFrequenciaDeCorte(2, sliderfrq2);
			}
		});
		instrumentoPanel.add(sliderFrq2);
		
		JLabel unidade3Label = new JLabel("Amplitude do ruido da amplitude: ");
		unidade3Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade3Label);
		sliderAmp1 = constroiSliderAmplitude(0, 10, 1, 2);
		slideramp1 = (float) sliderAmp1.getValue()/2;
		sliderAmp1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				slideramp1 = (float) sliderAmp1.getValue()/2;
				//System.out.println(slideramp1);
				((InstrumentoB) instrumentoEscolhido).setAmplitude(1, slideramp1);
			}
		});
		instrumentoPanel.add(sliderAmp1);
		
		JLabel unidade4Label = new JLabel("Amplitude do ruido da frequencia: ");
		unidade4Label.setForeground(Color.WHITE);
		instrumentoPanel.add(unidade4Label);
		sliderAmp2 = constroiSliderAmplitude(0, 10, 2, 2);
		slideramp2 = (float) sliderAmp2.getValue()/2;
		sliderAmp2.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				slideramp2 = (float) sliderAmp2.getValue()/2;
				//System.out.println(slideramp2);
				((InstrumentoB) instrumentoEscolhido).setAmplitude(2, slideramp2);
			}
		});
		instrumentoPanel.add(sliderAmp2);
		
		sliderFrq1.setEnabled(false);
		sliderFrq2.setEnabled(false);
		sliderAmp1.setEnabled(false);
		sliderAmp2.setEnabled(false);
		
		return instrumentoPanel;
	}
	
	private  JPanel criaSalvarPanel(){
		JPanel salvarPanel = new JPanel();
		salvarPanel.setOpaque(false);
		salvarPanel.setBorder(BorderFactory.createEmptyBorder(0,20,20,20));
		
		salvarButton = constroiButton("    SALVAR    ", 15);
		
		salvarPanel.add(salvarButton);
		salvarPanel.add(Box.createRigidArea(new Dimension(0,20)));
		salvarButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e) {
            		salvarSom();
            }
		});
		
		tocarButton = constroiButton("    TOCAR    ", 15);
		salvarPanel.add(tocarButton);
		salvarPanel.add(Box.createRigidArea(new Dimension(0,20)));
		
		tocarButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e) {
            		tocarSom();
            }
		});
		
		visualizarButton = constroiButton(" VISUALIZAR ", 15);
		salvarPanel.add(visualizarButton);
		salvarPanel.add(Box.createRigidArea(new Dimension(0,20)));
		visualizarButton.addActionListener(new ActionListener(){
			@Override
            public void actionPerformed(ActionEvent e) {
				visualizarSom();
            }
		});
		
		salvarButton.setEnabled(false);
		tocarButton.setEnabled(false);
		visualizarButton.setEnabled(false);
		
		return salvarPanel;
	}
	
	public  JButton constroiButton(final String legenda, float tamanhoFonte){
        JButton botao = new JButton(legenda);
        botao.setMargin(new Insets(2, 2, 2, 2));
        botao.setFocusable(false);
        botao.setFont(botao.getFont().deriveFont(Font.PLAIN));
        botao.setFont(botao.getFont().deriveFont(tamanhoFonte));
        botao.setBackground(Color.BLACK);
        botao.setForeground(Color.WHITE);
		botao.setActionCommand(legenda);
        
        return botao;
	}
	
	public  JSlider constroiSlider(int min, int max, int ini, final int divisor){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, ini);
		final JLabel label = new JLabel();
		label.setText(""+(float) ini/divisor);
		label.setForeground(Color.WHITE);
		final Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
		labelTable.put(ini, label);
		
		slider.addChangeListener(new ChangeListener(){
	        public void stateChanged(ChangeEvent e){
	            JSlider source = (JSlider)e.getSource();
	            if(!source.getValueIsAdjusting()){
	            	int valor = (int) source.getValue();
	            	label.setText("" + valor/divisor + "");
	            	labelTable.clear();
	            	labelTable.put(valor, label);
	            }
	        }
	    });
		
		slider.setPaintLabels(true);
		slider.setLabelTable(labelTable);
		slider.setMaximumSize(new Dimension(400,60));
	    slider.setFocusable(false);
	    slider.setOpaque(false);
		
	    return slider;
	}
	
	public JSlider constroiSliderFase(int min, int max, int ini, final int divisor){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, ini);
		final JLabel label = new JLabel();
		label.setText(" " + (float) ini/divisor + " ");
		label.setForeground(Color.WHITE);
		final Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
		labelTable.put(ini, label);
		
		slider.addChangeListener(new ChangeListener(){
	        public void stateChanged(ChangeEvent e){
	            JSlider source = (JSlider)e.getSource();
	            if(!source.getValueIsAdjusting()){
	            	int valor = (int) source.getValue();
	            	label.setText("" + valor/divisor + "");
	            	labelTable.clear();
	            	labelTable.put(valor, label);
	            }
	        }
	    });
		
		slider.setPaintLabels(true);
		slider.setLabelTable(labelTable);
		slider.setMaximumSize(new Dimension(400,60));
	    slider.setFocusable(false);
	    slider.setOpaque(false);
		
	    return slider;
	}
	
	public  JSlider constroiSliderAndamento(int min, int max, int ini){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, ini);
		final JLabel label = new JLabel();
		label.setText("" + (float) (0.5+(ini*0.5)));
		label.setForeground(Color.WHITE);
		final Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
		labelTable.put(ini, label);
		
		slider.addChangeListener(new ChangeListener(){
		    public void stateChanged(ChangeEvent e){
		        JSlider source = (JSlider)e.getSource();
		        if(!source.getValueIsAdjusting()){
		        	int valor = (int) source.getValue();
		        	label.setText("" + (0.5+(valor*0.5)));
		        	labelTable.clear();
		        	labelTable.put(valor, label);
		        }
		    }
		});
		
		slider.setPaintLabels(true);
		slider.setLabelTable(labelTable);
		slider.setMaximumSize(new Dimension(400,60));
		slider.setFocusable(false);
		slider.setOpaque(false);
		
		return slider;
	}
	
	public  JSlider constroiSliderFrequencia(int min, int max, int ini){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, ini);
		final JLabel label = new JLabel();
		label.setText(""+ (float) ini);
		label.setForeground(Color.WHITE);
		final Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
		labelTable.put(ini, label);
		
		slider.addChangeListener(new ChangeListener(){
	        public void stateChanged(ChangeEvent e){
	            JSlider source = (JSlider)e.getSource();
	            if(!source.getValueIsAdjusting()){
	            	int valor = (int) source.getValue();
	            	label.setText("" + (int)(/*10**/valor));	            	           		 
	            	labelTable.clear();
	            	labelTable.put(valor, label);
	            }
	        }
	    });
		
		slider.setPaintLabels(true);
		slider.setLabelTable(labelTable);
		slider.setMaximumSize(new Dimension(400,60));
	    slider.setFocusable(false);
	    slider.setOpaque(false);
		
	    return slider;
	}
	
	public  JSlider constroiSliderAmplitude(int min, int max, int ini, final int divisor){
		JSlider slider = new JSlider(JSlider.HORIZONTAL, min, max, ini);
		final JLabel label = new JLabel();
		label.setText("" + (float) ini/divisor);
		label.setForeground(Color.WHITE);
		final Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
		labelTable.put(ini, label);
		
		slider.addChangeListener(new ChangeListener(){
	        public void stateChanged(ChangeEvent e){
	            JSlider source = (JSlider)e.getSource();
	            if(!source.getValueIsAdjusting()){
	            	int valor = (int) source.getValue();
	            	//label.setText("" + valor);
	            	label.setText(String.valueOf((float)valor/divisor) + "");
	            	labelTable.clear();
	            	labelTable.put(valor, label);
	            }
	        }
	    });
		
		slider.setPaintLabels(true);
		slider.setLabelTable(labelTable);
		slider.setMaximumSize(new Dimension(400,60));
	    slider.setFocusable(false);
	    slider.setOpaque(false);
		
	    return slider;
	}
	
	public void actionPerformed(ActionEvent evento){
		if("escolherMelodia".equals(evento.getActionCommand())){
			escolherMelodias();		
		}
		else if("escolherInstrumento".equals(evento.getActionCommand())){
			escolherInstrumentos();
		}
		else if("Salvar".equals(evento.getActionCommand())){
			salvarSom();
		}
		else if("Visualizar".equals(evento.getActionCommand())){
			visualizarSom();
		}
		else if("Tocar".equals(evento.getActionCommand())){
			tocarSom();
		}
	}
	
	public  void escolherMelodias (){
		//System.out.println("escolheu melodia");	
		melodiaEscolhida = null;
		
		switch (escolhidoMusica){
			case 0:
				System.gc();
				break;
			case 1:
				melodiaEscolhida = Melodias.skyrim();
				break;
			case 2:
				melodiaEscolhida = Melodias.fairytail();
				break;
			case 3:
				melodiaEscolhida = Tema.sonata_scarlatti();
				break;
			case 4:
				melodiaEscolhida = Tema.tema_aa_drawing_quintet_flauta();
				break;
			case 5:
				melodiaEscolhida = Tema.tema_duda_no_frevo_eq();
				break;
			case 6:
				melodiaEscolhida = ColecaoDeFrases.fraseparacorneingles();
				break;
			case 7:
				melodiaEscolhida = ColecaoDeFrases.tercamaior3_harpa();
				break;
			case 8:
				melodiaEscolhida = Escala.afinacao_igualmente_temperada();
				break;
			case 9:
				melodiaEscolhida = Escala.akebono();
				break;
			default:
				break;
		}
		
		if (escolhidoMusica != 0 & !melodiaSelecionada)
			melodiaSelecionada = true;
		else if (escolhidoMusica != 0 & melodiaSelecionada)
			escolhidoMusica = -1;
		else if (escolhidoMusica == 0)
			melodiaSelecionada = false;
	}
	

	public  void escolherInstrumentos (){
		//System.out.println("escolheu instrumento");
		instrumentoEscolhido = null;
		sliderFase.setEnabled(false);
		sliderEstereofonia.setEnabled(false);
		sliderUnidade1.setEnabled(false);
		sliderUnidade2.setEnabled(false);
		sliderUnidade3.setEnabled(false);
		sliderGanho.setEnabled(false);
		sliderFrq1.setEnabled(false);
		sliderFrq2.setEnabled(false);
		sliderAmp1.setEnabled(false);
		sliderAmp2.setEnabled(false);
		
		switch (escolhidoInstrumento){
			case 0:
				System.gc();
				break;
			case 1:
				instrumentoEscolhido = new InstrumentoA();
				sliderFase.setEnabled(true);
				sliderEstereofonia.setEnabled(true);
				sliderUnidade1.setEnabled(true);
				sliderUnidade2.setEnabled(true);
				sliderUnidade3.setEnabled(true);
				sliderGanho.setEnabled(true);
				
				/*((InstrumentoA) instrumentoEscolhido).setFase(fase);
				((InstrumentoA) instrumentoEscolhido).setLambda(lambda);
				((InstrumentoA) instrumentoEscolhido).setUH1(1, slideruni1);
				((InstrumentoA) instrumentoEscolhido).setUH1(2, slideruni2);
				((InstrumentoA) instrumentoEscolhido).setUH1(3, slideruni3);
				((InstrumentoA) instrumentoEscolhido).setGanho(sliderganho);*/
				break;
			case 2:
				instrumentoEscolhido = new InstrumentoB();
				sliderEstereofonia.setEnabled(true);
				sliderFrq1.setEnabled(true);
				sliderFrq2.setEnabled(true);
				sliderAmp1.setEnabled(true);
				sliderAmp2.setEnabled(true);
				sliderGanho.setEnabled(true);
				
				/*((InstrumentoB) instrumentoEscolhido).setLambda(lambda);
				((InstrumentoB) instrumentoEscolhido).setFrequenciaDeCorte(1, sliderfrq1);
				((InstrumentoB) instrumentoEscolhido).setFrequenciaDeCorte(2, sliderfrq2);
				((InstrumentoB) instrumentoEscolhido).setAmplitude(1, slideramp1);
				((InstrumentoB) instrumentoEscolhido).setAmplitude(2, slideramp2);
				((InstrumentoB) instrumentoEscolhido).setGanho(sliderganho);*/
				break;
			case 3:
				instrumentoEscolhido = BancoDeInstrumentos.flauta_nao_harmonica_tonal();
				break;
			case 4:
				instrumentoEscolhido = BancoDeInstrumentos.marimba_i51();
				break;
			case 5:
				instrumentoEscolhido = BancoDeInstrumentos.trompete01();
				break;
			case 6:
				instrumentoEscolhido = BancoDeInstrumentos.gongo_aa_1994();
				break;
			case 7:
				instrumentoEscolhido = BancoDeInstrumentos.sompuro();
				break;
			default:
				break;
		}
		
		//configurarInfosInstrumento ();
		
		if (escolhidoInstrumento != 0 & !instrumentoSelecionado)
			instrumentoSelecionado = true;
		else if (escolhidoInstrumento != 0 & instrumentoSelecionado)
			escolhidoInstrumento = -1;
		else if (escolhidoInstrumento == 0)
			instrumentoSelecionado = false;
		
		//analisarVisualizar (instrumentoEscolhido);
	}


	public  void salvarSom (){
		formarSom();
		som.salvawave ();
	}

	public  void configurarNomeSom (){
		//nomeSom = Interface.nomeWave.getText();
		
		if (nomeSom.equals("nome do arquivo")|| nomeSom.equals(""))
			nomeSom = "sem_nome";
	}

	public  void tocarSom (){
		salvarSom();
		som.tocawave();
	}

	public  void visualizarSom (){
		formarSom();
		som.visualiza();
	}

	private  void formarSom (){
		som = null;
		System.gc();
		
		if (!melodiaEscolhida.equals(null) & !instrumentoEscolhido.equals(null)){
			som = melodiaEscolhida.getSom(instrumentoEscolhido);
			//System.out.println("ok");
			configurarNomeSom();
			som.setNome(nomeSom);
		} //else System.out.println("m ok");
	}

}

class JPanelFundo extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage painelFundo;
	
    public JPanelFundo(String file) {
        try {
        	painelFundo = ImageIO.read(new File(file));
        } catch(IOException e) {
            e.printStackTrace();
        }
        setOpaque(true);
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(painelFundo, 0, 0, getWidth(), getHeight(), this);
    }
}