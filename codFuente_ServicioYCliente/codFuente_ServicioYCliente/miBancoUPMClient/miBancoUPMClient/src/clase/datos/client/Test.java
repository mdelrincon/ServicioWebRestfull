package clase.datos.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
//import javax.ws.rs.client.config.DefaultClientConfig;
//import javax.ws.rs.representation.Form;

import clase.datos.Cliente;
import clase.datos.Clientes;
import clase.datos.Cuenta_Bancaria;
import clase.datos.Cuentas_Bancarias;
import clase.datos.Fecha;
import clase.datos.Link;
import clase.datos.LinkSaldo;
import clase.datos.MovimientoLink;
import clase.datos.Movimientos;
import clase.datos.Retirada_Efectivo;
import clase.datos.Retiradas_Efectivo;
import clase.datos.Transferencia;
import clase.datos.Transferencias;

import java.util.Iterator;
import java.util.ArrayList;
public class Test {
  public static void main(String[] args) {

	    ClientConfig config = new ClientConfig();
	    Client client = ClientBuilder.newClient(config);
	    WebTarget target = client.target(getBaseURI());
	    
	    //Crear un cliente
	    //(String nombre,String apellido,String domicilio,String telefono,String DNI)
	    Cliente cliente = new Cliente("Paco", "Gomez", "Carril,22", "674654654", "7384384A");
	   
	    Response response = target.path("api").path("/clientes")
		    	.request().post(Entity.xml(cliente),Response.class);
	    
	    
	    // Se comprueba que el código http devuelto es 201 == created
	    System.out.println("Creamos un cliente\n");
	    System.out.println("Status:" + response.getStatus());
		if(response.getHeaders().containsKey("Location")) {
			 System.out.println("URI del cliente\n");
			Object location = response.getHeaders().get("Location").get(0);
			System.out.println("Location: " + location.toString());
			System.out.println("\n");
		}  
		
		//Ver los datos de un cliente
		Cliente cl_ver = response.readEntity(Cliente.class);
		int id_cliente = cl_ver.getId(); 
		
		// Obtiene el cliente en JSON para una aplicación
		 System.out.println("Datos del cliente creado:\n");
	    System.out.println(target.path("api").path("/clientes/"+id_cliente).request()
	        .accept(MediaType.APPLICATION_JSON).get(String.class));
	    System.out.println("\n");
	  /*  // Obtiene el cliente en XML para una aplicación
	    System.out.println(target.path("api").path("/clientes/"+id_cliente).request()
	        .accept(MediaType.APPLICATION_XML).get(String.class));	*/
	    System.out.println("Modificamos los datos del cliente\n");
	    //Modificar datos de un cliente
	    cl_ver.setNombre("Manolo");
	    cl_ver.setApellido("Sanz");
	    cl_ver.setDNI("38492349Y");
	    response = target.path("api").path("/clientes/"+id_cliente)
		    	.request().put(Entity.xml(cl_ver),Response.class);
	    // Se comprueba que el código http devuelto es 200 == se ha modificado con exito
	    System.out.println("Status:" + response.getStatus());
	    System.out.println("\n");
	    System.out.println("Nuevos datos del cliente modificado\n");
		
	    
	 /*// Obtiene el Cliente con XML
	    System.out.println(target.path("api").path("/clientes/"+id_cliente).request()
	 	       .accept(MediaType.APPLICATION_XML).get(String.class));*/
	  
	 // Obtiene el cliente en JSON para ver si se ha modificado
	    System.out.println(target.path("api").path("/clientes/"+id_cliente).request()
	           .accept(MediaType.APPLICATION_JSON).get(String.class));
	    System.out.println("\n");
	 /*// Obtiene el cliente en JSON para una aplicación
	    System.out.println(target.path("api").path("/clientes/"+id_cliente).request()
	           .accept(MediaType.APPLICATION_XML).get(String.class));	*/
	 	    
	 // Elimina el cliente
	    System.out.println("Eliminamos el cliente creado anteriormente\n");
	    response = target.path("api").path("/clientes/"+id_cliente).request().delete();
	    System.out.println("Status:" + response.getStatus());  
	    System.out.println("\n");
	 
	 //Creamos otro cliente   
	    System.out.println("Creamos de nuevo otro cliente \n");
	    Cliente cliente2 = new Cliente("Paco", "Gomez", "Carril,22", "674654654", "7384384A");
	    Response response2 = target.path("api").path("/clientes")
		    	.request().post(Entity.xml(cliente2),Response.class);
	    
	    Cliente cl_ver2 = response2.readEntity(Cliente.class);
	    int id_cl2 = cl_ver2.getId();
	    
	   //Crear cuenta bancaria para cliente2 con saldo 0 para despues borrarla
	    //int id_cliente,String nombreDuenio, String apellidoDuenio, int saldo
	   Cuenta_Bancaria cbancaria = new Cuenta_Bancaria(id_cl2, cl_ver2.getNombre(), 
			                       cl_ver2.getApellido(), 0);
	   //@Path("/clientes/{id_cliente}/cuentas_bancarias")
	   Response response_cb = target.path("api").path("/clientes/"+id_cl2)
			   .path("/cuentas_bancarias").request()
			   .post(Entity.xml(cbancaria),Response.class);
	    	    cbancaria=response_cb.readEntity(Cuenta_Bancaria.class);
	    	    int id_cuenta=cbancaria.getId();
	    // Se comprueba que el código http devuelto es 201 == created
	   System.out.println("Creamos una cuenta bancaria para ese cliente con saldo 0\n");
	    System.out.println("Status:" + response_cb.getStatus());
	    System.out.println("\n");
		if(response_cb.getHeaders().containsKey("Location")) {
			System.out.println("URI de esa cuenta creada\n");
			Object location = response_cb.getHeaders().get("Location").get(0);
			System.out.println("Location: " + location.toString());
			System.out.println("\n");
		}
		System.out.println("Datos de la cuenta creada: \n");
	    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+id_cuenta).request()
		           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que el saldo ha cambiado
		    System.out.println("\n");
		
		//ahora al intentar borrar el cliente al tener una cuenta abierta nos debe dar un error de precondicion
		System.out.println("Ahora intentamos borrar el cliente, pero al tener una cuenta abierta nos devuelve error\n");
		response = target.path("api").path("/clientes/"+id_cl2).request().delete();
	    System.out.println("Status:" + response.getStatus()); //codigo 412 PRECONDITION FAILED 
	    
		    
	  // Eliminamos esa cuenta creada con saldo 0
		System.out.println("Borramos la cuenta creada con saldo 0\n");
		Response response_cbx = target.path("api").path("/clientes/"+id_cl2)
				   .path("/cuentas_bancarias/"+cbancaria.getId()).request().delete();
	    System.out.println("Status:" + response_cbx.getStatus()); 
	    System.out.println("\n");
	    //ahora cambiamos el saldo para crear una que si tenga dinero
	    Cuenta_Bancaria nueva=new Cuenta_Bancaria(id_cl2, cl_ver2.getNombre(), 
                cl_ver2.getApellido(), 500);
	    
		System.out.println("Creamos una nueva cuenta con saldo 500\n");
	    response_cb = target.path("api").path("/clientes/"+id_cl2)
				   .path("/cuentas_bancarias").request()
				   .post(Entity.xml(nueva),Response.class);
		 cbancaria=response_cb.readEntity(Cuenta_Bancaria.class);   	    
		    // Se comprueba que el código http devuelto es 201 == created
		    System.out.println("Status:" + response_cb.getStatus());
		    System.out.println("\n");
			if(response_cb.getHeaders().containsKey("Location")) {
				Object location = response_cb.getHeaders().get("Location").get(0);
				System.out.println("Location: " + location.toString());
				System.out.println("\n");
			}
			 // Obtiene la cuenta bancaria creada en JSON 
		    System.out.println(target.path("api").path("/clientes/"+id_cliente).path("/cuentas_bancarias/"+cbancaria.getId()).request()
		           .accept(MediaType.APPLICATION_JSON).get(String.class));
		    System.out.println("\n");
		    
			// intentamos eliminar esa cuenta creada con saldo 500
			System.out.println("Intentamos borrar esa cuenta pero al tener saldo distinto de 0 no es posible\n");
			 response_cb = target.path("api").path("/clientes/"+id_cl2)
					   .path("/cuentas_bancarias/"+cbancaria.getId()).request().delete();
		    System.out.println("Status:" + response.getStatus()); //debería darnos un codigo 412 ya que la precondicion de que tuviese saldo 0 no la cumple
		    System.out.println("\n");
		//creamos otra cuenta para el mismo cliente con saldo 300
			System.out.println("Creamos otra cuenta para el mismo cliente con saldo 300\n");
		    Cuenta_Bancaria cbancaria2 = new Cuenta_Bancaria(id_cl2, cl_ver2.getNombre(), 
                    cl_ver2.getApellido(), 300);
		  //@Path("/clientes/{id_cliente}/cuentas_bancarias")
			   Response response_cb2 = target.path("api").path("/clientes/"+id_cl2)
					   .path("/cuentas_bancarias").request()
					   .post(Entity.xml(cbancaria2),Response.class);
			   cbancaria2=response_cb2.readEntity(Cuenta_Bancaria.class); 
			    	    
				
			//realizamos transferencias entre estas cuentas
			  System.out.println("Realizamos transferencias entre estas cuentas.\n");
			  System.out.println("Primer caso: no se puede realizar la transferencia ya que no hay suficiente saldo para ella\n");
			  System.out.println("Tratamos de realizar una transferencia de  500 la segunda cuenta creada(300 de saldo)\n");
				//primer caso: no se puede realizar la transferencia ya que no hay suficiente saldo para ella
				//trata de realizar una transferencia de  500 la segunda cuenta creada(300 de saldo) 
				//int cuentaRealiza,int cuentaRecibe,String fecha,int saldoTransferido
				Transferencia transferencia=new Transferencia(cbancaria2.getId(),cbancaria.getId(),"2020-01-01 10:00:00",500);
				//@Path("clientes/{id_cliente}/cuentas_bancarias/movimientos/{cuenta_bancaria_id}/transferencias")
				Response response_trans = target.path("api").path("/clientes/"+id_cl2)
						   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
						   .post(Entity.xml(transferencia),Response.class);
				System.out.println("Status:" + response_trans.getStatus()); //codigo 412: Vemos que falla la precondicion de que haya suficiente saldo
				System.out.println("\n");
				
				// Obtiene la cuenta bancaria creada en JSON 
				System.out.println("Como vemos, nos ha dado código de error y como podemos observar la cuenta sigue teniendo el mismo saldo:\n");
			    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria2.getId()).request()
			           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que no ha cambiado el saldo de la cuenta
			    System.out.println("\n");
			    
				//segundo caso:se realiza la transferencia con suficiente saldo
			    System.out.println("Segundo caso:se realiza una transferencia de 50 con suficiente saldo\n");
				transferencia=new Transferencia(cbancaria2.getId(),cbancaria.getId(),"2020-01-01 10:00:00",50);
				//@Path("clientes/{id_cliente}/cuentas_bancarias/movimientos/{cuenta_bancaria_id}/transferencias")
				 response_trans = target.path("api").path("/clientes/"+id_cl2)
						   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
						   .post(Entity.xml(transferencia),Response.class);
				 System.out.println("Status:" + response_cb2.getStatus()); //comprobamos que se ha podido crear perfectamente
				 System.out.println("\n");
				 
				 if(response_trans.getHeaders().containsKey("Location")) {
					 System.out.println("URI de la transferencia\n");
						Object location = response_trans.getHeaders().get("Location").get(0);
						System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
						System.out.println("\n");
				 }
				// Obtiene la cuenta bancaria creada en JSON 
				   System.out.println("Como observamos han cambiado el saldo de las dos cuentas \n");
				    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria.getId()).request()
				           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que el saldo ha cambiado
				    System.out.println("\n");
				    
				 // Obtiene la cuenta bancaria creada en JSON 
				    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria2.getId()).request()
				           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que tambien ha cambiado de la otra cuenta
				    System.out.println("\n");
				    
				transferencia=response_trans.readEntity(Transferencia.class); 
				//vemos los datos de esa transferencia concreta 
				System.out.println("Vemos los datos de esa transferencia en concreto\n");
				System.out.println(target.path("api").path("/clientes/"+id_cl2)
				   .path("/cuentas_bancarias/"+cbancaria.getId()).path("/movimientos/").path("/transferencias/"+transferencia.getId()).request()
				   .accept(MediaType.APPLICATION_JSON).get(String.class)); 
				 System.out.println("\n");
	    		
				 
				 //tercer caso: realizamos esa transferencia, vemos que ha cambiado, la borramos y vemos que las cuentas han vuelto a tener su antiguo saldo
				 System.out.println("Tercer caso: realizamos la misma transferencia (con distinta hora), vemos que ha cambiado, la borramos y vemos que las cuentas han vuelto a tener su antiguo saldo.\n");
				 transferencia=new Transferencia(cbancaria2.getId(),cbancaria.getId(),"2020-01-01 15:00:00",50);
					//@Path("clientes/{id_cliente}/cuentas_bancarias/movimientos/{cuenta_bancaria_id}/transferencias")
					 response_trans = target.path("api").path("/clientes/"+id_cl2)
							   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
							   .post(Entity.xml(transferencia),Response.class);
					 System.out.println("Status:" + response_trans.getStatus()); //comprobamos que se ha podido crear perfectamente
					 System.out.println("\n");
					 
					 if(response_trans.getHeaders().containsKey("Location")) {
							Object location = response_trans.getHeaders().get("Location").get(0);
							System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
							System.out.println("\n");
					 }
					// Obtiene la cuenta bancaria creada en JSON 
					 System.out.println("Observamos que los saldos de las dos cuentas han cambiado \n");
					 	System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria.getId()).request()
					           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que el saldo ha cambiado
					    System.out.println("\n");
					    
					 // Obtiene la cuenta bancaria creada en JSON 
					    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria2.getId()).request()
					           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que tambien ha cambiado de la otra cuenta
					    System.out.println("\n");
					    
					    System.out.println("Ahora borramos esa transferencia y las cuentas tienen el mismo saldo que antes de realizarla\n");
					transferencia=response_trans.readEntity(Transferencia.class); 
					response_trans=target.path("api").path("/clientes/"+id_cl2)
							   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias/"+transferencia.getId()).request()
							   .delete();
					 System.out.println("Status:" + response_trans.getStatus()); //comprobamos que se ha podido borrar
					 System.out.println("\n");
					// Obtiene la cuenta bancaria creada en JSON 
					    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria.getId()).request()
					           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que el saldo ha cambiado
					    System.out.println("\n");
					    
					 // Obtiene la cuenta bancaria creada en JSON 
					    System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria2.getId()).request()
					           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que tambien ha cambiado de la otra cuenta
					    System.out.println("\n");
					    
					    System.out.println("Como hemos podido comprobar los saldos han vuelto a ser los mismos \n");
					 
					  //Ahora vamos con las retiradas de efectivo 
					    System.out.println("Ahora probaremos las retiradas de efectivo."
					    		+ " Como con las transferencias hay dos casos:\n"
					    		+ "Primer caso: realizamos una retirada de efectivo de 1000 en una cuenta en la que no hay suficiente saldo");
					    Retirada_Efectivo retirada=new Retirada_Efectivo(cbancaria2.getId(),"2021-01-01 04:28:00",1000);
					    Response response_ret= target.path("api").path("/clientes/"+id_cl2)
								   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo").request()
								   .post(Entity.xml(retirada),Response.class);
					    System.out.println("Status:" + response_ret.getStatus()); //comprobamos que se ha podido crear perfectamente
						 System.out.println("\n");
						 System.out.println("Como podemos observar nos devuelve un codigo de status de error\n"
						 		+ "Vamos ahora con el caso de una retirada de efectivo de 5 que si se puede realizar\n");
						 retirada=new Retirada_Efectivo(cbancaria2.getId(),"2021-01-01 10:28:00",5);
						     response_ret= target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo").request()
									   .post(Entity.xml(retirada),Response.class);
						    System.out.println("Status:" + response_ret.getStatus()); //comprobamos que se ha podido crear perfectamente
							 System.out.println("\n");
							 retirada=response_ret.readEntity(Retirada_Efectivo.class);
						 if(response_ret.getHeaders().containsKey("Location")) {
							 	System.out.println("URI de la Retirada de Efectivo realizada\n");
								Object location = response_ret.getHeaders().get("Location").get(0);
								System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
								System.out.println("\n");
						 }
						 System.out.println("Vemos los datos de esa retirada en concreto\n");
							System.out.println(target.path("api").path("/clientes/"+id_cl2)
							   .path("/cuentas_bancarias/"+cbancaria.getId()).path("/movimientos/").path("/retiradas_efectivo/"+retirada.getId()).request()
							   .accept(MediaType.APPLICATION_JSON).get(String.class)); 
							 System.out.println("\n");
						System.out.println("Y ahora vemos que se ha actualizado el saldo de esa cuenta:\n");	 
						 System.out.println(target.path("api").path("/clientes/"+id_cl2).path("/cuentas_bancarias/"+cbancaria2.getId()).request()
						           .accept(MediaType.APPLICATION_JSON).get(String.class)); //observamos que tambien ha cambiado de la otra cuenta
						    System.out.println("\n");
						    
					   //Listado de todas las transferncias emitidas de una cuenta 
						    System.out.println("Obtenemos una lista de todas las transferencias emitidas por una cuenta"
						    		+ ". Para ello crearemos varias transferencias con distintos años, una del 2000 y otra en 2025");
						    transferencia= new Transferencia(cbancaria2.getId(),cbancaria.getId(),"2000-01-01 15:00:00",10);
						    response_trans = target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
									   .post(Entity.xml(transferencia),Response.class);
						    if(response_trans.getHeaders().containsKey("Location")) {
						    	System.out.println("URI de la Transferencia del año 2000\n");
								Object location = response_trans.getHeaders().get("Location").get(0);
								System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
								System.out.println("\n");
						 }
						    transferencia=new Transferencia(cbancaria2.getId(),cbancaria.getId(),"2025-01-01 15:00:00",10);
						    response_trans = target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
									   .post(Entity.xml(transferencia),Response.class);
						    if(response_trans.getHeaders().containsKey("Location")) {
						    	System.out.println("URI de la Transferencia del año 2025\n");
								Object location = response_trans.getHeaders().get("Location").get(0);
								System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
								System.out.println("\n");
						 }
						    System.out.println("Obtenemos la lista de todas las transferencias, es decir, sin filtros\n");   
						    Transferencias lista =  target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").request()
							        .accept(MediaType.APPLICATION_XML).get(Transferencias.class);
							    Iterator<Link> i  = lista.getTransferencias().iterator();
							    while (i.hasNext()) {
							    	System.out.println(i.next().getUrl());
							    }
							    System.out.println("\nComo se puede observar, nos muestra las tres transferencias que hemos realizado. Ahora filtraremos por "
							    		+ "número de movimientos, le pediremos solamente un movimiento.\n");
							    lista =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias").queryParam("limite1", "1").request()
								        .accept(MediaType.APPLICATION_XML).get(Transferencias.class);
							    i  = lista.getTransferencias().iterator();
							    while (i.hasNext()) {
							    	System.out.println(i.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos desde el primer movimiento hasta el segundo, lo que nos deberia mostrar todos menos el ultimo realizado\n.");
							    lista =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias")
										   .queryParam("limite1","1")
										   .queryParam("limite2","2")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Transferencias.class);
							    i  = lista.getTransferencias().iterator();
							    while (i.hasNext()) {
							    	System.out.println(i.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos las transferencias hechas desde 2020 hasta 2025, lo que nos deberia mostrar solo las dos hechas en ese periodo\n.");
							    lista =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias")
										   .queryParam("desde","2020-01-01 00:00:00")
										   .queryParam("hasta","2025-01-01 00:00:00")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Transferencias.class);
							    i  = lista.getTransferencias().iterator();
							    while (i.hasNext()) {
							    	System.out.println(i.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos las transferencias hechas desde 2020 hasta 2025 con limite de 1, lo que nos deberia mostrar solo una de las dos hechas durante ese periodo\n.");
							    lista =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/transferencias")
										   .queryParam("desde","2020-01-01 00:00:00")
										   .queryParam("hasta", "2025-01-01 00:00:00")
										   .queryParam("limite1", "1")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Transferencias.class);
							    i  = lista.getTransferencias().iterator();
							    while (i.hasNext()) {
							    	System.out.println(i.next().getUrl());
							    }
							    
							  //Listado de todas las retiradas emitidas de una cuenta
							    System.out.println("Ahora haremos lo mismo pero con retiradas de efectivo."
							    		+ " Para ello crearemos varias retiradas con distintos años, una del 2000 de 50 y otra en 2025 de 2\n");
							    retirada= new Retirada_Efectivo(cbancaria2.getId(),"2000-01-01 09:28:00",50);
							     response_ret= target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo").request()
										   .post(Entity.xml(retirada),Response.class);
							    System.out.println("Status:" + response_ret.getStatus()); //comprobamos que se ha podido crear perfectamente
								 System.out.println("\n");
								 retirada=response_ret.readEntity(Retirada_Efectivo.class);
							 if(response_ret.getHeaders().containsKey("Location")) {
								 	System.out.println("URI de la Retirada de Efectivo realizada en 2000\n");
									Object location = response_ret.getHeaders().get("Location").get(0);
									System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
									System.out.println("\n");
							 }
							 retirada=new Retirada_Efectivo(cbancaria2.getId(),"2025-01-01 00:28:00",2);
						     response_ret= target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo").request()
									   .post(Entity.xml(retirada),Response.class);
						    System.out.println("Status:" + response_ret.getStatus()); //comprobamos que se ha podido crear perfectamente
							 System.out.println("\n");
							 retirada=response_ret.readEntity(Retirada_Efectivo.class);
						 if(response_ret.getHeaders().containsKey("Location")) {
							 	System.out.println("URI de la Retirada de Efectivo realizada en 2025\n");
								Object location = response_ret.getHeaders().get("Location").get(0);
								System.out.println("Location: " + location.toString()); //nos devuelve la uri con ese recur
								System.out.println("\n");
						 }
						 
						 System.out.println("Obtenemos la lista de todas las retiradas, es decir, sin filtros\n");   
						    Retiradas_Efectivo listaR =  target.path("api").path("/clientes/"+id_cl2)
									   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo").request()
							        .accept(MediaType.APPLICATION_XML).get(Retiradas_Efectivo.class);
							    Iterator<Link> iR  = listaR.getRetiradas_Efectivo().iterator();
							    while (iR.hasNext()) {
							    	System.out.println(iR.next().getUrl());
							    }
							    System.out.println("\nComo se puede observar, nos muestra las tres retiradas que hemos realizado. Ahora filtraremos por "
							    		+ "cantidad de dinero, le pediremos solamente las retiradas mayores de 4.\n");
							    listaR =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo")
										   .queryParam("cantidadX", "4")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Retiradas_Efectivo.class);
							    iR  = listaR.getRetiradas_Efectivo().iterator();
							    while (iR.hasNext()) {
							    	System.out.println(iR.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos entre 1 y 10, lo que nos deberia mostrar todos menos el de 50\n.");
							    listaR =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo")
										   .queryParam("cantidadX","1")
										   .queryParam("cantidadY","10")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Retiradas_Efectivo.class);
							    iR  = listaR.getRetiradas_Efectivo().iterator();
							    while (iR.hasNext()) {
							    	System.out.println(iR.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos las retiradas hechas desde 2020 hasta 2026, lo que nos deberia mostrar solo las dos hechas en ese periodo\n.");
							    listaR =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo")
										   .queryParam("desde","2020-01-01 00:00:00")
										   .queryParam("hasta","2026-01-01 00:00:00")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Retiradas_Efectivo.class);
							    iR  = listaR.getRetiradas_Efectivo().iterator();
							    while (iR.hasNext()) {
							    	System.out.println(iR.next().getUrl());
							    }
							    System.out.println("\nAhora pediremos las retiradas hechas desde 2020 hasta 2026 que sea mayor de 4, lo que nos deberia mostrar solo una de las dos hechas durante ese periodo\n.");
							    listaR =  target.path("api").path("/clientes/"+id_cl2)
										   .path("/cuentas_bancarias/"+cbancaria2.getId()).path("/movimientos/").path("/retiradas_efectivo")
										   .queryParam("desde","2020-01-01 00:00:00")
										   .queryParam("hasta", "2026-01-01 00:00:00")
										   .queryParam("cantidadX", "4")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(Retiradas_Efectivo.class);
							    iR  = listaR.getRetiradas_Efectivo().iterator();
							    while (iR.hasNext()) {
							    	System.out.println(iR.next().getUrl());
							    }
							    
							  //Listado de todos los movimientos emitidos de una cuenta
							    System.out.println("Ahora consultaremos todos los movimientos:\n");
							    System.out.println("Obtenemos la lista de todos los movimientos, es decir, sin filtros\n");   
							    MovimientoLink listaM =  target.path("api").path("/clientes/"+id_cl2)
							    			.path("/cuentas_bancarias/")
										   .path("/movimientos/")
										   .request()
								        .accept(MediaType.APPLICATION_XML).get(MovimientoLink.class);
								    Iterator<Link> iM  = listaM.getMovimientos().iterator();
								    while (iM.hasNext()) {
								    	System.out.println(iM.next().getUrl());
								    }
								    System.out.println("\nComo se puede observar, nos muestra los tres movimientos que hemos realizado. Ahora filtraremos por "
								    		+ "número de movimientos, le pediremos solamente un movimiento.\n");
								    listaM =  target.path("api").path("/clientes/"+id_cl2)
								    		.path("/cuentas_bancarias/")
											   .path("/movimientos/")
											   .queryParam("limite1", "1").request()
									        .accept(MediaType.APPLICATION_XML).get(MovimientoLink.class);
								    iM  = listaM.getMovimientos().iterator();
								    while (iM.hasNext()) {
								    	System.out.println(iM.next().getUrl());
								    }
								    System.out.println("\nAhora pediremos desde el primer movimiento hasta el segundo, lo que nos deberia mostrar todos menos el ultimo realizado\n.");
								    listaM =  target.path("api").path("/clientes/"+id_cl2)
								    		.path("/cuentas_bancarias/")
											   .path("/movimientos/")
											   .queryParam("limite1","1")
											   .queryParam("limite2","2")
											   .request()
									        .accept(MediaType.APPLICATION_XML).get(MovimientoLink.class);
								    iM  = listaM.getMovimientos().iterator();
								    while (iM.hasNext()) {
								    	System.out.println(iM.next().getUrl());
								    }
								    System.out.println("\nAhora pediremos los movimientos hechos desde 2020 hasta 2025, lo que nos deberia mostrar solo las hechas en ese periodo\n.");
								    listaM =  target.path("api").path("/clientes/"+id_cl2)
								    		.path("/cuentas_bancarias/")
											   .path("/movimientos/")
											   .queryParam("desde","2020-01-01 00:00:00")
											   .queryParam("hasta","2025-01-01 00:00:00")
											   .request()
									        .accept(MediaType.APPLICATION_XML).get(MovimientoLink.class);
								    iM  = listaM.getMovimientos().iterator();
								    while (iM.hasNext()) {
								    	System.out.println(iM.next().getUrl());
								    }
								    System.out.println("\nAhora pediremos los movimientos hechos desde 2020 hasta 2025 con limite de 1, lo que nos deberia mostrar solo una de las dos hechas durante ese periodo\n.");
								    listaM =  target.path("api").path("/clientes/"+id_cl2)
								    		.path("/cuentas_bancarias/")
											   .path("/movimientos/")
											   .queryParam("desde","2020-01-01 00:00:00")
											   .queryParam("hasta", "2025-01-01 00:00:00")
											   .queryParam("limite1", "1")
											   .request()
									        .accept(MediaType.APPLICATION_XML).get(MovimientoLink.class);
								    iM  = listaM.getMovimientos().iterator();
								    while (iM.hasNext()) {
								    	System.out.println(iM.next().getUrl());
								    }
							    System.out.println("Ahora miraremos todos los clientes con sus respectivos saldos. Primero sin filtros:\n");
							    Clientes listaC= target.path("api").path("/clientes/").request()
								           .accept(MediaType.APPLICATION_JSON).get(Clientes.class);
							    Iterator<LinkSaldo> iC  = listaC.getClientes().iterator();
							    while (iC.hasNext()) {
							    	LinkSaldo aux= iC.next();
							    	System.out.println(aux.getUrl()+"\nsaldo:"+aux.getSaldo()+"\n");
							    }
								System.out.println("\n");
							    System.out.println("Ahora miraremos todos los clientes con saldos mayor a 100:\n");
							    listaC=target.path("api").path("/clientes/")
							    			.queryParam("desde", "100")
							    			.request()
								           .accept(MediaType.APPLICATION_JSON).get(Clientes.class);
							    iC  = listaC.getClientes().iterator();
							    while (iC.hasNext()) {
							    	LinkSaldo aux= iC.next();
							    	System.out.println(aux.getUrl()+"\nsaldo:"+aux.getSaldo()+"\n");
							    }
							    
							    System.out.println("Ahora queremos solo el primer cliente con mayor saldo que 100");
							    listaC=target.path("api").path("/clientes/")
						    			.queryParam("desde", "100")
						    			.queryParam("limite1", "1")
						    			.request()
							           .accept(MediaType.APPLICATION_JSON).get(Clientes.class);
							    iC  = listaC.getClientes().iterator();
							    while (iC.hasNext()) {
							    	LinkSaldo aux= iC.next();
							    	System.out.println(aux.getUrl()+"\nsaldo:"+aux.getSaldo()+"\n");
							    }
							    
							    System.out.println("Ahora veremos los datos basicos de un cliente, sus cuentas, sus saldos y los ultimos movimientos"
							    		+ " realizados en todas sus cuentas.");
							    Cliente cl=target.path("api").path("/clientes/"+id_cl2)
						    			.request()
							           .accept(MediaType.APPLICATION_JSON).get(Cliente.class);
							    System.out.println("Id del cliente: "+cl.getId()+"\n"
							    					+"Nombre del cliente: "+cl.getNombre()+"\n"
							    					+"Apellido del cliente: "+cl.getApellido()+"\n"
							    					+"DNI del cliente: "+cl.getDNI()+"\n"
							    					+"Domicilio del cliente: "+cl.getDomicilio()+"\n"
							    					+"Telefono del cliente: "+cl.getTelefono()+"\n"
							    					+"Cuentas Bancarias del cliente: {\n");
							    ArrayList<Cuenta_Bancaria> cuentasaux=cl.getCuentas_Bancarias();
							    for(int j=0;j<cuentasaux.size();j++) {
							    	Cuenta_Bancaria count=cuentasaux.get(j);
							    	System.out.println("[id: "+count.getId()+", saldo:"+count.getSaldo()+"]\n");
							    }
							    System.out.println("} \n Ultimos 10 movimientos del cliente: {\n");
							    ArrayList<Movimientos> move=cl.getMovimientos();
							    for(int j=0;j<move.size();j++) {
							    	Movimientos moveaux=move.get(j);
							    	System.out.println("[Tipo de movimiento: "+moveaux.getTipo()+", id_movimiento:"+moveaux.getIdMovimiento()+ ", id_cuenta:" + moveaux.getIdCuenta()+ ", Fecha: " + moveaux.getFecha()+ "]\n");
							    }
							    System.out.println("}");
							    					
							    			
		
  }

  private static URI getBaseURI() {
    return UriBuilder.fromUri("http://localhost:8080/miBancoUPM/").build();
  }
} 