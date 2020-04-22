package com.dlima.myfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dlima.myfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}