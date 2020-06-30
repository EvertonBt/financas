package com.batista.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

// classe q captura as execções, retonando uma msg amigável
@ControllerAdvice // ou @RestContollerAdvice
public class ExceptionConfig extends ResponseEntityExceptionHandler {

	// esse recurso está definido no arquivo message.properties c/ a mensagens de
	// validação
	@Autowired
	private MessageSource messageSource;

	// captura a msg de erro Bad Request 400, qnd o cliente passa uma atributo
	// inexiste q ele ñ consegue ler
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		// msg p/ usuário comum, o getMessage recebe a chave da mensagem de retorno no
		// arq messages.properties, o valor null, e o idioma em q a msg será enviada
		// (nesse caso ele usará o idioma corrente, ou seja, pt_BR
		String mensagemUsuario = messageSource.getMessage("atributo-invalido", null, LocaleContextHolder.getLocale());

		// mensagem p/ o desenvolvedor (se a causa for diferente de null ele a retorna,
		// do contrário ele ñ retorna a causa
		// obs: algumas exceções c/ EmptyResultDataAccessException ñ retornam a causa
		String mensagemDev = ex.getCause() != null ? ex.getCause().toString() : ex.toString();

		// P/ padronizar ele transforma a msg num formato de lista p/ o retorno
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));

		// anexa as duas mensagens jubtamente com o código 400 bad request
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	// captura a msg do Bean Validation qnd o usuário envia um atributo com valor
	// null ou q ñ cumpra os requisitos de numero de caracteres
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		List<Erro> erros = this.criaListaDeErros(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

// gera a lista de erros p/ exibição, usada pelo método acima
	private List<Erro> criaListaDeErros(BindingResult bindingResult) {
		List<Erro> erros = new ArrayList<>();
		// cpatura os erros através do binding result e monta uma lista de erros c/ as
		// mensagens p/ o usuário comum e p/ o desenvolviedor
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String mensagemUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String mensagemDev = fieldError.toString();
			erros.add(new Erro(mensagemUsuario, mensagemDev));
		}
		return erros;
	}

// ao deletar um recurso caso ele já tenha sido deletado emite um erro q é capturado por esse método + tb é capturada ao passar um codigo iexisten
	@ExceptionHandler(EmptyResultDataAccessException.class)
	protected ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
			WebRequest request) {
		String mensagemUsuario = messageSource.getMessage("recurso-inexistente", null, LocaleContextHolder.getLocale());
		String mensagemDev = ex.toString(); // note q nesse caso ñ usei o getCause, pois dava erro
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);

	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	protected ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {

		String mensagemUsuario = messageSource.getMessage("atributo-invalido", null, LocaleContextHolder.getLocale());
      //note q esse trecho é diferente, pois estou usando a bilioteca commons 
	  //q permite exibir a causa dos erros de forma mais detalhada p/ o dev
		String mensagemDev = ExceptionUtils.getRootCauseMessage(ex);
		List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

	}
	// execeção gerada qnd vc tenta salvar um lançamento, mas o código da pessoa associada é inválido ou a pessoa está inativa
	@ExceptionHandler(PessoaInexistenteOuInativaException.class)
	protected ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex, WebRequest request) {
		
		   String mensagemUsuario = messageSource.getMessage("pessoa-inexistente-inativa", null, LocaleContextHolder.getLocale());
			String mensagemDev = ex.toString();
			List<Erro> erros = Arrays.asList(new Erro(mensagemUsuario, mensagemDev));
			return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

}

// classe apenas p/ gerar as mensagens no formato json
class Erro {

	private String mensagemUsuario;
	private String mensagemDev;

	public Erro(String mensagemUsuario, String mensagemDev) {
		this.mensagemUsuario = mensagemUsuario;
		this.mensagemDev = mensagemDev;
	}

	public String getMensagemUsuario() {
		return mensagemUsuario;
	}

	public void setMensagemUsuario(String mensagemUsuario) {
		this.mensagemUsuario = mensagemUsuario;
	}

	public String getMensagemDev() {
		return mensagemDev;
	}

	public void setMensagemDev(String mensagemDev) {
		this.mensagemDev = mensagemDev;
	}

}
