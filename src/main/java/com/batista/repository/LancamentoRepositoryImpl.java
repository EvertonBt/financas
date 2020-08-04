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

import com.batista.model.Categoria_;
import com.batista.model.Lancamento;
import com.batista.model.Lancamento_;
import com.batista.model.Pessoa_;
import com.batista.projection.LancamentoResumo;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {
	
	@PersistenceContext
	private EntityManager manager;
	

	// 1) Método q filtra c/ base na data de vencimento e pela descriçao
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

// 2)  Método q aceita os mesmos filtros do anterior, mas q tb faz a paginação aceitando tb os filtros size e page
	// no exemplo ele fez somente um, mas mantive o anterior p/ estudo
@Override
public Page<Lancamento> filtroComPaginacao(LancamentoFilter lancamentoFilter, Pageable pageable) {
	CriteriaBuilder builder = manager.getCriteriaBuilder();
	CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
	Root<Lancamento> root = criteria.from(Lancamento.class);
	
	Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
	criteria.where(predicates);
	
	TypedQuery<Lancamento> query = manager.createQuery(criteria);
	adicionarRestricoesDePaginacao(query, pageable);
	// o getResultList = contém a listta de lançamentos propriamente dita; pagable = contém as info das páginas; total = método usado abaixo
	return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
}

// obtém os dados da página através do pageable p/ definir a quantidade de registros por página e quem será o primeiro registro de cada página
private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
	int paginaAtual = pageable.getPageNumber();
	int totalRegistrosPorPagina = pageable.getPageSize();
	int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;
	
	query.setFirstResult(primeiroRegistroDaPagina);
	query.setMaxResults(totalRegistrosPorPagina);
}

// obtém o numero total de registros de lancamentos retornados após a aplicação do filtro passaado
private Long total(LancamentoFilter lancamentoFilter) {
	CriteriaBuilder builder = manager.getCriteriaBuilder();
	CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
	Root<Lancamento> root = criteria.from(Lancamento.class);
	
	Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
	criteria.where(predicates);
	
	criteria.select(builder.count(root));
	return manager.createQuery(criteria).getSingleResult();
}


// 3) sm aos anteiores, porém retorna os dados do lanaçamemto de forma resumida
public Page<LancamentoResumo> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
	// cria a query
	CriteriaBuilder builder = manager.getCriteriaBuilder();
	CriteriaQuery<LancamentoResumo> criteria = builder.createQuery(LancamentoResumo.class);
	// extrai da classe raiz apenas os dados que serão usados no resumo
	Root<Lancamento> root = criteria.from(Lancamento.class);
	criteria.select(builder.construct(LancamentoResumo.class, 
			root.get(Lancamento_.codigo),
			root.get(Lancamento_.descricao),
			root.get(Lancamento_.dataVencimento), 
			root.get(Lancamento_.dataPagamento),
			root.get(Lancamento_.valor),
			root.get(Lancamento_.tipo),
			root.get(Lancamento_.categoria).get(Categoria_.nome),
			root.get(Lancamento_.pessoa).get(Pessoa_.nome)
			));
	
	Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
	criteria.where(predicates);
	
	TypedQuery<LancamentoResumo> query = manager.createQuery(criteria);
	adicionarRestricoesDePaginacao(query, pageable);
	return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
}


}
