package com.dlima.myfinancas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dlima.myfinancas.exception.RegraNegocioException;
import com.dlima.myfinancas.model.entity.Lancamento;
import com.dlima.myfinancas.model.enums.StatusLancamento;
import com.dlima.myfinancas.model.enums.TipoLancamento;
import com.dlima.myfinancas.model.repository.LancamentoRepository;
import com.dlima.myfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCadastro(LocalDate.now());
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId()); // deve passar um lancamento existente com id
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		Example example = Example.of(lancamentoFiltro, 
				ExampleMatcher.matching()
					.withIgnoreCase() // IgnoraCase - ignorar maiusculas e minusculas
					.withStringMatcher(StringMatcher.STARTING)); // contenha tal string

		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		if (lancamento.getDescricao() == null 
			|| lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}
		
		if (lancamento.getMes() == null 
			|| lancamento.getMes() < 1 
			|| lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido.");
		
		}
		
		if (lancamento.getAno() == null
			|| lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido.");
		}
		
		if (lancamento.getUsuario() == null 
			|| lancamento.getUsuario().getId() == null) {
			throw new RegraNegocioException("Informe um Usuário.");
		}
		
		if (lancamento.getValor() == null 
			|| lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) { // verificar se esta passando valor maior que 0
			throw new RegraNegocioException("Informe um Valor válido.");
		}
		
		if (lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um Tipo de lançamento.");
		}
		
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal obterSaldoPorUsuario(Long id) {
		
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO); 
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
		if (receitas == null) {
			receitas = BigDecimal.ZERO; // valor 0
		}
		
		if (despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas); // receitas - despesas
	}

}
