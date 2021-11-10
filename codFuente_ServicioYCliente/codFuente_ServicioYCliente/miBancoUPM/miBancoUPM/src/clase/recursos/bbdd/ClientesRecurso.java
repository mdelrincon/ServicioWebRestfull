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

/*import clase.datos.Cliente;
import clase.datos.Clientes;
import clase.datos.Link;
import clase.datos.Cuenta_Bancaria;*/
import clase.datos.*;


@Path("/clientes")
public class ClientesRecurso {
	
	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public ClientesRecurso() {
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
	
	// Lista de clientes JSON/XML generada con listas en JAXB
		@GET
		@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
		public Response getClientes2(@QueryParam("desde") String desde,
				@QueryParam("hasta") String hasta,
				@QueryParam("limite1") String numero1, 
				@QueryParam("limite2") String numero2 ) {
			try {
				
				
				//si no hay filtro
				if(desde==null&&numero1==null) {
				
				String sql = "SELECT * FROM Cliente order by id;";
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				Clientes g = new Clientes();
				ArrayList<LinkSaldo> clientes = g.getClientes();
				rs.beforeFirst();
				int saldo=0;
				PreparedStatement ps2;
				ResultSet rs2;
				while (rs.next()) {
					sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";";
					ps2=conn.prepareStatement(sql);
					rs2=ps2.executeQuery();
					while(rs2.next()) {
						saldo=saldo+rs2.getInt("saldo");
					}
					clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
					saldo=0;
				}
				return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
				}
				//filtro solo si es solo por cantidad de informacion 
				else if(numero1==null) {
					//filtro por solo una cantidad mayor de X informacion
					if(hasta==null&&desde!=null) {
					int desde1=Integer.parseInt(desde);
					String sql = "SELECT * FROM Cliente  ORDER BY id DESC;"; //hay que conseguir todas las clientes
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					Clientes g = new Clientes();
					ArrayList<LinkSaldo> Clientes = g.getClientes();
					rs.beforeFirst();
					int saldo=0;
					PreparedStatement ps2;
					ResultSet rs2;
					while (rs.next()) {
						sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
						ps2=conn.prepareStatement(sql);
						rs2=ps2.executeQuery();
						while(rs2.next()) {
							saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
						}
						Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
						saldo=0;
					}
					int i=0;
					while(i<Clientes.size()) {
						if(Clientes.get(i).getSaldo()<desde1 /*|| Clientes.get(i).getSaldo()>hasta1*/) {
							Clientes.remove(i);
						}
						else {i++;}
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
				 }
					//filtro por intervalo de informacion
					else if(desde!=null&&hasta!=null){
						int desde1=Integer.parseInt(desde);
						int hasta1=Integer.parseInt(hasta);
						String sql = "SELECT * FROM Cliente  ORDER BY id DESC;"; //hay que conseguir todas las clientes
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						int i=0;
						while(i<Clientes.size()) {
							if(Clientes.get(i).getSaldo()<desde1 || Clientes.get(i).getSaldo()>hasta1) {
								Clientes.remove(i);
							}
							else {i++;}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
				}
				
				//si hay dos limites 
				else if(numero2!=null) {
					int limite1 = Integer.parseInt(numero1);
					//filtro por una cantidad mayor de X dinero y dos limites 
					if(desde!=null && hasta==null) {
						int desde1=Integer.parseInt(desde);
						int limite2= Integer.parseInt(numero2);
						String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1)+","+(limite2)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						int i=0;
						while(i<Clientes.size()) {
							if(Clientes.get(i).getSaldo()<desde1 /*|| Clientes.get(i).getSaldo()>hasta1*/) {
								Clientes.remove(i);
							}
							else {i++;}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//filtro por un intervalo de fecha y dos limites
					else if(desde!=null&&hasta!=null) {
						int desde1=Integer.parseInt(desde);
						int hasta1=Integer.parseInt(hasta);
						int limite2= Integer.parseInt(numero2);
						String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1)+","+(limite2)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						int i=0;
						while(i<Clientes.size()) {
							if(Clientes.get(i).getSaldo()<desde1 || Clientes.get(i).getSaldo()>hasta1) {
								Clientes.remove(i);
							}
							else {i++;}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//filtro por dos limites 
					else if(numero1!=null&&numero2!=null){
						int limite2= Integer.parseInt(numero2);
						String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1-1)+","+(limite2-1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
				}
				//si solo hay un limite
				else if(numero2==null){
					//filtro por una cantidad mayor de y un limite
					if(desde!=null&&hasta==null) {
					int desde1=Integer.parseInt(desde);
					int limite1 = Integer.parseInt(numero1);
					String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					Clientes g = new Clientes();
					ArrayList<LinkSaldo> Clientes = g.getClientes();
					rs.beforeFirst();
					int saldo=0;
					PreparedStatement ps2;
					ResultSet rs2;
					while (rs.next()) {
						sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
						ps2=conn.prepareStatement(sql);
						rs2=ps2.executeQuery();
						while(rs2.next()) {
							saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
						}
						Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
						saldo=0;
					}
					int i=0;
					while(i<Clientes.size()) {
						if(Clientes.get(i).getSaldo()<desde1 /*|| Clientes.get(i).getSaldo()>hasta1*/) {
							Clientes.remove(i);
						}
						else {i++;}
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//filtro por intervalo de cantidad de dinero y limites
					else if(desde!=null&&hasta!=null) {
						int desde1=Integer.parseInt(desde);
						int hasta1=Integer.parseInt(hasta);
						int limite1 = Integer.parseInt(numero1);
						String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						int i=0;
						while(i<Clientes.size()) {
							if(Clientes.get(i).getSaldo()<desde1 || Clientes.get(i).getSaldo()>hasta1) {
								Clientes.remove(i);
							}
							else {i++;}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//filtro por un limite
					else {
						int limite1 = Integer.parseInt(numero1);
						String sql = "SELECT * FROM Cliente ORDER BY id DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						Clientes g = new Clientes();
						ArrayList<LinkSaldo> Clientes = g.getClientes();
						rs.beforeFirst();
						int saldo=0;
						PreparedStatement ps2;
						ResultSet rs2;
						while (rs.next()) {
							sql="SELECT saldo FROM Cuenta_Bancaria where Cliente_id="+rs.getInt("id")+";"; //obtenemos el saldo de todas sus cuentas
							ps2=conn.prepareStatement(sql);
							rs2=ps2.executeQuery();
							while(rs2.next()) {
								saldo=saldo+rs2.getInt("saldo"); //lo acumulamos
							}
							Clientes.add(new LinkSaldo(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self",saldo));
							saldo=0;
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					}
					return Response.status(Response.Status.BAD_REQUEST).entity("Parametros erroneos")
							.build();
				
				
			} catch (NumberFormatException e) {
				return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
						.build();
			} catch (SQLException e) {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
			}
		}
		
		
		
	//Obtener datos de un cliente 
	@GET
	@Path("{cliente_id}")
	@Produces({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response getCliente(@PathParam("cliente_id") String id) {
		try {
			int int_id = Integer.parseInt(id);
			String sql = "SELECT * FROM Cliente where id=" + int_id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Cliente cliente =  clienteFromRS(rs);
				//metemos en Cliente las cuentas bancarias que tenga y sus movimientos
				sql= "SELECT *  FROM Cuenta_Bancaria where Cliente_id="+int_id+";";
				ps= conn.prepareStatement(sql);
				rs=ps.executeQuery();
				while(rs.next()) {
					Cuenta_Bancaria cuenta=cuenta_BancariaFromRS(rs);
					cliente.añadirCuenta(cuenta);
				}
				//ahora metemos todos sus movimientos
				sql= "SELECT * FROM Movimientos where  id_cliente="+int_id+" ORDER BY fecha DESC LIMIT 10;";
				ps= conn.prepareStatement(sql);
				rs=ps.executeQuery();
				while(rs.next()) {
					Movimientos movimiento=movimientosFromRS(rs);
					cliente.añadirMovimiento(movimiento);
				}
				return Response.status(Response.Status.OK).entity(cliente).build();
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
		} catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
	}
	
	//Para añadir un cliente del banco o crear un perfil de usuario 
	@POST
	@Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
	public Response createCliente(Cliente cliente) {
		try {
			String sql = "INSERT INTO `miBancoUPM`.`Cliente` (`nombre`, `apellido`, `domicilio`, `telefono`, `DNI`) " + "VALUES ('"
					+ cliente.getNombre() + "', '" + cliente.getApellido() + "', '" + cliente.getDomicilio() + "', '" + cliente.getTelefono() + "', '" + cliente.getDNI()+ "');";
			PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			int affectedRows = ps.executeUpdate();
			
			// Obtener el ID del elemento recién creado. 
			// Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
			ResultSet generatedID = ps.getGeneratedKeys();
			if (generatedID.next()) {
				cliente.setId(generatedID.getInt(1));
				String location = uriInfo.getAbsolutePath() + "/" + cliente.getId();
				return Response.status(Response.Status.CREATED).entity(cliente).header("Location", location).header("Content-Location", location).build();
			}
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el cliente").build();
			
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el cliente\n" + e.getStackTrace()).build();
		}
	}
	
	//Actualizar datos de un cliente 
	@PUT
	@Consumes({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
	@Path("{cliente_id}")
	public Response updateCliente(@PathParam("cliente_id") String id, Cliente nuevoCliente) {
		try {
			Cliente cliente;
			int int_id = Integer.parseInt(id);
			String sql = "SELECT * FROM Cliente where id=" + int_id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				cliente =  clienteFromRS(rs);
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
			cliente.setDomicilio(nuevoCliente.getDomicilio());
			cliente.setApellido(nuevoCliente.getApellido());
			cliente.setNombre(nuevoCliente.getNombre());
			cliente.setTelefono(nuevoCliente.getTelefono());
			cliente.setDNI(nuevoCliente.getDNI());
			

			sql = "UPDATE `miBancoUPM`.`Cliente` SET "
					+ "`nombre`='" + cliente.getNombre() 
					+ "', `apellido`='" + cliente.getApellido()
					+ "', `domicilio`='" + cliente.getDomicilio() 
					+ "', `telefono`='" + cliente.getTelefono()
					+ "', `DNI`='" + cliente.getDNI() 
					+ " ' WHERE id=" + int_id + ";";
			
			ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			
			// Location a partir del URI base (host + root de la aplicación + ruta del servlet)
			String location = uriInfo.getBaseUri() + "clientes"+ "/" + cliente.getId();
			return Response.status(Response.Status.OK).entity(cliente).header("Content-Location", location).build();			
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el cliente\n" + e.getStackTrace()).build();
		}
	}
	
	//borrar datos de un cliente siempre y cuando no haya cuentas abiertas
	@DELETE
	@Path("{cliente_id}")
	public Response deleteCliente(@PathParam("cliente_id") String id) {
		try {
			Cliente cliente;
			int int_id = Integer.parseInt(id);
			//obtenemos ese cliente 
			String sql = "SELECT * FROM Cliente where id=" + int_id + ";";
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				cliente =  clienteFromRS(rs);
			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
			//metemos en Cliente las cuentas bancarias que tenga y sus movimientos
			sql= "SELECT *  FROM Cuenta_Bancaria where Cliente_id="+int_id+";";
			ps= conn.prepareStatement(sql);
			rs=ps.executeQuery();
			while(rs.next()) {
				Cuenta_Bancaria cuenta=cuenta_BancariaFromRS(rs);
				cliente.añadirCuenta(cuenta);
			}
			if(cliente.getCuentas_Bancarias().size()==0) {
			sql = "DELETE FROM `miBancoUPM`.`Cliente` WHERE `id`='" + int_id + "';";
		    ps = conn.prepareStatement(sql);
			int affectedRows = ps.executeUpdate();
			if (affectedRows == 1)
				return Response.status(Response.Status.NO_CONTENT).build();
			else 
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();	
			}
			else {
				return Response.status(Response.Status.PRECONDITION_FAILED).entity("No se puede eliminar cliente ya que tiene cuentas abiertas").build();
			}
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el cliente\n" + e.getStackTrace()).build();
		}
	}
	
	//para los datos básicos de un cliente
	private Cliente clienteFromRS(ResultSet rs) throws SQLException {
		Cliente cliente = new Cliente(rs.getString("nombre"), rs.getString("apellido"), rs.getString("domicilio"), rs.getString("telefono"), rs.getString("DNI"));
		cliente.setId(rs.getInt("id"));
		return cliente;
	}
	
	//para crear una cuenta bancaria 
	private Cuenta_Bancaria cuenta_BancariaFromRS(ResultSet rs) throws SQLException {
				Cuenta_Bancaria cuenta_Bancaria = new Cuenta_Bancaria(rs.getInt("Cliente_id"), rs.getString("nombreDuenio"), rs.getString("apellidoDuenio"), rs.getInt("saldo"));
				cuenta_Bancaria.setId(rs.getInt("id"));
				return cuenta_Bancaria;
			}
			
	//para crear un Movimiento 
	private Movimientos movimientosFromRS(ResultSet rs) throws SQLException {
		Movimientos movimientos = new Movimientos(rs.getInt("id_cliente"), rs.getInt("id_Movimiento"), rs.getString("tipo"), rs.getString("fecha"), rs.getInt("id_cuenta"));
		return movimientos;
			}			
	
}
