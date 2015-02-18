/**
 * Juego
 *
 * 
 *
 * @author Gonzalo Gutierrez Sanchez  -  A01175768
 * @version ??
 * @date 11/febrero/2015
 * asdasdasdas
 */
 
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author AntonioM
 */
public class Juego extends Applet implements Runnable, KeyListener {

    private final int iMAXANCHO = 10; // maximo numero de personajes por ancho
    private final int iMAXALTO = 8;  // maxuimo numero de personajes por alto
    private Base basPrincipal;         // Objeto principal
    private Base basMalo;         // Objeto malo
    private boolean bAcabar;
    private int iSpeed;
    
    /* objetos para manejar el buffer del Applet y este no parpadee */
    private Image    imaImagenApplet;   // Imagen a proyectar en Applet	
    private Graphics graGraficaApplet;  // Objeto grafico de la Imagen
    private AudioClip adcSonidoChimpy;   // Objeto sonido de Chimpy
    private AudioClip adcSonidoChimpy2;
    private boolean bPausa;
    
    private int iDireccion;
    private LinkedList <Base> lklFantasma;
    private LinkedList <Base> lklJuanito;
    private int iPuntos;
    private int iJuaniVidas;
    private int iVidas;
   
    
	
    /** 
     * init
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se inizializan las variables o se crean los objetos
     * a usarse en el <code>Applet</code> y se definen funcionalidades.
     * 
     */
    public void init() {
        // hago el applet de un tamaño 500,500
        setSize(800,500);
        iPuntos = 0;
        iDireccion = 0;
        iJuaniVidas = 0;
        bPausa = false;
        iVidas = (int) (Math.random() * 3) + 3;
        iSpeed = 2;
        
        addKeyListener(this);
             
	URL urlImagenPrincipal = this.getClass().getResource("juanito.gif");
        
                
        // se crea el objeto para principal 
	basPrincipal = new Base(0, 0, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenPrincipal));

        // se posiciona a principal  en la esquina superior izquierda del Applet 
        basPrincipal.setX(getWidth() / 2 );
        basPrincipal.setY(getHeight() / 2);
        
        // defino la imagen del malo
	URL urlImagenMalo = this.getClass().getResource("chimpy.gif");
        
        // se crea el objeto para malo 
        int iPosX =  getWidth() / 2;
        int iPosY = getHeight() / 2;        
	basMalo = new Base(iPosX,iPosY, getWidth() / iMAXANCHO,
                getHeight() / iMAXALTO,
                Toolkit.getDefaultToolkit().getImage(urlImagenMalo));
        
        URL urlSonidoChimpy = this.getClass().getResource("monkey2.wav");
        URL urlSonidoChimpy2 = this.getClass().getResource("monkey1.wav");
        adcSonidoChimpy2 = getAudioClip (urlSonidoChimpy2);
        adcSonidoChimpy = getAudioClip (urlSonidoChimpy);
        adcSonidoChimpy.play();
        
        URL urlImagenFantasmita = this.getClass().getResource("fantasmita.gif");
        lklFantasma = new LinkedList();
        int iAzar = (int) (Math.random() * 10) + 8;
        
        // genero cada animal y lo añado a la lista
        for (int iI = 0; iI <= iAzar; iI ++) {
            iPosX = - (int) (Math.random() * ( 3 * getWidth() ));   
            iPosY = (int) (Math.random() * ( 3 * getHeight() /4)); 
            Base basFantasmita;  
            // se crea el objeto elefante 2
            basFantasmita = new Base(iPosX,iPosY,100,100,
                Toolkit.getDefaultToolkit().getImage(urlImagenFantasmita));
            
            int iAzar2 = (int) (Math.random()* (getHeight() - basFantasmita.getAlto()));
            basFantasmita.setY(iAzar2);
            
            lklFantasma.add(basFantasmita);
        }
        
        URL urlImagenJuanito = this.getClass().getResource("juanito.gif");
        lklJuanito = new LinkedList();
       
        
        int iRandom = (int) (Math.random() * 6) + 10;
        
        
        for (int iI = 0; iI < iRandom; iI ++) {
            iPosX =  (int) (Math.random() * ( 3 * getWidth()/4 ));   
            iPosY = -(int) (Math.random() * ( 3 * getHeight() ));   
            // se crea el objeto elefante 2
            Base basJuanito = new Base(iPosX,iPosY,100,100,
                    Toolkit.getDefaultToolkit().getImage(urlImagenJuanito));
            lklJuanito.add(basJuanito);
        }
        
        // genero cada animal y lo añado a la lista
        
        
        
        
        
        
    }
	
    /** 
     * start
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>.<P>
     * En este metodo se crea e inicializa el hilo
     * para la animacion este metodo es llamado despues del init o 
     * cuando el usuario visita otra pagina y luego regresa a la pagina
     * en donde esta este <code>Applet</code>
     * 
     */
    public void start () {
        // Declaras un hilo
        Thread th = new Thread (this);
        // Empieza el hilo
        th.start ();
    }
	
    /** 
     * run
     * 
     * Metodo sobrescrito de la clase <code>Thread</code>.<P>
     * En este metodo se ejecuta el hilo, que contendrá las instrucciones
     * de nuestro juego.
     * 
     */
    public void run () {
        /* mientras dure el juego, se actualizan posiciones de jugadores
           se checa si hubo colisiones para desaparecer jugadores o corregir
           movimientos y se vuelve a pintar todo
        */ 
        while (true) {
            actualiza();
            checaColision();
            repaint();
            try	{
                // El thread se duerme.
                Thread.sleep (20);
                
                while (bPausa) {
                   Thread.sleep (20);
                }
            }
            catch (InterruptedException iexError) {
                System.out.println("Hubo un error en el juego " + 
                        iexError.toString());
            }
	}
    }
	
    /** 
     * actualiza
     * 
     * Metodo que actualiza la posicion de los objetos 
     * 
     */
    public void actualiza(){
         switch(iDireccion) {
            case 1: { //se mueve hacia arriba
                basMalo.setY(basMalo.getY() - 2); 
                
                                            
                break;    
            }
            case 2: { //se mueve hacia abajo
                basMalo.setY(basMalo.getY() + 2);
                
                break;    
            }
            case 3: { //se mueve hacia izquierda
                basMalo.setX(basMalo.getX() - 2);
                
                break;    
            }
            case 4: { //se mueve hacia derecha
                basMalo.setX(basMalo.getX() + 2);
               
                break;    	
            }
        }
         
         for (Base basFantasmita : lklFantasma) {
            int iMover = (int) (Math.random() * 3) + 2;
            basFantasmita.setX(basFantasmita.getX() + iMover);
        }
         for (Base basJuanito : lklJuanito) {
           
            basJuanito.setY(basJuanito.getY() + iSpeed);
        }

    }
	
    /**
     * checaColision
     * 
     * Metodo usado para checar la colision entre objetos
     * 
     */
    public void checaColision(){
        for (Base basFantasmita : lklFantasma) {
            //checo la colision entre ambos elefantes
            if (basMalo.intersecta(basFantasmita)) {
                int iPosY = (int) (Math.random() * ( 3 * getHeight() / 4));
                int iPosX = - (int) (Math.random() * ( 3 * getWidth() ));
                basFantasmita.setX(iPosX);
                basFantasmita.setY(iPosY);
                iPuntos++;
                if (iVidas>0){
                   adcSonidoChimpy.play(); 
                }
                
            }
            
            if(basFantasmita.getX() + basFantasmita.getAncho() > getWidth()) {
                int iPosY = (int) (Math.random() * (  3 * getHeight() /4)); 
                int iPosX = - (int) (Math.random() * ( 3 * getWidth() ));
                basFantasmita.setX(iPosX);
                basFantasmita.setY(iPosY);
                
                
            }
        }
        
         for (Base basJuanito : lklJuanito) {
            //checo la colision entre ambos elefantes
            if (basMalo.intersecta(basJuanito)) {
                int iPosY = -(int) (Math.random() * ( 3 * getHeight() / 4));
                int iPosX = (int) (Math.random() * ( 3 * getWidth() ));
                basJuanito.setX(iPosX);
                basJuanito.setY(iPosY);
                iJuaniVidas++;
                if(iJuaniVidas >= 5) {
                    iVidas--;
                    iSpeed++;
                    iJuaniVidas = 0;
                }
                
                if (iVidas>0){
                   adcSonidoChimpy2.play(); 
                }
            }
            
            if(basJuanito.getY() + basJuanito.getAlto() > getHeight()) {
                int iPosY = -(int) (Math.random() * (  3 * getHeight() /4)); 
                int iPosX =  (int) (Math.random() * ( 3 * getWidth() ));
                basJuanito.setX(iPosX);
                basJuanito.setY(iPosY);
                
                
            }
        }
        
        
        
        
        
        
        
        
        
        if(basMalo.getY() < 0) { // y esta pasando el limite
                    
                    basMalo.setY(0);
                   
                }
                  	
                 
            
                // y se esta saliendo del applet
                if (basMalo.getY() + basMalo.getAlto() > getHeight()) {
                    
                    basMalo.setY(getHeight() - basMalo.getAlto());
                    
                }
                 	
             
            
                if (basMalo.getX() < 0) { // y se sale del applet
                   
                    basMalo.setX(0);
                    
                }
                 	
              
            
                // si se esta saliendo del applet
                if (basMalo.getX() + basMalo.getAncho() > getWidth()) { 
                    
                    basMalo.setX(getWidth() - basMalo.getAncho());
                    
                }

    }
	
    /**
     * update
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo lo que hace es actualizar el contenedor y 
     * define cuando usar ahora el paint
     * 
     * @param graGrafico es el <code>objeto grafico</code> usado para dibujar.
     * 
     */
    public void update (Graphics graGrafico){
        // Inicializan el DoubleBuffer
        if (imaImagenApplet == null){
                imaImagenApplet = createImage (this.getSize().width, 
                        this.getSize().height);
                graGraficaApplet = imaImagenApplet.getGraphics ();
        }

        // Actualiza la imagen de fondo.
        URL urlImagenFondo = this.getClass().getResource("Ciudad.png");
        Image imaImagenFondo = Toolkit.getDefaultToolkit().getImage(urlImagenFondo);
         graGraficaApplet.drawImage(imaImagenFondo, 0, 0, getWidth(), getHeight(), this);

        // Actualiza el Foreground.
        graGraficaApplet.setColor (getForeground());
        paint(graGraficaApplet);

        // Dibuja la imagen actualizada
        graGrafico.drawImage (imaImagenApplet, 0, 0, this);
    }
    
    /**
     * paint
     * 
     * Metodo sobrescrito de la clase <code>Applet</code>,
     * heredado de la clase Container.<P>
     * En este metodo se dibuja la imagen con la posicion actualizada,
     * ademas que cuando la imagen es cargada te despliega una advertencia.
     * 
     * @param graDibujo es el objeto de <code>Graphics</code> usado para dibujar.
     * 
     */
    public void paint(Graphics graDibujo) {
        if (bAcabar || iVidas <=0) {
            graDibujo.setColor(Color.red);
            
                graDibujo.drawString("GAME OVER!! ", 340, 200);
        }
        
        
       else if (basMalo != null && lklFantasma != null && lklJuanito != null) {
                graDibujo.setColor(Color.red);
                graDibujo.drawString("Puntos: "+ iPuntos, 20, 20);
                graDibujo.drawString("Vidas: "+ iVidas, 400, 20);
               
                
                
                //Dibuja la imagen de susanita en el Applet
                basMalo.paint(graDibujo, this);
                //Dibuja la imagen de fantasmita en el Applet
                for (Base basJuanito : lklJuanito) {
                    
                    
                    basJuanito.paint(graDibujo, this);
                }
                for (Base basFantasma : lklFantasma) {
                    
                    
                    basFantasma.paint(graDibujo, this);
                }
                
        }
        
        
        
         // sino se ha cargado se dibuja un mensaje 
        else {
                //Da un mensaje mientras se carga el dibujo	
                graDibujo.drawString("No se cargo la imagen..", 20, 20);
        }
        
        
        
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        if(keyEvent.getKeyCode() == KeyEvent.VK_W) {
           iDireccion = 1;
       }
       if(keyEvent.getKeyCode() == KeyEvent.VK_S) {
           iDireccion = 2;
       }
       if(keyEvent.getKeyCode() == KeyEvent.VK_A) {
           iDireccion = 3;
       }
       if(keyEvent.getKeyCode() == KeyEvent.VK_D) {
           iDireccion = 4;
       }
       if(keyEvent.getKeyCode() == KeyEvent.VK_P) {
           if (bPausa) {
               bPausa = false;
           }
           else{
               bPausa =  true;
           }
       }
       if(keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
           bAcabar = true;
       }
    }
}