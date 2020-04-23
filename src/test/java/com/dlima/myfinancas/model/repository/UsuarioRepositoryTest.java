package com.dlima.myfinancas.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.dlima.myfinancas.model.entity.Usuario;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test") // declarar que vai usar application-test
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Test
	public void deveVerificarExistenciaDeEmail() {
		// cenario
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@mail.com").build();
		repository.save(usuario);
		
		// acao/ execucao
		boolean resultado = repository.existsByEmail("usuario@mail.com");
		
		// verificacao
		Assertions.assertThat(resultado).isTrue();
	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
		// cenario
		repository.deleteAll();
		
		// acao
		boolean resultado = repository.existsByEmail("usuario@gmail.com");
				
		// verificacao
		Assertions.assertThat(resultado).isFalse();
	}

}
