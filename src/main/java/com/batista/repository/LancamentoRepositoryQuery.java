package com.batista.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.batista.model.Lancamento;

public interface LancamentoRepositoryQuery {

	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter);
	public Page<Lancamento> filtroComPaginacao(LancamentoFilter lancamentoFilter, Pageable pageable);
}
