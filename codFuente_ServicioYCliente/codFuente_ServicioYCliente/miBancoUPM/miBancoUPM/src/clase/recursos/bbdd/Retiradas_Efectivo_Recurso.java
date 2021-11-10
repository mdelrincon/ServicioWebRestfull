package clase.recursos.bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;

import clase.datos.*;

@Path("/clientes/{id_cliente}/cuentas_bancarias/{cuenta_bancaria_id}/movimientos/retiradas_efectivo")

public class Retiradas_Efectivo_Recurso {

	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public Retiradas_Efectivo_Recurso () {
		InitialContext ctx;
		try {
			ctx = new InitialContext();
			NamingContext envCtx = (NamingContext) ctx.lookup("java:comp/env");

			ds = (DataSource) envCtx.lookup("jdbc/miBancoUPM");
			conn = ds.getConnection();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Lista de retiradas de efectivo con o sin filtro JSON/XML generada con listas en JAXB
			@GET
			@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
			public Response getRetiradas_Efectivo
			(@PathParam("cuenta_bancaria_id") String id,
					@QueryParam("desde") String desde,
					@QueryParam("hasta") String hasta,
					@QueryParam("cantidadX") String cantidadX, 
					@QueryParam("cantidadY") String cantidadY)  {
				try {
					//si no hay filtros 
					if(desde==null&&cantidadX==null) {
					int id_cuenta=Integer.parseInt(id);
					int off = 1;
					int c = 10;
					String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id="+id_cuenta+") order by id LIMIT " + (off - 1) + "," + c + ";"; //hay que conseguir todas las transferencias 
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					Retiradas_Efectivo g = new Retiradas_Efectivo();
					ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
					rs.beforeFirst();
					while (rs.next()) {
						Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//si solo hay fechas
					else if(cantidadX==null) {
						int id_cuenta=Integer.parseInt(id);
						String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id=" +id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Retiradas_Efectivo g = new Retiradas_Efectivo();
						ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
						rs.beforeFirst();
						while (rs.next()) {
							Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//hay cantidad de dinero
					else if(cantidadX!=null){
						int id_cuenta=Integer.parseInt(id);
						int cantidadDesde=Integer.parseInt(cantidadX);
						//miramos si hay fechas
						if(desde!=null) {
							//miramos si hay una cantidad Y
							if(cantidadY!=null) {
							int cantidadHasta=Integer.parseInt(cantidadY);
							String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id=" +id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' AND saldoRetirado BETWEEN "+cantidadDesde+" AND "+cantidadHasta+" ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Retiradas_Efectivo g = new Retiradas_Efectivo();
							ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
							rs.beforeFirst();
							while (rs.next()) {
								Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
							}
							//si no hay cantidad Y
							else {
								String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id=" +id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' AND saldoRetirado >="+cantidadDesde+" ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
								PreparedStatement ps = conn.prepareStatement(sql);
								ResultSet rs = ps.executeQuery();
								Retiradas_Efectivo g = new Retiradas_Efectivo();
								ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
								rs.beforeFirst();
								while (rs.next()) {
									Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
								}
								return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
								}
							}
						//si no hay fechas 
						else {
							//miramos si hay una cantidad Y
							if(cantidadY!=null) {
							int cantidadHasta=Integer.parseInt(cantidadY);
							String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id=" +id_cuenta+") AND saldoRetirado BETWEEN "+cantidadDesde+" AND "+cantidadHasta+" ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Retiradas_Efectivo g = new Retiradas_Efectivo();
							ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
							rs.beforeFirst();
							while (rs.next()) {
								Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
							}
							//si no hay cantidad Y
							else {
								String sql = "SELECT * FROM Retirada_Efectivo WHERE (cuenta_id=" +id_cuenta+") AND  saldoRetirado >="+cantidadDesde+" ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
								PreparedStatement ps = conn.prepareStatement(sql);
								ResultSet rs = ps.executeQuery();
								Retiradas_Efectivo g = new Retiradas_Efectivo();
								ArrayList<Link> Retiradas_Efectivo = g.getRetiradas_Efectivo();
								rs.beforeFirst();
								while (rs.next()) {
									Retiradas_Efectivo.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
								}
								return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
								}
						}
						
		
		
					}
					else {return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
							.build();}
					
				} catch (NumberFormatException e) {
					return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
							.build();
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
				}
			}
			
			//Obtener datos de una retirada de efectivo concreta 
			@GET
			@Path("{retirada_efectivo_id}")
			@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
			public Response getCliente(@PathParam("retirada_efectivo_id") String id) {
				try {
					int int_id = Integer.parseInt(id);
					String sql = "SELECT * FROM Retirada_Efectivo where id=" + int_id + ";";
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						Retirada_Efectivo Retirada_Efectivo =  retiradaFromRS(rs);
						return Response.status(Response.Status.OK).entity(Retirada_Efectivo).build();
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
					}
				} catch (NumberFormatException e) {
					return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
				}
			}
				
	//crea una retirada de efectivo
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON}) 
	public Response createRetirada_Efectivo(@PathParam("cuenta_bancaria_id") String cuenta,Retirada_Efectivo retirada_efectivo) {
		try {
			PreparedStatement ps;
			String sql;
			ResultSet rs;
			int saldoDeCuenta;
			int id_cuenta = Integer.parseInt(cuenta);
			retirada_efectivo.setCuenta(id_cuenta); 
			sql = "SELECT * FROM Cuenta_Bancaria WHERE id =" + id_cuenta + ";";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			if(rs.next()) {
			   saldoDeCuenta = rs.getInt("saldo");
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("No se encuentra la cuenta bancaria").build();
			}	
		
			if(saldoDeCuenta >= retirada_efectivo.getSaldoRetirado()) {
				int nuevoSaldo = saldoDeCuenta - retirada_efectivo.getSaldoRetirado();
				sql = "INSERT INTO `miBancoUPM`.`Retirada_Efectivo` (`cuenta_id`, `fecha`, `saldoRetirado`) " + "VALUES ('"
						+ id_cuenta + "', '" + retirada_efectivo.getFecha() + "', '" + retirada_efectivo.getSaldoRetirado() + "');";
			    ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			    int affectedRows = ps.executeUpdate();
			    ResultSet generatedID = ps.getGeneratedKeys();
			    if(generatedID.next()) {
			      retirada_efectivo.setId(generatedID.getInt(1));   
			      sql = "UPDATE `miBancoUPM`.`Cuenta_Bancaria` SET `saldo` =" + (nuevoSaldo)+ " WHERE `id` =" + id_cuenta + ";";
			      ps = conn.prepareStatement(sql);	
			      affectedRows = ps.executeUpdate();
			    sql = "SELECT * FROM Cuenta_Bancaria WHERE id =" + id_cuenta + ";";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				int id_cliente;
				if(rs.next()) {
					id_cliente = rs.getInt("Cliente_id");
				}else {
					return Response.status(Response.Status.NOT_FOUND).entity("No se encuentra la cuenta bancaria").build();
				}
			    sql = "INSERT INTO `miBancoUPM`.`Movimientos` (`id_cliente`, `id_Movimiento`, `tipo`, `fecha`, `id_cuenta` ) " + "VALUES ('"
						+ id_cliente + "', '" + retirada_efectivo.getId() + "', '" + "Retirada_Efectivo'" + ",'"+ retirada_efectivo.getFecha()+"','"+id_cuenta+"');";
			    ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			    affectedRows=ps.executeUpdate();	
			    String location = uriInfo.getAbsolutePath() + "/" + retirada_efectivo.getId(); 
			    return Response.status(Response.Status.CREATED).entity(retirada_efectivo).header("Location", location).header("Content-Location", location).build();			    
			 }
			    else {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo realizar la retirada de efectivo").build();
			    }
			}
			else {
				return Response.status(Response.Status.PRECONDITION_FAILED).entity("No dispone del saldo necesario").build();
		    }			
			
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo realizar la retirada de efectivo").build();
		}
	}

	//para crear una retirada
	private Retirada_Efectivo retiradaFromRS(ResultSet rs) throws SQLException {
		Retirada_Efectivo Retirada_Efectivo = new Retirada_Efectivo(rs.getInt("cuenta_id"), rs.getString("fecha"), rs.getInt("saldoRetirado"));
		Retirada_Efectivo.setId(rs.getInt("id"));
		return Retirada_Efectivo;
	}

}
