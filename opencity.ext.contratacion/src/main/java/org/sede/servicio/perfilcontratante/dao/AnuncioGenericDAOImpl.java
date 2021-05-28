/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
package org.sede.servicio.perfilcontratante.dao;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.core.rest.CheckeoParametros;
import org.sede.core.rest.MimeTypes;
import org.sede.core.utils.Funciones;
import org.sede.core.utils.Propiedades;
import org.sede.servicio.perfilcontratante.Utils;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class AnuncioGenericDAOImpl extends GenericDAOImpl <Anuncio, BigDecimal> implements AnuncioGenericDAO {
	
	@PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
	public void setEntityManager(EntityManager entityManager) {
		this.setEm(entityManager);
	}
	public Set<ConstraintViolation<Object>> validar(Object registro) {
		ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
		Validator validator = factory.getValidator();
		return validator.validate(registro);
	}
	public long updateVisible(BigDecimal id, String value) {
		
		Query propWeb = this.em().createNativeQuery("update " + persistentClass.getAnnotation(Table.class).name() 
				+ " set gcz_publicado=?, gcz_usuariopub=?, gcz_fechapub=? where ID_ANUNCIO=?");
		
		propWeb.setParameter(1, value);
		propWeb.setParameter(2, Funciones.getPeticion().getClientId());
		propWeb.setParameter(3, new Date());
		propWeb.setParameter(4, id);
		return propWeb.executeUpdate();

	}
	@Override
	public Boolean contratoSellado(BigDecimal id) {
		try {
			Query q = em().createNativeQuery("select id_contrato from perfil_anuncio a, perfil_sello s where a.id_contrato = ? and a.id_anuncio = s.id_anuncio")
					.setParameter(1, id);
			return q.getSingleResult() != null;
		} catch (NoResultException e) {
			return false;
			}
	}
	@Override
	public void asociarAnexos(final Anuncio registro, final MultipartFile file) throws IOException {
		em().unwrap(Session.class).doReturningWork(new ReturningWork<Integer>() {
			public Integer execute(Connection connection) throws SQLException {
				if (!file.isEmpty()) {
					PreparedStatement st = null;
					try {
						String sql = "update perfil_anuncio set adjunto = ?, nombradjunto = ? where id_anuncio = ?";
						st = connection.prepareStatement(sql);
						st.setBinaryStream(1, file.getInputStream(), file.getSize());
						st.setString(2, file.getOriginalFilename());
						st.setBigDecimal(3, registro.getId());

						return st.executeUpdate();

					} catch (IOException e) {

						e.printStackTrace();
						return 0;
					} finally {

						if (st != null) {
							st.close();
						}
					}
				} else {
					return 0;
				}
			}
		});


	}
	@Override
	public byte[] obtenerAnexos(final Anuncio registro) {
		return em().unwrap(Session.class).doReturningWork(new ReturningWork<byte[]>() {
			public byte[] execute(Connection connection) throws SQLException {
				PreparedStatement st = null;
				ResultSet rs = null;
				try {

					String sql = "select adjunto from perfil_anuncio where id_anuncio = " + registro.getId();
					st = connection.prepareStatement(sql);
					rs = st.executeQuery(sql);
					if (rs.next()) {
						return rs.getBytes(1);
					} else {
						return new byte[] {};
					}
				} finally {
					if (rs != null) {
						rs.close();
					}
					if (st != null) {
						st.close();
					}
				}
			}
		});
		
	}
	@Override
	public boolean eliminar(BigDecimal id) {
		// Borramos los sellos que tenga asociados
		Query propWeb = this.em().createNativeQuery("delete from PERFIL_SELLO where id_anuncio=?");
		propWeb.setParameter(1, id);
		propWeb.executeUpdate();
		// borramos el anuncio
		return this.removeById(id);
	}

	private String guardarSello(BigDecimal id) throws IOException {
		if (Propiedades.isLocal()) {
			return "111111";
		} else {
		
			HttpClient client = new HttpClient();
			GetMethod method = new GetMethod(Propiedades.getFehacienteRutaSellado() + id);
			method.addRequestHeader(CheckeoParametros.USERAGENT, "seccionwebapp");
			method.addRequestHeader(CheckeoParametros.ACCEPTHEADER, MimeTypes.JSON);
			
			if (Propiedades.getProxyHost().length() > 0) {
				client.getHostConfiguration().setProxy(Propiedades.getProxyHost(), Integer.parseInt(Propiedades.getProxyPort()));
			}
	        
			method.addRequestHeader("clientId", Propiedades.getFehacienteClientId());
			method.addRequestHeader("pass", Propiedades.getFehacientePass());
			String respuesta = "";
			try {
				client.executeMethod(method);
				respuesta = new String(method.getResponseBody(), CharEncoding.UTF_8);
			} catch (HttpException e) {
				respuesta = e.getMessage();
			} catch (IOException e) {
				respuesta = e.getMessage();
			} finally {
				// Release the connection.
				method.releaseConnection();
			}
			if (respuesta.indexOf("evidencia") < 0) {
				throw new IOException("Error al generar el sello para el anuncio:" + id);
			} else {
				
				ObjectMapper mapper = new ObjectMapper();
				JsonNode actualObj = mapper.readTree(respuesta);
				return actualObj.get("evidencia").asText();
			}
		}
		
	}
	@Override
	public void almacenar(final Anuncio registro) throws IOException {
		
		if (Utils.necesitaSellado(registro)) {
			Anuncio sellado = em().unwrap(Session.class).doReturningWork(new ReturningWork<Anuncio>() {					
				public Anuncio execute(Connection connection) throws SQLException {
					PreparedStatement st = null;
			        try {
			        	st = connection.prepareStatement("insert into perfil_anuncio(ID_ANUNCIO," + 
			        			"TITULO," + 
			        			"TEXTO," + 
			        			"ID_CONTRATO," + 
			        			"ID_TIPOANUNCIO," + 
			        			"GCZ_FECHAALTA," + 
			        			"GCZ_FECHAMOD," + 
			        			"GCZ_FECHACAD," + 
			        			"GCZ_PUBLICADO," + 
			        			"GCZ_FECHAPUB," + 
			        			"GCZ_USUARIOALTA," + 
			        			"GCZ_USUARIOMOD," + 
			        			"GCZ_USUARIOPUB," + 
			        			"VISUALIZARPRIMERA," + 
//			        			"FUENTE_PUBLICACION," +
								"ADJUNTO,"+
			        			"ID_LENGUAJE) values(SEQ_PERFIL_ANUNCIO.nextval"
			        			+ ",?/*titulo*/"
			        			+ ",?/*texto*/"
			        			+ ",?/*id_contrato*/"
			        			+ ",?/*id_tipoanuncio*/"
			        			+ ",?/*gcz_fechaalta*/"
			        			+ ",?/*gcz_fechamod*/"
			        			+ ",?/*gcz_fechacad*/"
			        			+ ",?/*gcz_publicado*/"
			        			+ ",?/*gcz_fechapub*/"
			        			+ ",?/*gcz_usuarioalta*/"
			        			+ ",?/*gcz_usuariomod*/"
			        			+ ",?/*gcz_usuariopub*/"
			        			+ ",?/*visualizarprimera*/"
			        			+ ",?/*adjunto*/"
//			        			+ ",?/*fuente_publicacion*/"
			        			+ ",?/*id_lenguaje*/)");
			        	int i = 0;
			        	if (registro.getTitle() != null) {
							st.setString(++i, registro.getTitle());
				
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
			        	if (registro.getDescription() != null) {
							st.setString(++i, registro.getDescription());
				
						} else {
							st.setNull(++i, java.sql.Types.CLOB);
						}
						st.setLong(++i, registro.getContrato().getId().longValue());
				
						
						if (registro.getType() != null && registro.getType().getId() != null) {
							st.setLong(++i, registro.getType().getId().longValue());
				
						} else {
							st.setNull(++i, java.sql.Types.NUMERIC);
						}
						
						if (registro.getCreationDate() != null) {
							st.setTimestamp(++i, new Timestamp(registro.getCreationDate().getTime()));
				
						} else {
							st.setNull(++i, java.sql.Types.DATE);
						}
						
						if (registro.getLastUpdated() != null) {
							st.setTimestamp(++i, new Timestamp(registro.getLastUpdated().getTime()));
				
						} else {
							st.setNull(++i, java.sql.Types.DATE);
						}
						
						if (registro.getExpiration() != null) {
							st.setTimestamp(++i, new Timestamp(registro.getExpiration().getTime()));
				
						} else {
							st.setNull(++i, java.sql.Types.DATE);
						}
									
						if (registro.getVisible() != null) {
							st.setString(++i, registro.getVisible());
				
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						
						if (registro.getPubDate() != null) {
							st.setTimestamp(++i, new Timestamp(registro.getPubDate().getTime()));
				
						} else {
							st.setNull(++i, java.sql.Types.DATE);
						}
						
						if (registro.getUsuarioAlta() != null) {
							st.setString(++i, registro.getUsuarioAlta());
				
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						
						if (registro.getUsuarioMod() != null) {
							st.setString(++i, registro.getUsuarioMod());
				
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						
						if (registro.getUsuarioPub() != null) {
							st.setString(++i, registro.getUsuarioPub());
				
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						if (registro.getVisualizarprimera() != null) {
							st.setString(++i, registro.getVisualizarprimera());
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						if (registro.getAdjunto() != null) {
							st.setBytes(++i, registro.getAdjunto());
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
						if (registro.getLenguaje() != null && registro.getLenguaje().getId() != null) {
							st.setLong(++i, registro.getLenguaje().getId().longValue());
						} else {
							st.setNull(++i, java.sql.Types.VARCHAR);
						}
			        	// execute insert SQL stetement
			        	st.executeUpdate();
			        	connection.commit();
			        	Statement stC = null;
			            ResultSet rs = null;
			            try {
			            	stC = connection.createStatement();
			            	rs = stC.executeQuery("select SEQ_PERFIL_ANUNCIO.nextval from dual");
			            	if (rs.next()) {
			            		registro.setId(rs.getBigDecimal(1));
			            	} else {
			            		throw new IOException("Error al obtener el identificador del anuncio");
			            	}
			            } catch (SQLException e) {
			    			;
			    		} finally {
			    			if (rs != null) {
			    				rs.close();
			            	}
			            	if (st != null) {
			            		st.close();
			            	}
			            }
			        	return registro;
			        } catch (Exception e) {
			        	e.printStackTrace();
						return null;
					} finally {
			        	if (st != null) {
			        		try {
								st.close();
							} catch (SQLException e) {
								;
							}
			        	}
			        }
				}
			});
			this.guardarSello(sellado.getId());
		} else {
			this.save(registro);
			this.flush();	
		}
	}

	
	
}