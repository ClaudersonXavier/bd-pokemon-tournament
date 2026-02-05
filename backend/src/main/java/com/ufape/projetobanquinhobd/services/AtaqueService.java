package com.ufape.projetobanquinhobd.services;

import com.ufape.projetobanquinhobd.entities.Ataque;
import com.ufape.projetobanquinhobd.repositories.AtaqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AtaqueService {
	@Autowired
	private AtaqueRepository ataqueRepository;

	public Ataque salvar(Ataque ataque) {
		return ataqueRepository.save(ataque);
	}

	public List<Ataque> listarTodos() {
		return ataqueRepository.findAll();
	}

	public Optional<Ataque> buscarPorNome(String nome) {
		return ataqueRepository.findById(nome);
	}

	public void deletarPorNome(String nome) {
		ataqueRepository.deleteById(nome);
	}
}
