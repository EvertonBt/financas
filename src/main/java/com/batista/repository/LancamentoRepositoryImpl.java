package com.batista.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import com.batista.model.Lancamento;
import com.batista.model.Lancamento_;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {
	
	@PersistenceContext
	private EntityManager manager;
	

	// Método q filtra c/ base na data de vencimento e pela descriçao
	@Override
	public List<Lancamento> filtrar(LancamentoFilter lancamentoFilter) {
		// estrutrua básica p/ criar consultas via JPA
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);
		// define as restrições, vc pode passar um ou mais de um filtro
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);
		// retorna a lista de Lancamentos já filtrados
		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		return query.getResultList();
	}
	//Usada pelo método anterior, cria os critérios p/ os filtros por descricação, por dataVencimentoDe e dataVencimentoAte
	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {
		List<Predicate> predicates = new ArrayList<>();
		
		// filtro por descrição
		if (!StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicates.add(builder.like(
					builder.lower(root.get(Lancamento_.descricao)), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}
		// filtro por data de vencimento de (usando este junto c/ o debaixo permite criar intervalos)
		if (lancamentoFilter.getDataVencimentoDe() != null) {
			predicates.add(
					builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoDe()));
		}
		// filtro por data de vencimento até
		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicates.add(
					builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento), lancamentoFilter.getDataVencimentoAte()));
		}
		
		return predicates.toArray(new Predicate[predicates.size()]);
	}

// Método q aceita os mesmos filtros do anterior, mas q tb faz a paginação aceitando tb os filtros size e page
@Override
public Page<Lancamento> filtroComPaginacao(LancamentoFilter lancamentoFilter, Pageable pageable) {
	CriteriaBuilder builder = manager.getCriteriaBuilder();
	CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
	Root<Lancamento> root = criteria.from(Lancamento.class);
	
	Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
	criteria.where(predicates);
	
	TypedQuery<Lancamento> query = manager.createQuery(criteria);
	adicionarRestricoesDePaginacao(query, pageable);
	
	return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
}

private void adicionarRestricoesDePaginacao(TypedQuery<Lancamento> query, Pageable pageable) {
	int paginaAtual = pageable.getPageNumber();
	int totalRegistrosPorPagina = pageable.getPageSize();
	int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
	
	query.setFirstResult(primeiroRegistroDaPagina);
	query.setMaxResults(totalRegistrosPorPagina);
}

private Long total(LancamentoFilter lancamentoFilter) {
	CriteriaBuilder builder = manager.getCriteriaBuilder();
	CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
	Root<Lancamento> root = criteria.from(Lancamento.class);
	
	Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
	criteria.where(predicates);
	
	criteria.select(builder.count(root));
	return manager.createQuery(criteria).getSingleResult();
}
}
