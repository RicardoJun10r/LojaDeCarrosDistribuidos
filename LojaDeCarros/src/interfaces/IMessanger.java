package interfaces;

public abstract class IMessanger {
    
    private int PORTA;

    public IMessanger(int porta){ this.PORTA = porta; }

    abstract void start();

}
