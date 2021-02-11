package com.example.testinlogin;

public class User {

    public String email, escola, nome, tipo_utilizador;

    public User(){

    }


    public User(String email, String escola, String nome, String tipo_utilizador) {
        this.email = email;
        this.escola = escola;
        this.nome = nome;
        this.tipo_utilizador = tipo_utilizador;
    }
}
