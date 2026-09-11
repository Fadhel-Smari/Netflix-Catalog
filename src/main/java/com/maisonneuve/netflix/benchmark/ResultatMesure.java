package com.maisonneuve.netflix.benchmark;

public class ResultatMesure {

    private final String nomAlgo;
    private final int taille;
    private final long tempsNanoSec;

    public ResultatMesure(String nomAlgo, int taille, long tempsNanoSec) {
        this.nomAlgo = nomAlgo;
        this.taille = taille;
        this.tempsNanoSec = tempsNanoSec;
    }

    public String getNomAlgo() {
        return nomAlgo;
    }

    public int getTaille() {
        return taille;
    }

    public long getTempsNanoSec() {
        return tempsNanoSec;
    }
}