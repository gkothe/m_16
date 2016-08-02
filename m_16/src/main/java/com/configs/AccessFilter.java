package com.configs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.JSONObject;


//@WebFilter(urlPatterns = {"/*"})
public class AccessFilter implements Filter {
	private FilterConfig filterConfig = null;

	public void destroy() {
		
	}

	public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
	
		System.out.println("oi_filter");
		
		if (filterConfig == null)
			return;
		if ( servletRequest instanceof HttpServletRequest ){
			
			servletRequest.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			
			request.setAttribute("exibelogin", new Boolean(false));
			request.setAttribute( "data_hoje", new SimpleDateFormat("dd/MM/yyyy").format(new Date()) );
			request.setAttribute( "resources_root", request.getContextPath() );  
			request.setAttribute( "servlet_root",  request.getContextPath()+request.getServletPath() );
			
			String path = request.getRequestURI();
			
			if (path.startsWith("/")){
				path = path.substring(1);
			}
			String[] partes = path.split("/");
			
			if ( partes.length >= 0 ){
				String strNome = partes[partes.length-1];
				
				if ( strNome.toLowerCase().endsWith("css") ){
					// Verifica se esta nos css permitidos
					//filterChain.doFilter(request, response);
				}else if ( strNome.toLowerCase().endsWith("js") ){
					// Verifica se esta nos js permitidos
					//filterChain.doFilter(request, response);
				}else if ( strNome.toLowerCase().endsWith("jpg") || strNome.toLowerCase().endsWith("gif") || strNome.toLowerCase().endsWith("png")  ){
					//Verifica se esta nas imagens permitidas
					//filterChain.doFilter(request, response);
				}else if ( strNome.toLowerCase().endsWith("woff") || strNome.toLowerCase().endsWith("ttf") ){
					//Verifica se esta nas imagens permitidas
					//filterChain.doFilter(request, response);
				}else{
					
					String identEmpresa = null; 
					String identAcao = null;
					
					if ( partes.length > 2 ){
						identAcao = partes[2];
					}
					
					if ( identAcao == null ){
						identAcao = "login";
					}

					if (identAcao!=null){ //TODO se for diferente de login tb? testar
						request.setAttribute( "endereco_invalido","false");
					}
					
					boolean exibeLogin = false;
					Long coddis = null;
					if ( request.getSession(false) == null ){
						exibeLogin = true;
					}else{
						coddis = (Long)request.getSession(false).getAttribute("coddis");
						if ( !identAcao.equals( "login") ) {
							if ( coddis == null ){
								exibeLogin = true;
							}
						}
					}

					// Dispach para login da empresa
					if ( exibeLogin && !identAcao.equals( "login") ){
						request.setAttribute("errologin","Tempo limite de inatividade atingido.");
						identAcao = "login";
					}
					
					String acao = null;
					String descAcao = "login";
					String endereco = "";

					if ( identAcao.toLowerCase().equalsIgnoreCase("cli") ){
						acao = "C";
						descAcao = "clientes";
					}else if ( identAcao.toLowerCase().equalsIgnoreCase("crm") ){
						acao = "C";
						descAcao = "clientes";
					}
					
					HashMap<String, String> campos = new HashMap<String, String>();
					campos.put("app_root",     request.getContextPath()+request.getServletPath()+request.getPathInfo() );
					campos.put("tarefa_root",  request.getContextPath()+request.getServletPath()+ "/" +  "/home" );
					campos.put("app_rootnav",  request.getContextPath()+request.getServletPath()+request.getPathInfo() );
					campos.put("sys_acao",  descAcao );
					
					ArrayList<String> scripts = new ArrayList<String>();
									
					request.setAttribute( "linkjs", scripts );
					request.setAttribute( "camposhidden", campos );
					if ( request.getSession(false) != null ){
						request.setAttribute( "empresa", request.getSession(false).getAttribute("nomeempresa") );
						request.setAttribute( "nome_usuario", request.getSession(false).getAttribute("nome_usuario") );
						request.setAttribute("tipointerface", request.getSession(false).getAttribute("tipointerface"));
					}
					request.setAttribute( "acao", identAcao );

					if ( acao != null ){
					/*	try{
						//	verificaPermissao(request, response, identEmpresa, coddis, acao, endereco );
						}catch(ServletException ex){
							throw ex;
						}catch(Exception ex){
							throw new ServletException(ex);
						}
						*/
					}
					
				}
			}
			
		//	filterChain.doFilter(servletRequest, response);

		} 
	}

	public void init(FilterConfig arg0) throws ServletException {
		filterConfig = arg0;
	}


	/**
	 * @author Virtuallis
	 * Neste método, é feito uma requisicao ao Master que:
	 * 1) Consulta se o cliente logado é o mesmo que o informado no endereco
	 * 2) Verifica se o módulo tem permissao de acesso
	 * Caso tenha erro, adiciona a variavel no request "exibelogin", que informa para o controller exibir a tela de login.
	 */
	
	
	private String getPermissao(JSONObject retornoJson,  String modulo) throws Exception{
		Object retorno = retornoJson.get( modulo );
		retorno = retorno == null ? "false" : retorno.toString(); 
		
		return retorno.toString();
	}
	
}
