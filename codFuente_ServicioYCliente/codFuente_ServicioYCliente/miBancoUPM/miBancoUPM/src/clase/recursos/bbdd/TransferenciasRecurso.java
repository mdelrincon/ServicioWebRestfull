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

@Path("clientes/{id_cliente}/cuentas_bancarias/{cuenta_bancaria_id}/movimientos/transferencias")
public class TransferenciasRecurso {
	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public TransferenciasRecurso() {
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
	
	/*// Lista de transferencias filtrada por fecha JSON/XML generada con listas en JAXB
	@GET
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getTransferenciasFecha
	(@QueryParam("cuenta_bancaria_id") String id,@QueryParam("fecha1") String desde, @QueryParam("fecha2") String hasta) {
		try {
			int id_cuenta = Integer.parseInt(id);
			String sql = "SELECT * FROM Transferencia WHERE (cuentaRecibe_ID=" +id_cuenta+" OR cuentaRealiza_ID="+id_cuenta +") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Transferencias g = new Transferencias();
			ArrayList<Link> transferencias = g.getTransferencias();
			rs.beforeFirst();
			while (rs.next()) {
				transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
			}
			return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
					.build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}*/
	
	/*// Lista de transferencias filtrada por limite JSON/XML generada con listas en JAXB
		@GET
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Response getTransferenciasLimite
		(@PathParam("clientes/{id_cliente}/cuentas_bancarias/{cuenta_bancaria_id}") String id,
				@QueryParam("limite") String numero1, 
				@QueryParam("limite") String numero2 ) {
			try {
				int limite1 = Integer.parseInt(numero1);
				int id_cuenta= Integer.parseInt(id);
				//si hay dos limites 
				if(numero2!=null) {
					int limite2= Integer.parseInt(numero2);
					String sql = "SELECT * FROM Transferencia WHERE (cuenta_Realiza_ID="+id_cuenta+" OR cuenta_Recibe_ID="+id_cuenta+") ORDER BY id asc LIMIT "+(limite1-1)+","+limite2+";"; //hay que conseguir todas las transferencias 
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					ArrayList<Integer> id_transferencias=new ArrayList<Integer>();
					Transferencias g = new Transferencias();
					ArrayList<Link> transferencias = g.getTransferencias();
					rs.beforeFirst();
					while(rs.next() && limite1!=0) {
						limite1--;
					}
					while(rs.next() && limite2!=0) {
						transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
						limite2--;
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
				}
				else {
				String sql = "SELECT * FROM Transferencia WHERE (cuenta_Realiza_ID="+id_cuenta+" OR cuenta_Recibe_ID="+id_cuenta+") ORDER BY id asc LIMIT "+(limite1-1)+";"; //hay que conseguir todas las transferencias 
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				ArrayList<Integer> id_transferencias=new ArrayList<Integer>();
				Transferencias g = new Transferencias();
				ArrayList<Link> transferencias = g.getTransferencias();
				rs.beforeFirst();
				while (rs.next() && limite1!=0) {
					transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
					limite1--;
				}
				return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
				}	
			} catch (NumberFormatException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
						.build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
			}
		}
		*/
	
		// Lista de transferencias filtrada por limite y fecha JSON/XML generada con listas en JAXB
				@GET
				@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
				public Response getTransferenciasLimiteYFecha
				(@PathParam("cuenta_bancaria_id") String id,
						@QueryParam("desde") String desde,
						@QueryParam("hasta") String hasta,
						@QueryParam("limite1") String numero1, 
						@QueryParam("limite2") String numero2 ) {
					try {
						//en caso de que no haya ningun filtro 
						if(desde==null&&numero1==null) {
							int id_cuenta=Integer.parseInt(id);
							int off = 1;
							int c = 10;
							String sql = "SELECT * FROM Transferencia WHERE (cuentaRealiza_ID="+id_cuenta+" OR cuentaRecibe_ID="+id_cuenta+") order by fecha LIMIT " + (off - 1) + "," + c + ";"; //hay que conseguir todas las transferencias 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Transferencias g = new Transferencias();
							ArrayList<Link> transferencias = g.getTransferencias();
							rs.beforeFirst();
							while (rs.next()) {
								transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)

						}
						//filtro solo por fechas
						if(numero1==null) {
							int id_cuenta=Integer.parseInt(id);
							String sql = "SELECT * FROM Transferencia WHERE (cuentaRecibe_ID=" +id_cuenta+" OR cuentaRealiza_ID="+id_cuenta +") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Transferencias g = new Transferencias();
							ArrayList<Link> transferencias = g.getTransferencias();
							rs.beforeFirst();
							while (rs.next()) {
								transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
						int limite1 = Integer.parseInt(numero1);
						int id_cuenta= Integer.parseInt(id);
						//si hay dos limites 
						if(numero2!=null) {
							//filtro por fechas y dos limites 
							if(desde!=null) {
							int limite2= Integer.parseInt(numero2);
							String sql = "SELECT * FROM Transferencia WHERE (cuentaRealiza_ID="+id_cuenta+" OR cuentaRecibe_ID="+id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC LIMIT "+(limite1)+","+(limite2)+";"; //hay que conseguir todas las transferencias 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Transferencias g = new Transferencias();
							ArrayList<Link> transferencias = g.getTransferencias();
							rs.beforeFirst();
							while(rs.next()) {
								transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
							}
							//filtro por dos limites 
							else {
								int limite2= Integer.parseInt(numero2);
								String sql = "SELECT * FROM Transferencia WHERE (cuentaRealiza_ID="+id_cuenta+" OR cuentaRecibe_ID="+id_cuenta+")  ORDER BY fecha DESC LIMIT "+(limite1)+","+(limite2)+";"; //hay que conseguir todas las transferencias 
								PreparedStatement ps = conn.prepareStatement(sql);
								ResultSet rs = ps.executeQuery();
								Transferencias g = new Transferencias();
								ArrayList<Link> transferencias = g.getTransferencias();
								rs.beforeFirst();
								while(rs.next()) {
									transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
								}
								return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
							}
						}
						//si solo hay un limite 
						else {
						//filtro por fecha y un limite
						if(desde!=null) {
						String sql = "SELECT * FROM Transferencia WHERE (cuentaRealiza_ID="+id_cuenta+" OR cuentaRecibe_ID="+id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Transferencias g = new Transferencias();
						ArrayList<Link> transferencias = g.getTransferencias();
						rs.beforeFirst();
						while (rs.next()) {
							transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
						//filtro por un limite
						else {
							String sql = "SELECT * FROM Transferencia WHERE (cuentaRealiza_ID="+id_cuenta+" OR cuentaRecibe_ID="+id_cuenta+") ORDER BY fecha DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							Transferencias g = new Transferencias();
							ArrayList<Link> transferencias = g.getTransferencias();
							rs.beforeFirst();
							while (rs.next()) {
								transferencias.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
						}	
					} catch (NumberFormatException e) {
						return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
								.build();
					} catch (SQLException e) {
						return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
					}
				}
				
		
		
		//Obtener datos de una transferencia concreta 
		@GET
		@Path("{transferencia_id}")
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Response getCliente(@PathParam("transferencia_id") String id) {
			try {
				int int_id = Integer.parseInt(id);
				String sql = "SELECT * FROM Transferencia where id=" + int_id + ";";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					Transferencia Transferencia =  transferenciaFromRS(rs);
					return Response.status(Response.Status.OK).entity(Transferencia).build();
				} else {
					return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				}
			} catch (NumberFormatException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
			}
		}
	
	//Para crear una transferencia
			@POST
			@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
			public Response createTransferencia(@PathParam("cuenta_bancaria_id") String cuenta,Transferencia transferencia) {
				try {
					//primero debemos ver que tenga saldo suficiente la cuenta que transfiere el dienro 
					int id_cuenta= Integer.parseInt(cuenta);
					int saldoDeCuenta;
					int saldoDeCuentaRecibe;
					transferencia.setCuentaRealiza(id_cuenta);
					//obtenemos esa cuenta 
					String sql = "SELECT * FROM Cuenta_Bancaria where id=" + id_cuenta + ";";
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						saldoDeCuenta =  rs.getInt("saldo");
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
					}
					if(saldoDeCuenta>=transferencia.getSaldoTransferido()) {
				     sql = "INSERT INTO `miBancoUPM`.`Transferencia` (`cuentaRealiza_ID`, `cuentaRecibe_ID`, `fecha`, `saldoTransferido`) " + "VALUES ('"
							+ id_cuenta + "', '" + transferencia.getCuentaRecibe() + "', '" + transferencia.getFecha() + "', '" + transferencia.getSaldoTransferido()+ "');";
				    ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
					int affectedRows = ps.executeUpdate();
					
					// Obtener el ID del elemento recién creado. 
					// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
					ResultSet generatedID = ps.getGeneratedKeys();
					if (generatedID.next()) {
						transferencia.setId(generatedID.getInt(1));
						String location = uriInfo.getAbsolutePath() + "/" + transferencia.getId();
						saldoDeCuenta=saldoDeCuenta-transferencia.getSaldoTransferido();
						//Una vez creada la transferencia debemos actualizar saldos
						//actualizamos el saldo de la cuenta que realiza la transferencia 
						sql= "UPDATE `miBancoUPM`.`Cuenta_Bancaria` SET `saldo` = "+(saldoDeCuenta)+ " WHERE id="+id_cuenta+";";
						ps = conn.prepareStatement(sql);
						affectedRows = ps.executeUpdate();
						//ahora conseguimos la cuenta que recibe el dinero 
						sql= "SELECT * FROM Cuenta_Bancaria where id=" + transferencia.getCuentaRecibe() + ";";
						ps=conn.prepareStatement(sql);
						rs = ps.executeQuery();
						if (rs.next()) {
							saldoDeCuenta =  rs.getInt("saldo");
						} else {
							return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
						}
						//actualizamos la cuenta que lo recibe
						saldoDeCuenta=saldoDeCuenta+transferencia.getSaldoTransferido();
						sql= "UPDATE `miBancoUPM`.`Cuenta_Bancaria` SET `saldo` = "+(saldoDeCuenta)+ " WHERE id="+transferencia.getCuentaRecibe()+";";
						ps = conn.prepareStatement(sql);
						affectedRows = ps.executeUpdate();
						
						//metemos en la tabla de movimientos esta transferencia
						//primero necesitamos la cuenta bancaria del cliente 
						sql="SELECT * FROM Cuenta_Bancaria where id="+id_cuenta+";";
						ps= conn.prepareStatement(sql);
						rs=ps.executeQuery();
						int id_cliente;
						if (rs.next()) {
							id_cliente =  rs.getInt("Cliente_id");
						} else {
							return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
						}
						sql= "INSERT INTO `miBancoUPM`.`Movimientos` (`id_cliente`, `id_Movimiento`, `tipo`, `fecha`, `id_cuenta`) " + "VALUES ('"
								 + id_cliente + "', '" + transferencia.getId() + "', '" + "Transferencia" + "', '" + transferencia.getFecha()+ "', '"+id_cuenta+ "');";
						ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
						affectedRows = ps.executeUpdate();
						
						//ahora metemos en la tabla de movimientos la transferencia para el cliente que la recibe
						sql="SELECT * FROM Cuenta_Bancaria where id="+transferencia.getCuentaRecibe()+";";
						ps= conn.prepareStatement(sql);
						rs=ps.executeQuery();
						if (rs.next()) {
							id_cliente =  rs.getInt("Cliente_id");
						} else {
							return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
						}
						sql= "INSERT INTO `miBancoUPM`.`Movimientos` (`id_cliente`, `id_Movimiento`, `tipo`, `fecha`, `id_cuenta`) " + "VALUES ('"
								 + id_cliente + "', '" + transferencia.getId() + "', '" + "Transferencia" + "', '" + transferencia.getFecha()+ "', '"+transferencia.getCuentaRecibe()+ "');";
						ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
						affectedRows = ps.executeUpdate();
						//terminamos 
						return Response.status(Response.Status.CREATED).entity(transferencia).header("Location", location).header("Content-Location", location).build();
					}
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear la transferencia").build();
					}
					else {
						return Response.status(Response.Status.PRECONDITION_FAILED).entity("No se puede realizar la transferencia ya que no tiene suficiente saldo para realizarla").build();
					}
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear la transferencia\n" + e.getStackTrace()).build();
				}
			}
			
			//borrar transferencia 
			@DELETE
			@Path("{transferencia_id}")
			public Response deleteTransferncia(@PathParam("transferencia_id") String id, @PathParam("cuenta_bancaria_id") String cuenta) {
				try {
					Transferencia transferencia;
					int saldoDeCuenta;
					int int_id = Integer.parseInt(id);
					int id_cuenta = Integer.parseInt(cuenta);
					//obtenemos esa cuenta 
					String sql = "SELECT * FROM Transferencia where id=" + int_id + ";";
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						transferencia =  transferenciaFromRS(rs);
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
					}
					//Actualizamos los saldos afectados por la transferencia 
					//ahora conseguimos la cuenta que recibe el dinero 
					sql= "SELECT * FROM Cuenta_Bancaria where id=" + transferencia.getCuentaRecibe() + ";";
					ps=conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if (rs.next()) {
						saldoDeCuenta =  rs.getInt("saldo");
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
					}
					//actualizamos la cuenta que lo recibe
					saldoDeCuenta=saldoDeCuenta-transferencia.getSaldoTransferido();
					sql= "UPDATE `miBancoUPM`.`Cuenta_Bancaria` SET `saldo` = "+(saldoDeCuenta)+ " WHERE id="+transferencia.getCuentaRecibe()+";";
					ps = conn.prepareStatement(sql);
					int affectedRows = ps.executeUpdate();
					//ahora conseguimos la cuenta que realizo la transferencia 
					sql= "SELECT * FROM Cuenta_Bancaria where id=" + transferencia.getCuentaRealiza() + ";";
					ps=conn.prepareStatement(sql);
					rs = ps.executeQuery();
					if (rs.next()) {
						saldoDeCuenta =  rs.getInt("saldo");
					} else {
						return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
					}
					//actualizamos la cuenta que lo realiza
					saldoDeCuenta=saldoDeCuenta+transferencia.getSaldoTransferido();
					sql= "UPDATE `miBancoUPM`.`Cuenta_Bancaria` SET `saldo` = "+(saldoDeCuenta)+ " WHERE id="+transferencia.getCuentaRealiza()+";";
					ps = conn.prepareStatement(sql);
					affectedRows = ps.executeUpdate();
					
					//ahora borramos la transferencia de la base de datos 
					sql = "DELETE FROM `miBancoUPM`.`Transferencia` WHERE `id`=" + int_id + ";";
				    ps = conn.prepareStatement(sql);
					affectedRows = ps.executeUpdate();
					if (affectedRows == 1) {
						//ahora borramos el movimiento de la base de datos 
						//primero necesitamos la cuenta bancaria del cliente que realiza la transferencia
						sql="SELECT * FROM Cuenta_Bancaria where id="+id_cuenta+";";
						ps= conn.prepareStatement(sql);
						rs=ps.executeQuery();
						int id_cliente;
						if (rs.next()) {
							id_cliente =  rs.getInt("Cliente_id");
						} else {
							return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
						}
						sql="DELETE FROM `miBancoUPM`.`Movimientos` WHERE( `id_Movimiento`="+int_id+" AND `id_cliente`="+id_cliente+" AND `id_cuenta`="+id_cuenta+") ;";
						ps = conn.prepareStatement(sql);
						affectedRows = ps.executeUpdate();
						if (affectedRows == 1) {
							//ahora necesitamos borrar el movimiento de la base de datos de quien recibe la transferencia
							sql="SELECT * FROM Cuenta_Bancaria where id="+transferencia.getCuentaRecibe()+";";
							ps= conn.prepareStatement(sql);
							rs=ps.executeQuery();
							if (rs.next()) {
								id_cliente =  rs.getInt("Cliente_id");
							} else {
								return Response.status(Response.Status.NOT_FOUND).entity("Saldo de cuenta que recibe no encontrado").build();
							}
							sql="DELETE FROM `miBancoUPM`.`Movimientos` WHERE( `id_Movimiento`="+int_id+" AND `id_cliente`="+id_cliente+" AND `id_cuenta`="+transferencia.getCuentaRecibe()+") ;";
							ps = conn.prepareStatement(sql);
							affectedRows = ps.executeUpdate();
							if (affectedRows == 1) {
							return Response.status(Response.Status.NO_CONTENT).build();
							}
							else 
								return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
						}
						else 
							return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
						
					}
					else 
						return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
				} catch (SQLException e) {
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el cliente\n" + e.getStackTrace()).build();
				}
			}
			
			//para crear una transferencia
			private Transferencia transferenciaFromRS(ResultSet rs) throws SQLException {
				Transferencia transferencia = new Transferencia(rs.getInt("cuentaRealiza_ID"), rs.getInt("cuentaRecibe_ID"), rs.getString("fecha"), rs.getInt("saldoTransferido"));
				transferencia.setId(rs.getInt("id"));
				return transferencia;
			}
}
