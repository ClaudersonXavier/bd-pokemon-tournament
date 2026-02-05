package com.ufape.projetobanquinhobd.facade;

import com.ufape.projetobanquinhobd.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Fachada {
    @Autowired
    private AtaqueService ataqueService;
    @Autowired
    private BatalhaService batalhaService;
    @Autowired
    private EspecieService especieService;
    @Autowired
    private PokemonService pokemonService;
    @Autowired
    private TimeService timeService;
    @Autowired
    private TipoService tipoService;
    @Autowired
    private TorneioService torneioService;
    @Autowired
    private TreinadorService treinadorService;

    public AtaqueService getAtaqueService() { return ataqueService; }
    public BatalhaService getBatalhaService() { return batalhaService; }
    public EspecieService getEspecieService() { return especieService; }
    public PokemonService getPokemonService() { return pokemonService; }
    public TimeService getTimeService() { return timeService; }
    public TipoService getTipoService() { return tipoService; }
    public TorneioService getTorneioService() { return torneioService; }
    public TreinadorService getTreinadorService() { return treinadorService; }
}
