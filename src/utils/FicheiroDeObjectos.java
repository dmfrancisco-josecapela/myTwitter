package utils;

import java.io.*;


/**
 * Classe para leitura e escrita de ficheiros de Objectos.
 * @author António José Mendes (professor de Programação Orientada aos Objectos)
 */
public class FicheiroDeObjectos {
    private ObjectInputStream iS;
    private ObjectOutputStream oS;

    //Método para abrir um ficheiro para leitura
    public void abreLeitura(String nomeDoFicheiro) throws IOException {
        iS = new ObjectInputStream(new FileInputStream(nomeDoFicheiro));
    }

    //Método para abrir um ficheiro para escrita. Recebe o nome do ficheiro
    public void abreEscrita(String nomeDoFicheiro) throws IOException {
        oS = new ObjectOutputStream(new FileOutputStream(nomeDoFicheiro));
    }

    //Método para ler um objecto do ficheiro. Devolve o objecto lido
    public Object leObjecto() throws IOException, ClassNotFoundException {
        return iS.readObject();
    }

    //Método para escrever um objecto no ficheiro. Recebe o objecto a escrever
    public void escreveObjecto(Object o) throws IOException {
        oS.writeObject(o);
    }

    //Método para fechar um ficheiro aberto em modo leitura
    public void fechaLeitura() throws IOException{
        iS.close();
    }

    //Método para fechar um ficheiro aberto em modo escrita
    public void fechaEscrita() throws IOException{
        oS.close();
    }
}
