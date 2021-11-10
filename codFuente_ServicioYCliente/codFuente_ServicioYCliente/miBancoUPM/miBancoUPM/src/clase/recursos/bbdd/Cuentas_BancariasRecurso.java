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

@Path("/clientes/{id_cliente}/cuentas_bancarias")
public class Cuentas_BancariasRecurso {
	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public Cuentas_BancariasRecurso() {
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
	
	// Lista de cuentas de un cliente JSON/XML generada con listas en JAXB
			@GET
			@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
			public Response getCuentass2(@PathParam("id_cliente")String id,@QueryParam("offset") @DefaultValue("1") String offset,
					@QueryParam("count") @DefaultValue("10") String count) {
				try {
					int id_cliente=Integer.parseInt(id);
					int off = Integer.parseInt(offset);
					int c = Integer.parseInt(count);
					String sql = "SELECT * FROM Cuenta_Bancaria WHERE Cliente_id="+id_cliente+" order by id LIMIT " + (off - 1) + "," + c + ";";
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					Cuentas_Bancarias g = new Cuentas_Bancarias();
					ArrayList<Link> cuentas_bancarias = g.getCuentas_Bancarias();
					rs.beforeFirst();
					while (rs.next()) {
						cuentas_bancarias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
				} catch (NumberFormatException e) {
					return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
							.build();
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
				}
			}
			
			//Obtener datos de una cuenta bancaria 
			@GET
			@Path("{cuenta_bancaria_id}")
			@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
			public Response getCuentaBancaria(@PathParam("cuenta_bancaria_id") String id) {
				try {
					int int_id = Integer.parseInt(id);
					String sql = "SELECT * FROM Cuenta_Bancaria where id=" + int_id + ";";
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						Cuenta_Bancaria cuenta =  cuenta_BancariaFromRS(rs);
						//metemos en Cuenta_Bancaria las transferencias ordenadas
						sql= "SELECT *  FROM Transferencia WHERE (cuentaRealiza_ID="+int_id+" OR cuentaRecibe_ID="+ int_id+ ") ORDER BY fecha desc;";
						ps= conn.prepareStatement(sql);
						rs=ps.executeQuery();
						while(rs.next()) {
							Transferencia Transferencia=transferenciaFromRS(rs);
						//	cuenta.añadirTransferencia(Transferencia);
						}
						//ahora metemos todos sus retiradas de efectivo
						sql= "SELECT * FROM Retirada_Efectivo where  cuenta_id="+int_id+" ORDER BY fecha desc;";
						ps= conn.prepareStatement(sql);
						rs=ps.executeQuery();
						while(rs.next()) {
							Retirada_Efectivo Retirada_Efectivo=retiradaFromRS(rs);
							//cuenta.añadirRetirada(Retirada_Efectivo);
						}
						return Response.status(Response.Status.OK).entity(cuenta).build();
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
					}
				} catch (NumberFormatException e) {
					return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
				}
			}
			
	//Para crear una cuenta bancaria 
		@POST
		@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Response createCuenta_Bancaria(Cuenta_Bancaria cuenta_bancaria, @PathParam("id_cliente") String cliente) {
			try {
				int id_cliente=Integer.parseInt(cliente);
				cuenta_bancaria.setIdCliente(id_cliente);
				String sql = "INSERT INTO `miBancoUPM`.`Cuenta_Bancaria` (`Cliente_id`, `nombreDuenio`, `apellidoDuenio`, `saldo`) " + "VALUES ('"
						+ id_cliente + "', '" + cuenta_bancaria.getNombreDuenio() + "', '" + cuenta_bancaria.getApellidoDuenio() + "', '" + cuenta_bancaria.getSaldo()+ "');";
				PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
				int affectedRows = ps.executeUpdate();
				
				// Obtener el ID del elemento recién creado. 
				// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
				ResultSet generatedID = ps.getGeneratedKeys();
				if (generatedID.next()) {
					cuenta_bancaria.setId(generatedID.getInt(1));
					String location = uriInfo.getAbsolutePath() + "/" + cuenta_bancaria.getId();
					return Response.status(Response.Status.CREATED).entity(cuenta_bancaria).header("Location", location).header("Content-Location", location).build();
				}
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear la cuenta bancaria").build();
				
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear la cuenta bancaria\n" + e.getStackTrace()).build();
			}
		}
		
		//borrar cuenta bancaria si esta tiene saldo 0
		@DELETE
		@Path("{cuenta_bancaria_id}")
		public Response deleteCuentaBancaria(@PathParam("cuenta_bancaria_id") String id) {
			try {
				Cuenta_Bancaria cuenta_bancaria;
				int int_id = Integer.parseInt(id);
				//obtenemos esa cuenta 
				String sql = "SELECT * FROM Cuenta_Bancaria where id=" + int_id + ";";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					cuenta_bancaria =  cuenta_BancariaFromRS(rs);
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
				//borramos la cuenta
				if(cuenta_bancaria.getSaldo()==0) {
				sql = "DELETE FROM `miBancoUPM`.`Cuenta_Bancaria` WHERE `id`=" + int_id + ";";
			    ps = conn.prepareStatement(sql);
				int affectedRows = ps.executeUpdate();
				if (affectedRows == 1) {
					//borramos las retiradas de esa cuenta 
					sql= "DELETE FROM `miBancoUPM`.`Retirada_Efectivo` where `cuenta_id`='"+int_id+"';";
					ps = conn.prepareStatement(sql);
				    affectedRows = ps.executeUpdate();
				    return Response.status(Response.Status.NO_CONTENT).build();
				}
				else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();	
				}
				
				}
				else {
					return Response.status(Response.Status.PRECONDITION_FAILED).entity("No se puede eliminar cuenta: saldo mayor de 0.").build();
				}
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el cliente\n" + e.getStackTrace()).build();
			}
		}
		
		//para crear una cuenta bancaria 
		private Cuenta_Bancaria cuenta_BancariaFromRS(ResultSet rs) throws SQLException {
			Cuenta_Bancaria cuenta_Bancaria = new Cuenta_Bancaria(rs.getInt("Cliente_id"), rs.getString("nombreDuenio"), rs.getString("apellidoDuenio"), rs.getInt("saldo"));
			cuenta_Bancaria.setId(rs.getInt("id"));
			return cuenta_Bancaria;
		}
		
		//para crear una transferencia
		private Transferencia transferenciaFromRS(ResultSet rs) throws SQLException {
			Transferencia transferencia = new Transferencia(rs.getInt("cuentaRealiza_ID"), rs.getInt("cuentaRecibe_ID"), rs.getString("fecha"), rs.getInt("saldoTransferido"));
			transferencia.setId(rs.getInt("id"));
			return transferencia;
		}
		
		//para crear una retirada
		private Retirada_Efectivo retiradaFromRS(ResultSet rs) throws SQLException {
			Retirada_Efectivo Retirada_Efectivo = new Retirada_Efectivo(rs.getInt("cuenta_id"), rs.getString("fecha"), rs.getInt("saldoRetirado"));
			Retirada_Efectivo.setId(rs.getInt("id"));
			return Retirada_Efectivo;
		}
	
}
