/*PLAYER MIDI
~~~~~~~~~~~~~~~~~~~~~
INTEGRANTES DO GRUPO:

Ingrid Santana Lopes		 		14/0083065
Marcos Paulo Cayres Rosa			14/0027131 
Rennê Ruan Alves Oliveira	 		14/0030930
Bruno José Bergamaschi Kumer Reis 	14/0017666
Amanda Lopes Dantas 				13/0100391
*/

import java.text.DecimalFormat;
import java.io.File;
import java.io.IOException;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Soundbank;

import javax.imageio.ImageIO;


public class Player extends JFrame implements Runnable {                          
        
	private static final long serialVersionUID = 1L;
	private  int largura = 800;
    private  int altura  = 500;
    
    Dimension tamanhoTela = Toolkit.getDefaultToolkit().getScreenSize();
    private  int posx   = (int)tamanhoTela.getWidth()/2-largura/2;
    private  int posy   = (int)tamanhoTela.getHeight()/2-altura/2;

    ImageIcon logo      = null;
    private static String path;
    
    final JButton botaoINICIO            = constroiBotao("\u25c0\u25c0 |", 9);
    final JButton botaoTOCAR             = constroiBotao("\u25b6", 9);
    final JButton botaoVOLTAR            = constroiBotao("\u25c0\u25c0", 9);
    final JButton botaoAVANCAR          = constroiBotao("\u25b6\u25b6", 9);  
    final JButton botaoFAZERPAUSA        = constroiBotao("\u25ae\u25ae", 9);
    final JButton botaoPARAR             = constroiBotao("\u25a0", 9);
    final JButton botaoABRIR  			 = constroiBotao("   ABRIR MIDI  ", 15);
    final JButton botaoINFO            	 = constroiBotao("INFORMACOES", 14);
    final JButton botaoEVENTOS			 = constroiBotao("    EVENTOS    ", 15);
    final JButton botaoConverter		 = constroiBotao("Converter .Java",15);
    
    final JLabel  nomeArquivo = new JLabel(" Arquivo:");
    final JLabel  nomeDiretorio = new JLabel("  Diretorio:");
    final JButton botaoMOSTRADORduracao  	= constroiBotao(" 00h 00m 00s", 10); 
    final JButton botaoMOSTRADORinstante 	= constroiBotao(" ", 9);  
    final JButton botaoMOSTRADORvalorvolume = constroiBotao(" ", 9); 
    private BufferedImage imageSOM = null;
    
    private ImageIcon frameIcon = new ImageIcon("img/icon.png");
    final JDialog dialog = new JDialog(this, "Informacoes MIDI");
    final JDialog dialog2 = new JDialog(this, "Relacao MIDI");
    final JDialog dialogEventos = new JDialog(this, "Eventos MIDI");
    final JDialog dialogParam = new JDialog(this, "Parametros MIDI");
    final JDialog exec = new JDialog(this, "Informacoes em execucao");
    private JTextArea areaTexto = null;
 
    private String nomeArq;
    private static Sequencer sequenciador = null;
	private static Sequence sequencia;
	private Sequence sequenciaNova;
	private static Synthesizer sintetizador;
	private Receiver receptor = null;
	private long inicio = 0L; 
	
	static Soundbank   banco_selecionado;
	
	private int          volumeATUAL             = 96; // vol = 75
	private JSlider      sliderVolume            = new JSlider(JSlider.HORIZONTAL,0, 127, volumeATUAL);        
	private JProgressBar sliderPROGRESSOinstante = new JProgressBar();
	
	private boolean   soando = false;
	private boolean   aberto = false;
	
    Color colorPlayer = new Color(100,220,225);
    Color colorBorda = new Color(64, 64, 64);
    Color colorBotao = new Color(0, 100, 100);
    Color colorArq = new Color(50, 100,100);
    Color colorBlack = new Color(0,0,0);
    Color colorNome = new Color(204, 229, 225);
	
	static final int FORMULA_DE_COMPASSO = 0x58;
	static final int MENSAGEM_TONALIDADE = 0x59;  
	static final int MENSAGEM_TEXTO = 0x01;  
	
	double dur = 0;
    double t;
    int    pos = 0, contInst = 0;
	
	static private class Par{
		int x, y;
	    
	    Par (int x_, int y_){
	    	this.x = x_;
	    	this.y = y_;          
	    }
	  
	    int getX(){
	    	return x;
	    }
	    
	    int getY(){
	    	return y;
	    }
	}

	public Player(){  
		super("Player");
        
        setIconImage(frameIcon.getImage());       
        
        try{
   	       	// Panels e layouts
        	JPanel panelConteudo = new JPanel();
        	panelConteudo.setLayout(new BorderLayout());
        
            JPanel painelStartPause = new JPanel();
            JPanel painelArquivos = new JPanel();
            JPanel painelTocador = new JPanel();
            JPanel painelVolume = new JPanel();
            try {
                imageSOM = ImageIO.read(new File("img/speaker.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            JPanel lateralDir = new JPanel();
            JPanelFundo centro = new JPanelFundo("img/background0.jpg");
            JPanel baixo = new JPanel();
            
            // Listeners
            botaoABRIR.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		abrirMIDI();
            	}
            });
            
            botaoINICIO.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		parar();
            		tocar(path, inicio);
            	}
            });  

            botaoTOCAR.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		tocar(path, inicio);
            	}
            });
            
            botaoConverter.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
            		converter(path);
				}
            });
            
            botaoVOLTAR.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		if(aberto && dur != 0){
            			inicio -= 3000000;
            			if(inicio < 0) inicio = 0L;
	            		if(soando) sequenciador.setMicrosecondPosition(inicio);

	                    t     = inicio/1000000;
	                    pos   = (int) ((t*100)/dur);
                    	sliderPROGRESSOinstante.setValue(pos);								
                        botaoMOSTRADORinstante.setText(formataInstante(t));
	            	}
            	}
            });
            
            botaoAVANCAR.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		if(aberto && dur != 0){
            			inicio += 3000000;
            			if(inicio > (dur*1000000)) parar();
	            		if(soando) sequenciador.setMicrosecondPosition(inicio);
	            		
	            		t     = inicio/1000000;
	                    pos   = (int) ((t*100)/dur);
                    	sliderPROGRESSOinstante.setValue(pos);								
                        botaoMOSTRADORinstante.setText(formataInstante(t));
	            	}
            	}
            });

            botaoFAZERPAUSA.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		fazerpausa();          
            	}
            });

            botaoPARAR.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		parar();
            	}
            });
            
            botaoINFO.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		determInst();
            		determInfo();
            	}
            });
            
            botaoEVENTOS.addActionListener(new ActionListener(){
            	public void actionPerformed(ActionEvent e){
            		determEventos();
            	}
            });
            
            sliderVolume.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e){
                    JSlider source = (JSlider)e.getSource();
                    if(!source.getValueIsAdjusting()){
                        int valor = (int) source.getValue();
                        
                        ShortMessage mensagemDeVolume = new ShortMessage();
                        for(int i = 0; i < 16; i++){
                            try {
                            	mensagemDeVolume.setMessage(ShortMessage.CONTROL_CHANGE, i, 7, valor);
                                receptor.send(mensagemDeVolume, -1);
                            }
                            catch (InvalidMidiDataException e1) {
                            	e1.printStackTrace();
                            }
                        }
                        
                        volumeATUAL = valor;
                        botaoMOSTRADORvalorvolume.setText("" + (volumeATUAL*100)/127 + "%");
                    }
                }
            });
            
            // Lateral Direita
            lateralDir.setLayout(new BoxLayout(lateralDir, BoxLayout.PAGE_AXIS));
            lateralDir.setBackground(colorBlack);
            botaoABRIR.setBackground(colorBotao);
            botaoINFO.setBackground(colorBotao);
            botaoConverter.setBackground(colorBotao);
            botaoEVENTOS.setBackground(colorBotao);
            
            botaoABRIR.setForeground(Color.WHITE);
            botaoINFO.setForeground(Color.WHITE);
            botaoConverter.setForeground(Color.WHITE);
            botaoEVENTOS.setForeground(Color.WHITE);
            
            botaoABRIR.setEnabled(true);
            botaoINFO.setEnabled(false);
            botaoEVENTOS.setEnabled(false);
            botaoConverter.setEnabled(false);
            
            botaoABRIR.setAlignmentX(Component.CENTER_ALIGNMENT);
            botaoINFO.setAlignmentX(Component.CENTER_ALIGNMENT);
            botaoEVENTOS.setAlignmentX(Component.CENTER_ALIGNMENT);
            botaoConverter.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            lateralDir.add(Box.createRigidArea(new Dimension(0,100)));
            lateralDir.add(botaoABRIR); lateralDir.add(Box.createRigidArea(new Dimension(0,15)));
            lateralDir.add(botaoINFO); lateralDir.add(Box.createRigidArea(new Dimension(0,15)));
            lateralDir.add(botaoEVENTOS); lateralDir.add(Box.createRigidArea(new Dimension(0,15)));
            lateralDir.add(botaoConverter);
            lateralDir.setPreferredSize(new Dimension(140, 0));
            lateralDir.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
            
            panelConteudo.add(lateralDir, BorderLayout.LINE_END);
            
            // Centro
            nomeArquivo.setFont(new Font("Arial Black", java.awt.Font.BOLD, 25));
            nomeArquivo.setForeground(colorArq);
            //nomeArquivo.setBackground(Color.black);
            nomeArquivo.setOpaque(false);
            nomeArquivo.setPreferredSize(new Dimension(580,40));
            Border border = BorderFactory.createLineBorder(colorBorda, 3);
            nomeArquivo.setBorder(border);
            painelArquivos.add(nomeArquivo);
            
            
            nomeDiretorio.setFont(new Font("Arial Black", java.awt.Font.PLAIN, 15));
            nomeDiretorio.setForeground(colorArq);
            //nomeDiretorio.setBackground(Color.black);
            nomeDiretorio.setOpaque(false);
            nomeDiretorio.setPreferredSize(new Dimension(580,30));
            Border border2 = BorderFactory.createLineBorder(colorBorda, 3);
            nomeDiretorio.setBorder(border2);
            painelArquivos.add(nomeDiretorio);
            
            Border border4 = BorderFactory.createLineBorder(colorBorda, 2);
            botaoTOCAR.setBorder(border4); botaoTOCAR.setOpaque(true);
            botaoTOCAR.setPreferredSize(new Dimension(50,50));
            botaoTOCAR.setForeground(colorPlayer);
            botaoTOCAR.setFont(new Font("SwingText", java.awt.Font.BOLD, 20));
           
            botaoFAZERPAUSA.setBorder(border4); botaoFAZERPAUSA.setOpaque(true);
            botaoFAZERPAUSA.setPreferredSize(new Dimension(50,50));
            botaoFAZERPAUSA.setForeground(colorPlayer);
            botaoFAZERPAUSA.setFont(new Font("SwingText", java.awt.Font.BOLD, 15));

            botaoPARAR.setBorder(border4); botaoPARAR.setOpaque(true);       
            botaoPARAR.setPreferredSize(new Dimension(50,50));
            botaoPARAR.setForeground(colorPlayer);
            botaoPARAR.setFont(new Font("SwingText", java.awt.Font.BOLD, 20));
            
            botaoINICIO.setBorder(border4); botaoPARAR.setOpaque(true);       
            botaoINICIO.setPreferredSize(new Dimension(40,40));
            botaoINICIO.setForeground(colorPlayer);
            botaoINICIO.setFont(new Font("SwingText", java.awt.Font.BOLD, 15));
            
            botaoVOLTAR.setBorder(border4); botaoPARAR.setOpaque(true);       
            botaoVOLTAR.setPreferredSize(new Dimension(40,40));
            botaoVOLTAR.setForeground(colorPlayer);
            botaoVOLTAR.setFont(new Font("SwingText", java.awt.Font.BOLD, 15));
            
            botaoAVANCAR.setBorder(border4); botaoPARAR.setOpaque(true);       
            botaoAVANCAR.setPreferredSize(new Dimension(40,40));
            botaoAVANCAR.setForeground(colorPlayer);
            botaoAVANCAR.setFont(new Font("SwingText", java.awt.Font.BOLD, 15));
            
            
            botaoTOCAR.setEnabled(false);
            botaoFAZERPAUSA.setEnabled(false);
            botaoPARAR.setEnabled(false);
            botaoINICIO.setEnabled(false);
            botaoVOLTAR.setEnabled(false);
            botaoAVANCAR.setEnabled(false);
            
            painelStartPause.setOpaque(false);
            painelStartPause.setPreferredSize(new Dimension(600, 70));
            painelStartPause.add(Box.createRigidArea(new Dimension(1,200)));
            painelStartPause.add(botaoINICIO); painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            painelStartPause.add(botaoVOLTAR); painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            painelStartPause.add(botaoTOCAR); painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            painelStartPause.add(botaoFAZERPAUSA); painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            painelStartPause.add(botaoPARAR);painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            painelStartPause.add(botaoAVANCAR); painelStartPause.add(Box.createRigidArea(new Dimension(15,10)));
            
            botaoMOSTRADORinstante.setBackground(colorArq);
            botaoMOSTRADORinstante.setPreferredSize(new Dimension(100,20));
            botaoMOSTRADORinstante.setForeground(Color.white);
            botaoMOSTRADORvalorvolume.setBackground(Color.white);
            
            Image dimg = imageSOM.getScaledInstance(12, 12, Image.SCALE_SMOOTH);
            ImageIcon dicon = new ImageIcon(dimg);
            JLabel volImg = new JLabel(dicon);
            
            sliderVolume.setMaximumSize(new Dimension(500,20));
            sliderVolume.setFocusable(false);
            sliderVolume.setEnabled(false);
            sliderVolume.setOpaque(false);
            
            botaoMOSTRADORvalorvolume.setText("" + (volumeATUAL*100)/127 + "%");
            botaoMOSTRADORvalorvolume.setOpaque(false);
            botaoMOSTRADORvalorvolume.setForeground(colorNome);
            
            painelVolume.setLayout(new BoxLayout(painelVolume, BoxLayout.X_AXIS));
            painelVolume.setPreferredSize(new Dimension(600, 60));;
            painelVolume.setAlignmentY(Component.CENTER_ALIGNMENT);
            painelVolume.add(volImg);
            painelVolume.add(sliderVolume);
            painelVolume.add(botaoMOSTRADORvalorvolume);
            painelVolume.setOpaque(false);
            
            centro.setLayout(new BoxLayout(centro, BoxLayout.PAGE_AXIS));
            painelArquivos.setOpaque(false);
            painelArquivos.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            centro.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            centro.add(painelArquivos); centro.add(Box.createRigidArea(new Dimension(0,20)));
            centro.add(painelStartPause); centro.add(Box.createRigidArea(new Dimension(0,20)));
            centro.add(painelVolume);
            panelConteudo.add(centro, BorderLayout.CENTER);
            
            // Baixo
            baixo.setBackground(colorBlack);
            
            sliderPROGRESSOinstante.setPreferredSize(new Dimension(500,20));
            sliderPROGRESSOinstante.setFocusable(false);
            painelTocador.add(botaoMOSTRADORinstante);
            painelTocador.add(sliderPROGRESSOinstante);
            painelTocador.setOpaque(false);
                               
            botaoMOSTRADORduracao.setBackground(colorArq);
            botaoMOSTRADORduracao.setPreferredSize(new Dimension(100,20));
            botaoMOSTRADORduracao.setForeground(Color.white);
            painelTocador.add(botaoMOSTRADORduracao);
            painelTocador.setPreferredSize(new Dimension(1000, 70));
            
            baixo.add(painelTocador);
            panelConteudo.add(baixo, BorderLayout.PAGE_END);
            
            // Geral
            setSize(largura, altura);  
            setLocation(posx,posy); 
            setDefaultCloseOperation(EXIT_ON_CLOSE);                     
            setVisible(true);  
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setResizable(false);
            setContentPane(panelConteudo);
        } catch(Exception e){
        	System.out.println(e.getMessage());
        }
        
	}
	
	public void abrirMIDI(){  
        JFileChooser selecao = new JFileChooser(".");  
        selecao.setFileSelectionMode(JFileChooser.FILES_ONLY);              
        selecao.setFileFilter(new FileFilter(){
            public boolean accept(File f){
                if (!f.isFile()) return true;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".mid"))  return true;
                if (name.endsWith(".midi")) return true;
                return false;
            }

            public String getDescription(){
            	return "Arquivo Midi (*.mid,*.midi)";
            }
        });
        selecao.showOpenDialog(this);  

        if(selecao.getSelectedFile() != null){
	        path = selecao.getSelectedFile().toString();
	        nomeDiretorio.setText("  Diretorio: " + path);
	        File arqseqnovo = selecao.getSelectedFile();
	        try { 
	        	if(sequenciador != null && sequenciador.isRunning()) {
	        		sequenciador.stop();
	        		sequenciador.close();
	        		sequenciador = null;
	        	}
				sequenciaNova = MidiSystem.getSequence(arqseqnovo);           
			    double duracao = sequenciaNova.getMicrosecondLength()/1000000.0d;
			    
			    nomeArq = arqseqnovo.getName();
			    nomeArquivo.setText(" Arquivo: " + nomeArq);
		
			    botaoMOSTRADORduracao.setText(formataInstante(duracao));                   
			    
			    botaoTOCAR.setEnabled(true);
			    botaoFAZERPAUSA.setEnabled(false);
			    botaoPARAR.setEnabled(false);
			    botaoINFO.setEnabled(true);
			    botaoEVENTOS.setEnabled(false);
			    botaoConverter.setEnabled(true);
			    
			    aberto = true; dur = 0;
	        } catch (Throwable e1) {
	        	System.out.println("Erro em carregaArquivoMidi: "+ e1.toString());
	        }
        }
	}
	
	public void tocar(String caminho, long inicio){  
	    try {  
	    	File arqMidi = new File(caminho);
	    	
	    	if(sequenciador == null){
	    		
	    		sequencia    = MidiSystem.getSequence(arqMidi);  
	    		sequenciador = MidiSystem.getSequencer();
	
	    		sequenciador.setSequence(sequencia); 
	    		sequenciador.open();
	    	}
	                retardo(200);
	        sequenciador.start();
	        receptor = sequenciador.getTransmitters().iterator().next().getReceiver();
	        sequenciador.getTransmitter().setReceiver(receptor);
             
            nomeArquivo.setText(" Arquivo: " + arqMidi.getName() + "\"");
                                               
            long duracao  = sequencia.getMicrosecondLength()/1000000;
            botaoMOSTRADORduracao.setText(formataInstante(duracao)); 
            botaoMOSTRADORinstante.setText(formataInstante(inicio/1000000));                
                                            
            sequenciador.setMicrosecondPosition(inicio);

            if (sequenciador.isRunning()){
            	soando = true;
            } else {
            	soando = false;
                //sequenciador.stop();
                //sequenciador.close();
                inicio = 0L;
                //duracao = 0;
             }  
            
             botaoABRIR.setEnabled(false);
             botaoTOCAR.setEnabled(false);
             botaoVOLTAR.setEnabled(true);
             botaoAVANCAR.setEnabled(true);
             botaoFAZERPAUSA.setEnabled(true);
             botaoPARAR.setEnabled(true);   
             botaoEVENTOS.setEnabled(true);
             botaoINICIO.setEnabled(true);
             sliderVolume.setEnabled(true);
             botaoConverter.setEnabled(true);
        }
       
        catch(Exception e){
        	System.out.println(e.toString());
        }
	}
	
	public void converter(String caminho){
		File arqMidi = new File(caminho);
    	new Conversor(arqMidi);
	}
        
	public void fazerpausa(){
		inicio = sequenciador.getMicrosecondPosition();
        soando = false;
        sequenciador.stop();  
        
        botaoABRIR.setEnabled(false);
        botaoTOCAR.setEnabled(true);
        botaoFAZERPAUSA.setEnabled(false);  
        botaoINICIO.setEnabled(false);
        botaoConverter.setEnabled(true);
        sliderVolume.setEnabled(false);
	}

	public void parar(){
		soando = false;
        sequenciador.stop();  
        sequenciador.close();
        sequenciador = null;
        inicio = 0L;
        
        botaoABRIR.setEnabled(true);
	    botaoTOCAR.setEnabled(true);
	    botaoFAZERPAUSA.setEnabled(false);
	    botaoConverter.setEnabled(true);
	    botaoPARAR.setEnabled(false);
	    botaoINICIO.setEnabled(false);
	    sliderVolume.setEnabled(false);
	    
	    sliderPROGRESSOinstante.setValue(0);             
	    botaoMOSTRADORinstante.setText(formataInstante(0));      
	}

	public void run(){ 
  
        sliderPROGRESSOinstante.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent e) {
        		if(aberto && dur != 0){
	                int mouseX = e.getX();
	                
	                pos = (int)Math.round(((double)mouseX /
	                		(double)sliderPROGRESSOinstante.getWidth()) * sliderPROGRESSOinstante.getMaximum());
	
	                long aux1 =  ((long) pos);
	                long aux2 = ((long)1000000.0)* ((long)dur);
	                long tNova = aux1 * aux2;
	                tNova = tNova/100L;
	                
	                sliderPROGRESSOinstante.setValue(pos);
	                if(soando) sequenciador.setMicrosecondPosition(tNova);
	                inicio = tNova;
        		}
        	}
        });
        
        while(true){
        	if(soando){
        		dur   = sequenciador.getMicrosecondLength()/1000000;
        		inicio = sequenciador.getMicrosecondPosition();
                t     = inicio/1000000;
                pos   = (int) ((t*100)/dur);
                
                try {
                	especInstante();
	        		       
                	sliderPROGRESSOinstante.setValue(pos);								
                    botaoMOSTRADORinstante.setText(formataInstante(t));     
                    retardo(1000);
                    if(t >= dur) {
                    	parar();
                        
                        if(dialogParam.isShowing() && areaTexto != null){
                        	areaTexto.append("* * * \n");
                        }
                    }
                } catch(Exception e) {
                	System.out.println(e.getMessage());  
                }  
        	} else { 
                try { 
                    retardo(1000); 
                } catch(Exception e) {
                	System.out.println(e.getMessage());
                }
            }   
        }
            
	}
	
	public void determInfo(){

		int resolucao = sequenciaNova.getResolution();
        long duracao = sequenciaNova.getMicrosecondLength() / 1000000;
        long totalTitques = sequenciaNova.getTickLength();
        float durTique = (float) duracao / totalTitques;
        float durSeminima = durTique * resolucao;
        float bpm = 60 / durSeminima;
        int totalSeminimas = (int) (duracao / durSeminima);

        JLabel  nome = new JLabel(nomeArq);
        nome.setFont(new Font("Times New Roman", java.awt.Font.BOLD, 25));
        nome.setForeground(colorNome);

        JLabel informacoes = new JLabel(
        		"<html><table border='0'>"
                + "<tr><td> Resolucao: " 
            		+ resolucao + "</td></tr><br>"
                + "<tr><td> Duracao: " 
            		+ duracao + "</td></tr><br>" 
            	+ "<tr><td> Andamento: " 
            		+ Math.round(bpm) + "</td></tr><br>"
                + "<tr><td> Numero de Tiques: " 
            		+ totalTitques + "</td></tr><br>"
                + "<tr><td> Duracao do Tique: " 
            		+ durTique + "</td></tr><br>"
                + "<tr><td> Duracao da Seminima: " 
            		+ durSeminima + "</td></tr><br>"
                + "<tr><td> Total de Seminimas: " 
            		+ totalSeminimas  + "</td></tr><br>"
            + "</table></html>"
        );
        informacoes.setForeground(colorNome);
        informacoes.setFont(new Font("Times New Roman", java.awt.Font.ITALIC, 15));

        JPanelFundo contentPanel = new JPanelFundo("img/background1.jpg");
        contentPanel.setOpaque(false);
        
        JPanel nomePanel = new JPanel();
        nomePanel.setPreferredSize(new Dimension(350, 40));
        nomePanel.setOpaque(false);
        nomePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JPanel infoPanel = new JPanel();
        infoPanel.setPreferredSize(new Dimension(350, 200));
        infoPanel.setOpaque(false);
        
        JPanel rodapePanel = new JPanel();
        rodapePanel.setPreferredSize(new Dimension(300, 40));
        rodapePanel.setOpaque(false);      

        nomePanel.add(nome);
        infoPanel.add(informacoes);
        
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        contentPanel.add(nomePanel);
        contentPanel.add(infoPanel);
        contentPanel.add(rodapePanel);
        contentPanel.setOpaque(true);
        
        dialog.setContentPane(contentPanel);
        dialog.setSize(new Dimension(400, 340));
        dialog.setIconImage(frameIcon.getImage());
        dialog.setResizable(false);
        dialog.setLocation(450, 0);
        dialog.setVisible(true);
        
    }
	
	public void determInst(){

        JPanelFundo contentPanel = new JPanelFundo("img/relacao.png");
        contentPanel.setOpaque(false);
        
        dialog2.setContentPane(contentPanel);
        dialog2.setSize(new Dimension(920, 505));
        dialog2.setIconImage(frameIcon.getImage());
        dialog2.setResizable(false);
        dialog2.setLocation(0, 0);
        dialog2.setVisible(true);
        
    }
	  
	public void determEventos(){
		
        JLabel  nome = new JLabel("    Eventos:");
        Track[] trilhas = sequencia.getTracks();
        
        DefaultTableModel modelo = new DefaultTableModel(0, 0);
        String header[] = new String[] { "Trilha N", "Evento N", "Mensagem", "Instante", "Parametros" };
        modelo.setColumnIdentifiers(header);
        JTable table = new JTable();
        table.setModel(modelo);
        
        
        for(int i = 0; i < trilhas.length; i++){	        
		    Track trilha =  trilhas[i];
		    	for(int j = 0; j < trilha.size(); j++){            
		    		MidiEvent   e          = trilha.get(j);
		    		MidiMessage mensagem   = e.getMessage();
		    		long        tique      = e.getTick();
		    		Boolean flag_metamensagem = false; 
		    		
		    		String parametros = "";
		    		byte[] data = mensagem.getMessage();
		    		
		    		int n = mensagem.getStatus();
		    		int tamanho_bites = mensagem.getLength();
		    		
		    		if(tamanho_bites <= 1){
		            	parametros = "NÃ£o existem dados";
		            }
		            else{
		            	StringBuffer buffer = new StringBuffer();
		        		for (int k = 1; k < tamanho_bites; k++) {
		        			buffer.append(' ');
		        			appendByte(buffer, data[k]);
		        		}
		        		parametros = buffer.toString();
		            }
	            
		    		String nomecomando = "" + n;
		    		if (n < 144){
		    			nomecomando = "noteOFF";
		    		}
		    		else if (n < 160){
		    			nomecomando = "noteON";
		    		}
		    		else if(n < 176){
		    			nomecomando = "Polyphonic Pressure";
		    		}
		    		else if (n < 192){
		    			nomecomando = "Controller change";
		    		}
		    		else if (n < 208){
		    			nomecomando = "Program change";
		    		}
		    		else if (n < 224){
		    			nomecomando = "Channel Pressure";
		    		}
		    		else if (n < 255){
		    			nomecomando = "Pitch bend";
		    		}
		    		else{
		    			nomecomando = "MetaMensagem";
		    			flag_metamensagem = true;
		    		}
		    		
		    		if(flag_metamensagem){
		            	parametros = decodifica_metamensagens(data, tamanho_bites);
		            }
		    		
		    		modelo.addRow(new Object[] { i , j , nomecomando, tique, parametros });
		    	}
        	}
    
        
        nome.setFont(new Font("Times New Roman", java.awt.Font.PLAIN, 25));
        nome.setForeground(colorNome);
        
        JScrollPane rolagem = new JScrollPane(table);
		rolagem.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		rolagem.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		rolagem.setPreferredSize(new Dimension(600, 400));

        JPanelFundo contentPanel = new JPanelFundo("img/background0.jpg");
        
        JPanel nomePanel = new JPanel();
        nomePanel.setPreferredSize(new Dimension(350, 40));
        nomePanel.setOpaque(false);
        nomePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nomePanel.add(nome);

        JPanel infoPanel = new JPanel();
        infoPanel.add(rolagem);
        infoPanel.setOpaque(false);
        
        JPanel rodapePanel = new JPanel();
        rodapePanel.setPreferredSize(new Dimension(300, 20));
        rodapePanel.setOpaque(false);   
        rodapePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        contentPanel.add(nomePanel);
        contentPanel.add(rodapePanel);
        contentPanel.add(infoPanel);
        
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        dialogEventos.setContentPane(contentPanel);
        dialogEventos.setSize(new Dimension(720, 520));
        dialogEventos.setIconImage(frameIcon.getImage());
        dialogEventos.setResizable(false);
        dialogEventos.setLocation(450, 0);
        dialogEventos.setVisible(true);
        
    }
	
	void retardo(int miliseg){  
        try {
        	Thread.sleep(miliseg);
        }
        catch(InterruptedException e) { }
	}
	
	public void especInstante(){
		if(dialogParam.isShowing() && areaTexto != null){
    		long  posicao = sequenciador.getMicrosecondPosition();
    		int   seg     = Math.round(posicao*0.000001f);

    		areaTexto.append(seg + " ");
    		contInst++;
    		if(contInst == 20) contInst = 0;
    	}
	}

	public String formataInstante(double t1){
        double h1  = (int)(t1/3600.0);
        double m1  = (int)((t1 - 3600*h1)/60);
        double s1  = (t1 - (3600*h1 + 60*m1));

        //double h1r  = t1/3600.0;
        //double m1r  = (t1 - 3600*h1)/60.0f;
        double s1r  = (t1 - (3600*h1 + 60*m1));

        String sh1 = "";
        String sm1 = "";
        String ss1 = "";

        if     (h1 == 0) sh1 = "00";
        else if(h1 < 10) sh1 = "0" + reformata(h1, 0);
        else if(h1 < 100) sh1 = "" + reformata(h1, 0);
        else            sh1 = "" + reformata(h1, 0);

        if     (m1 == 0) sm1 = "00";
        else if(m1 < 10) sm1= "0" + reformata(m1, 0);
        else if(m1 < 60) sm1 = "" + reformata(m1, 0);

        if     (s1 == 0) ss1 = "00";
        else if(s1 < 10) ss1 = "0" + reformata(s1r, 2);
        else if(s1 < 60) ss1 = reformata(s1r, 2);

        return " " + sh1 + "h " + sm1 + "m " + ss1 + "s";
	}
       
	public String reformata(double x, int casas){
		DecimalFormat df = new DecimalFormat() ;
        df.setGroupingUsed(false);
        df.setMaximumFractionDigits(casas);
        return df.format(x);
	}
  
	public JButton constroiBotao(String legenda){
        JButton botao = new JButton(legenda);
        botao.setMargin(new Insets(2, 2, 2, 2));
        botao.setFocusable(false);
        botao.setFont(botao.getFont().deriveFont(Font.PLAIN));
        return botao;
	}
        
	public JButton constroiBotao(String legenda, float tamanhoFonte){
        JButton botao = new JButton(legenda);
        botao.setMargin(new Insets(2, 2, 2, 2));
        botao.setFocusable(false);
        botao.setFont(botao.getFont().deriveFont(Font.PLAIN));
        botao.setFont(botao.getFont().deriveFont(tamanhoFonte));
        
        return botao;
	}
	
	private void appendByte(StringBuffer buffer, byte b) { 
		String hex = Integer.toHexString(b & 0xFF).toUpperCase(); 
			for (int i = hex.length(); i < 2; i++) { 
				buffer.append('0'); 
			} 
			buffer.append(hex); 
	}
	
	private String decodifica_metamensagens(byte[] bytes, int tamanho_bytes){
		String comentarios = "";
		int flag_erro = 0;
		
		switch(bytes[1]){
			case 0:
				if(bytes[2] == 2){
					comentarios = "Especifica o numero de uma sequencia.\nNro: " + bytes[3] + " " + bytes[4] + "\n";
				}
				else{
					flag_erro = 1;
				}
				break;
			case 1:
				comentarios = "Comentario:\n ";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}	
				break;
			case 2:
				comentarios = "Copyright:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}	
				break;
			case 3:
				comentarios = "Nome da sequencia ou da faixa:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}
				break;
			case 4:
				comentarios = "Nome do instrumento:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}
				break;
			case 5:
				comentarios = "Letra de Musica:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}
				break;
			case 6:
				comentarios = "Marcador:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}
				break;
			case 7:
				comentarios = "Cue point:\n";
				for(int i = 0; i < bytes[2]; i++){
					comentarios = comentarios + (char)bytes[i+3];
				}
				break;
			case 32:
				if(bytes[2] == 1){
					comentarios = "Canal:\n" + bytes[3];
				}
				else{
					flag_erro = 1;
				}
				break;
			case 33:
				if (bytes[2] == 1){
					comentarios = "Porta:\n" + bytes[3];
				}
				else{
					flag_erro = 1;
				}
				break;
			case 47:
				if (bytes[2] == 0){
					comentarios = "Fim da Faixa.";
				}
				else{
					flag_erro = 1;
				}
				break;
			case 81:
				if(bytes[2] ==3){
					comentarios = "Mudanca de Tempo para " + bytes [3] + " " + bytes[4] + " " + bytes[5];
				}
				else{
					flag_erro = 1;
				}
				break;
			case 84:
				if (bytes[2] == 5){
					comentarios = "Tempo (SMPTE) = " + bytes[3] + " horas " + bytes[4] + " minutos " + bytes[5]
									+ " segundos " + bytes[6] + " frames " + bytes[7] + "subframes.";
				}
				else{
					flag_erro = 1;
				}
				break;
			case 88:
				if(bytes[2] == 3){
					comentarios = "Time signature = " + bytes[3] + " numerador " + bytes[4] +" denominador " + bytes[5] + " clocks " + bytes[6] + " MIDI quater note (24 MIDI clocks)";
				}
				else{
					flag_erro = 1;
				}
				break;
			case 89:
				if(bytes[2] == 2){
					comentarios = "Assinatura de Clave = " + bytes[3] + " (sf) ";
					if(bytes[4] == 0){
						comentarios += "maior";
					}
					else{
						comentarios += "menor";
					}	
				}
				else{
					flag_erro = 1;
				}
				break;
			case 127:
				comentarios = "Dados de propriedade";
				break;
			default:
				flag_erro = 1;
				break;
		}
		
		if (flag_erro == 1){
			comentarios = "Nao foi possivel decodificar a MetaMensagem";
		}
		
		return comentarios;
	}
	
	static Par getFormulaDeCompasso(Track trilha){
		int p = 1;
	    int q = 1;

	    for(int i = 0; i < trilha.size(); i++){
	    	MidiMessage m = trilha.get(i).getMessage();
	        if(m instanceof MetaMessage){
	        	if(((MetaMessage)m).getType() == FORMULA_DE_COMPASSO){
	            	MetaMessage mm = (MetaMessage)m;
	            	byte[] data = mm.getData();
	            	p = data[0];
	            	q = data[1];
	            	return new Par(p,q);
	        	}
	        }
	    }
	    
	    return new Par(p,q);
	}
	
	static String getTonalidade(Track trilha) throws InvalidMidiDataException {       
		String stonalidade = "";
	    for(int i = 0; i < trilha.size(); i++){
	    	MidiMessage m = trilha.get(i).getMessage();
	     
	    	if(((MetaMessage)m).getType() == MENSAGEM_TONALIDADE){
	    		MetaMessage mm        = (MetaMessage)m;
				byte[]     data       = mm.getData();
				byte       tonalidade = data[0];
				byte       maior      = data[1];
				
				String       smaior = "Maior";
				if(maior == 1) smaior = "Menor";

				if(smaior.equalsIgnoreCase("Maior")){
	            	switch (tonalidade){
	                	case -7: stonalidade = "Dob Maior"; break;
	                	case -6: stonalidade = "Solb Maior"; break;
	                	case -5: stonalidade = "Reb Maior"; break;
	                	case -4: stonalidade = "Lab Maior"; break;
	                	case -3: stonalidade = "Mib Maior"; break;
	                	case -2: stonalidade = "Sib Maior"; break;
	                	case -1: stonalidade = "Fa Maior"; break;
	                	case  0: stonalidade = "Do Maior"; break;
	                	case  1: stonalidade = "Sol Maior"; break;
	                	case  2: stonalidade = "Re Maior"; break;
	                	case  3: stonalidade = "La Maior"; break;
	                	case  4: stonalidade = "Mi Maior"; break;
	                	case  5: stonalidade = "Si Maior"; break;
	                	case  6: stonalidade = "Fa# Maior"; break;
	                	case  7: stonalidade = "Do# Maior"; break;
	            	}
				} else if(smaior.equalsIgnoreCase("Menor")){
					switch (tonalidade){
	                  	case -7: stonalidade = "Lab Menor"; break;
	                  	case -6: stonalidade = "Mib Menor"; break;
	                  	case -5: stonalidade = "Sib Menor"; break;
	                  	case -4: stonalidade = "Fa Menor"; break;
	                  	case -3: stonalidade = "Do Menor"; break;
	                  	case -2: stonalidade = "Sol Menor"; break;
	                  	case -1: stonalidade = "Re Menor"; break;
	                  	case  0: stonalidade = "La Menor"; break;
	                  	case  1: stonalidade = "Mi Menor"; break;
	                  	case  2: stonalidade = "Si Menor"; break;
	                  	case  3: stonalidade = "Fa# Menor"; break;
	                  	case  4: stonalidade = "Do# Menor"; break;
	                  	case  5: stonalidade = "Sol# Menor"; break;
	                  	case  6: stonalidade = "Re# Menor"; break;
	                  	case  7: stonalidade = "La# Menor"; break;
					}
				}
	    	}
	    }
	    
	    return stonalidade;
	}
	        
	static String getTexto(Track trilha) throws InvalidMidiDataException {       
		String stexto = "";

	    for(int i = 0; i < trilha.size(); i++){
	    	MidiMessage m = trilha.get(i).getMessage();
	            
	    	if(((MetaMessage)m).getType() == MENSAGEM_TEXTO){                
	    		MetaMessage mm  = (MetaMessage)m;
	    		byte[]     data = mm.getData();

	    		for(int j = 0; j < data.length; j++){
	    			stexto += (char)data[j];
	    		}         
	    	}       
	    }
	    
	    return stexto;
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

}
