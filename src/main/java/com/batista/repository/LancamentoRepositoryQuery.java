package com.batista.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.batista.model.Lancamento;
import com.batista.projection.LancamentoResumo;

public interface LancamentoRepositoryQuery {
	
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter); // filtro por descricao, data de pagamento e vencimento
	public Page<Lancamento> filtroComPaginacao(LancamentoFilter lancamentoFilter, Pageable pageable); // sm ao anterior, mas tb fornece paginaçao
	public Page<LancamentoResumo> resumir(LancamentoFilter lancamentoFilter, Pageable pageable); // sm aos anteriores, mas retorna apenas um resumo dos dados de lancaçmento
}
