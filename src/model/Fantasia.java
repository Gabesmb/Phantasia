package model;

public class Fantasia {
    private String nome;
    private String imageSource;
    private boolean disponivel;

    public Fantasia(String nome, String imageSource, boolean disponivel) {
        this.nome = nome;
        this.imageSource = imageSource;
        this.disponivel = disponivel;
    }

    public String getNome() {
        return nome;
    }

    public String getImageSource() {
        return imageSource;
    }

    public boolean isDisponivel() {
        return disponivel;
    }
    
}
