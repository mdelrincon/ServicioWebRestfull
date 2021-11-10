package clase.recursos.bbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
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

@Path("clientes/{id_cliente}/cuentas_bancarias/movimientos")

public class MovimientosRecurso {
	@Context
	private UriInfo uriInfo;

	private DataSource ds;
	private Connection conn;

	public MovimientosRecurso () {
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
				public Response getMovimientos
				(@PathParam("id_cliente") String id,
						@QueryParam("desde") String desde,
						@QueryParam("hasta") String hasta,
						@QueryParam("limite1") String numero1, 
						@QueryParam("limite2") String numero2)  {
					try {
						//si no hay filtros 
						if(desde==null&&numero1==null) {
						int id_cuenta=Integer.parseInt(id);
						int off = 1;
						int c = 10;
						String sql = "SELECT * FROM Movimientos WHERE (id_cliente="+id_cuenta+") order by fecha LIMIT " + (off - 1) + "," + c + ";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						MovimientoLink g = new MovimientoLink();
						ArrayList<Link> MovimientoLink = g.getMovimientos();
						rs.beforeFirst();
						while (rs.next()) {
							if(rs.getString("tipo").equals("Transferencia")) {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "transferencias" + "/" + rs.getInt("id_Movimiento"),"self"));
							}
							else {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/" + id+"/cuentas_bancarias/"+rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo" + "/" + rs.getInt("id_Movimiento"),"self"));
							}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
					//filtro solo por fechas
					if(numero1==null) {
						int id_cuenta=Integer.parseInt(id);
						String sql = "SELECT * FROM Movimientos WHERE (id_cliente=" +id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC;"; //hay que conseguir todas las transferencias entre fechas 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						MovimientoLink g = new MovimientoLink();
						ArrayList<Link> MovimientoLink = g.getMovimientos();
						rs.beforeFirst();
						while (rs.next()) {
							if(rs.getString("tipo").equals("Transferencia")) {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "transferencias" + "/" + rs.getInt("id_Movimiento"),"self"));
							}
							else {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo" + "/" + rs.getInt("id_Movimiento"),"self"));
							}
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
						String sql = "SELECT * FROM Movimientos WHERE (id_cliente="+id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC LIMIT "+(limite1-1)+","+(limite2-1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						MovimientoLink g = new MovimientoLink();
						ArrayList<Link> MovimientoLink = g.getMovimientos();
						rs.beforeFirst();
						while (rs.next()) {
							if(rs.getString("tipo").equals("Transferencia")) {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "transferencias" + "/" + rs.getInt("id_Movimiento"),"self"));
							}
							else {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo/"+ rs.getInt("id_Movimiento"),"self"));
							}
						}
						return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
						//filtro por dos limites 
						else {
							int limite2= Integer.parseInt(numero2);
							String sql = "SELECT * FROM Movimientos WHERE (id_cliente="+id_cuenta+")  ORDER BY fecha DESC LIMIT "+(limite1)+","+(limite2)+";"; //hay que conseguir todas las transferencias 
							PreparedStatement ps = conn.prepareStatement(sql);
							ResultSet rs = ps.executeQuery();
							MovimientoLink g = new MovimientoLink();
							ArrayList<Link> MovimientoLink = g.getMovimientos();
							rs.beforeFirst();
							while (rs.next()) {
								if(rs.getString("tipo").equals("Transferencia")) {
									MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "transferencias" + "/"+ rs.getInt("id_Movimiento"),"self"));
								}
								else {
									MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo" + "/"+rs.getInt("id_Movimiento"),"self"));
								}
							}
							return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
						}
					}
					//si solo hay un limite 
					else {
					//filtro por fecha y un limite
					if(desde!=null) {
					String sql = "SELECT * FROM Movimientos WHERE (id_cliente="+id_cuenta+") AND fecha BETWEEN '"+desde+"' AND '"+hasta+"' ORDER BY fecha DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
					PreparedStatement ps = conn.prepareStatement(sql);
					ResultSet rs = ps.executeQuery();
					MovimientoLink g = new MovimientoLink();
					ArrayList<Link> MovimientoLink = g.getMovimientos();
					rs.beforeFirst();
					while (rs.next()) {
						if(rs.getString("tipo").equals("Transferencia")) {
							MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+"transferencias" + "/"+ rs.getInt("id_Movimiento"),"self"));
						}
						else {
							MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo" + "/"+rs.getInt("id_Movimiento"),"self"));
						}
					}
					return Response.status(Response.Status.OK).entity(g).build(); // No se puede devolver el ArrayList (para generar XML)
					}
					//filtro por un limite
					else {
						String sql = "SELECT * FROM Movimientos WHERE (id_cliente="+id_cuenta+") ORDER BY fecha DESC LIMIT "+(limite1)+";"; //hay que conseguir todas las transferencias 
						PreparedStatement ps = conn.prepareStatement(sql);
						ResultSet rs = ps.executeQuery();
						MovimientoLink g = new MovimientoLink();
						ArrayList<Link> MovimientoLink = g.getMovimientos();
						rs.beforeFirst();
						while (rs.next()) {
							if(rs.getString("tipo").equals("Transferencia")) {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+"transferencias" + "/"+ rs.getInt("id_Movimiento"),"self"));
							}
							else {
								MovimientoLink.add(new Link(uriInfo.getBaseUri() + "clientes/"+ id+"/cuentas_bancarias/"+ rs.getInt("id_cuenta") +"/movimientos/"+ "retiradas_efectivo" + "/"+rs.getInt("id_Movimiento"),"self"));
							}
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
}
